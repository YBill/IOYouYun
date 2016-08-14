package com.ioyouyun.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ioyouyun.R;
import com.ioyouyun.widgets.LoddingDialog;

/**
 * Created by YWB on 2016/6/5.
 *
 * MVP for Base Activity
 */
public abstract class BaseActivity<V, T extends BasePresenter<V>> extends AppCompatActivity implements View.OnClickListener{

    protected T presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mvp
        presenter = initPresenter();
        presenter.attach((V)this);

        // ParentActivity
        setContentView(getLayoutId());
        loddingDialog = new LoddingDialog(this);
        initView();
        setListener();
        initData();
    }

    protected abstract T initPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.dettach();
    }

    ///////////////////////////ParentActivity/////////////////////////////////////

    protected LoddingDialog loddingDialog;

    // 通用title
    protected  void setToolBar(){
        Toolbar toolbar = findView(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // findViewById
    public <T extends View> T findView(int resId) {
        return (T) findViewById(resId);
    }

    // setContentView
    protected abstract int getLayoutId();

    // findViewById
    protected abstract void initView();

    // listener
    protected abstract void setListener();

    // data
    protected abstract void initData();

    // onClick
    public abstract void widgetClick(View v);

    private long lastClick = 0;

    private boolean fastClick() {
        if (System.currentTimeMillis() - lastClick <= 1000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (fastClick())
            widgetClick(v);
    }

}
