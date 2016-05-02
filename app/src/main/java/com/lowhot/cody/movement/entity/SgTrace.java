package com.lowhot.cody.movement.entity;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lowhot.cody.movement.bean.NodeList;
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
    //NodeList 序列化字符串
    String traceData;

    public SgTrace() {
    }

    public SgTrace( String appId, NodeList nodeList) throws JsonProcessingException{
        this.appId = appId;
        this.traceData = CommonUtil.getObjectMapper().writeValueAsString(nodeList);
    }

    public NodeList getNodeList(){
        NodeList nodeList = null;
        try {
            nodeList = CommonUtil.getObjectMapper().readValue(traceData,NodeList.class);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return nodeList;
    }
}
