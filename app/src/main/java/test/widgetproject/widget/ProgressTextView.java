package test.widgetproject.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import com.mvp.base.util.DisplayUtils;

/**
 * Created on 2018/5/4.
 *
 * @author ChenFanlin
 */

public class ProgressTextView extends android.support.v7.widget.AppCompatTextView {
    private boolean mIsShowProgress = false;
    private Paint mPaint;
    private RectF mRectF;
    private float mCurrentSweep = 0, mStartAngle = 0, mFloat = 2;
    private ValueAnimator mAnimator;

    public ProgressTextView(Context context) {
        this(context, null);
    }

    public ProgressTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public ProgressTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DisplayUtils.dp2px(4));
        mPaint.setColor(Color.WHITE);
        mRectF = new RectF();
    }

    private void doAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        mAnimator = ValueAnimator.ofFloat(0, 360);
        mAnimator.setDuration(30000);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartAngle = (float) animation.getAnimatedValue();
                mCurrentSweep = 360 - (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                doAnimator();
            }
        });
        mAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int size = Math.min(w, h);
        int left = (w - size) / 2;
        int top = (h - size) / 2;
        mRectF.set(left, top, left + size, top + size);
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
            if (mIsShowProgress) {
                doAnimator();
            } else if (mAnimator.isRunning()) {
                mAnimator.cancel();
            }
            postInvalidate();
        }
    }
}