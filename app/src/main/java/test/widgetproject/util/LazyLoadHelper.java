package test.widgetproject.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class LazyLoadHelper {
    private boolean mInitialized = false;
    private Fragment mFragment;
    private ILazyLoadable mILazyLoadable;

    public LazyLoadHelper(Fragment fragment, ILazyLoadable ILazyLoadable) {
        mFragment = fragment;
        mILazyLoadable = ILazyLoadable;
    }

    public void onResume() {
        if (isFragmentVisible(mFragment) && isAllParentVisible(mFragment)) {
            onCombineResume();
        }
    }

    public void onPause() {
        if (mInitialized && isFragmentVisible(mFragment) && isAllParentVisible(mFragment)) {
            onCombinePause();
        }
    }

    public void onHiddenChanged(boolean hidden) {
        if (mFragment.isAdded()) {
            FragmentManager childFragmentManager = mFragment.getChildFragmentManager();
            for (Fragment childFragment : childFragmentManager.getFragments()) {
                childFragment.onHiddenChanged(hidden);
            }
            if (!hidden) {
                onCombineResume();
            } else {
                onCombinePause();
            }
        }
    }

    /**
     * 必须要在fragment的super.setUserVisibleHint()前
     */
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (mFragment.isAdded()) {
            if (isVisibleToUser == mFragment.getUserVisibleHint()) {
                return;
            }
            if (isVisibleToUser) {
                onCombineResume();
            } else {
                onCombinePause();
            }
        }
    }

    private static boolean isFragmentVisible(Fragment fragment) {
        return fragment.isVisible() && fragment.getUserVisibleHint() && isAllParentVisible(fragment);
    }

    private static boolean isAllParentVisible(Fragment fragment) {
        Fragment parent = fragment.getParentFragment();
        while (parent != null) {
            if (!parent.isVisible() || !parent.getUserVisibleHint()) {
                return false;
            }
            parent = parent.getParentFragment();
        }
        return true;
    }

    private void onCombineResume() {
        if (mILazyLoadable.isLazyLoad()) {
            mILazyLoadable.onLazyEnter(mInitialized);
            mInitialized = true;
        }
        mILazyLoadable.onCombineResume();
    }

    private void onCombinePause() {
        if (mILazyLoadable.isLazyLoad()) {
            mILazyLoadable.onLazyExit();
        }
        mILazyLoadable.onCombinePause();
    }

    public interface ILazyLoadable {
        void onCombineResume();

        void onCombinePause();

        void onLazyEnter(boolean isReenter);

        void onLazyExit();

        boolean isLazyLoad();
    }
}
