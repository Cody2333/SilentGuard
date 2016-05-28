package com.lowhot.cody.movement;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import com.lowhot.cody.movement.model.ScreenHandler;
import com.lowhot.cody.movement.model.SensorHandler;
import com.lowhot.cody.movement.utils.eventBus.AlertEvent;
import com.lowhot.cody.movement.utils.eventBus.LogEvent;
import com.lowhot.cody.movement.utils.eventBus.MonitorEvent;
import com.lowhot.cody.movement.utils.eventBus.PredictEvent;
import com.lowhot.cody.movement.utils.eventBus.RadioButtonEvent;
import com.lowhot.cody.movement.utils.ui.AlertDialogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressLint("ShowToast")
public class EventService extends Service implements SensorEventListener {
    private static final String TAG = "EventService";

    private SensorManager sm;
    private SensorHandler sensorHandler;
    private ScreenHandler screenHandler;
    private AlertDialogUtils alertDialogUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        Toast.makeText(getApplicationContext(), "serviceStart", Toast.LENGTH_SHORT).show();
        sensorHandler = new SensorHandler();
        screenHandler = new ScreenHandler(getApplicationContext(), sensorHandler);
        alertDialogUtils = new AlertDialogUtils(getApplicationContext());
        // 打开设备,监听键盘
        //screenHandler.openDev();
        EventBus.getDefault().post(new LogEvent("Event Service initlized"));
        screenHandler.StartEventMonitor();
        // 监听传感器
        initSensor();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAlertEvent(AlertEvent event) {
        if(event.alert){
            new AlertDialogUtils(getApplicationContext()).showAlertDialog();
        }
    }
    @Subscribe
    public void onPredictEvent(PredictEvent event) {
        if (event.flag == 0) {
            screenHandler.stopPredictMonitor();
        } else if (event.flag == 1) {
            screenHandler.continuePredictMonitor();
        }
    }

    @Subscribe
    public void onMonitorEvent(MonitorEvent event) {
        if (event.flag == 0) {
            screenHandler.stopEventMonitor();
        } else if (event.flag == 1) {
            screenHandler.continueMonitor();
        }
    }

    @Subscribe
    public void onRadioButtonEvent(RadioButtonEvent event) {
            screenHandler.setType(event.type);
    }


    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    public void initSensor() {
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 注册加速度传感器
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        // 注册陀螺仪传感器
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorHandler.addAccleratorData(event.values[0], event.values[1], event.values[2], screenHandler.isFLAG_SAVING_SCREEN_EVENT());
                //Log.e("Sensor:",String.valueOf(event.timestamp));
                break;
            case Sensor.TYPE_GYROSCOPE:
                sensorHandler.addGyroscopeData(event.values[0], event.values[1], event.values[2], screenHandler.isFLAG_SAVING_SCREEN_EVENT());
                break;
            default:
                break;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


}
