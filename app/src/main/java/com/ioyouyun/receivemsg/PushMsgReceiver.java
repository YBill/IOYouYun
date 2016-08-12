package com.ioyouyun.receivemsg;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.ioyouyun.R;
import com.ioyouyun.YouyunApplication;
import com.ioyouyun.login.LoginActivity;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.PushSharedUtil;
import com.ioyouyun.utils.SoundVibrateUtil;
import com.weimi.push.WeimiPush;
import com.weimi.push.WeimiPushReceiver;
import com.weimi.push.data.PayLoadMessage;

/**
 * Created by YWB on 2016/6/25.
 */
public class PushMsgReceiver extends WeimiPushReceiver {

    private int rq = 0;
    private Notification notification;

    @Override
    public void onMessage(Context context, PayLoadMessage payLoadMessage) {
        String alert = payLoadMessage.alert;
        if (alert == null || alert.equals(""))
            return;
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        intent.putExtra("pushMsg", alert);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, rq++,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (notification == null) {
            notification = new Notification(R.mipmap.ic_launcher,
                    context.getResources().getString(R.string.app_name), System.currentTimeMillis());
            notification.contentView = new RemoteViews(
                    context.getPackageName(), R.layout.layout_notifycation);
        }
        notification.contentView.setTextViewText(R.id.tv1, alert);
        notification.contentView.setViewVisibility(R.id.tv1, View.VISIBLE);
        notification.contentView.setViewVisibility(R.id.tv2, View.GONE);

        notification.icon = R.mipmap.ic_launcher;
        notification.tickerText = alert;
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        notification.contentIntent = pendingIntent;
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 500;
        notification.ledOffMS = 2000;
        nManager.cancel(300);
        nManager.notify(300, notification);

        if (payLoadMessage.sound != null) {
            if (PushSharedUtil.getInstance().getVibration()) {
                SoundVibrateUtil.checkIntervalTimeAndVibrate(context);
            }
            if (PushSharedUtil.getInstance().getSound()) {
                SoundVibrateUtil.checkIntervalTimeAndSound(context);
            }
        }

    }

    public static void startPush() {
        if (FunctionUtil.isOnlinePlatform)
            WeimiPush.connect(YouyunApplication.application, WeimiPush.pushServerIp, true);
        else
            WeimiPush.connect(YouyunApplication.application, WeimiPush.testPushServerIp, false);
    }

    public static void stopPush() {
        WeimiPush.disconnect(YouyunApplication.application);
    }

}
