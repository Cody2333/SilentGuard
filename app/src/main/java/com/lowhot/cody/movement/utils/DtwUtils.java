package com.lowhot.cody.movement.utils;

import com.dtw.FastDTW;
import com.dtw.TimeWarpInfo;
import com.timeseries.TimeSeries;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者: chengfeng
 * 创建时间： 2016-05-02 下午1:38
 * 任务号：
 * 描述：计算dtw距离
 */
public class DtwUtils {

    private static final Integer WINDOW = 10;

    /**
     * 计算trace2与traceTemplate的dtw距离
     * @param traceTemplate 模板
     * @param trace2 待比较的轨迹
     * @return 两个轨迹的dtw距离
     */
    public static double dtwDistance(List<double[]> traceTemplate, List<double[]> trace2){
        final TimeSeries tsI = new TimeSeries((ArrayList<double[]>) traceTemplate);
        final TimeSeries tsJ = new TimeSeries((ArrayList<double[]>) trace2);
        final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
        final TimeWarpInfo info = FastDTW.getWarpInfoBetween(tsI, tsJ, WINDOW, distFn);
        return info.getDistance()/info.getPath().size();
    }
}
