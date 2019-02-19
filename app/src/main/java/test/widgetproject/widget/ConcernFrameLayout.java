package test.widgetproject.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;

import test.widgetproject.main.R;

public class ConcernFrameLayout extends FrameLayout implements View.OnClickListener {
    private ProgressTextView mTvConcern;
    private ProgressTextView mTvConcerned;


    public ConcernFrameLayout(Context context) {
        this(context, null);
    }

    public ConcernFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConcernFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mTvConcerned = new ProgressTextView(getContext());
        mTvConcerned.setBackgroundResource(R.drawable.bg_gray_stroke_1dp_corner_4dp);
        mTvConcerned.setText("已关注");
        mTvConcerned.setTextColor(Color.parseColor("#FFF"));
        mTvConcerned.setFitTextHeight(true);
        mTvConcerned.setGravity(Gravity.CENTER);
        mTvConcerned.setOnClickListener(this);
        addView(mTvConcerned, new FrameLayout.LayoutParams(-1, -1, Gravity.CENTER));

        mTvConcern = new ProgressTextView(getContext());
        mTvConcern.setBackgroundResource(R.drawable.bg_blue_stroke_1dp_corner_4dp);
        mTvConcern.setText("+ 关注");
        mTvConcern.setTextColor(Color.parseColor("#FFF"));
        mTvConcern.setFitTextHeight(true);
        mTvConcern.setGravity(Gravity.CENTER);
        mTvConcern.setOnClickListener(this);
        addView(mTvConcern, new FrameLayout.LayoutParams(-1, -1, Gravity.CENTER));
    }

    @Override
    public void onClick(View v) {
        if (v == mTvConcern) {
            mTvConcern.setShowProgress(true);
            doCircularReveal(true);
        } else if (v == mTvConcerned) {
            mTvConcerned.setShowProgress(true);
            doCircularReveal(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doCircularReveal(boolean isConcern) {
        //执行动画的是下一层的View
        final ProgressTextView animatorView = isConcern ? mTvConcerned : mTvConcern;
        final ProgressTextView anotherView = isConcern ? mTvConcern : mTvConcerned;
        int centerX = animatorView.getWidth() / 2;
        int centerY = animatorView.getHeight() / 2;

        Animator animator = ViewAnimationUtils.createCircularReveal(animatorView, centerX,
                centerY, 0, (float) Math.hypot(centerX, centerY));
        animator.setDuration(1000)
                .addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        anotherView.setShowProgress(false);
                        anotherView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        //将下一层View显示并放到顶层
                        animatorView.setVisibility(View.VISIBLE);
                        bringChildToFront(animatorView);
                    }
                });
        animator.start();
    }
}
