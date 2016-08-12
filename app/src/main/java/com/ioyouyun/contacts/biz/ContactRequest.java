package com.ioyouyun.contacts.biz;

/**
 * Created by 卫彪 on 2016/6/25.
 */
public interface ContactRequest {

    /**
     * 添加地理位置
     *
     * @param uid
     * @param longitude 经度
     * @param latitude  纬度
     * @param type      0表示添加用户地理位置，1表示添加组地理位置
     * @param listener
     */
    void addLocation(String uid, double longitude, double latitude, int type, OnContactListener listener);

    /**
     * 获取附近的人
     *
     * @param uid
     * @param longitude
     * @param latitude
     * @param range     范围, 单位m
     * @param listener
     */
    void getNearbyUsers(String uid, double longitude, double latitude, long range, OnContactListener listener);

}
