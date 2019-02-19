package test.widgetproject.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class FragmentStateAdapter extends FragmentStatePagerAdapter {
    private List<FragmentTab> mFragmentTabs;

    public FragmentStateAdapter(FragmentManager fm, List<FragmentTab> fragmentTabs) {
        super(fm);
        mFragmentTabs = fragmentTabs;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentTabs.get(position).fragment;
    }

    @Override
    public int getCount() {
        return mFragmentTabs.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTabs.get(position).title;
    }

    public static class FragmentTab {
        public String title;
        public Fragment fragment;

        public FragmentTab(String title, Fragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }
    }
}
