package com.lowhot.cody.movement.utils.eventBus;

/**
 * Created by cody_local on 2016/5/24.
 */
public class PredictEvent {
    //flag : 1为start  0为stop
    public int flag;
    public PredictEvent(int flag) {
        this.flag = flag;
    }
}
