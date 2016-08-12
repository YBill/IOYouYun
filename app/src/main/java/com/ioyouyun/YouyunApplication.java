package com.ioyouyun;

import android.app.Application;

import com.ioyouyun.receivemsg.BroadCastCenter;
import com.ioyouyun.receivemsg.ReceiveRunnable;

/**
 * Created by 卫彪 on 2016/6/6.
 */
public class YouyunApplication extends Application{

    public static YouyunApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initReceive();
        BroadCastCenter.getInstance().init(getApplicationContext());

    }

    private void initReceive(){
        ReceiveRunnable receiveRunnable = new ReceiveRunnable(getApplicationContext());
        Thread msgHandler = new Thread(receiveRunnable);
        msgHandler.start();
    }

}
