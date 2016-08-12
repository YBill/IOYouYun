package com.ioyouyun.login.biz;

import android.app.Activity;

import com.ioyouyun.login.UserInfoEntity;

/**
 * Created by 卫彪 on 2016/6/6.
 */
public interface LoginRequest {

    /**
     * 登陆
     *
     * @param activity
     * @param listener
     */
    void login(Activity activity, OnRequestListener listener);

    /**
     * 设置用户信息
     * @param nickName
     * @param listener
     */
    void setNickName(String nickName, OnRequestListener listener);

    /**
     * 获取Sqlite里面的用户信息
     */
    UserInfoEntity getUserInfo();

    /**
     * 保存uid到Sqlite
     *
     * @param entity
     */
    void saveUserInfo(UserInfoEntity entity);

    /**
     * 初始化Media SDK
     *
     * @param activity
     */
    boolean initMediaSDK(Activity activity);

    /**
     * 获取用户Id
     *
     * @return
     */
    String getUid();

}
