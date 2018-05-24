package test.widgetproject.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mvp.base.util.DisplayUtils;
import com.mvp.base.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2018/5/23.
 * TODO 使用SurfaceView
 *
 * @author ChenFanlin
 */
public class MiPatternLock extends View {
    private static final String TAG = MiPatternLock.class.getSimpleName();

    private Paint mPaint;
    private int mMinSelectedPointCount = 4;
    private int mPointColor = Color.DKGRAY;
    private int mErrorPointColor = Color.parseColor("#E5523B");
    private int mLineColor = Color.LTGRAY;
    private int mErrorLineColor = Color.RED;
    private int mPatternPadding = DisplayUtils.dp2px(30);
    private int mPointTouchArea = DisplayUtils.dp2px(30);
    private int mPointSize = DisplayUtils.dp2px(8);
    private PatternPoint[] mPatternPoints = new PatternPoint[9];
    private PatternPoint mLastTouchedPoint;
    private boolean mIsError = false;
    private List<PatternPoint> mSelectedPoint = new ArrayList<>();
    private float mTouchingX;
    private float mTouchingY;
    private boolean mIsTouching = false;
    private String mSecretKey;

    public MiPatternLock(Context context) {
        this(context, null);
    }

    public MiPatternLock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiPatternLock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int length = mPatternPoints.length;
        for (int i = 0; i < length; i++) {
            mPatternPoints[i] = new PatternPoint(0, 0);
        }
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mPointSize);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                reset();
                mLastTouchedPoint = getTouchedPoint(event.getX(), event.getY());
                mIsTouching = true;
                break;
            case MotionEvent.ACTION_MOVE:
                //没有触摸到点
                PatternPoint newPoint = getTouchedPoint(event.getX(), event.getY());
                if (newPoint != null && !isPointSelected(newPoint)) {
                    addPoint(newPoint);
                    mLastTouchedPoint = newPoint;
                }
                mTouchingX = event.getX();
                mTouchingY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastTouchedPoint = null;
                mIsTouching = false;
                if (mSelectedPoint.size() >= mMinSelectedPointCount) {
                    String newSecretKey = mSelectedPoint.toString();
                    if (TextUtils.isEmpty(mSecretKey)) {
                        mSecretKey = newSecretKey;
                    } else if (TextUtils.equals(newSecretKey, mSecretKey)) {
                        ToastUtils.showShortSafe("密码正确");
                        mIsError = false;
                    } else {
                        ToastUtils.showShortSafe("密码错误");
                        mIsError = true;
                    }
                } else {
                    mIsError = true;
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int size = Math.min(w, h);
        int length = mPatternPoints.length;
        int offset = mPatternPadding + mPointSize / 2;
        int xOffset;
        int yOffset;
        for (int i = 0; i < length; i++) {
            PatternPoint patternPoint = mPatternPoints[i];
            xOffset = (1 - i % 3) * offset;
            yOffset = (1 - i / 3) * offset;
            patternPoint.index = i;
            patternPoint.x = (int) (i % 3 / 2.0 * size) + xOffset;
            patternPoint.y = (int) (i / 3 / 2.0 * size) + yOffset;
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int minSize = mPatternPadding * 2 + mPointSize * 3 + mPointTouchArea * 3;
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = minSize + getPaddingLeft() + getPaddingRight();
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = minSize + getPaddingTop() + getPaddingBottom();
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
            int size = mSelectedPoint.size();
            for (int i = 0; i < size - 1; i++) {
                mSelectedPoint.get(i).next = mSelectedPoint.get(i + 1);
            }
            for (PatternPoint patternPoint : mSelectedPoint) {
                if (patternPoint.next != null) {
                    canvas.drawLine(patternPoint.x, patternPoint.y, patternPoint.next.x, patternPoint.next.y, mPaint);
                }
            }
        }
    }

    /**
     * 将点(以及额外的点)加入被选择的点
     *
     * @param patternPoint 当前被选中的点
     */
    private void addPoint(PatternPoint patternPoint) {
        PatternPoint additionalPoint = getAdditionalPoint(patternPoint);
        if (additionalPoint != null) {
            mSelectedPoint.add(additionalPoint);
        }
        mSelectedPoint.add(patternPoint);
        Log.d(TAG, mSelectedPoint.toString());
    }

    /**
     * 添加额外需要的点,比如在跨行跨列时,添加中间的点
     *
     * @param patternPoint 被加入的点
     * @return 额外的点
     */
    @Nullable
    private PatternPoint getAdditionalPoint(PatternPoint patternPoint) {
        if (mLastTouchedPoint != null && mLastTouchedPoint != patternPoint) {
            int lastIndex = mLastTouchedPoint.index;
            int newIndex = patternPoint.index;
            PatternPoint centerPoint = mPatternPoints[(newIndex + lastIndex) / 2];
            if (isPointSelected(centerPoint)) {
                //如果中间的被添加了肯定不用判断
                return null;
            }
            //是否在同一行
            boolean sameRow = newIndex / 3 == lastIndex / 3;
            boolean skipColumn = sameRow && Math.abs(newIndex - lastIndex) == 2;
            if (sameRow && skipColumn) {
                return centerPoint;
            }
            //是否在同一列
            boolean sameColumn = newIndex % 3 == lastIndex % 3;
            boolean skipRow = sameColumn && Math.abs(newIndex - lastIndex) == 6;
            if (sameColumn && skipRow) {
                return centerPoint;
            }
            //是否在对角线
            boolean sameDiagonal = !sameRow && !sameColumn && (newIndex + lastIndex) == 8;
            if (sameDiagonal) {
                return centerPoint;
            }
        }
        return null;
    }

    private void reset() {
        mSelectedPoint.clear();
        for (PatternPoint patternPoint : mPatternPoints) {
            patternPoint.next = null;
        }
        mIsError = false;
    }

    /**
     * 绘制最近一个点到触摸的线
     *
     * @param canvas 画布
     */
    private void drawLineFollowFinger(Canvas canvas) {
        if (mLastTouchedPoint == null) {
            return;
        }
        mPaint.setColor(mLineColor);
        canvas.drawLine(mLastTouchedPoint.x, mLastTouchedPoint.y, mTouchingX, mTouchingY, mPaint);
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
            if (distance <= mPointSize + mPointTouchArea) {
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

    private static class PatternPoint {
        int x;
        int y;
        int index;
        PatternPoint next;

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
