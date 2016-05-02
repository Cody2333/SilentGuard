package com.lowhot.cody.movement.entity;

import com.lowhot.cody.movement.utils.Constants;
import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by cody_local on 2016/4/20.
 */
public class SgTraceInfo extends SugarRecord {
    /**
     * app名称
     */
    String appId = Constants.DEFAULT;
    /**
     * 轨迹id
     */
    long traceId = 0;

    /**
     * 下一个traceIds
     */
    String nextTraceIds;

    /**
     * 轨迹类型
     */
    String type = Constants.TYPE_UNKNOW;
    /**
     * 轨迹方向：向左，向右，向上，向下
     */
    String method = Constants.METHOD_UNKNOW;
    long length = 0;
    long duration = 0;
    int matchTimes = 0;

    public SgTraceInfo() {
    }

    public SgTraceInfo(String appId,long traceId,String nextTraceIds, String type, String method, long length, long duration, int matchTimes) {
        this.appId = appId;
        this.traceId = traceId;
        this.nextTraceIds = nextTraceIds;
        this.type = type;
        this.method = method;
        this.length = length;
        this.duration = duration;
        this.matchTimes = matchTimes;
    }

    /**
     * 得到当前轨迹的下一个轨迹
     * @return 轨迹
     */
    public List<SgTrace> getNextTraces(){
        //todo
        return null;
    }
}
