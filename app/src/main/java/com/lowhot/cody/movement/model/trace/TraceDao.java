package com.lowhot.cody.movement.model.trace;


import com.lowhot.cody.movement.bean.InputEvent;
import com.lowhot.cody.movement.entity.SgTrace;
import com.lowhot.cody.movement.entity.SgTraceInfo;
import com.lowhot.cody.movement.utils.DtwUtils;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者: chengfeng
 * 创建时间： 2016-04-24 下午3:40
 * 任务号：
 * 描述：处理轨迹相关逻辑，如轨迹匹配，轨迹选择
 */
public class TraceDao implements ITraceDao {

    private static final int slopeOffSet = 10;
    private static final int durationOffSet = 10;
    private static final int lengthOffset = 3;


    @Override
    public double traceDis(SgTrace trace1, SgTrace trace2) {
        return DtwUtils.dtwDistance(trace1.getInputEvent().getRelCoordinateList(),trace2.getInputEvent().getRelCoordinateList());
    }

    public List<SgTrace> selectTraceToCompare(SgTrace trace) {

        InputEvent inputEvent = trace.getInputEvent();
        ArrayList<SgTraceInfo> sgTraceInfos = (ArrayList<SgTraceInfo>) Select.from(SgTraceInfo.class)
                .where(Condition.prop("appId").eq(inputEvent.getAppId()),
                        Condition.prop("type").eq(inputEvent.getType()),
                        Condition.prop("method").eq(inputEvent.getMethod()),
                        Condition.prop("slope").gt(inputEvent.getSlope() - slopeOffSet).lt(inputEvent.getSlope() + slopeOffSet),
                        Condition.prop("duration").gt(inputEvent.getDuringTime() - durationOffSet).lt(inputEvent.getDuringTime() +durationOffSet),
                        Condition.prop("length").gt(inputEvent.getLength()-lengthOffset).lt(inputEvent.getLength()+lengthOffset)
                        )
                .orderBy("matchTimes")
                .list();
        if (null == sgTraceInfos){
            return null;
        }else {
            List<SgTrace> sgTraces = new ArrayList<>();
            for(SgTraceInfo sgTraceInfo:sgTraceInfos){
                sgTraces.add(getTraceById( sgTraceInfo.getTraceId()));
            }
            return sgTraces;
        }
    }

    @Override
    public final SgTrace getTraceById(int id) {
       return SgTrace.findById(SgTrace.class,id);
    }

    @Override
    public List<SgTrace> getNextTrace(int id) {
        SgTraceInfo sgTraceInfo = SgTraceInfo.findById(SgTraceInfo.class,id);
        if(null == sgTraceInfo){
            return null;
        }else {
           List<Integer> traceIds = sgTraceInfo.getNextTraceIds();
            if(null == traceIds){
                return null;
            }
            else {
                List<SgTrace> sgTraces = new ArrayList<>();
                for (Integer traceId: traceIds){
                    sgTraces.add(SgTrace.findById(SgTrace.class,traceId));
                }
                return sgTraces;
            }
        }
    }
}
