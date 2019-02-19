package test.widgetproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.mvp.base.util.DisplayUtils;
import com.mvp.base.util.ToastUtils;

import java.text.DecimalFormat;
import java.util.Random;

public class RulerView extends View {
    private static final String TAG = "RulerView";
    private static final int COLOR_PRIMARY = Color.parseColor("#4A4A4A");

    private Paint mPaint;
    private float mScaleLineSpacing = DisplayUtils.dp2px(5);
    private float mScaleLineWidth = DisplayUtils.dp2px(1);
    private float mShortLineHeight = DisplayUtils.dp2px(10);
    private float mMediumLineHeight = DisplayUtils.dp2px(15);
    private float mLongLineHeight = DisplayUtils.dp2px(20);
    private float mLargeLineValueSpacing = DisplayUtils.dp2px(8);
    private float mLargeLineValueTextSize = DisplayUtils.sp2px(18);

    private double mValuePerLine = 2;
    private double mMinValue = 50;
    private double mMaxValue = 250;
    private boolean mAttachToLine = true;
    private float mDragOffset = 0;
    private Indicator mIndicator;
    private DecimalFormat mDecimalFormat = new DecimalFormat("#.##");
    private Paint.FontMetricsInt mFontMetricsInt = new Paint.FontMetricsInt();
    private float mMaxOffset = 0;
    private float mLastX = 0;
    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private OnValueChangeListener mOnValueChangeListener;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mVelocityTracker = VelocityTracker.obtain();
        mScroller = new OverScroller(getContext());
        mMinFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicator = new DefaultIndicator();
        calMaxOffset();
        mOnValueChangeListener = new OnValueChangeListener() {
            @Override
            public void onChange(double value) {
                ToastUtils.showShortSafe("onChange: " + mDecimalFormat.format(value));
            }
        };
    }

    private void calMaxOffset() {
        mMaxOffset = (float) ((mMaxValue - mMinValue) * (mScaleLineWidth + mScaleLineSpacing) / mValuePerLine);
    }

    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            mPaint.setTextSize(mLargeLineValueTextSize);
            mPaint.getFontMetricsInt(mFontMetricsInt);
            int longLineValueHeight = mFontMetricsInt.bottom - mFontMetricsInt.top;
            heightSize = (int) (mLongLineHeight + mLargeLineValueSpacing
                    + longLineValueHeight + mLargeLineValueSpacing);
        }
        setMeasuredDimension(widthMeasureSpec, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mIndicator != null) {
            mIndicator.onSizeChange(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScaleLine(canvas);
        if (mIndicator != null) {
            mIndicator.onDraw(canvas);
        }
    }

    private void drawScaleLine(Canvas canvas) {
        mPaint.setStrokeWidth(mScaleLineWidth);
        mPaint.setTextSize(mLargeLineValueTextSize);
        mPaint.getFontMetricsInt(mFontMetricsInt);
        mPaint.setStyle(Paint.Style.FILL);

        float itemWidth = mScaleLineWidth + mScaleLineSpacing;
        int original = canvas.save();
        //偏移量,初始时0刻度线画在屏幕中央
        float translateX = getMeasuredWidth() / 2F - mDragOffset - mScaleLineWidth / 2;
        float validTranslateX = Math.max(0, translateX);
        //平移画布 设定初始位置
        canvas.translate(validTranslateX, 0);

        //多画10个 避免边缘数值显示问题
        int extraLineCount = 10;
        int lineCount = (int) ((getMeasuredWidth() - validTranslateX) / itemWidth + extraLineCount);
        //线偏移数值
        int lineOffset = 0;
        float halfExtraLineWidth = extraLineCount / 2F * itemWidth;
        //第一条线的x
        float lineStartX;
        if (translateX > 0) {
            //View左侧还有空白
            lineStartX = 0;
        } else if (translateX > -halfExtraLineWidth) {
            //还没有需要偏移的数值, 因为左侧每个都画出来了
            lineStartX = translateX;
        } else {
            //在(-halfExtraLineWidth, 0)之间多画额外线数量/2
            //线偏移数值 = 偏移量的绝对值/每条线及间隙-额外线数量/2
            lineOffset = (int) ((Math.abs(translateX) / (itemWidth) - extraLineCount / 2));
            lineStartX = -halfExtraLineWidth - Math.abs(translateX) % itemWidth;
        }

        float textStartY = mLongLineHeight + mLargeLineValueSpacing - mFontMetricsInt.top;
        mPaint.setColor(COLOR_PRIMARY);
        double value;
        float lineHeight;
        //当前是第几条线
        int currentLine;
        for (int i = 0; i < lineCount; i++) {
            currentLine = lineOffset + i;
            value = mMinValue + (currentLine) * mValuePerLine;
            if (value > mMaxValue) {
                break;
            }
            if (currentLine % 10 == 0) {
                //每10条
                String valueString = mDecimalFormat.format(value);
                float valueWidth = mPaint.measureText(valueString);
                canvas.drawText(valueString, lineStartX - valueWidth / 2, textStartY, mPaint);
                lineHeight = mLongLineHeight;
            } else if (currentLine % 5 == 0) {
                //每5条
                lineHeight = mMediumLineHeight;
            } else {
                lineHeight = mShortLineHeight;
            }
            canvas.drawLine(lineStartX, 0, lineStartX, lineHeight, mPaint);
            canvas.translate(itemWidth, 0);
        }
        canvas.restoreToCount(original);
        if (mOnValueChangeListener != null) {
            mOnValueChangeListener.onChange(getValue());
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) {
                moveToNearestLine();
            } else {
                mDragOffset = mScroller.getCurrX();
                makeDragOffsetValid();
                invalidate();
            }
        }
    }

    /**
     * 移动到最近的一条线
     */
    private void moveToNearestLine() {
        if (!mAttachToLine) {
            return;
        }
        float itemWidth = mScaleLineSpacing + mScaleLineWidth;
        float leftDes = -mDragOffset % (itemWidth);
        float rightDes = itemWidth - mDragOffset % (itemWidth);
        float des = Math.abs(leftDes) < rightDes ? leftDes : rightDes;
        if (des != 0) {
            mDragOffset += des;
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mDragOffset = (int) (mDragOffset - (event.getX() - mLastX));
                makeDragOffsetValid();
                mLastX = event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                float xVelocity = mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocity) > mMinFlingVelocity) {
                    mScroller.fling(((int) mDragOffset), 0, ((int) -xVelocity), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
                    invalidate();
                } else {
                    moveToNearestLine();
                }
                break;
        }
        return true;
    }

    /**
     * 验证数值合法性
     */
    private void makeDragOffsetValid() {
        if (mDragOffset < 0) {
            mDragOffset = 0;
        } else if (mDragOffset > mMaxOffset) {
            mDragOffset = mMaxOffset;
        }
    }

    public void randomOffset() {
        Random random = new Random();
        mDragOffset = random.nextInt((int) (mMaxValue * (mScaleLineWidth + mScaleLineSpacing)));
        makeDragOffsetValid();
        invalidate();
    }

    public void randomValue() {
        Random random = new Random();
        double value = random.nextInt((int) (mMaxValue - mMinValue)) + mMinValue;
        setValue(value);
    }

    private void refreshValue(boolean reCalculate) {
        if (reCalculate) {
            calMaxOffset();
        }
        makeDragOffsetValid();
        moveToNearestLine();
        invalidate();
    }

    /**
     * 跳到对应数值的点
     *
     * @param value value
     */
    public void setValue(double value) {
        value = value - mMinValue;
        mDragOffset = (float) ((mScaleLineSpacing + mScaleLineWidth) * value / mValuePerLine);
        refreshValue(false);
    }

    public double getValue() {
        return mDragOffset / (mScaleLineWidth + mScaleLineSpacing) * mValuePerLine + mMinValue;
    }

    public void setAttachToLine(boolean attachToLine) {
        mAttachToLine = attachToLine;
        moveToNearestLine();
    }

    public double getMinValue() {
        return mMinValue;
    }

    public void setMinValue(double minValue) {
        mMinValue = minValue;
        refreshValue(true);
    }

    public double getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(double maxValue) {
        mMaxValue = maxValue;
        refreshValue(true);
    }

    public double getValuePerLine() {
        return mValuePerLine;
    }

    public void setValuePerLine(double valuePerLine) {
        mValuePerLine = valuePerLine;
        refreshValue(true);
    }

    public Indicator getIndicator() {
        return mIndicator;
    }

    public void setIndicator(Indicator indicator) {
        mIndicator = indicator;
        invalidate();
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        mOnValueChangeListener = onValueChangeListener;
    }

    public interface OnValueChangeListener {
        void onChange(double value);
    }

    public interface Indicator {
        void onDraw(Canvas canvas);

        void onSizeChange(int viewWidth, int viewHeight);
    }

    public static class DefaultIndicator implements Indicator {
        private Paint mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Path mPath = new Path();
        private int mPathWidth = DisplayUtils.dp2px(9);
        private int mPathHeight = DisplayUtils.dp2px(6);

        public DefaultIndicator() {
            mIndicatorPaint.setStyle(Paint.Style.FILL);
            mIndicatorPaint.setColor(COLOR_PRIMARY);
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawPath(mPath, mIndicatorPaint);
        }

        @Override
        public void onSizeChange(int viewWidth, int viewHeight) {
            mPath.reset();
            mPath.moveTo((viewWidth - mPathWidth) / 2F, 0);
            mPath.rLineTo(mPathWidth, 0);
            mPath.rLineTo(-mPathWidth / 2F, mPathHeight);
            mPath.close();
        }
    }
}
