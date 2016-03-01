/*
 * Android Event Injector 
 *
 * Copyright (c) 2013 by Radu Motisan , radu.motisan@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * For more information on the GPL, please go to:
 * http://www.gnu.org/copyleft/gpl.html
 *
 */

package net.highcold.android.movementsecurity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity {
    final static String LT = "MainActivity";

    Events events = new Events();
    boolean m_bMonitorOn = false;                // used in the thread to poll for input event node  messages
    private Button btnStart;
    private Button btnStop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LT, "App created.");

        setContentView(R.layout.layout);
        btnStart = (Button) findViewById(R.id.btn_monitor_start);
        btnStop = (Button) findViewById(R.id.btn_monitor_stop);
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_bMonitorOn)
                    Toast.makeText(getApplicationContext(), "Event monitor already working. Consider opening more devices to monitor.", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(MainActivity.this, EventService.class);
                startService(serviceIntent);
            }
        });
        btnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EventService.class);
                stopService(intent);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LT, "App destroyed.");
        StopEventMonitor();
        events.Release();
    }


    public void StopEventMonitor() {
        m_bMonitorOn = false; //stop reading thread
    }

}
