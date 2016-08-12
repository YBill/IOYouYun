package com.ioyouyun.login;

/**
 * Created by 卫彪 on 2016/8/11.
 */
public interface LoginView {

    /**
     * 登录成功
     */
    void loginSuccess();

    /**
     * 登录失败
     */
    void loginFaild();

    /**
     * show Dialog
     */
    void showDialog();
}
