package com.lowhot.cody.movement.model.trace;


import com.lowhot.cody.movement.entity.SgTrace;
import com.lowhot.cody.movement.utils.DtwUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者: chengfeng
 * 创建时间： 2016-04-24 下午3:40
 * 任务号：
 * 描述：处理轨迹相关逻辑，如轨迹匹配，轨迹选择
 */
public class TraceDao implements ITraceDao {

    private static final double DISTANCE_THRESHOLD = 100;

    @Override
    public boolean isTraceAlike(SgTrace trace1, SgTrace trace2) {
        ArrayList<double[]> trace1List = (ArrayList<double[]>) trace1.getNodeList().getCoordinateList();
        ArrayList<double[]> trace2List = (ArrayList<double[]>) trace2.getNodeList().getCoordinateList();
        double distance = DtwUtils.dtwDistance(trace1List,trace2List);
        return distance < DISTANCE_THRESHOLD;
    }

    @Override
    public SgTrace matchTrace(SgTrace trace) {
        return null;
    }

    @Override
    public List<SgTrace> selectTraceToCompare(SgTrace trace) {
        return null;
    }

    @Override
    public SgTrace getTraceById(int id) {
        return null;
    }

    @Override
    public List<SgTrace> getNextTrace(int id) {
        return null;
    }
}
