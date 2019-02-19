package test.widgetproject.util;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.List;
import java.util.Map;

import test.widgetproject.main.SharePreviewActivity;

public abstract class TransitionHelper {
    private final FragmentActivity mActivity;

    public TransitionHelper(FragmentActivity activity) {
        mActivity = activity;
    }

    public void onReenter(Intent data) {
        if (data == null) {
            return;
        }

        if (mActivity == null) {
            return;
        }

        if (getRecyclerView() == null) {
            return;
        }

        mActivity.getWindow().getSharedElementExitTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mActivity.getWindow().getSharedElementExitTransition().removeListener(this);
                mActivity.setExitSharedElementCallback((MediaElementCallback) null);
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                mActivity.getWindow().getSharedElementExitTransition().removeListener(this);
                mActivity.setExitSharedElementCallback((MediaElementCallback) null);
            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

        final int position = data.getIntExtra(SharePreviewActivity.KEY_POSITION, 0);
        getRecyclerView().scrollToPosition(position);
        final MediaElementCallback callback = new MediaElementCallback();
        mActivity.setExitSharedElementCallback(callback);
        mActivity.supportPostponeEnterTransition();
        getRecyclerView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getRecyclerView().getViewTreeObserver().removeOnPreDrawListener(this);
                callback.setupElements(getSharedElements(position));
                mActivity.supportStartPostponedEnterTransition();
                return true;
            }
        });
    }

    public abstract RecyclerView getRecyclerView();

    public abstract Map<String, View> getSharedElements(int position);

    public interface ReenterListener {
        void onReenter(int resultCode, Intent data);
    }

    private static class MediaElementCallback extends SharedElementCallback {
        private Map<String, View> mSharedElements;

        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mSharedElements != null) {
                names.clear();
                sharedElements.clear();
                for (Map.Entry<String, View> entry : mSharedElements.entrySet()) {
                    names.add(entry.getKey());
                }
                sharedElements.putAll(mSharedElements);
            }
        }

        public void setupElements(Map<String, View> elements) {
            mSharedElements = elements;
        }
    }
}
