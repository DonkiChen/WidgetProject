package test.widgetproject.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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

    }

    @OnClick(R.id.tv_concern)
    public void onTextViewConcernClicked() {
        mockNetwork(mTvConcern, true);
    }

    @OnClick(R.id.tv_concerned)
    public void onTextViewConcernedClicked() {
        mockNetwork(mTvConcerned, false);
    }

    private void mockNetwork(final ProgressTextView view, final boolean isConcern) {
        Disposable disposable = Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        view.setShowProgress(true);
                        //因为上层的setClickable(false)以后,点击事件会传递到下层,所以两个设置都不可点击
                        //本来是想用setVisibility来控制下层的
                        mTvConcern.setClickable(false);
                        mTvConcerned.setClickable(false);
                    }
                }).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        doCircularReveal(isConcern);
                    }
                });
        addDisposable(disposable);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doCircularReveal(boolean isConcern) {
        final ProgressTextView animatorView = isConcern ? mTvConcerned : mTvConcern;
        final ProgressTextView anotherView = isConcern ? mTvConcern : mTvConcerned;
        int centerX = animatorView.getWidth() / 2;
        int centerY = animatorView.getHeight() / 2;
        Animator animator = ViewAnimationUtils.createCircularReveal(animatorView, centerX, centerY, 0, (float) Math.hypot(centerX, centerY));
        animator.setDuration(1000)
                .addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        anotherView.setShowProgress(false);
                        animatorView.setClickable(true);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        mFlContainer.bringChildToFront(animatorView);
                    }
                });
        animator.start();
    }
}
