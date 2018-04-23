package test.widgetproject.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mvp.base.util.DisplayUtils;

import test.widgetproject.main.R;

/**
 * Created on 2018/3/30.
 *
 * @author ChenFanlin
 */

public class OrderStatusView extends View {
    private static final String TAG = OrderStatusView.class.getSimpleName();

    private Paint mPaint;
    private String[] mStatusArray;
    private int mRadius = DisplayUtils.dp2px(16);
    private int mRingRadius = DisplayUtils.dp2px(8);
    private int mLineWidth = DisplayUtils.dp2px(1);
    private int mStatusSize = DisplayUtils.sp2px(14);
    private int mStatusColor = Color.BLACK;
    private int mCircleColor = Color.RED;
    private int mLineColor = Color.RED;
    private int mCircleTextSpacing = DisplayUtils.dp2px(16);

    private int mTextTop = 0;
    private int mItemSpacing = 0;

    public OrderStatusView(Context context) {
        this(context, null);
    }

    public OrderStatusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrderStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(mRingRadius);
        mPaint.setTextSize(mStatusSize);
        mTextTop = -mPaint.getFontMetricsInt().top;

        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.OrderStatusView);
            mRadius = ta.getDimensionPixelSize(R.styleable.OrderStatusView_osvRadius, mRadius);
            mRingRadius = ta.getDimensionPixelSize(R.styleable.OrderStatusView_osvRingRadius, mRingRadius);
            mLineWidth = ta.getDimensionPixelSize(R.styleable.OrderStatusView_osvLineWidth, mLineWidth);
            mStatusSize = ta.getDimensionPixelSize(R.styleable.OrderStatusView_android_textSize, mStatusSize);
            mStatusColor = ta.getColor(R.styleable.OrderStatusView_android_textColor, mStatusColor);
            mCircleColor = ta.getColor(R.styleable.OrderStatusView_osvCircleColor, mCircleColor);
            mLineColor = ta.getColor(R.styleable.OrderStatusView_osvLineColor, mLineColor);
            mCircleTextSpacing = ta.getDimensionPixelSize(R.styleable.OrderStatusView_osvCircleTitleSpacing, mCircleTextSpacing);
            CharSequence[] charSequences = ta.getTextArray(R.styleable.OrderStatusView_android_entries);
            mStatusArray = new String[charSequences.length];
            for (int i = 0; i < charSequences.length; i++) {
                mStatusArray[i] = charSequences[i].toString();
            }
            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = (mRadius * 2 + mRingRadius) * mStatusArray.length;
            Log.d(TAG, "onMeasure: " + widthSize);
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            int fontHeight = mPaint.getFontMetricsInt().bottom - mPaint.getFontMetricsInt().top;
            heightSize = mRadius * 2 + mRingRadius + fontHeight + mCircleTextSpacing;
            Log.d(TAG, "onMeasure: " + fontHeight + "`" + heightSize);
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!validTitles()) {
            return;
        }

        //最左和最右两圆圆点之间的距离
        int totalSpacing = getMeasuredWidth() - 2 * mRadius - mRingRadius - getPaddingLeft() - getPaddingRight();
        //每个圆的间隙
        mItemSpacing = totalSpacing / (mStatusArray.length - 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!validTitles()) {
            return;
        }
        for (int i = 0; i < mStatusArray.length; i++) {
            int circleX = mRadius + mRingRadius / 2 + i * mItemSpacing;
            // TODO: 2018/3/30 isComplete
            drawCircle(canvas, circleX, getMeasuredHeight() - mRingRadius / 2 - mRadius, i <= 1);
            if (i != mStatusArray.length - 1) {
                drawConnectionLine(canvas, circleX, getMeasuredHeight() - mRingRadius / 2 - mRadius);
            }

            int titleX;
            if (i == 0) {
                titleX = 0;
            } else if (i == mStatusArray.length - 1) {
                titleX = getMeasuredWidth() - (int) mPaint.measureText(mStatusArray[i]);
            } else {
                titleX = (int) (circleX - mPaint.measureText(mStatusArray[i]) / 2);
            }
            drawTitle(canvas, titleX, mTextTop, mStatusArray[i]);
        }
    }

    private void drawCircle(Canvas canvas, int x, int y, boolean isCompleted) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mCircleColor);
        if (isCompleted) {
            mPaint.setStrokeWidth(mRingRadius);
            canvas.drawCircle(x, y, mRadius, mPaint);
        } else {
            mPaint.setStrokeWidth(mLineWidth);
            //为了保证完成与不完成的圆大小一致
            canvas.drawCircle(x, y, mRadius + (mRingRadius - mLineWidth) / 2, mPaint);
        }
    }

    private void drawTitle(Canvas canvas, int x, int y, String title) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mStatusColor);
        canvas.drawText(title, x, y, mPaint);
    }

    private void drawConnectionLine(Canvas canvas, int circleX, int circleY) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(mLineWidth);
        canvas.drawLine(circleX + mRingRadius / 2 + mRadius, circleY,
                circleX + mItemSpacing - mRingRadius / 2 - mRadius,
                circleY, mPaint);
    }

    private boolean validTitles() {
        return mStatusArray != null && mStatusArray.length > 0;
    }

    public void setStatusArray(String... statusArray) {
        mStatusArray = statusArray;
        requestLayout();
    }

    public interface OnComparator {
        boolean isCompleted(int index, String status);
    }
}
