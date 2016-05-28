package com.lowhot.cody.movement;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lowhot.cody.movement.bean.Config;
import com.lowhot.cody.movement.svm.src.svm_main;
import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.FileUtils;
import com.lowhot.cody.movement.utils.eventBus.LogEvent;
import com.lowhot.cody.movement.utils.eventBus.MonitorEvent;
import com.lowhot.cody.movement.utils.eventBus.PredictEvent;
import com.lowhot.cody.movement.utils.eventBus.RadioButtonEvent;
import com.lowhot.cody.movement.utils.ui.ErrorAlertDialogUtil;
import com.lowhot.cody.movement.utils.ui.ProgressDialogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";

    Events events = new Events();
    boolean m_bMonitorOn = false;                // used in the thread to poll for input event node  messages
    boolean predictOn = false;
    @Bind(R.id.btn_monitor_start)
    Button btnStart;
    @Bind(R.id.btn_monitor_stop)
    Button btnStop;
    @Bind(R.id.id_rg)
    RadioGroup rg;
    @Bind(R.id.id_et_name)
    EditText name;
    @Bind(R.id.id_rb_master)
    RadioButton rb_master;
    @Bind(R.id.id_rb_guest)
    RadioButton rb_guest;
    @Bind(R.id.btn_train)
    Button btnTrain;
    @Bind(R.id.btn_predict_start)
    Button btnPredictStart;
    @Bind(R.id.btn_predict_stop)
    Button btnPredictStop;
    @Bind(R.id.tv_log)
    TextView tvLog;
    Boolean isMaster = true;
    String str_name;
    public Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        activity = this;
        initListener();
        Intent serviceIntent = new Intent(MainActivity.this, EventService.class);
        startService(serviceIntent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogEvent(LogEvent event) {
        tvLog.append(event.getLogInfo());
    }
    public void initListener() {

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new LogEvent("running at collecting mode"));
                m_bMonitorOn = true;
                str_name = name.getText().toString();
                Log.e(TAG, str_name);
                if (str_name.length() == 0 && isMaster == false) {
                    ErrorAlertDialogUtil.showErrorDialog(activity, "请输入客人名字", null);
                } else {
                    EventBus.getDefault().post(new MonitorEvent(1));
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new LogEvent("collecting mode stopped"));
                m_bMonitorOn = false;
                EventBus.getDefault().post(new MonitorEvent(0));
            }
        });

        btnPredictStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new LogEvent("running at monitoring mode"));
                if (predictOn == false) {
                    EventBus.getDefault().post(new PredictEvent(1));
                    predictOn = true;
                }
            }
        });
        btnPredictStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new LogEvent("monitoring mode stopped"));
                if (predictOn) {
                    EventBus.getDefault().post(new PredictEvent(0));
                    predictOn = false;
                }
            }
        });
        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 训练模型
                EventBus.getDefault().post(new LogEvent("===== training model ====="));
                TrainTask task = new TrainTask();
                task.execute();

            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.id_rb_guest:
                        str_name = name.getText().toString();
                        if (str_name.length() != 0) {
                            EventBus.getDefault().post(new RadioButtonEvent("guest/" + str_name));
                        } else {
                            ErrorAlertDialogUtil.showErrorDialog(activity, "请输入客人名字", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    rb_master.setChecked(true);
                                }
                            });

                        }
                        break;
                    case R.id.id_rb_master:
                        EventBus.getDefault().post(new RadioButtonEvent("master"));
                        break;

                }

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "App destroyed.");
        Intent intent = new Intent(getApplicationContext(), EventService.class);
        stopService(intent);
        events.Release();
    }


    class TrainTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.progressDialogShow(activity);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            svm_main m = new svm_main();
            try {
                //遍历master文件夹中的数据文件进行训练生成模型
                File root = new File(FileUtils.BASE_TRAIN_DIR);
                File configFile = new File(FileUtils.CONFIG_PATH);
                File[] files = root.listFiles();
                for (File f : files) {
                    if (f.isFile()) {
                        //先判断app.config中是否有该appName的条目，如果没有则新建一个默认配置条目。
                        String name = f.getName();
                        Config config = null;
                        Boolean isExisted = false;
                        InputStreamReader read = new InputStreamReader(
                                new FileInputStream(configFile)
                        );
                        BufferedReader bufferedReader = new BufferedReader(read);
                        String line = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            Log.i("File Context", line);
                            String[] argsList = line.split(",");
                            if (argsList.length != 6) {
                                Log.e("File Context", "argsList not match");
                                break;
                            }
                            if (argsList[0].equals(name)) {
                                isExisted = true;
                                int s = Integer.parseInt(argsList[1]);
                                int t = Integer.parseInt(argsList[2]);
                                double g = Double.parseDouble(argsList[3]);
                                int r = Integer.parseInt(argsList[4]);
                                double n = Double.parseDouble(argsList[5]);
                                config = new Config(name, s, t, g, r, n);
                                m.train(config);
                                break;
                            }
                        }
                        read.close();
                        if (!isExisted) {
                            config = new Config(f.getName());
                            FileUtils.saveConfig(config);
                            m.train(config);
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ProgressDialogUtil.progressDialogDismiss();
            EventBus.getDefault().post(new LogEvent("training model finished"));
        }
    }

}
