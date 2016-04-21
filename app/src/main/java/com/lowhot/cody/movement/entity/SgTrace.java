package com.lowhot.cody.movement.entity;

import com.orm.SugarRecord;

/**
 * Created by cody_local on 2016/4/20.
 */
public class SgTrace extends SugarRecord {
    String appId;
    String traceData;
    long beginTimestamp = 0;
    long endTimestamp = 0;

    public SgTrace() {
    }

    public SgTrace( String appId, String values, long beginTimestamp, long endTimestamp) {

        this.appId = appId;
        this.traceData = values;
        this.beginTimestamp = beginTimestamp;
        this.endTimestamp = endTimestamp;
    }
}
