package com.ioyouyun.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    protected boolean isDebug = true;

    protected final String KEY_UID = "key_uid"; // 用户Id
    protected final String KEY_NICKNAME = "key_nickname"; // 昵称
    protected final String KEY_GID = "key_gid"; // 群Id
    protected final String KEY_FLAG = "key_flag"; // flag
    protected final String KEY_ROOMID = "key_roomid"; // roomId
    protected final String KEY_KEY = "key_key"; // key

    // Log
    protected void $Log(String msg) {
        if (isDebug) {
            Log.d(this.getClass().getName(), msg);
        }
    }

    // Toast
    protected void $toast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // startActivity
    protected void $startActivity(Class<?> cls) {
        $startActivity(cls, null);
    }

    // startActivity
    protected void $startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    // startActivityForResult
    protected void $startActivityForResult(Class<?> cls, int requestCode) {
        $startActivityForResult(cls, null, requestCode);
    }

    // startActivityForResult
    protected void $startActivityForResult(Class<?> cls, Bundle bundle, int requestCode){
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    // getIntent
    protected Bundle $getIntentExtra() {
        Intent intent = getIntent();
        Bundle bundle = null;
        if (null != intent)
            bundle = intent.getExtras();
        return bundle;
    }

    // 通用title
    protected  void $setToolBar(){
        Toolbar toolbar = $findViewById(R.id.toolbar);
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
    public <T extends View> T $findViewById(int resId) {
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
