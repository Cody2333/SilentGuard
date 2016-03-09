package com.lowhot.cody.movement.uitls;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

/**
 * Created by cody_local on 2016/3/9.
 * 工具函数
 */
public class Utils {
    /**
     * 获取当前时间戳
     * @return
     */
    public static long getTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取 当前的activity名称
     */
    public static String getCurrentActivityName(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Activity.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }

}
