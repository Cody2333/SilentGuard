package com.lowhot.cody.movement.utils.eventBus;

import android.util.Log;

/**
 * Created by cody_local on 2016/5/28.
 */
public class AlertEvent {
    public Boolean alert;

    public AlertEvent(Boolean alert) {
        Log.e("alert event","true");
        this.alert = alert;
    }
}
