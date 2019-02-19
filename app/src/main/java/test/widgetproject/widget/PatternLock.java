package test.widgetproject.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mvp.base.util.CollectionUtils;
import com.mvp.base.util.DisplayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import test.widgetproject.main.R;

/**
 * Created on 2018/5/23.
 * TODO 使用SurfaceView
 *
 * @author ChenFanlin
 */
public class PatternLock extends View {
    private static final String TAG = PatternLock.class.getSimpleName();

    private int mMinSelectedPointCount = 4;
    private int mPointColor = Color.DKGRAY;
    private int mErrorPointColor = Color.RED;
    private int mLineColor = Color.LTGRAY;
    private int mErrorLineColor = Color.RED;
    private int mPointTouchRadius = DisplayUtils.dp2px(20);
    private int mPointRadius = DisplayUtils.dp2px(4);
    private int mSpanCount = 3;

    private Paint mPaint;
    private PatternPoint[] mPatternPoints;
    private PatternPoint mLatestTouchedPoint;
    private boolean mIsError = false;
    private List<PatternPoint> mSelectedPoint = new ArrayList<>();
    private float mTouchingX;
    private float mTouchingY;
    private boolean mIsTouching = false;
    private OnResultListener mOnResultListener;
    private SecretKeyComparator mSecretKeyComparator;
    private OnConfigListener mOnConfigListener;
    //是否在设置密码
    private boolean mIsConfigMode = false;
    //是否在设定延迟后自动清除图案
    private boolean mAutoCleanPattern = true;
    //清除图案延迟
    private int mAutoCleanDelay = 1000;
    private Runnable mAutoCleanRunnable = new Runnable() {
        @Override
        public void run() {
            reset();
        }
    };

    public PatternLock(Context context) {
        this(context, null);
    }

