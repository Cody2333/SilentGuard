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
    public ScreenHandler(Context ctx, SensorHandler sensorHandler) {
        this.ctx = ctx;
        this.sensorHandler = sensorHandler;
        events = new Events();
        FLAG_SAVING_SCREEN_EVENT = false;
        //初始化 events
        events.Init();
        Events.intEnableDebug(1);
    }

    public void stopEventMonitor(){
        isRunning = false;
    }
    public void continueMonitor(){
        isRunning = true;
    }
    public void StartEventMonitor() {
        Toast.makeText(ctx, "serviceStart",
                Toast.LENGTH_SHORT).show();

        // 打开所有设备
        for (Events.InputDevice idev : events.m_Devs) {
            if (idev.Open(true)) {
                // Toast.makeText(getApplicationContext(),
                // "Device opened successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx,
                        "Device failed to open. Do you have root?",
                        Toast.LENGTH_SHORT).show();
            }
        }
        isRunning = true ;
        Thread b = new Thread(new Runnable() {

            public void run() {

                File originalScreenDataFile = Utils.createFile("eventInjectOrignial");

                CodeHandler codeHandler = new CodeHandler();
                while (isRunning) {
                    try {
                        for (Events.InputDevice idev : events.m_Devs) {
                            // Open more devices to see their messages
                            if (idev.getOpen() && (0 == idev.getPollingEvent())) {


                                int type = idev.getSuccessfulPollingType();
                                int code = idev.getSuccessfulPollingCode();
                                int value = idev.getSuccessfulPollingValue();
                                long t = idev.getTime();
                                //Log.e("screen:", String.valueOf(t));
                                //Log.e("timestap:",String.valueOf(Utils.getTimestamp()));
                                final String line = Utils.formatLineForOriginData(ctx, idev.getName(), type, code, value);
                                Utils.writeTxt(originalScreenDataFile, line);

                                //如果触摸事件结束返回true
                                Boolean isEnd = codeHandler.handle(type, code, value);

                                if (isEnd) {
                                    FLAG_SAVING_SCREEN_EVENT = true; // 让传感器值写入缓冲队列
                                    ScreenEvent screenEvent = new ScreenEvent(
                                            codeHandler.getNodeList(), sensorHandler.getAcceleratorQueue(), sensorHandler.getGyroscopeQueue(),
                                            Utils.getCurrentActivityName(ctx));
                                    FLAG_SAVING_SCREEN_EVENT = false; // 将传感器值写入生成队列
                                    if (screenEvent.judge()) {
                                        try {
                                            screenEvent.save(); // 保存主人数据到文件
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
}
