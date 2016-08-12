package com.ioyouyun.media.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.media.view.ConferenceView;
import com.ioyouyun.receivemsg.BroadCastCenter;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.wchat.WeimiInstance;
import com.weimi.media.WMedia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 卫彪 on 2016/7/19.
 */
public class ConferencePresenter extends BasePresenter<ConferenceView> {

    private final static int CALL_TIME = 1000;
    private MyInnerReceiver receiver;
    private Activity activity;
    private List<String> memberList = new ArrayList<>();
    private boolean isSpeakerEnabled = false, isMicMuted = false;

    public ConferencePresenter(Activity activity) {
        this.activity = activity;
        registerReceiver();
    }

    /**
     * 刷新成员列表
     */
    public void refreshMember(String roomId, String groupId) {
        WeimiInstance.getInstance().conferenceRoommateList(roomId, groupId);
    }

    public void toggleSpeaker() {
        isSpeakerEnabled = !isSpeakerEnabled;
        WMedia.getInstance().toggleSpeaker(isSpeakerEnabled);
        if (mView != null) {
            mView.toggleSpeaker(isSpeakerEnabled);
        }

    }

    public void toggleMicro() {
        isMicMuted = !isMicMuted;
        WMedia.getInstance().toggleMicro(isMicMuted);
        if (mView != null) {
            mView.toggleMicro(isMicMuted);
        }
    }

    /**
     * 挂断
     */
    public void hangUp() {
        WMedia.getInstance().toHangUp();
    }

    private int mTime;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CALL_TIME:
                    setCallTime("通话时间：" + getTime());
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
            mView.setCallTime(time);
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

    private void startTime() {
        handler.removeMessages(CALL_TIME);
        handler.sendEmptyMessageDelayed(CALL_TIME, 1000);
        mTime = 0;
    }

    /**
     * 注册本地广播
     */
    private void registerReceiver() {
        receiver = new MyInnerReceiver();
        BroadCastCenter.getInstance().registerReceiver(receiver,
                FunctionUtil.MEDIA_CALL_CONNECTED, FunctionUtil.MEDIA_CALL_END,
                FunctionUtil.MEDIA_CALL_ERROR, FunctionUtil.CONFERENCE_LIST);
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
            Logger.v("conference:" + action);
            if (FunctionUtil.MEDIA_CALL_END.equals(action) || FunctionUtil.MEDIA_CALL_ERROR.equals(action)) {
                activity.finish();
            } else if (FunctionUtil.MEDIA_CALL_CONNECTED.equals(action)) {
                if(mView != null)
                    mView.getRoomList();
                startTime();
            } else if (FunctionUtil.CONFERENCE_LIST.equals(action)) {
                String content = intent.getStringExtra(FunctionUtil.CONTENT);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    if (!jsonObject.has("userList"))
                        return;
                    String string = jsonObject.getString("userList");
                    JSONArray array = new JSONArray(string);
                    if (array == null)
                        return;
                    memberList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        memberList.add(array.getString(i));
                    }
                    if (mView != null) {
                        mView.setListView(memberList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
