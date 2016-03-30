package com.lowhot.cody.movement;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lowhot.cody.movement.bean.Config;
import com.lowhot.cody.movement.svm.src.svm_main;
import com.lowhot.cody.movement.utils.ErrorAlertDialogUtil;
import com.lowhot.cody.movement.utils.FileUtils;
import com.lowhot.cody.movement.utils.ProgressDialogUtil;
import com.lowhot.cody.movement.utils.ToastUtils;
import com.lowhot.cody.movement.utils.eventBus.MonitorEvent;
import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.eventBus.RadioButtonEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";

    Events events = new Events();
    boolean m_bMonitorOn = false;                // used in the thread to poll for input event node  messages
    private Button btnStart;
    private Button btnStop;
    public Activity activity;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private RadioGroup rg;
    private EditText name;
    private RadioButton rb_master;
    private RadioButton rb_guest;
    private Button btnTrain;
    Boolean isMaster=true;
    String str_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        init();
        initView();
        initListener();
        Intent serviceIntent = new Intent(MainActivity.this, EventService.class);
        startService(serviceIntent);


    }

    private void init() {
        ToastUtils.register(this);
        FileUtils.register(this);
    }

    public void initView() {
        btnStart = (Button) findViewById(R.id.btn_monitor_start);
        btnStop = (Button) findViewById(R.id.btn_monitor_stop);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rg = (RadioGroup) findViewById(R.id.id_rg);
        name = (EditText)findViewById(R.id.id_et_name);
        rb_master=(RadioButton)findViewById(R.id.id_rb_master);
        rb_guest=(RadioButton)findViewById(R.id.id_rb_guest);
        btnTrain=(Button)findViewById(R.id.btn_train);
    }

    public void initListener() {

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_bMonitorOn = true;
                str_name = name.getText().toString();
                Log.e(TAG, str_name);
                if(str_name.length()==0 && isMaster == false){
                    ErrorAlertDialogUtil.showErrorDialog(activity, "请输入客人名字",null);
                }else{
                    EventBus.getDefault().post(new MonitorEvent(1));
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_bMonitorOn = false;
                EventBus.getDefault().post(new MonitorEvent(0));
            }
        });

        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 训练模型
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "App destroyed.");
        Intent intent = new Intent(getApplicationContext(), EventService.class);
        stopService(intent);
        events.Release();
    }


    class TrainTask extends AsyncTask{

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
                File root  = new File(FileUtils.BASE_TRAIN_DIR);
                File configFile = new File(FileUtils.CONFIG_PATH);
                File[] files = root.listFiles();
                for (File f : files){
                    if(f.isFile()){
                        //先判断app.config中是否有该appName的条目，如果没有则新建一个默认配置条目。
                        String name = f.getName();
                        Config config = null;
                        Boolean isExisted = false;
                        InputStreamReader read = new InputStreamReader(
                                new FileInputStream(configFile)
                        );
                        BufferedReader bufferedReader = new BufferedReader(read);
                        String line = null;
                        while ((line = bufferedReader.readLine())!=null){
                            Log.i("File Context",line);
                            String[] argsList = line.split(",");
                            Log.i("list",String.valueOf(argsList.length));
                            if (argsList.length !=6){
                                Log.e("File Context","argsList not match");
                                break;
                            }
                            if (argsList[0].equals(name)){
                                isExisted = true;
                                int s= Integer.parseInt(argsList[1]);
                                int t= Integer.parseInt(argsList[2]);
                                double g=Double.parseDouble(argsList[3]);
                                int r= Integer.parseInt(argsList[4]);
                                double n=Double.parseDouble(argsList[5]);
                                config = new Config(name,s,t,g,r,n);
                                m.train(config);
                                break;
                            }
                        }
                        read.close();
                        if(!isExisted){
                            config = new Config(f.getName());
                            FileUtils.saveConfig(config);
                            m.train(config);
                        }

                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ProgressDialogUtil.progressDialogDismiss();
            ToastUtils.showShort("训练完成");
        }
    }

}
