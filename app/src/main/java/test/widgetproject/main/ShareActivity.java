package test.widgetproject.main;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import test.widgetproject.adapter.FragmentStateAdapter;
import test.widgetproject.util.TransitionHelper;

public class ShareActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_share;
    }

    @Override
    public void initView() {
        List<FragmentStateAdapter.FragmentTab> fragmentTabs = new ArrayList<>();
        fragmentTabs.add(new FragmentStateAdapter.FragmentTab("页面1", ShareFragment.newInstance()));
        fragmentTabs.add(new FragmentStateAdapter.FragmentTab("页面2", ShareFragment.newInstance()));

        mViewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), fragmentTabs));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Fragment fragment =
                    ((FragmentStateAdapter) mViewPager.getAdapter()).getItem(mViewPager.getCurrentItem());
            if (fragment instanceof TransitionHelper.ReenterListener) {
                ((TransitionHelper.ReenterListener) fragment).onReenter(resultCode, data);
            }
        }
    }

}
