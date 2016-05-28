package com.lowhot.cody.movement.entity;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lowhot.cody.movement.bean.InputEvent;
import com.lowhot.cody.movement.utils.CommonUtil;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.IOException;

/**
 * Created by cody_local on 2016/4/20.
 */
public class SgTrace extends SugarRecord {

    @Ignore
    private static final String TAG = "sgTrace";
    String appId;
    //InputEvent 序列化字符串
    String traceData;

    public SgTrace() {
    }

    public SgTrace( String appId, InputEvent inputEvent) throws JsonProcessingException{
        this.appId = appId;
        this.traceData = CommonUtil.getObjectMapper().writeValueAsString(inputEvent);
    }

    public InputEvent getInputEvent(){
        InputEvent inputEvent = null;
        try {
            inputEvent = CommonUtil.getObjectMapper().readValue(traceData,InputEvent.class);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return inputEvent;
    }
}
