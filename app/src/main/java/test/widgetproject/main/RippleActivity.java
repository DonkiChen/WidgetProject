package test.widgetproject.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import test.widgetproject.widget.ProgressTextView;

/**
 * Created on 2018/5/4.
 *
 * @author ChenFanlin
 */

public class RippleActivity extends BaseActivity {
    @BindView(R.id.fl_container)
    FrameLayout mFlContainer;
    @BindView(R.id.tv_concern)
    ProgressTextView mTvConcern;
    @BindView(R.id.tv_concerned)
    ProgressTextView mTvConcerned;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_ripple;
    }

    @Override
    public void initView() {
        mTvConcern.setShowProgress(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.tv_concern, R.id.tv_concerned})
    public void onClick(final View view) {
        final View animatorView = view.getId() == R.id.tv_concern ? mTvConcerned : mTvConcern;
        int centerX = animatorView.getWidth() / 2;
        int centerY = animatorView.getHeight() / 2;
        Animator animator = ViewAnimationUtils.createCircularReveal(animatorView, centerX, centerY, 0, (float) Math.hypot(centerX, centerY));
        animator.setDuration(3000)
                .addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mFlContainer.bringChildToFront(animatorView);
                    }
                });
        animator.start();
    }
}
