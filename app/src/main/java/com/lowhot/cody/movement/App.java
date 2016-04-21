package com.lowhot.cody.movement;

import android.app.Application;

import com.lowhot.cody.movement.utils.FileUtils;
import com.lowhot.cody.movement.utils.ToastUtils;
import com.orm.SugarContext;

/**
 * Created by cody_local on 2016/4/20.
 */
public class App extends Application{

    private static App appContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        SugarContext.init(this);
        ToastUtils.register(this);
        FileUtils.register(this);
    }

    @Override
    public void onTerminate() {
        SugarContext.terminate();
        super.onTerminate();
    }

    public static App getInstance(){
        return appContext;
    }
}
