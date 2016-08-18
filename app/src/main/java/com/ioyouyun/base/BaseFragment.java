package com.ioyouyun.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by YWB on 2016/6/20.
 * <p>
 * MVP for Base Fragment
 */
public abstract class BaseFragment<V, T extends BasePresenter<V>> extends Fragment {

    protected T presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
        presenter.attach((V) this);
    }

    protected abstract T initPresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.dettach();
    }

    protected final String KEY_UID = "key_uid"; // 用户Id
    protected final String KEY_NICKNAME = "key_nickname"; // 昵称
    protected final String KEY_GID = "key_gid"; // 群Id
    protected final String KEY_FLAG = "key_flag"; // flag
    protected final String KEY_ROOMID = "key_roomid"; // roomId
    protected final String KEY_KEY = "key_key"; // key

    // startActivity
    protected void $startActivity(Class<?> cls) {
        $startActivity(cls, null);
    }

    // startActivity
    protected void $startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(getActivity(), cls);
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
    protected void $startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    // getIntent
    protected Bundle $getIntentExtra() {
        Intent intent = getActivity().getIntent();
        Bundle bundle = null;
        if (null != intent)
            bundle = intent.getExtras();
        return bundle;
    }


}
