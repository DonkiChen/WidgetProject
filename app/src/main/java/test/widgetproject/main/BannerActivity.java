package test.widgetproject.main;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created on 2018/5/21.
 *
 * @author ChenFanlin
 */
public class BannerActivity extends BaseActivity {
    private static final String TAG = BannerActivity.class.getSimpleName();
    private static final String KEY_URL_1 = "http://xiaolaiimage.yinglai.ren/unsafe/dev_/2018/05/17/16484955425736355.png";

    @BindView(R.id.banner)
    ViewPager mBanner;

    private List<String> mUrls = new ArrayList<>();

    @Override
    public int getLayoutRes() {
        return R.layout.activity_banner;
    }

    @Override
    public void initView() {
        initBanner();
    }

    private void initBanner() {
        mUrls.add(KEY_URL_1);
        int urlSize = mUrls.size();
        if (urlSize == 0) {
            return;
        }
        int imageSize = urlSize + 2;
        List<ImageView> imageViews = new ArrayList<>(imageSize);
        for (int i = 0; i < imageSize; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String url;
            if (i == 0) {
                url = mUrls.get(urlSize - 1);
            } else if (i == imageSize - 1) {
                url = mUrls.get(0);
            } else {
                url = mUrls.get(i - 1);
            }
            Glide.with(this).load(url).into(imageView);
            imageViews.add(imageView);
        }
        BannerAdapter bannerAdapter = new BannerAdapter(imageViews);
        mBanner.setAdapter(bannerAdapter);
        mBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int currentItem = mBanner.getCurrentItem();
                int viewCount = mBanner.getAdapter().getCount();
                switch (state) {
//                    case ViewPager.SCROLL_STATE_IDLE:
                    case ViewPager.SCROLL_STATE_DRAGGING: {
                        Log.d(TAG, "onPageScrollStateChanged: ");
                        if (currentItem == 0) {
                            mBanner.setCurrentItem(viewCount - 2, false);
                        } else if (currentItem == viewCount - 1) {
                            mBanner.setCurrentItem(1, false);
                        }
                        break;
                    }
                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;
                }
            }
        });
        mBanner.setCurrentItem(1, false);
    }

    private static class BannerAdapter extends PagerAdapter {

        private List<ImageView> mImageViews;

        BannerAdapter(List<ImageView> imageViews) {
            mImageViews = imageViews;
        }

        @Override
        public int getCount() {
            return mImageViews.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mImageViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
