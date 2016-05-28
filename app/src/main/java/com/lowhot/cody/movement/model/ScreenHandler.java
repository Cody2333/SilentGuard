package com.lowhot.cody.movement.model;

import android.content.Context;
import android.util.Log;

import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.FileUtils;
import com.lowhot.cody.movement.utils.eventBus.AlertEvent;
import com.lowhot.cody.movement.utils.ui.ToastUtils;

import org.greenrobot.eventbus.EventBus;

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
    private Boolean isPredicting = false;
    //type ---> "master" or "guest"
    volatile String accountType = "master";
    //当前匹配的轨迹id
    volatile int currentMatchedId;
    volatile String dir = "";

    public ScreenHandler(Context ctx, SensorHandler sensorHandler) {
        this.ctx = ctx;
        //EventBus.getDefault().register(this);
        this.sensorHandler = sensorHandler;
        events = new Events();
        FLAG_SAVING_SCREEN_EVENT = false;
        //初始化 events
        events.Init();
        Events.intEnableDebug(1);
    }

    public void stopPredictMonitor() {
        ToastUtils.showShort("predict stop");
        if(isPredicting){
            isRunning=false;
            isPredicting=false;
        }
    }

    public void continuePredictMonitor() {
        ToastUtils.showShort("predict continue");
        if(!isPredicting){
            isRunning=true;
            isPredicting=true;
        }
    }
    public void stopEventMonitor() {
        ToastUtils.showShort("service stop");
        Log.i(TAG, "service stop");
        isRunning = false;
        isPredicting=false;

    }

    public void continueMonitor() {
        //dir = "/data/" + getType() + "/" + FileUtils.getTimestamp();
        dir = "/data/" + getType();
        ToastUtils.showShort("service continue,store dir" + dir);
        isRunning = true;
        isPredicting=false;

        Log.i(TAG, "service continue");
    }

    public void StartEventMonitor() {

        // 打开所有设备
        for (Events.InputDevice idev : events.m_Devs) {
            if (idev.Open(true)) {
            } else {
                ToastUtils.showShort( "Device failed to open. Do you have root?");
            }
        }
        isRunning = false;
        isPredicting = false;
        Thread b = new Thread(new Runnable() {

            public void run() {

                CodeHandler codeHandler = new CodeHandler();

                while (true) {
                    if (!isRunning) {
                    } else if (isRunning && !isPredicting){
                        //收集数据，但不进行预测
                        try {
                            for (Events.InputDevice idev : events.m_Devs) {
                                if (idev.getOpen() && (0 == idev.getPollingEvent())) {
                                    int type = idev.getSuccessfulPollingType();
                                    int code = idev.getSuccessfulPollingCode();
                                    int value = idev.getSuccessfulPollingValue();
                                    //下面两行记录原始数据
                                    //final String line = FileUtils.formatLineForOriginData(ctx, idev.getName(), type, code, value);
                                    //FileUtils.writeTxt(originalScreenDataFile, line);
                                    //如果触摸事件结束返回true
                                    Boolean isEnd = codeHandler.handle(type, code, value);

                                    if (isEnd) {
                                        FLAG_SAVING_SCREEN_EVENT = true; // 让传感器值写入缓冲队列
                                        Boolean isAdmin;
                                        if(accountType.equals("master")){
                                            isAdmin = true;
                                        }else{
                                            isAdmin = false;
                                        }
                                        ScreenEvent screenEvent = new ScreenEvent(
                                                codeHandler.getNodeList(), sensorHandler.getAcceleratorQueue(), sensorHandler.getGyroscopeQueue(),
                                                FileUtils.getCurrentActivityName(ctx), dir,isAdmin);
                                        FLAG_SAVING_SCREEN_EVENT = false; // 将传感器值写入生成队列

                                        if (true) {
                                            try {
                                        //long x = FileUtils.getTimestamp();
                                                screenEvent.save(); // 保存主人数据到文件
                                        //Log.e("SAVE OPERATION TIME",String.valueOf(FileUtils.getTimestamp()-x));
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
                    } else if (isRunning && isPredicting){
                        //预测数据
                        try {
                            for (Events.InputDevice idev : events.m_Devs) {
                                if (idev.getOpen() && (0 == idev.getPollingEvent())) {
                                    int type = idev.getSuccessfulPollingType();
                                    int code = idev.getSuccessfulPollingCode();
                                    int value = idev.getSuccessfulPollingValue();
                                    Boolean isEnd = codeHandler.handle(type, code, value);

                                    if (isEnd) {
                                        FLAG_SAVING_SCREEN_EVENT = true; // 让传感器值写入缓冲队列
                                        ScreenEvent screenEvent = new ScreenEvent(
                                                codeHandler.getNodeList(), sensorHandler.getAcceleratorQueue(), sensorHandler.getGyroscopeQueue(),
                                                FileUtils.getCurrentActivityName(ctx), dir,true);
                                        FLAG_SAVING_SCREEN_EVENT = false; // 将传感器值写入生成队列

                                        if (screenEvent.judge()) {
                                            try {
                                                Log.i(TAG,"distinguished as master");
                                                ////todo 是否保存数据??
                                                //screenEvent.save(); // 保存主人数据到文件
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Log.e(TAG,"distinguished as guest");
                                            EventBus.getDefault().post(new AlertEvent(true));
                                            Judger.getInstance().reset();
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


    public void setType(String type) {
        this.accountType = type;
    }

    public String getType() {
        return this.accountType;
    }
}
