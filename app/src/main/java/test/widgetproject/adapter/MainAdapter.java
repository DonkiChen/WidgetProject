package test.widgetproject.adapter;

import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Arrays;

import test.widgetproject.main.CityActivity;
import test.widgetproject.main.LabelActivity;
import test.widgetproject.main.LinkRecyclerViewActivity;
import test.widgetproject.main.PatternLockActivity;
import test.widgetproject.main.R;
import test.widgetproject.main.RevealActivity;
import test.widgetproject.main.RoundCornerActivity;
import test.widgetproject.main.RulerActivity;
import test.widgetproject.main.ShareActivity;
import test.widgetproject.main.SwipeImageVerifyActivity;
import test.widgetproject.main.TopicActivity;

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
        LINK_RECYCLER_VIEW("联动RecyclerView", LinkRecyclerViewActivity.class),
        SWIPE_IMAGE_VERIFY("滑动验证码", SwipeImageVerifyActivity.class),
        CITY("城市选择", CityActivity.class),
        LABEL("标签", LabelActivity.class),
        REVEAL("关注动画", RevealActivity.class),
        ROUND_CORNER("圆角约束布局", RoundCornerActivity.class),
        PATTERN_LOCK("图案解锁", PatternLockActivity.class),
        TOPIC("话题Span", TopicActivity.class),
        RULER_VIEW("尺子", RulerActivity.class),
        //LAZY_FRAGMENT("lazy_fragment", LazyActivity.class),
        SHARE_ELEMENT("ViewPager,Fragment与共享动画", ShareActivity.class);

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
