package com.lowhot.cody.movement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lowhot.cody.movement.utils.eventBus.MonitorEvent;
import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.eventBus.RadioButtonEvent;

import org.greenrobot.eventbus.EventBus;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setSupportActionBar(toolbar);
        activity = this;
        initListener();

        Intent serviceIntent = new Intent(MainActivity.this, EventService.class);
        startService(serviceIntent);
    }

    public void initView() {
        btnStart = (Button) findViewById(R.id.btn_monitor_start);
        btnStop = (Button) findViewById(R.id.btn_monitor_stop);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rg = (RadioGroup) findViewById(R.id.id_rg);
    }

    public void initListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own function", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_bMonitorOn = true;
                EventBus.getDefault().post(new MonitorEvent(1));
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_bMonitorOn = false;
                EventBus.getDefault().post(new MonitorEvent(0));
            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.id_rb_guest:
                        EventBus.getDefault().post(new RadioButtonEvent("guest"));
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


}
