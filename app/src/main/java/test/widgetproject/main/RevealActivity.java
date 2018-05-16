package test.widgetproject.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;

import com.mvp.base.util.ToastUtils;

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

public class RevealActivity extends BaseActivity {
    @BindView(R.id.fl_container)
    FrameLayout mFlContainer;
    @BindView(R.id.tv_concern)
    ProgressTextView mTvConcern;
    @BindView(R.id.tv_concerned)
    ProgressTextView mTvConcerned;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_reveal;
    }

    @Override
    public void initView() {
        //初始Gone的话,第一次动画不会执行
//        mTvConcerned.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_concern)
    public void onTextViewConcernClicked() {
        mockNetwork(mTvConcern, true);
    }

    @OnClick(R.id.tv_concerned)
    public void onTextViewConcernedClicked() {
        mockNetwork(mTvConcerned, false);
    }

    @OnClick(R.id.fl_container)
    public void onContainerClicked() {
        ToastUtils.showShortSafe("点击事件传递到了FrameLayout");
    }

    /**
     * 模拟网络请求
     *
     * @param view      被点击的View
     * @param isConcern 是否是关注操作
     */
    private void mockNetwork(final ProgressTextView view, final boolean isConcern) {
        Disposable disposable = Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        view.setShowProgress(true);
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
                        mFlContainer.bringChildToFront(animatorView);
                    }
                });
        animator.start();
    }
}
