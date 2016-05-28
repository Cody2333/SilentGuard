package com.lowhot.cody.movement.model.trace;

import com.lowhot.cody.movement.entity.SgTrace;

import java.util.List;

/**
 * 创建者: chengfeng
 * 创建时间： 2016-04-24 下午3:41
 * 任务号：
 * 描述：
 */
public interface ITraceDao {

    /**
     * 计算两个轨迹的距离
     * @param trace1 轨迹1
     * @param trace2 轨迹2
     * @return 距离
     */
    double traceDis(SgTrace trace1, SgTrace trace2);

    /**
     * 挑出需要比较的轨迹
     * @param trace   轨迹
     * @return 需要被比较的轨迹
     */
    List<SgTrace> selectTraceToCompare(SgTrace trace);

    /**
     * 根据id获取轨迹
     * @param id id
     * @return 轨迹
     */
    SgTrace getTraceById(int id);

    /**
     * 获取下一个轨迹
     * @param id    id
     * @return  下一个轨迹
     */
    List<SgTrace> getNextTrace(int id);


}
