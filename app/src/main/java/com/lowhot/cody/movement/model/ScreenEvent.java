package com.lowhot.cody.movement.model;

import android.util.Log;

import com.lowhot.cody.movement.bean.Accelerator;
import com.lowhot.cody.movement.bean.Gyroscope;
import com.lowhot.cody.movement.bean.NodeList;
import com.lowhot.cody.movement.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *处理屏幕点击滑动事件，同时处理坐标数据和传感器数据，组装数据,保存数据，判断此事件是主人还是客人
 */
public class ScreenEvent {
    public static String TAG = "ScreenEvent";
    private ArrayList<Accelerator> acceleratorQueue = new ArrayList<>();    //用来保存加速器数据
    private ArrayList<Gyroscope> gyroscopeQueue = new ArrayList<>();        //用来保存陀螺仪数据
    private NodeList nodeList;                                      //保存坐标数据
    private String appName;                                          //保存app名称
    private String dir;
    private Boolean isAdmin;

    /**
     * 构造函数，初始化
     * @param nodeList  坐标数据
     * @param acceleratorQueue  加速器数据
     * @param gyroscopeQueue    陀螺仪数据
     * @param appName   应用名称
     * @param dir   目录
     * @param isAdmin   是否主人
     */
    public ScreenEvent(NodeList nodeList,
                       LinkedBlockingQueue<Accelerator> acceleratorQueue,
                       LinkedBlockingQueue<Gyroscope> gyroscopeQueue, String appName, String dir,Boolean isAdmin) {
        this.nodeList = nodeList;
        this.appName = appName;
        this.dir = dir;
        this.isAdmin = isAdmin;
        // 选取该时间段内的 sensors
        long beginTimestamp = nodeList.getBeginStamp() - 30;
        long endTimestamp = nodeList.getEndStamp();
        // add 该时间段内所有的加速器值

        Accelerator av;
        while ((av = acceleratorQueue.poll()) != null
                && av.getTimestamp() < beginTimestamp) {
        }
        if (av != null)
            this.acceleratorQueue.add(av);
        while ((av = acceleratorQueue.poll()) != null
                && av.getTimestamp() < endTimestamp) {
            this.acceleratorQueue.add(av);
        }
        // add 该时间段内所有陀螺仪值
        Gyroscope gv;
        while ((gv = gyroscopeQueue.poll()) != null
                && gv.getTimestamp() < beginTimestamp) {
        }
        if (gv != null)
            this.gyroscopeQueue.add(gv);
        while ((gv = gyroscopeQueue.poll()) != null
                && gv.getTimestamp() < endTimestamp) {
            this.gyroscopeQueue.add(gv);
        }
    }


    /**
     * 判断是否是主人
     * @return 主人返回是
     */
    public boolean judge() {
        //TODO
        return true;
    }

    /**
     * 保存数据
     */
    public void save(){
        // TODO: 16/3/30
    }


}
