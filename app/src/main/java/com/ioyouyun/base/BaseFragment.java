package com.ioyouyun.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by YWB on 2016/6/20.
 *
 * MVP for Base Fragment
 */
public abstract class BaseFragment<V, T extends BasePresenter<V>> extends Fragment {

    protected T presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
        presenter.attach((V)this);
    }

    protected abstract T initPresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.dettach();
    }
}
