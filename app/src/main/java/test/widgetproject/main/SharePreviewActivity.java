package test.widgetproject.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static com.mvp.base.util.BaseUtils.getAppPackageName;

public class SharePreviewActivity extends BaseActivity {
    public static final String KEY_POSITION = "POSITION";
    private static final String KEY_IMAGE_URLS = "IMAGE_URLS";
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private List<String> mUrls;

    public static Intent newIntent(ArrayList<String> urls, int position) {
        Intent intent = new Intent();
        intent.setClassName(getAppPackageName(), SharePreviewActivity.class.getName());
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_IMAGE_URLS, urls);
        bundle.putInt(KEY_POSITION, position);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_share_preview;
    }

    @Override
    public void initView() {
        mUrls = getIntent().getStringArrayListExtra(KEY_IMAGE_URLS);
        final int position = getIntent().getIntExtra(KEY_POSITION, 0);
        final List<ImageView> imageViews = new ArrayList<>();

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                names.clear();
                sharedElements.clear();
                int currentPosition = mViewPager.getCurrentItem();
                String url = mUrls.get(currentPosition);
                names.add(url);
                sharedElements.put(url, imageViews.get(currentPosition));
            }
        });

        for (String url : mUrls) {
            ImageView imageView = new ImageView(this);
            imageView.setTransitionName(url);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    supportFinishAfterTransition();
                }
            });
            imageViews.add(imageView);
        }
        mViewPager.setAdapter(new ImagePagerAdapter(this, mUrls, imageViews));
        mViewPager.setCurrentItem(position);
        supportPostponeEnterTransition();
    }

    @Override
    public void finishAfterTransition() {
        Intent intent = new Intent();
        intent.putExtra(KEY_POSITION, mViewPager.getCurrentItem());
        setResult(RESULT_OK, intent);
        super.finishAfterTransition();
    }

    private static class ImagePagerAdapter extends PagerAdapter {
        private AppCompatActivity mAppCompatActivity;
        private List<String> mUrls;
        private List<ImageView> mImageViews;

        ImagePagerAdapter(AppCompatActivity appCompatActivity, List<String> urls, List<ImageView> imageViews) {
            mAppCompatActivity = appCompatActivity;
            mUrls = urls;
            mImageViews = imageViews;
        }

        @Override
        public int getCount() {
            return mImageViews.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = mImageViews.get(position);
            Glide.with(mAppCompatActivity).load(mUrls.get(position))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            mAppCompatActivity.supportStartPostponedEnterTransition();
                            return false;
                        }
                    }).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(((View) object));
        }
    }
}
