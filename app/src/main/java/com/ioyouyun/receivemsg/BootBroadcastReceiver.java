package com.ioyouyun.receivemsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by 卫彪 on 2016/7/4.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PushMsgReceiver.startPush();

    }
}
