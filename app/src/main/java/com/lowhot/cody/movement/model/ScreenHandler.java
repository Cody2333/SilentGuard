package com.lowhot.cody.movement.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.FileUtils;
import com.lowhot.cody.movement.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by cody_local on 2016/3/9.
 * 处理触摸事件相关事务
 */
public class ScreenHandler {
    private static final String TAG = "ScreenHandler";
    private Events events;
    private volatile boolean FLAG_SAVING_SCREEN_EVENT;
    private Context ctx;
    private SensorHandler sensorHandler;
    private Boolean isRunning = false;
    //type ---> "master" or "guest"
    volatile String accountType = "master";
    volatile String dir = "";

    public ScreenHandler(Context ctx, SensorHandler sensorHandler) {
        this.ctx = ctx;
        this.sensorHandler = sensorHandler;
        events = new Events();
        FLAG_SAVING_SCREEN_EVENT = false;
        //初始化 events
        events.Init();
        Events.intEnableDebug(1);
    }

    /**
     * 停止监控
     */
    public void stopEventMonitor() {
        ToastUtils.showShort("service stop");
        Log.i(TAG,"service stop");
        isRunning = false;
    }

    /**
     * 继续监控
     */
    public void continueMonitor() {
        dir = "/" + getType() + "/" + FileUtils.getTimestamp();
        ToastUtils.showShort("service continue,store dir" + dir);
        isRunning = true;
        Log.i(TAG, "service continue");
    }

    /**
     * 开始监控
     */
    public void StartEventMonitor() {

        // 打开所有设备
        for (Events.InputDevice idev : events.m_Devs) {
            if (idev.Open(true)) {
            } else {
                ToastUtils.showShort( "Device failed to open. Do you have root?");
            }
        }
        isRunning = false;

        Thread b = new Thread(new Runnable() {
            public void run() {

                File originalScreenDataFile = FileUtils.createFile("eventInjectOrignial");
                CodeHandler codeHandler = new CodeHandler();

                while (true) {
                    if (!isRunning) {
                    } else {
                        try {
                            for (Events.InputDevice idev : events.m_Devs) {
                                // Open more devices to see their messages
                                if (idev.getOpen() && (0 == idev.getPollingEvent())) {

                                    int type = idev.getSuccessfulPollingType();
                                    int code = idev.getSuccessfulPollingCode();
                                    int value = idev.getSuccessfulPollingValue();
                                    Log.i("code",type+":"+code+":"+value+":"+FileUtils.getCurrentActivityName(ctx));
                                    //如果触摸事件结束返回true
                                    Boolean isEnd = codeHandler.handle(type, code, value);

                                    //一次触事件结束
                                    if (isEnd) {
                                        FLAG_SAVING_SCREEN_EVENT = true; // 让传感器值写入缓冲队列
                                        // TODO: 16/3/30
                                        //处理事件结束
                                        FLAG_SAVING_SCREEN_EVENT = false; // 将传感器值写入生成队列


                                        codeHandler.reset();
                                    }
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

        }
        );
        b.start();
    }

    public boolean isFLAG_SAVING_SCREEN_EVENT() {
        return FLAG_SAVING_SCREEN_EVENT;
    }


    public Events getEvents() {
        return events;
    }

    public void setType(String type) {
        this.accountType = type;
    }

    public String getType() {
        return this.accountType;
    }
}
