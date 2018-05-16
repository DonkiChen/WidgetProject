package test.widgetproject.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import test.widgetproject.main.R;

/**
 * Created on 2018/5/4.
 *
 * @author ChenFanlin
 */

public class ProgressTextView extends android.support.v7.widget.AppCompatTextView {

    private boolean mIsShowProgress = false;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mRectF = new RectF();
    private float mCurrentSweep = 0, mStartAngle = 0;
    private AnimatorSet mAnimatorSet;
    private int mProgressColor;
    private int mProgressWidth;
    private boolean mIsFitTextHeight;
    private boolean mIsIgnorePadding = true;
    private Paint.FontMetricsInt mFontMetricsInt = new Paint.FontMetricsInt();

    public ProgressTextView(Context context) {
        this(context, null);
    }

    public ProgressTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public ProgressTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressTextView);
            mProgressColor = typedArray.getColor(R.styleable.ProgressTextView_ptvColor, getCurrentTextColor());
            mProgressWidth = typedArray.getDimensionPixelSize(R.styleable.ProgressTextView_ptvWidth, 6);
            mIsFitTextHeight = typedArray.getBoolean(R.styleable.ProgressTextView_ptvFitTextHeight, true);
            mIsIgnorePadding = typedArray.getBoolean(R.styleable.ProgressTextView_ptvIgnorePadding, true);
            typedArray.recycle();
        }
        mPaint.setStyle(Paint.Style.STROKE);
        setProgressColor(mProgressColor);
        setProgressWidth(mProgressWidth);
    }

    /**
     * 在显示进度条时不可点击
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return isShowProgress() || super.dispatchTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int realW;
        int realH;
        if (mIsFitTextHeight) {
            getPaint().getFontMetricsInt(mFontMetricsInt);
            realH = mFontMetricsInt.bottom - mFontMetricsInt.top;
            realW = realH;
        } else if (mIsIgnorePadding) {
            realW = w;
            realH = h;
        } else {
            realW = w - getPaddingLeft() - getPaddingRight();
            realH = h - getPaddingTop() - getPaddingBottom();
        }

        int size = Math.min(realW, realH) - mProgressWidth / 2;
        int left = (w - size) / 2;
        int top = (h - size) / 2;
        if (mIsIgnorePadding) {
            mRectF.set(left, top,
                    left + size, top + size);
        } else {
            mRectF.set(left + getPaddingLeft(), top + getPaddingTop(),
                    left + size - getPaddingRight(), top + size - getPaddingBottom());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
    }

    // FIXME: 2018/5/7 使角度不固定
    private void doAnimator() {
        mAnimatorSet = new AnimatorSet();
        ValueAnimator startAngleAnimator = ValueAnimator.ofFloat(0, 360);
        startAngleAnimator.setDuration(1000);
        startAngleAnimator.setInterpolator(new LinearInterpolator());
        startAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartAngle = (float) animation.getAnimatedValue();
            }
        });

        ValueAnimator sweepAnimator = ValueAnimator.ofFloat(30, 180, 30);
        sweepAnimator.setDuration(1000);
        sweepAnimator.setInterpolator(new LinearInterpolator());
        sweepAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentSweep = (float) animation.getAnimatedValue();
                invalidate(((int) mRectF.left), ((int) mRectF.top),
                        ((int) mRectF.right), ((int) mRectF.bottom));
            }
        });

        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            boolean isCanceled = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                isCanceled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isCanceled) {
                    doAnimator();
                } else {
                    isCanceled = false;
                }
            }
        });
        mAnimatorSet.play(startAngleAnimator).with(sweepAnimator);
        mAnimatorSet.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mIsShowProgress) {
            canvas.drawArc(mRectF, mStartAngle, mCurrentSweep, false, mPaint);
        } else {
            super.onDraw(canvas);
        }
    }

    public boolean isShowProgress() {
        return mIsShowProgress;
    }

    public void setShowProgress(boolean showProgress) {
        if (mIsShowProgress != showProgress) {
            mIsShowProgress = showProgress;
            setClickable(!mIsShowProgress);
            if (mIsShowProgress) {
                doAnimator();
            } else if (mAnimatorSet.isRunning()) {
                mAnimatorSet.cancel();
            }
            postInvalidate();
        }
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        mPaint.setColor(progressColor);
        if (mProgressColor != progressColor) {
            mProgressColor = progressColor;
            postInvalidate();
        }
    }

    public int getProgressWidth() {
        return mProgressWidth;
    }

    public void setProgressWidth(int progressWidth) {
        mPaint.setStrokeWidth(progressWidth);
        if (mProgressWidth != progressWidth) {
            mProgressWidth = progressWidth;
            requestLayout();
        }
    }

    public boolean isFitTextHeight() {
        return mIsFitTextHeight;
    }

    public void setFitTextHeight(boolean fitTextHeight) {
        if (mIsFitTextHeight != fitTextHeight) {
            mIsFitTextHeight = fitTextHeight;
            requestLayout();
        }
    }
}