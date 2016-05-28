package com.lowhot.cody.movement.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lowhot.cody.movement.utils.CommonUtil;
import com.lowhot.cody.movement.utils.Constants;
import com.orm.SugarRecord;

import java.io.IOException;
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
    int traceId = 0;

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

    /**
     * 斜率
     */
    double slope = 0;

    long length = 0;
    long duration = 0;
    int matchTimes = 0;

    public SgTraceInfo() {
    }

    public SgTraceInfo(String appId,int traceId,List<Integer> nextTraceIds, String type, String method, double slope,long length, long duration, int matchTimes) throws JsonProcessingException {
        this.appId = appId;
        this.traceId = traceId;
        this.nextTraceIds = CommonUtil.getObjectMapper().writeValueAsString(nextTraceIds);
        this.type = type;
        this.method = method;
        this.slope = slope;
        this.length = length;
        this.duration = duration;
        this.matchTimes = matchTimes;
    }

    /**
     * 得到当前轨迹的下一个轨迹
     * @return 轨迹
     */
    public List<Integer> getNextTraceIds(){
        try {
            return CommonUtil.getObjectMapper().readValue(nextTraceIds, new TypeReference<List<Integer>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getTraceId() {
        return traceId;
    }

    public void setTraceId(int traceId) {
        this.traceId = traceId;
    }

    public void setNextTraceIds(String nextTraceIds) {
        this.nextTraceIds = nextTraceIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getMatchTimes() {
        return matchTimes;
    }

    public void setMatchTimes(int matchTimes) {
        this.matchTimes = matchTimes;
    }
}
