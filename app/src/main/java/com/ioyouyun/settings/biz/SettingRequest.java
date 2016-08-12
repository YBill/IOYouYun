package com.ioyouyun.settings.biz;

/**
 * Created by 卫彪 on 2016/6/25.
 */
public interface SettingRequest {

    /**
     * 设置PUSH勿扰时段
     *
     * @param startTime
     * @param endTime
     */
    void pushCreate(String startTime, String endTime, OnSettingListener listener);

    /**
     * 获取PUSH时段
     *
     * @param listener
     */
    void getPushTime(OnSettingListener listener);

}
