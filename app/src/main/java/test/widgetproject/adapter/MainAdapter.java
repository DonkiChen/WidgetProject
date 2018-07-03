package test.widgetproject.adapter;

import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Arrays;

import test.widgetproject.main.BannerActivity;
import test.widgetproject.main.CityActivity;
import test.widgetproject.main.DemoActivity;
import test.widgetproject.main.KeyboardActivity;
import test.widgetproject.main.LabelActivity;
import test.widgetproject.main.LinkRecyclerViewActivity;
import test.widgetproject.main.PatternLockActivity;
import test.widgetproject.main.R;
import test.widgetproject.main.RevealActivity;
import test.widgetproject.main.RoundCornerActivity;
import test.widgetproject.main.SwipeImageVerifyActivity;

/**
 * Created on 2018/5/23.
 *
 * @author ChenFanlin
 */
public class MainAdapter extends BaseQuickAdapter<MainAdapter.Bean, BaseViewHolder> {

    public MainAdapter() {
        super(R.layout.item_main, Arrays.asList(Bean.values()));
        setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Bean item = getItem(position);
                if (item != null) {
                    Intent intent = new Intent(mContext, item.getActivityClass());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void convert(BaseViewHolder helper, Bean item) {
        helper.setText(R.id.btn_main, item.getDescription())
                .addOnClickListener(R.id.btn_main);
    }

    enum Bean {
        LINK_RECYCLER_VIEW("link_recycler_view", LinkRecyclerViewActivity.class),
        SWIPE_IMAGE_VERIFY("swipe_image_verify", SwipeImageVerifyActivity.class),
        CITY("city", CityActivity.class),
        LABEL("label", LabelActivity.class),
        REVEAL("reveal", RevealActivity.class),
        BANNER("banner", BannerActivity.class),
        ROUND_CORNER("round_corner", RoundCornerActivity.class),
        MI_PATTERN_LOCK("mi_pattern_lock", PatternLockActivity.class),
        DEMO("demo", DemoActivity.class),
        KEYBOARD("keyboard", KeyboardActivity.class);

        private final String mDescription;
        private final Class<?> mActivityClass;

        Bean(String description, Class<?> activityClass) {
            mDescription = description;
            mActivityClass = activityClass;
        }

        public String getDescription() {
            return mDescription == null ? "" : mDescription;
        }

        public Class<?> getActivityClass() {
            return mActivityClass;
        }
    }
}
