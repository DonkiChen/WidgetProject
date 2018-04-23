package test.widgetproject;

import android.app.Application;

import com.mvp.base.MVPClient;

/**
 * Created on 2018/3/30.
 *
 * @author ChenFanlin
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MVPClient.initUtils(this);
    }
}
