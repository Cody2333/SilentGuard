package com.lowhot.cody.movement.utils;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 创建者: chengfeng
 * 创建时间： 2016-05-02 上午9:43
 * 任务号：
 * 描述：公共工具类
 */
public class CommonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static void stateTimespent(RUNNER runner,String tag) throws Exception{
        long x = FileUtils.getTimestamp();
        runner.run();
        Log.e(tag, String.valueOf(FileUtils.getTimestamp() - x)+"ms");
    }

    public interface RUNNER{

        void run() throws Exception;
    }
}