    public PatternLock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PatternLock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        init();
        initPatternPoints();
    }

    /**
     * 判断3个点是否在同一直线上
     */
    private static boolean isThreePointsInLine(PatternPoint point1,
                                               PatternPoint point2,
                                               PatternPoint point3) {
        //deltaX1*deltaY2 = deltaX2*deltaY1 两段斜率是否相等
        return (point1.x - point2.x) * (point2.y - point3.y) ==
                (point2.x - point3.x) * (point1.y - point2.y);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mPointRadius * 2);
    }

    private void initPatternPoints() {
        mPatternPoints = new PatternPoint[mSpanCount * mSpanCount];
        int length = mPatternPoints.length;
        for (int i = 0; i < length; i++) {
            mPatternPoints[i] = new PatternPoint(0, 0);
        }
    }

    private void initAttrs(@Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PatternLock);
        mMinSelectedPointCount = typedArray.getInteger(R.styleable.PatternLock_plMinSelectedCount, mMinSelectedPointCount);
        mPointColor = typedArray.getColor(R.styleable.PatternLock_plPointColor, mPointColor);
        mErrorPointColor = typedArray.getColor(R.styleable.PatternLock_plErrorPointColor, mErrorPointColor);
        mLineColor = typedArray.getColor(R.styleable.PatternLock_plLineColor, mLineColor);
        mErrorLineColor = typedArray.getColor(R.styleable.PatternLock_plErrorLineColor, mErrorLineColor);
        mPointTouchRadius = typedArray.getDimensionPixelSize(R.styleable.PatternLock_plPointTouchRadius, mPointTouchRadius);
        mPointRadius = typedArray.getDimensionPixelSize(R.styleable.PatternLock_plPointRadius, mPointRadius);
        mSpanCount = typedArray.getDimensionPixelSize(R.styleable.PatternLock_plSpanCount, mSpanCount);
        typedArray.recycle();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouchingX = event.getX();
        mTouchingY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                reset();
                mLatestTouchedPoint = getTouchedPoint(event.getX(), event.getY());
                mIsTouching = true;
                break;
            case MotionEvent.ACTION_MOVE:
                PatternPoint newPoint = getTouchedPoint(event.getX(), event.getY());
                if (mLatestTouchedPoint == null && newPoint == null) {
                    //没有触摸到点
                    return true;
                } else if (newPoint != null && !isPointSelected(newPoint)) {
                    addPoint(newPoint);
                    mLatestTouchedPoint = newPoint;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                mLatestTouchedPoint = null;
                mIsTouching = false;
                String secretKey = mSelectedPoint.toString();
                if (mSelectedPoint.size() > 0) {
                    if (mIsConfigMode) {
                        //设置密码时
                        if (mOnConfigListener != null) {
                            if (mSelectedPoint.size() >= mMinSelectedPointCount) {
                                mOnConfigListener.onSuccess(mSelectedPoint, secretKey);
                            } else {
                                mOnConfigListener.onFailed(mSelectedPoint);
                            }
                        }
                    } else {
                        //解锁时
                        if (mSelectedPoint.size() >= mMinSelectedPointCount) {
                            if (mSecretKeyComparator != null && mOnResultListener != null) {
                                mIsError = !mSecretKeyComparator.onCompare(secretKey);
                                if (mIsError) {
                                    mOnResultListener.onFailed(mSelectedPoint);
                                } else {
                                    mOnResultListener.onSuccess(mSelectedPoint, secretKey);
                                }
                            }
                        } else {
                            mIsError = true;
                            mOnResultListener.onFailed(mSelectedPoint);
                        }
                    }
                    if (mAutoCleanPattern) {
                        //自动清除画的图案
                        postDelayed(mAutoCleanRunnable, mAutoCleanDelay);
                    }
                    invalidate();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calPointCoordinate(w, h);
    }

    private void calPointCoordinate(int w, int h) {
        int size = Math.min(w - getPaddingLeft() - getPaddingRight(), h - getPaddingTop() - getPaddingBottom());
        int length = mPatternPoints.length;
        int offset = mPointRadius;
        int xOffset;
        int yOffset;
        for (int i = 0; i < length; i++) {
            PatternPoint patternPoint = mPatternPoints[i];
            xOffset = (1 - i % mSpanCount) * offset;
            yOffset = (1 - i / mSpanCount) * offset;
            patternPoint.index = i;
            patternPoint.x = (int) (i % mSpanCount / (mSpanCount - 1.0) * size + xOffset) + getPaddingLeft();
            patternPoint.y = (int) (i / mSpanCount / (mSpanCount - 1.0) * size + yOffset) + getPaddingTop();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (PatternPoint patternPoint : mPatternPoints) {
            drawPoint(canvas, patternPoint);
        }
        if (mIsTouching) {
            drawLineFollowFinger(canvas);
        }
        drawLineBetweenPoints(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        getHandler().removeCallbacks(mAutoCleanRunnable);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int minSize = mPointRadius * mSpanCount + mPointTouchRadius * mSpanCount;
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = Math.max(widthSize, minSize + getPaddingLeft() + getPaddingRight());
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = Math.max(heightSize, minSize + getPaddingTop() + getPaddingBottom());
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 绘制点
     *
     * @param canvas       画布
     * @param patternPoint 需要绘制的点
     */
    private void drawPoint(Canvas canvas, PatternPoint patternPoint) {
        if (mIsError && isPointSelected(patternPoint)) {
            mPaint.setColor(mErrorPointColor);
        } else {
            mPaint.setColor(mPointColor);
        }
        canvas.drawPoint(patternPoint.x, patternPoint.y, mPaint);
    }

    /**
     * 绘制被选中的点之间的线
     *
     * @param canvas 画布
     */
    private void drawLineBetweenPoints(Canvas canvas) {
        if (mSelectedPoint.size() > 1) {
            if (mIsError) {
                mPaint.setColor(mErrorLineColor);
            } else {
                mPaint.setColor(mLineColor);
            }
            PatternPoint prePatternPoint = null;
            for (PatternPoint patternPoint : mSelectedPoint) {
                if (prePatternPoint == null) {
                    prePatternPoint = patternPoint;
                    continue;
                }
                canvas.drawLine(prePatternPoint.x, prePatternPoint.y, patternPoint.x, patternPoint.y, mPaint);
                prePatternPoint = patternPoint;
            }
        }
    }

    /**
     * 将点(以及额外的点)加入被选择的点
     *
     * @param patternPoint 当前被选中的点
     */
    private void addPoint(PatternPoint patternPoint) {
        if (mLatestTouchedPoint != null && mLatestTouchedPoint != patternPoint) {
            List<PatternPoint> additionalPoints = getAdditionalPoint(patternPoint);
            if (!CollectionUtils.isEmpty(additionalPoints)) {
                mSelectedPoint.addAll(additionalPoints);
            }
        }
        mSelectedPoint.add(patternPoint);
        Log.d(TAG, mSelectedPoint.toString());
    }

    /**
     * 添加额外需要的点,比如在跨行跨列时,添加中间的点
     *
     * @param nextPoint 被加入的点
     * @return 额外的点
     */
    @Nullable
    private List<PatternPoint> getAdditionalPoint(PatternPoint nextPoint) {
        List<PatternPoint> additionalPoints = null;
        int min = Math.min(nextPoint.index, mLatestTouchedPoint.index);
        int max = Math.max(nextPoint.index, mLatestTouchedPoint.index);
        PatternPoint patternPoint;
        for (int i = min + 1; i < max; i++) {
            //遍历范围是两个点之间的点
            patternPoint = mPatternPoints[i];
            if (isPointSelected(patternPoint)) {
                continue;
            }
            if (additionalPoints == null) {
                additionalPoints = new ArrayList<>();
            }
            if (isThreePointsInLine(mLatestTouchedPoint, nextPoint, patternPoint)) {
                //3个点都在一条线上说明跨过了中间的点
                additionalPoints.add(patternPoint);
            }
        }
        //如果跨过了1个点以上,则需要注意点加入的顺序,默认是升序
        int orderType = nextPoint.index - mLatestTouchedPoint.index;
        if (additionalPoints != null && additionalPoints.size() > 1 && orderType < 0) {
            Collections.reverse(additionalPoints);
        }
        return additionalPoints;
    }

    public void reset() {
        removeCallbacks(mAutoCleanRunnable);
        mLatestTouchedPoint = null;
        mSelectedPoint.clear();
        mIsError = false;
        invalidate();
    }

    /**
     * 绘制最近一个点到触摸的线
     *
     * @param canvas 画布
     */
    private void drawLineFollowFinger(Canvas canvas) {
        if (mLatestTouchedPoint == null) {
            return;
        }
        mPaint.setColor(mLineColor);
        canvas.drawLine(mLatestTouchedPoint.x, mLatestTouchedPoint.y, mTouchingX, mTouchingY, mPaint);
    }

    /**
     * 获取当前触摸到的点
     *
     * @param x 当前x坐标
     * @param y 当前y坐标
     * @return 被触摸的点
     */
    @Nullable
    private PatternPoint getTouchedPoint(float x, float y) {
        double distance;
        for (PatternPoint patternPoint : mPatternPoints) {
            distance = Math.hypot(x - patternPoint.x, y - patternPoint.y);
            if (distance <= mPointRadius + mPointTouchRadius) {
                return patternPoint;
            }
        }
        return null;
    }

    /**
     * 当前点是否已加入
     *
     * @param patternPoint 需要判断的点
     * @return 是否已加入
     */
    private boolean isPointSelected(PatternPoint patternPoint) {
        return mSelectedPoint.contains(patternPoint);
    }

    public int getMinSelectedPointCount() {
        return mMinSelectedPointCount;
    }

    public void setMinSelectedPointCount(int minSelectedPointCount) {
        if (mMinSelectedPointCount != minSelectedPointCount) {
            mMinSelectedPointCount = minSelectedPointCount;
            invalidate();
        }
    }

    public int getPointColor() {
        return mPointColor;
    }

    public void setPointColor(int pointColor) {
        if (mPointColor != pointColor) {
            mPointColor = pointColor;
            invalidate();
        }
    }

    public int getErrorPointColor() {
        return mErrorPointColor;
    }

    public void setErrorPointColor(int errorPointColor) {
        if (mErrorPointColor != errorPointColor) {
            mErrorPointColor = errorPointColor;
            invalidate();
        }
    }

    public int getLineColor() {
        return mLineColor;
    }

    public void setLineColor(int lineColor) {
        if (mLineColor != lineColor) {
            mLineColor = lineColor;
            invalidate();
        }
    }

    public int getErrorLineColor() {
        return mErrorLineColor;
    }

    public void setErrorLineColor(int errorLineColor) {
        if (mErrorLineColor != errorLineColor) {
            mErrorLineColor = errorLineColor;
            invalidate();
        }
    }

    public int getPointTouchRadius() {
        return mPointTouchRadius;
    }

    public void setPointTouchRadius(int pointTouchRadius) {
        if (mPointTouchRadius != pointTouchRadius) {
            mPointTouchRadius = pointTouchRadius;
            requestLayout();
        }
    }

    public int getPointRadius() {
        return mPointRadius;
    }

    public void setPointRadius(int pointRadius) {
        if (mPointRadius != pointRadius) {
            mPointRadius = pointRadius;
            requestLayout();
        }
    }

    public int getSpanCount() {
        return mSpanCount;
    }

    public void setSpanCount(int spanCount) {
        if (mSpanCount != spanCount) {
            mSpanCount = spanCount;
            initPatternPoints();
            reset();
            calPointCoordinate(getMeasuredWidth(), getMeasuredHeight());
            requestLayout();
        }
    }

    public boolean isError() {
        return mIsError;
    }

    public void setError(boolean error) {
        if (mIsError != error) {
            mIsError = error;
            invalidate();
        }
    }

    public boolean isAutoCleanPattern() {
        return mAutoCleanPattern;
    }

    public void setAutoCleanPattern(boolean autoCleanPattern) {
        mAutoCleanPattern = autoCleanPattern;
    }

    public int getAutoCleanDelay() {
        return mAutoCleanDelay;
    }

    public void setAutoCleanDelay(int autoCleanDelay) {
        mAutoCleanDelay = autoCleanDelay;
    }

    public List<PatternPoint> getSelectedPoint() {
        return mSelectedPoint;
    }

    public OnResultListener getOnResultListener() {
        return mOnResultListener;
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        mOnResultListener = onResultListener;
    }

    public SecretKeyComparator getSecretKeyComparator() {
        return mSecretKeyComparator;
    }

    public void setSecretKeyComparator(SecretKeyComparator secretKeyComparator) {
        mSecretKeyComparator = secretKeyComparator;
    }

    public boolean isConfigMode() {
        return mIsConfigMode;
    }

    public void setConfigMode(boolean configMode) {
        if (mIsConfigMode != configMode) {
            mIsConfigMode = configMode;
            invalidate();
        }
    }

    public OnConfigListener getOnConfigListener() {
        return mOnConfigListener;
    }

    public void setOnConfigListener(OnConfigListener onConfigListener) {
        mOnConfigListener = onConfigListener;
    }

    public interface OnResultListener {
        void onSuccess(List<PatternPoint> selectedPoints, String secretKey);

        void onFailed(List<PatternPoint> selectedPoints);
    }

    public interface OnConfigListener {
        void onSuccess(List<PatternPoint> selectedPoints, String secretKey);

        void onFailed(List<PatternPoint> selectedPoints);
    }

    public interface SecretKeyComparator {
        boolean onCompare(String secretKey);
    }

    public static class PatternPoint {
        int x;
        int y;
        int index;

        PatternPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return String.valueOf(index);
        }
    }
}
