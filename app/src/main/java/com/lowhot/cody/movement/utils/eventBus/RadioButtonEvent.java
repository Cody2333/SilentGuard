package com.lowhot.cody.movement.utils.eventBus;

import android.util.Log;

/**
 * Created by cody_local on 2016/3/17.
 */
public class RadioButtonEvent {
    public String type;

    public RadioButtonEvent(String type) {
        Log.i("RadioButtonEvent",type);
        this.type = type;
    }
}
