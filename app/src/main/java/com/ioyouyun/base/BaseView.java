package com.ioyouyun.base;

/**
 * Created by YWB on 2016/6/5.
 * <p>
 * MVP for Base View
 */
public interface BaseView {

    /**
     * 显示进度条
     */
    void showLoading();

    /**
     * 取消进度条
     */
    void hideLoading();

}
