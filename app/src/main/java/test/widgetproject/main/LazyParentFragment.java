package test.widgetproject.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.widgetproject.adapter.FragmentStateAdapter;

public class LazyParentFragment extends BaseLazyFragment {
    private static final String KEY_PARENT_PREFIX = "PARENT_PREFIX";

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private String mParentPrefix;

    public static LazyParentFragment newInstance(String parentPrefix) {
        Bundle args = new Bundle();
        args.putString(KEY_PARENT_PREFIX, parentPrefix);
        LazyParentFragment fragment = new LazyParentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_lazy_parent, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<FragmentStateAdapter.FragmentTab> fragments = new ArrayList<>();
        mParentPrefix = getArguments().getString(KEY_PARENT_PREFIX);
        for (int i = 0; i < 10; i++) {
            String title = mParentPrefix + String.valueOf(i);
            fragments.add(new FragmentStateAdapter.FragmentTab(title, LazyChildFragment.newInstance(title)));
        }
        mViewPager.setAdapter(new FragmentStateAdapter(getChildFragmentManager(), fragments));
    }

    @Override
    public void onLazyEnter(boolean isReenter) {
    }

    @Override
    public void onLazyExit() {
    }

    @Override
    public void onCombineResume() {
    }

    @Override
    public void onCombinePause() {
    }

    @Override
    public String toString() {
        return mParentPrefix;
    }
}
