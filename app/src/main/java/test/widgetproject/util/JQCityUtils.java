package test.widgetproject.util;

import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mvp.base.util.BaseUtils;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import test.widgetproject.entity.JQCity;

/**
 * Created on 2018/4/27.
 *
 * @author ChenFanlin
 */

public class JQCityUtils extends BaseUtils {
    private static final String TAG = JQCityUtils.class.getSimpleName();

    public static void getJQCities(final OnDataLoadListener listener) {
        Flowable.just(jsonToString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        listener.onStart();
                    }
                })
                .flatMap(new Function<String, Publisher<JQCity>>() {
                    @Override
                    public Publisher<JQCity> apply(String s) throws Exception {
                        Log.d(TAG, "apply: 解析开始" + System.currentTimeMillis());
                        List<JQCity> items = new Gson().fromJson(s, new TypeToken<List<JQCity>>() {
                        }.getType());
                        Log.d(TAG, "apply: 解析结束" + System.currentTimeMillis());
                        return Flowable.fromIterable(items);
                    }
                })
                .toSortedList()
                .subscribe(new Consumer<List<JQCity>>() {
                    @Override
                    public void accept(List<JQCity> cities) throws Exception {
                        listener.onFinished(cities);
                    }
                });

    }

    private static String jsonToString() {
        StringBuilder sb = new StringBuilder();
        try (AssetManager assetManager = context.getAssets()) {
            InputStream is = assetManager.open("jq_city.json");
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

        void onFinished(List<JQCity> list);
    }
}
