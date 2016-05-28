package com.lowhot.cody.movement.utils.eventBus;

/**
 * Created by cody_local on 2016/5/28.
 */
public class LogEvent {
    String logInfo;

    public LogEvent(String logInfo) {
        this.logInfo = logInfo+"\n";
    }

    public String getLogInfo() {
        return logInfo;
    }

}
