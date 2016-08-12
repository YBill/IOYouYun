package com.ioyouyun.base;

/**
 * Created by YWB on 2016/6/5.
 *
 * MVP for Base Presenter
 */
public abstract class BasePresenter<T> {

    protected T mView;

    protected void attach(T mView){
        this.mView = mView;
    }

    protected void dettach(){
        this.mView = null;
    }
}
