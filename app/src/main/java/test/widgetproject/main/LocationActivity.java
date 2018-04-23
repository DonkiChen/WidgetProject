package test.widgetproject.main;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mvp.base.util.DisplayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import test.widgetproject.adapter.CityAdapter;
import test.widgetproject.adapter.HeaderCityAdapter;
import test.widgetproject.database.CityDao;
import test.widgetproject.database.CityDatabase;
import test.widgetproject.entity.City;

/**
 * Created on 2018/4/20.
 *
 * @author ChenFanlin
 */

public class LocationActivity extends BaseActivity {
    private static final String TAG = LocationActivity.class.getSimpleName();
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private View mHeaderView;

    private CityAdapter mCityAdapter;
    private CompositeDisposable mDisposables = new CompositeDisposable();
    private CityDao mCityDao;
    private HashMap<String, Integer> mPinyinMap = new HashMap<>();
    private Paint mPaint = new Paint();
    private int mPinyinHeight = DisplayUtils.dp2px(36);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mCityDao = CityDatabase.getInstance().getCityDao();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        mDisposables.clear();
        CityDatabase.getInstance().close();
        super.onDestroy();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_location;
    }

    @Override
    public void initView() {
        mCityAdapter = new CityAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initHeaderView();
        mCityAdapter.addHeaderView(mHeaderView);
        mRecyclerView.setAdapter(mCityAdapter);
        mDisposables.add(mCityDao.queryCities()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<City>>() {
                    @Override
                    public void accept(List<City> cities) throws Exception {
                        mCityAdapter.setNewData(cities);
                        mPinyinMap.clear();
                        int count = cities.size();
                        for (int i = 0; i < count; i++) {
                            City city = cities.get(i);
                            String firstLetter = String.valueOf(city.cityNamePinyin.charAt(0));

                            if (!mPinyinMap.containsKey(firstLetter)) {
                                mPinyinMap.put(firstLetter, i + mCityAdapter.getHeaderLayoutCount());
                            }
                        }
                    }
                }));
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(DisplayUtils.sp2px(14));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View view = parent.getChildAt(i);
                    int position = parent.getChildAdapterPosition(view);
                    if (mPinyinMap.containsValue(position)) {
                        mPaint.setColor(Color.parseColor("#E2E2E2"));
                        c.drawRect(view.getLeft(), view.getTop() - mPinyinHeight,
                                view.getRight(),
                                view.getTop(), mPaint);
                        String letter = null;
                        for (Map.Entry<String, Integer> entry : mPinyinMap.entrySet()) {
                            if (entry.getValue() == position) {
                                letter = entry.getKey();
                                break;
                            }
                        }
                        if (letter == null) {
                            continue;
                        }
                        mPaint.setColor(Color.BLACK);
                        // 垂直居中的baseline计算方法: ascent 和 descent 都是相对于 baseline, 所以得出等式
                        // (Ascent, Descent为相对于view坐标)
                        // 1. pinyin.centerY = (Ascent + Descent)/2
                        // 2. Ascent = baseline + ascent
                        // 3. Descent = baseline + descent
                        // => pinyin.centerY = (baseline + ascent + baseline + descent) / 2
                        // => baseline = pinyin.centerY - (ascent + descent) / 2
                        // 4. pinyin.centerY = (view.getTop() - mPinyinHeight + view.getTop()) / 2
                        // => baseline = view.getTop() - (ascent + descent + mPinyinHeight) / 2

                        float textY = view.getTop() - (mPaint.ascent() + mPaint.descent() + mPinyinHeight) / 2;
                        c.drawText(letter, 0, textY, mPaint);
                    }
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (mPinyinMap.containsValue(position)) {
                    outRect.top = mPinyinHeight;
                }
            }
        });
    }

    private void initHeaderView() {
        mHeaderView = getLayoutInflater().inflate(R.layout.header_location_gps, mRecyclerView, false);
        RecyclerView recyclerViewHistory = mHeaderView.findViewById(R.id.recyclerViewHistory);
        RecyclerView recyclerViewHot = mHeaderView.findViewById(R.id.recyclerViewHot);

        List<City> items = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            City city = new City();
            city.cityName = String.valueOf(i);
            items.add(city);
        }

        HeaderCityAdapter cityHistoryAdapter = new HeaderCityAdapter();
        recyclerViewHistory.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewHistory.setAdapter(cityHistoryAdapter);
        cityHistoryAdapter.setNewData(items);

        HeaderCityAdapter cityHotAdapter = new HeaderCityAdapter();
        recyclerViewHot.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewHot.setAdapter(cityHotAdapter);
        cityHotAdapter.setNewData(items);

    }
}
