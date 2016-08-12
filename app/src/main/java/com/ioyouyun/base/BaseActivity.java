package com.ioyouyun.base;

import android.os.Bundle;

import com.ioyouyun.ParentActivity;

/**
 * Created by YWB on 2016/6/5.
 *
 * MVP for Base Activity
 */
public abstract class BaseActivity<V, T extends BasePresenter<V>> extends ParentActivity {

    protected T presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
        presenter.attach((V)this);
    }

    protected abstract T initPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.dettach();
    }
}
