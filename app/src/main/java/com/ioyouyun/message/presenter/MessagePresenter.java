
package com.ioyouyun.message.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.chat.ChatActivity;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.message.view.MessageView;
import com.ioyouyun.receivemsg.BroadCastCenter;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.wchat.message.ConvType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/6/20.
 */
public class MessagePresenter extends BasePresenter<MessageView> {

    private Context context;
    private List<ChatMsgEntity> messageList = new ArrayList<>();
    private MyInnerReceiver receiver;
    private Handler handler;

    public MessagePresenter(Context context) {
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
        registerReceiver();
    }

    public void onDestory() {
        unregisterReceiver();
    }

    /**
     * 最近联系人
     */
    public void getRecentContact() {
        Logger.v("本地查询联系人");
        messageList.clear();
        List list = YouyunDbManager.getIntance().getRecentContact();
        if (list != null)
            messageList.addAll(list);
        if (mView != null) {
            mView.refreshList(messageList);
        }
    }

    public void receiveNotify() {
        Logger.v("本地查询通知数");
        long count = YouyunDbManager.getIntance().getUnreadNotifyNum();
        if (mView != null) {
            mView.showNotify(count);
        }
    }

    /**
     * 收到IM消息
     *
     * @param chatMsgEntity
     */
    public void receiveMessage(ChatMsgEntity chatMsgEntity) {
        boolean isHaveUser = false;
        int index = -1;
        for (int i = 0; i < messageList.size(); i++) {
            if (chatMsgEntity.getOppositeId().equals(messageList.get(i).getOppositeId())) {
                isHaveUser = true;
                index = i;
                break;
            }
        }
        if (isHaveUser){
            // 删除当前位置记录，插入到头部
            messageList.remove(index);
        }
        messageList.add(0, chatMsgEntity);
        if (mView != null)
            mView.refreshList(messageList);
    }

    /**
     * onclick
     *
     * @param position
     */
    public void onItemClick(int position) {
        ChatMsgEntity entity = messageList.get(position);
        if (entity != null) {
            String tid = entity.getOppositeId();
            if (entity.getUnreadMsgNum() > 0) {
                YouyunDbManager.getIntance().updateUnreadNumber(tid, 0);
            }
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("toUid", tid);
            intent.putExtra("nickName", entity.getName());
            int type = 0;
            if (ConvType.group == entity.getConvType())
                type = 1;
            intent.putExtra("chatType", type);
            context.startActivity(intent);
        }
    }

    /**
     * 注册本地广播
     */
    private void registerReceiver() {
        receiver = new MyInnerReceiver();
        BroadCastCenter.getInstance().registerReceiver(receiver, FunctionUtil.MSG_TYPE_RECEIVE_TEXT,
                FunctionUtil.MSG_TYPE_RECEIVE_AUDIO, FunctionUtil.MSG_TYPE_RECEIVE_IMAGE,
                FunctionUtil.MSG_TYPE_NOTIFY);
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
            if (FunctionUtil.MSG_TYPE_RECEIVE_TEXT.equals(action)) {
                ChatMsgEntity entity = (ChatMsgEntity) intent.getSerializableExtra(FunctionUtil.TYPE_TEXT);
                if (mView != null)
                    mView.refreshMessageList(entity);
            } else if (FunctionUtil.MSG_TYPE_RECEIVE_AUDIO.equals(action)) {
                ChatMsgEntity entity = (ChatMsgEntity) intent.getSerializableExtra(FunctionUtil.TYPE_AUDIO);
                if (mView != null)
                    mView.refreshMessageList(entity);
            } else if (FunctionUtil.MSG_TYPE_RECEIVE_IMAGE.equals(action)) {
                ChatMsgEntity entity = (ChatMsgEntity) intent.getSerializableExtra(FunctionUtil.TYPE_IMAGE);
                if (mView != null)
                    mView.refreshMessageList(entity);
            } else if (FunctionUtil.MSG_TYPE_NOTIFY.equals(action)) {
//                    MsgBodyTemplate entity = intent.getParcelableExtra(FunctionUtil.TYPE_NOTIFY);
                if (mView != null)
                    mView.refreshMessageNotify();
            }
        }
    }

}
