package com.ioyouyun.media.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.media.view.VoIPView;
import com.ioyouyun.receivemsg.BroadCastCenter;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.weimi.media.WMedia;


/**
 * Created by 卫彪 on 2016/7/21.
 */
public class VoIPPresenter extends BasePresenter<VoIPView> {

    private final static int CALL_TIME = 1000;
    private MyInnerReceiver receiver;
    private Activity activity;

    public VoIPPresenter(Activity activity) {
        this.activity = activity;
        registerReceiver();
    }

    private int mTime;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CALL_TIME:
                    setCallTime("网络通话中 " + getTime());
                    mTime++;
                    handler.sendEmptyMessageDelayed(CALL_TIME, 1000);
                    break;
            }
        }
    };

    private String getTime() {
        try {
            int sec = mTime % 60;
            String secString = sec < 10 ? "0" + sec : String.valueOf(sec);
            int min = mTime / 60;
            String minString = min < 10 ? "0" + min : String.valueOf(min);
            return minString + ":" + secString;
        } catch (Exception e) {
            return "00:00";
        }
    }

    private void setCallTime(String time) {
        if (mView != null)
            mView.setTipsText(time);
    }

    public void onDestory() {
        Logger.v("onDestory");
        hangUp();
        unregisterReceiver();
        if (handler != null) {
            handler.removeMessages(CALL_TIME);
            handler = null;
        }
    }

    public void startTime() {
        handler.removeMessages(CALL_TIME);
        handler.sendEmptyMessageDelayed(CALL_TIME, 1000);
        mTime = 0;
    }

    /**
     * 挂断
     */
    public void hangUp() {
        WMedia.getInstance().toHangUp();
    }

    /**
     * 注册本地广播
     */
    private void registerReceiver() {
        receiver = new MyInnerReceiver();
        BroadCastCenter.getInstance().registerReceiver(receiver,
                FunctionUtil.MEDIA_CALL_CONNECTED, FunctionUtil.MEDIA_CALL_END, FunctionUtil.MEDIA_CALL_ERROR);
    }

    /**
     * 注销广播
     */
    private void unregisterReceiver() {
        if (receiver != null)
            BroadCastCenter.getInstance().unregisterReceiver(receiver);
    }

    class MyInnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.v("media:" + action);
            if (FunctionUtil.MEDIA_CALL_END.equals(action) || FunctionUtil.MEDIA_CALL_ERROR.equals(action)) {
                setCallTime("通话挂断");
                activity.finish();
            } else if (FunctionUtil.MEDIA_CALL_CONNECTED.equals(action)) {
                startTime();
            }

        }
    }

}
