package com.lowhot.cody.movement.utils.eventBus;

/**
 * Created by cody_local on 2016/3/12.
 */
public class MonitorEvent {
    //flag : 1为start  0为stop
    public int flag;

    public MonitorEvent(int flag) {
        this.flag = flag;
    }
}
