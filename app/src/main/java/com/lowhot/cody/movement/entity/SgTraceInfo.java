package com.lowhot.cody.movement.entity;

import com.lowhot.cody.movement.utils.Constants;
import com.orm.SugarRecord;

/**
 * Created by cody_local on 2016/4/20.
 */
public class SgTraceInfo extends SugarRecord {
    String appId = Constants.DEFAULT;
    String type = Constants.TYPE_UNKNOW;
    String method = Constants.METHOD_UNKNOW;
    long length = 0;
    long duration = 0;
    int matchTimes = 0;

    public SgTraceInfo() {
    }

    public SgTraceInfo(String appId, String type, String method, long length, long duration, int matchTimes) {
        this.appId = appId;
        this.type = type;
        this.method = method;
        this.length = length;
        this.duration = duration;
        this.matchTimes = matchTimes;
    }
}
