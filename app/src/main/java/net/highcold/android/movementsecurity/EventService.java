package net.highcold.android.movementsecurity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import net.highcold.android.movementsecurity.Events.InputDevice;
import net.highcold.android.utils.AcceleratorValue;
import net.highcold.android.utils.GyroscopeValue;
import net.highcold.android.utils.Node;
import net.highcold.android.utils.ScreenEvent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class EventService extends Service implements SensorEventListener {
    private static final String TAG = "EventService";
    private IBinder myBinder = new MyBinder();
    private SensorManager sm;
    Events events = new Events();
    Builder builder;
    Dialog dialog;
    Intent i;

    boolean flag = false;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        // return myBinder;
        return null;
    }

    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        i = new Intent(getBaseContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        builder = new Builder(getApplicationContext());
        builder.setTitle("Alert");
        builder.setMessage("You are not the owner!");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    public void onDestory() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "ExampleService-onStart");
        int res = events.Init();
        // debug results
        Log.d(TAG, "Event files:" + res);
        Events.intEnableDebug(1);
        // 监听键盘
        StartEventMonitor();
        // 监听传感器
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 注册加速度传感器
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        // 注册陀螺仪传感器
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);
    }

    public class MyBinder extends Binder {
        EventService getService() {
            return EventService.this;
        }
    }

    public void StartEventMonitor() {
        Toast.makeText(getApplicationContext(), "serviceStart",
                Toast.LENGTH_SHORT).show();

        // 打开所有设备
        for (InputDevice idev : events.m_Devs) {
            if (idev.Open(true)) {
                Toast.makeText(getApplicationContext(),
                        "Device opened successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Device failed to open. Do you have root?",
                        Toast.LENGTH_SHORT).show();
            }
        }

        Thread b = new Thread(new Runnable() {

            public void run() {
                File outFile = new File("/sdcard/eventInject.txt");
                File originalScreenDataFile = new File(
                        "/sdcard/eventInjectOrignial.txt");

                if (!outFile.exists()) {
                    try {
                        Log.i(TAG, "create file");
                        outFile.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (!originalScreenDataFile.exists()) {
                    try {
                        Log.i(TAG, "create file");
                        originalScreenDataFile.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                int type, code, value;
                boolean hasPressure = false;
                Node temp = new Node();
                while (true) {
                    try {
                        for (InputDevice idev : events.m_Devs) {
                            // Open more devices to see their messages
                            if (idev.getOpen() && (0 == idev.getPollingEvent())) {

                                long currentTimeMillis = System
                                        .currentTimeMillis(); // 当前时间戳

                                type = idev.getSuccessfulPollingType();
                                code = idev.getSuccessfulPollingCode();
                                value = idev.getSuccessfulPollingValue();

                                final String line = idev.getName() + ":"
                                        + type + " "
                                        + code + " "
                                        + value
                                        + " timestamp:" + currentTimeMillis
                                        + " appName:"
                                        + getCurrentActivityName();
                                Log.i("data", line);
                                BufferedWriter bufferedWriter = new BufferedWriter(
                                        new FileWriter(originalScreenDataFile,
                                                true));
                                bufferedWriter.write(line);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                bufferedWriter.close();

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        b.start();
    }

    /**
     * 获取 当前的activity名称
     */
    private String getCurrentActivityName() {
        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }


    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        File outFile = new File("/sdcard/eventInjectSensor.txt");
        if (!outFile.exists()) {
            try {
                Log.i(TAG, "create file");
                outFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        long currentTimeMillis = System.currentTimeMillis(); // 当前时间戳
        String line = "";
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                line = "Accelerometer: " + event.values[0] + ", " + event.values[1]
                        + ", " + event.values[2] + " timestamp:"
                        + currentTimeMillis;

                double acce = event.values[0] * event.values[0] + event.values[1]
                        * event.values[1] + event.values[2] * event.values[2];
                acce = Math.sqrt(acce);
                break;
            case Sensor.TYPE_GYROSCOPE:
                line = "Gyroscope: " + event.values[0] + ", " + event.values[1]
                        + ", " + event.values[2] + " timestamp:"
                        + currentTimeMillis;
                double gyro = event.values[0] * event.values[0] + event.values[1]
                        * event.values[1] + event.values[2] * event.values[2];
                gyro = Math.sqrt(gyro);
                break;
            default:
                break;
        }

        try {
            // Log.i("data", line);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                    outFile, true));
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    private void showBox() {
        dialog.show();
    }

}
