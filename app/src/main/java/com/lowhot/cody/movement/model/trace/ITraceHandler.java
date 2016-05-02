package com.lowhot.cody.movement.model.trace;

import com.lowhot.cody.movement.bean.NodeList;

import java.util.List;

/**
 * 创建者: chengfeng
 * 创建时间： 2016-04-24 下午3:41
 * 任务号：
 * 描述：
 */
public interface ITraceHandler {

    /**
     * 比较两个轨迹是否相似
     * @param trace1 轨迹1
     * @param trace2 轨迹2
     * @return 相似返回true,否则false
     */
    boolean isTraceAlike(NodeList trace1, NodeList trace2);

    /**
     * 挑出需要比较的轨迹
     * @param trace   轨迹
     * @return 需要被比较的轨迹
     */
    List<NodeList> selectTraceToCompare(NodeList trace);

    /**
     * 根据id获取轨迹
     * @param id id
     * @return 轨迹
     */
    NodeList getTraceById(int id);

    /**
     * 获取下一个轨迹
     * @param id    id
     * @return  下一个轨迹
     */
    NodeList getNextTrace(int id);

    /**
     * 获取一组动作的所有id
     * @param id 动作中的id
     * @return 一组动作id
     */
    long[] getTraceSeries(int id);

}
