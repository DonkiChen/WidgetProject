package test.widgetproject.main;

import android.util.Log;

import com.mvp.base.util.CollectionUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import test.widgetproject.database.DbHelper;
import test.widgetproject.entity.JQCity;
import test.widgetproject.util.JQCityUtils;
import test.widgetproject.widget.LabelView;

/**
 * Created on 2018/4/25.
 *
 * @author ChenFanlin
 */

public class LabelActivity extends BaseActivity {
    private static final String TAG = LabelActivity.class.getSimpleName();

    @BindView(R.id.labelView)
    LabelView mLabelView;

    private int mGravityValue = 0;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_label;
    }

    @Override
    public void initView() {
        getJQCities();
    }


    private void getJQCities() {
        addDisposable(DbHelper.getInstance().getJQCityDao().queryCities()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Predicate<List<JQCity>>() {
                    @Override
                    public boolean test(List<JQCity> cities) throws Exception {
                        if (CollectionUtils.isEmpty(cities)) {
                            JQCityUtils.getJQCities(new JQCityUtils.OnDataLoadListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onFinished(List<JQCity> list) {
                                    DbHelper.getInstance().getJQCityDao().insertCities(list.toArray(new JQCity[list.size()]));
                                    getJQCities();
                                }
                            });
                            return false;
                        }
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<JQCity>>() {
                    @Override
                    public void accept(List<JQCity> cities) throws Exception {
                        Log.d(TAG, "accept: ");
                    }
                }));
    }

    @OnClick(R.id.btn_label)
    public void onLabelClicked() {
        mLabelView.setLabelGravity(LabelView.LabelGravity.fromValue((++mGravityValue) % 4));
    }
}
