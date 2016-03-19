package com.lowhot.cody.movement.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.Utils;

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

    public void stopEventMonitor() {
        Toast.makeText(ctx, "service stop", Toast.LENGTH_SHORT).show();
        Log.i(TAG,"service stop");
        isRunning = false;
    }

    public void continueMonitor() {
        dir = "/" + getType() + "/" + Utils.getTimestamp();
        Toast.makeText(ctx, "service continue,store dir" + dir, Toast.LENGTH_SHORT).show();
        isRunning = true;
        Log.i(TAG,"service continue");
    }

    public void StartEventMonitor() {

        // 打开所有设备
        for (Events.InputDevice idev : events.m_Devs) {
            if (idev.Open(true)) {
            } else {
                Toast.makeText(ctx, "Device failed to open. Do you have root?", Toast.LENGTH_SHORT).show();
            }
        }
        isRunning = false;
        Thread b = new Thread(new Runnable() {

            public void run() {

                File originalScreenDataFile = Utils.createFile("eventInjectOrignial");
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

                                    final String line = Utils.formatLineForOriginData(ctx, idev.getName(), type, code, value);
                                    Utils.writeTxt(originalScreenDataFile, line);

                                    //如果触摸事件结束返回true
                                    Boolean isEnd = codeHandler.handle(type, code, value);

                                    if (isEnd) {
                                        FLAG_SAVING_SCREEN_EVENT = true; // 让传感器值写入缓冲队列
                                        ScreenEvent screenEvent = new ScreenEvent(
                                                codeHandler.getNodeList(), sensorHandler.getAcceleratorQueue(), sensorHandler.getGyroscopeQueue(),
                                                Utils.getCurrentActivityName(ctx), dir);
                                        FLAG_SAVING_SCREEN_EVENT = false; // 将传感器值写入生成队列

                                        if (screenEvent.judge()) {
                                            try {
                                                screenEvent.save(); // 保存主人数据到文件
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
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
