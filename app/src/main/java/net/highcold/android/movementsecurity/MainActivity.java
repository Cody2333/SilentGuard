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

import java.util.ArrayList;

import net.highcold.android.movementsecurity.Events.InputDevice;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener {
	final static String LT 					= "MainActivity";
	
	Events 				events 				= new Events();
	boolean 			m_bMonitorOn 		= false; 				// used in the thread to poll for input event node  messages
	
	// interface view ids
	final static int 	idButScan 			= Menu.FIRST + 1001,
						idLVDevices 		= Menu.FIRST + 1002,
						idSelSpin 			= Menu.FIRST + 1003, 
						idButInjectKey 		= Menu.FIRST + 1004,
						idButInjectTouch	= Menu.FIRST + 1005,
						idButMonitorStart	= Menu.FIRST + 1006,
						idButMonitorStop	= Menu.FIRST + 1007,
						idButTest			= Menu.FIRST + 1008,
						idLVFirstItem 		= Menu.FIRST + 5000;
	// interface views
	TextView			m_tvMonitor; 								// used to display monitored events, in the format code-type-value. See input.h in the NDK
	ListView			m_lvDevices;								// the listview showing devices found
	Spinner 			m_selDevSpinner; 							// The spinner is used to select a target for the Key and Touch buttons
	int					m_selectedDev		= -1;					// index of spinner selected device, or -1 is no selection
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LT, "App created.");
        
     // create a basic user interface
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        setContentView(panel);
        
        Button b = new Button(this);
		b.setText("开始监视");
		b.setId(idButMonitorStart);
		b.setOnClickListener(this);
		panel.addView(b);
		
		
     	
        
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d(LT, "App destroyed.");
    	StopEventMonitor();
    	events.Release();
    }
    
	public void onClick(View v) {
		int id = v.getId();
		
		switch (id) {
			case idButMonitorStart:
				if (m_bMonitorOn)
					Toast.makeText(this, "Event monitor already working. Consider opening more devices to monitor.", Toast.LENGTH_SHORT).show();
					Intent serviceIntent = new Intent(MainActivity.this,EventService.class);
					startService(serviceIntent);
					break;
					//StartEventMonitor();
			case idButMonitorStop:
				Intent intent = new Intent(this, EventService.class);
				stopService(intent);
				break;
				}
			
			
		}
	
	/**
	 * Handle events when the user selects a new item in the spinner. 
	 * The spinner is used to select a target for the Key and Touch buttons
	 * So what we do here, is to find which open-dev has been selected from within our global events.m_Devs structure
	 * result saved in m_selectedDev, as the index of our selected open dev.
	 */
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		int sel = arg2;
		
		int i = 0, j = 0;
		m_selectedDev = -1;
		for (InputDevice idev:events.m_Devs) {
			if (idev.getOpen()) {
				if (i == sel) { m_selectedDev = j; break; }
				else
					i++;
			}
			j ++;
		}
		if (m_selectedDev != -1) {
			String name = events.m_Devs.get(m_selectedDev).getName();
			Log.d(LT, "spinner selected:"+sel+ " Name:"+name);
			Toast.makeText(this, "New device selected:"+name, Toast.LENGTH_SHORT).show();
		} else
			Toast.makeText(this, "Invalid device selection!", Toast.LENGTH_SHORT).show();
		
	}
	
	// not used
	public void onNothingSelected(AdapterView<?> arg0) {}
	
	
	
	/**
	 * Stops our event monitor thread
	 */
	public void StopEventMonitor() {
		m_bMonitorOn = false; //stop reading thread
	}
	
	
	/**
	 * Starts our event monitor thread that does the data extraction via polling
	 * all data is displayed in the textview, as type-code-value, see input.h in the Android NDK for more details
	 * Monitor output is also sent to Logcat, so make sure you used that as well
	 */
	public void StartEventMonitor() {
		m_bMonitorOn = true;
		Thread b = new Thread(new Runnable() {
			public void run() {
				while (m_bMonitorOn) {
					for (InputDevice idev:events.m_Devs) {
						// Open more devices to see their messages
						if (idev.getOpen() && (0 == idev.getPollingEvent())) {
							final String line = idev.getName()+
									":" + idev.getSuccessfulPollingType()+
									" " + idev.getSuccessfulPollingCode() + 
									" " + idev.getSuccessfulPollingValue();
							//Log.d(LT, "Event:"+line);
							Log.d("type", line);
							//m_tvMonitor.setText(line);
							// update textview to show data
							//if (idev.getSuccessfulPollingValue() != 0)
							m_tvMonitor.post(new Runnable() {
								public void run() {
									m_tvMonitor.append(line+"||");
								}
							});
						}
						
					}
				}
			}
		});
		b.start();    
	}

	/**
	 * Finds an open device that has a name containing keypad. This probably is the event node associated with the keypad
	 * Its purpose is to handle all hardware Android buttons such as Back, Home, Volume, etc
	 * Key codes are defined in input.h (see NDK) , or use the Event Monitor to see keypad messages
	 * This function sends the Settings key 
	 */
	public void SendSettingsKeyToKeypad() {
		for (InputDevice idev:events.m_Devs) {
			//* Finds an open device that has a name containing keypad. This probably is the keypad associated event node
			if (idev.getOpen() && idev.getName().contains("keypad")) {
				idev.SendKey(139, true); // settings key down
				idev.SendKey(139, false); // settings key up
			}
		}
	}
	/**
	 * Finds an open device that has a name containing keypad. This probably is the event node associated with the keypad
	 * Its purpose is to handle all hardware Android buttons such as Back, Home, Volume, etc
	 * Key codes are defined in input.h (see NDK) , or use the Event Monitor to see keypad messages
	 * This function sends the HOME key 
	 */
	public void SendHomeKeyToKeypad() {
		boolean found = false;
		for (InputDevice idev:events.m_Devs) {
			//* Finds an open device that has a name containing keypad. This probably is the keypad associated event node
			if (idev.getOpen() && idev.getName().contains("keypad")) {
				idev.SendKey(102, true); // home key down
				idev.SendKey(102, false); // home key up
				found  = true; break;
			}
		}
		if (found == false)
			Toast.makeText(this, "Keypad not found.", Toast.LENGTH_SHORT).show();
	}
	/**
	 * Finds an open device that has a name containing keypad. This probably is the event node associated with the keypad
	 * Its purpose is to handle all hardware Android buttons such as Back, Home, Volume, etc
	 * Key codes are defined in input.h (see NDK) , or use the Event Monitor to see keypad messages
	 * This function sends the BACK key 
	 */
	public void SendBackKeyToKeypad() {
		for (InputDevice idev:events.m_Devs) {
			//* Finds an open device that has a name containing keypad. This probably is the keypad associated event node
			if (idev.getOpen() && idev.getName().contains("keypad")) {
				idev.SendKey(158, true); // Back key down
				idev.SendKey(158, false); // back key up
			}
		}
	}
}
