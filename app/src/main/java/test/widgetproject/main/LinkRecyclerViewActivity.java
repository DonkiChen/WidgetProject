package test.widgetproject.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mvp.base.util.DisplayUtils;

import java.util.ArrayList;

import test.widgetproject.adapter.ItemAdapter;
import test.widgetproject.adapter.TypeAdapter;
import test.widgetproject.entity.TypeBean;
import test.widgetproject.util.MathUtils;

/**
 * Created on 2018/4/13.
 *
 * @author ChenFanlin
 */

public class LinkRecyclerViewActivity extends AppCompatActivity {

    private RecyclerView mRvType, mRvItem;
    private TextView mTvTitle;
    private ImageView mIvShop;
    private ArrayList<TypeBean> mTypeBeanList = new ArrayList<>();
    private ArrayList<TypeBean.ItemBean> mTotalItemBeanList = new ArrayList<>();
    private FrameLayout mFlRoot;
    private int mTitleAndStatusHeight;
    private TypeAdapter mTypeAdapter;
    private int mAnimationViewSize = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_recycler_view);
        for (int i = 0; i < 10; i++) {
            ArrayList<TypeBean.ItemBean> items = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                items.add(new TypeBean.ItemBean(i + "`" + j));
            }
            mTotalItemBeanList.addAll(items);
            mTypeBeanList.add(new TypeBean(items, String.valueOf(i)));
        }
        initView();
    }

    private void initView() {
        mRvType = findViewById(R.id.rv_type);
        mRvItem = findViewById(R.id.rv_item);
        mTvTitle = findViewById(R.id.tv_title);
        mFlRoot = findViewById(R.id.fl_root);
        mIvShop = findViewById(R.id.iv_shop);

        mRvType.setLayoutManager(new LinearLayoutManager(this));
        mTypeAdapter = new TypeAdapter();
        mRvType.setAdapter(mTypeAdapter);
        mTypeAdapter.setNewData(mTypeBeanList);
        mRvType.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mTypeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TypeBean typeBean = mTypeAdapter.getItem(position);
//                mRvItem.smoothScrollToPosition(findItemFirstPosition(typeBean));
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRvItem.getLayoutManager();
                layoutManager.scrollToPositionWithOffset(findItemFirstPosition(typeBean), 0);
            }
        });
        mRvType.getItemAnimator().setChangeDuration(0);
        mTypeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TypeBean typeBean = mTypeAdapter.getItem(position);
                if (typeBean != null) {
                    int itemPosition = findItemFirstPosition(typeBean);
//                    mRvItem.smoothScrollToPosition(itemPosition);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mRvItem.getLayoutManager();
                    layoutManager.scrollToPositionWithOffset(itemPosition, 0);
                }
            }
        });

        final LinearLayoutManager itemLayoutManager = new LinearLayoutManager(this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(LinkRecyclerViewActivity.this) {
                    @Override
                    public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                        return boxStart - viewStart;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
        mRvItem.setLayoutManager(itemLayoutManager);
        final ItemAdapter itemAdapter = new ItemAdapter();
        mRvItem.setAdapter(itemAdapter);
        itemAdapter.setNewData(mTotalItemBeanList);
        mRvItem.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRvItem.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //获取子列表中的第一个item,获取所属的type,将其选中
                int firstPosition = itemLayoutManager.findFirstVisibleItemPosition();
                TypeBean currentType = findType(itemAdapter.getItem(firstPosition));
                mTypeAdapter.setSelectedType(currentType);
                if (currentType != null) {
                    mTvTitle.setText(currentType.getType());
                }
                //使当前type可见
                int typePosition = mTypeAdapter.getData().indexOf(currentType);
                mRvType.smoothScrollToPosition(typePosition);
            }
        });
        itemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final TextView tvAnimation = new TextView(LinkRecyclerViewActivity.this);
                tvAnimation.setText(itemAdapter.getItem(position).getItem());
                tvAnimation.setGravity(Gravity.CENTER);
                tvAnimation.setBackgroundColor(Color.BLUE);
                mFlRoot.addView(tvAnimation, mAnimationViewSize, mAnimationViewSize);
                animateByPath(view, tvAnimation);
            }
        });
    }

    private int findTypeByTouch(float x, float y) {
        int count = mRvType.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = mRvType.getChildAt(i);
            if (MathUtils.between(y, view.getTop(), view.getBottom()) && MathUtils.between(x, view.getLeft(), view.getRight())) {
                return mRvType.getChildAdapterPosition(view);
            }
        }
        return -1;
    }

    private int findItemFirstPosition(TypeBean typeBean) {
        TypeBean.ItemBean firstItem = typeBean.getItems().get(0);
        return mTotalItemBeanList.indexOf(firstItem);
    }

    /**
     * 查找目标item所属的type
     *
     * @param item 目标item
     * @return 所属type
     */
    private TypeBean findType(TypeBean.ItemBean item) {
        for (TypeBean typeBean : mTypeBeanList) {
            if (typeBean.getItems().contains(item)) {
                return typeBean;
            }
        }
        return null;
    }

    /**
     * 利用Path实现落到购物车的动画
     *
     * @param clickedView
     * @param tvAnimation
     */
    private void animateByPath(View clickedView, final TextView tvAnimation) {
        int[] startLoc = new int[2];
        clickedView.getLocationInWindow(startLoc);
        int[] endLoc = new int[2];
        mIvShop.getLocationInWindow(endLoc);

        int startX = startLoc[0] + (clickedView.getWidth() - mAnimationViewSize) / 2;
        int startY = startLoc[1] - mTitleAndStatusHeight + (clickedView.getHeight() - mAnimationViewSize) / 2;
        float toX = endLoc[0] + (mIvShop.getWidth() - mAnimationViewSize) / 2F;
        float toY = endLoc[1] - mTitleAndStatusHeight + (mIvShop.getHeight() - mAnimationViewSize) / 2F;

        Path path = new Path();
        path.moveTo(startX, startY);
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        //利用PathMeasure计算动画中当前点的坐标
        final PathMeasure measure = new PathMeasure(path, false);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, measure.getLength());
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float[] current = new float[2];

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                measure.getPosTan(value, current, null);
                tvAnimation.setTranslationX(current[0]);
                tvAnimation.setTranslationY(current[1]);
            }
        });
        valueAnimator.start();
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFlRoot.removeView(tvAnimation);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mTitleAndStatusHeight = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop() + DisplayUtils.getStatusHeight();
    }

}
