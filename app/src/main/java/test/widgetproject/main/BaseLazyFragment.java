package test.widgetproject.main;

import android.support.v4.app.Fragment;
import android.util.Log;

import test.widgetproject.util.LazyLoadHelper;

public class BaseLazyFragment extends Fragment implements LazyLoadHelper.ILazyLoadable {
    private LazyLoadHelper mLazyLoadHelper = new LazyLoadHelper(this, this);

    private String TAG = this.toString();

    @Override
    public void onResume() {
        super.onResume();
        mLazyLoadHelper.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mLazyLoadHelper.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mLazyLoadHelper.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        mLazyLoadHelper.setUserVisibleHint(isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onLazyEnter(boolean isReenter) {
        Log.d(TAG, "onLazyEnter: isReenter = " + isReenter);
    }

    @Override
    public void onLazyExit() {
        Log.d(TAG, "onLazyExit: ");
    }

    @Override
    public void onCombineResume() {
        Log.d("onCombine", "onCombineResume: ");
    }

    @Override
    public void onCombinePause() {
        Log.d("onCombine", "onCombinePause: ");
    }

    public boolean isLazyLoad() {
        return true;
    }
}
