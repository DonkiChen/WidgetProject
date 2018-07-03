package test.widgetproject.util;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.mvp.base.util.BaseUtils;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import test.widgetproject.entity.City;

/**
 * Created on 2018/4/20.
 *
 * @author ChenFanlin
 */

public class CityUtils extends BaseUtils {

    @SuppressLint("CheckResult")
    public static void getCities(final OnDataLoadListener listener) {
        getCitiesFlowable()
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        listener.onStart();
                    }
                })
                .subscribe(new Consumer<List<City>>() {
                    @Override
                    public void accept(List<City> cities) throws Exception {
                        listener.onFinished(cities);
                    }
                });
    }

    public static Flowable<List<City>> getCitiesFlowable() {
        return Flowable.create(new FlowableOnSubscribe<JSONArray>() {
            @Override
            public void subscribe(FlowableEmitter<JSONArray> e) throws Exception {
                JSONArray jsonArray = new JSONArray(jsonToString());
                e.onNext(jsonArray);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .map(new Function<JSONArray, List<JSONArray>>() {
                    @Override
                    public List<JSONArray> apply(JSONArray jsonArray) throws Exception {
                        LinkedList<JSONArray> jsonArrays = new LinkedList<>();
                        int count = jsonArray.length();
                        for (int i = 0; i < count; i++) {
                            jsonArrays.add(((JSONObject) jsonArray.get(i)).getJSONArray("cities"));
                        }
                        return jsonArrays;
                    }
                })
                .map(new Function<List<JSONArray>, List<City>>() {
                    @Override
                    public List<City> apply(List<JSONArray> jsonArrays) throws Exception {
                        LinkedList<City> cities = new LinkedList<>();
                        for (JSONArray jsonArray : jsonArrays) {
                            int count = jsonArray.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject jsonObject = ((JSONObject) jsonArray.opt(i));
                                if (TextUtils.equals("省直辖县级行政区划", jsonObject.optString("areaName"))) {
                                    continue;
                                }
                                City city = new City();
                                city.cityId = jsonObject.optString("areaId");
                                city.cityName = jsonObject.optString("areaName");
                                city.cityNamePinyin = PinyinHelper.toHanyuPinyinStringArray(city.cityName.charAt(0))[0].toUpperCase();
                                city.provinceId = TextUtils.concat(city.cityId.substring(0, 2), "0000").toString();
                                cities.add(city);
                            }
                        }
                        return cities;
                    }
                });
    }

    private static String jsonToString() {
        StringBuilder sb = new StringBuilder();
        AssetManager assetManager = BaseUtils.getApplicationContext().getAssets();
        try {
            InputStream is = assetManager.open("city.json");
            byte[] data = new byte[is.available()];
            int len;
            while ((len = is.read(data)) != -1) {
                sb.append(new String(data, 0, len, "utf-8"));
            }
            is.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public interface OnDataLoadListener {
        void onStart();

        void onFinished(List<City> list);
    }
}
