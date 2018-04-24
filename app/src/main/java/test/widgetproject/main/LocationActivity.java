package test.widgetproject.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mvp.base.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import test.widgetproject.adapter.CityAdapter;
import test.widgetproject.adapter.HeaderCityAdapter;
import test.widgetproject.database.CityDao;
import test.widgetproject.database.CityDatabase;
import test.widgetproject.entity.City;
import test.widgetproject.util.CityUtils;
import test.widgetproject.widget.HeaderDecoration;
import test.widgetproject.widget.SlideBar;

/**
 * Created on 2018/4/20.
 *
 * @author ChenFanlin
 */

public class LocationActivity extends BaseActivity {
    private static final String TAG = LocationActivity.class.getSimpleName();
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.slideBar)
    SlideBar mSlideBar;

    private View mHeaderView;

    private CityAdapter mCityAdapter;
    private CompositeDisposable mDisposables = new CompositeDisposable();
    private CityDao mCityDao;
    private HashMap<String, Integer> mPinyinMap = new HashMap<>();
    private LinearLayoutManager mLinearLayoutManager;
    private PopupWindow mPinyinWindow;
    private TextView mTvPinyin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mCityDao = CityDatabase.getInstance().getCityDao();
        super.onCreate(savedInstanceState);
        getCities();
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
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        initHeaderView();
        mCityAdapter.addHeaderView(mHeaderView);
        mRecyclerView.setAdapter(mCityAdapter);
        HeaderDecoration headerDecoration = new HeaderDecoration(mPinyinMap);
        mRecyclerView.addItemDecoration(headerDecoration);

        mPinyinWindow = new PopupWindow(getWindow().getDecorView(), -2, -2, false);
        mPinyinWindow.setContentView(getLayoutInflater().inflate(R.layout.dialog_pinyin, null, false));
        mTvPinyin = mPinyinWindow.getContentView().findViewById(R.id.tv_pinyin);
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

    private void getCities() {
        mDisposables.add(mCityDao.queryCities()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Predicate<List<City>>() {
                    @Override
                    public boolean test(List<City> cities) throws Exception {
                        if (CollectionUtils.isEmpty(cities)) {
                            CityUtils.getCities(new CityUtils.OnDataLoadListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onFinished(List<City> list) {
                                    mCityDao.insertCities(list.toArray(new City[list.size()]));
                                    getCities();
                                }
                            });
                            return false;
                        }
                        return true;
                    }
                })
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
                        mPinyinMap.put("热门", 0);
                        initSliderBar();

                        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                int position = mLinearLayoutManager.findFirstVisibleItemPosition();
                                if (mPinyinMap.containsValue(position)) {
                                    String value = null;
                                    for (Map.Entry<String, Integer> entry : mPinyinMap.entrySet()) {
                                        if (entry.getValue() == position) {
                                            value = entry.getKey();
                                            break;
                                        }
                                    }
                                    if (value == null) {
                                        return;
                                    }
                                    mSlideBar.setSelected(value, false);
                                }
                            }
                        });
                    }
                }));
    }

    private void initSliderBar() {
        String[] pinyinArray = new String[mPinyinMap.size()];
        mPinyinMap.keySet().toArray(pinyinArray);
        Arrays.sort(pinyinArray, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return mPinyinMap.get(o1) - mPinyinMap.get(o2);
            }
        });

        mSlideBar.clear();
        mSlideBar.addItems(pinyinArray);
        mSlideBar.setOnBarTouchListener(new SlideBar.OnBarTouchListener() {
            @Override
            public void onTouchDown(int index, String item) {
                int position = mPinyinMap.get(item);
                mLinearLayoutManager.scrollToPositionWithOffset(position, 0);

                mTvPinyin.setText(item.substring(0, 1));
                mPinyinWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            }

            @Override
            public void onTouchUp() {
                mPinyinWindow.dismiss();
            }
        });
    }
}
