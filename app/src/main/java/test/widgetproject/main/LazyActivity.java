package test.widgetproject.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.OnClick;

public class LazyActivity extends BaseActivity {

    @BindView(R.id.fl_container)
    FrameLayout mFlContainer;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private LazyParentFragment mLazyParentFragment1;
    private LazyParentFragment mLazyParentFragment2;
    private int mCurrentPosition;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_lazy;
    }

    @Override
    public void initView() {
        mLazyParentFragment1 = LazyParentFragment.newInstance("0");
        mLazyParentFragment2 = LazyParentFragment.newInstance("1");

        /*List<FragmentStateAdapter.FragmentTab> fragmentTabs = new ArrayList<>();
        fragmentTabs.add(new FragmentStateAdapter.FragmentTab("0", mLazyParentFragment1));
        fragmentTabs.add(new FragmentStateAdapter.FragmentTab("1", mLazyParentFragment2));
        mViewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), fragmentTabs));
        mViewPager.setVisibility(View.VISIBLE);*/

        getSupportFragmentManager().beginTransaction()
                .add(mFlContainer.getId(), mLazyParentFragment1)
                .commit();
        mFlContainer.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_next)
    public void onClick() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mCurrentPosition = (mCurrentPosition + 1) % 2;
        Fragment nextFragment, currentFragment;
        if (mCurrentPosition == 0) {
            nextFragment = mLazyParentFragment1;
            currentFragment = mLazyParentFragment2;
        } else {
            nextFragment = mLazyParentFragment2;
            currentFragment = mLazyParentFragment1;
        }
        transaction.hide(currentFragment);
        if (nextFragment.isAdded()) {
            transaction.show(nextFragment);
        } else {
            transaction.add(mFlContainer.getId(), nextFragment);
        }
        transaction.commit();
    }
}
