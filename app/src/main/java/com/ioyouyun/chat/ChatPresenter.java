package com.ioyouyun.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.hzmc.audioplugin.MediaManager;
import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.chat.biz.ChatRequest;
import com.ioyouyun.chat.biz.ChatRequestImpl;
import com.ioyouyun.chat.biz.OnChatListener;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.chat.model.ChatPicInfo;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.observer.MessageEvent;
import com.ioyouyun.receivemsg.BroadCastCenter;
import com.ioyouyun.utils.FileUtil;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.wchat.message.AudioMessage;
import com.ioyouyun.wchat.message.ConvType;
import com.ioyouyun.wchat.message.FileMessage;
import com.ioyouyun.wchat.message.HistoryMessage;
import com.ioyouyun.wchat.message.NoticeType;
import com.ioyouyun.wchat.message.TextMessage;
import com.ioyouyun.wchat.util.HttpCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by YWB on 2016/6/5.
 */
public class ChatPresenter extends BasePresenter<ChatView> {

    private ChatRequest chatRequest;
    private MyInnerReceiver receiver;
    private Handler handler;
    public List<ChatMsgEntity> msgList = new ArrayList<>();

    public ChatPresenter() {
        handler = new Handler(Looper.getMainLooper());
        chatRequest = new ChatRequestImpl();
        registerReceiver();
        EventBus.getDefault().register(this);
    }

    /**
     * 注册本地广播
     */
    private void registerReceiver() {
        receiver = new MyInnerReceiver();
        BroadCastCenter.getInstance().registerReceiver(receiver, FunctionUtil.MSG_TYPE_RECEIVE_TEXT,
                FunctionUtil.MSG_TYPE_RECEIVE_AUDIO, FunctionUtil.MSG_TYPE_RECEIVE_IMAGE);
    }

    /**
     * 注销广播
     */
    private void unregisterReceiver() {
        if (receiver != null)
            BroadCastCenter.getInstance().unregisterReceiver(receiver);
    }

    public void onDestroy() {
        unregisterReceiver();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 发送文本
     *
     * @param toUid
     * @param text
     * @param nickName
     * @param convType
     */
    public void sendText(final String toUid, final String text, final String nickName, final ConvType convType) {
        final String msgId = FunctionUtil.genLocalMsgId();
        JSONObject object = new JSONObject();
        try {
            if (ConvType.single == convType)
                object.put("nickname", FunctionUtil.nickname);
            else if (ConvType.group == convType)
                object.put("nickname", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] padding = null;
        try {
            padding = object.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        chatRequest.sendText(msgId, toUid, text, convType, padding, new OnChatListener() {

            @Override
            public void onSuccess() {
                Logger.v("发送成功");
                if (mView != null)
                    mView.clearChatContent();

                ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), text, ChatMsgEntity.CHAT_TYPE_SEND_TEXT);
                entity.setMsgId(msgId);
                entity.setOppositeId(toUid);
                entity.setFromId(FunctionUtil.uid);
                entity.setToId(toUid);
                entity.setName(nickName);
                entity.setConvType(convType);
                entity.setUnreadMsgNum(0);
                refreshAdapter(entity);
                insertDatas(entity);
            }

            @Override
            public void onFaild() {

            }
        });
    }

    /**
     * 发送图片
     *
     * @param toUid
     * @param nickName
     * @param filePath
     * @param fileName
     * @param convType
     */
    public void sendImage(final String toUid, final String nickName, String filePath, String fileName, final ConvType convType) {
        Logger.v("toUid:" + toUid + "|filePath:" + filePath + "|fileName:" + fileName);
        final String msgId = FunctionUtil.genLocalMsgId();

        JSONObject object = new JSONObject();
        try {
            if (ConvType.single == convType)
                object.put("nickname", FunctionUtil.nickname);
            else if (ConvType.group == convType)
                object.put("nickname", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] padding = null;
        try {
            padding = object.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] thumbnail = null;

        String path = null;
        ChatPicInfo chatPicInfo = null;
        if (!TextUtils.isEmpty(filePath)) {
            String desPath = FileUtil.getChatImagePath(fileName);
            path = FileUtil.compressImage(filePath, desPath);
            int fileLength = 0;
            File file = new File(path);
            if (file.exists())
                fileLength = (int) file.length();
            chatPicInfo = new ChatPicInfo(null, path, fileLength, 0);
        }

        String thumbnailPath = "";
        if (thumbnail == null && path != null) {
            thumbnail = FileUtil.genSendImgThumbnail(path);
            if (thumbnail != null) {
                thumbnailPath = FileUtil.getThumbnailPath(FunctionUtil.uid, msgId);
                FileUtil.saveImg(thumbnail, thumbnailPath); //保存缩略图
            }
        }

        String sendPath = "";
        if (chatPicInfo.local != null) {
            sendPath = chatPicInfo.local;
        }

        final String tp = thumbnailPath;
        final ChatPicInfo ci = chatPicInfo;
        chatRequest.sendImage(msgId, toUid, sendPath, fileName, convType, padding, thumbnail, new OnChatListener() {
            @Override
            public void onSuccess() {
                ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), "", ChatMsgEntity.CHAT_TYPE_SEND_IMAGE);
                entity.setMsgId(msgId);
                entity.setOppositeId(toUid);
                entity.setFromId(FunctionUtil.uid);
                entity.setToId(toUid);
                entity.setName(nickName);
                entity.setImgThumbnail(tp);
                entity.setImgMsg(ChatPicInfo.getJsonStr(ci));
                entity.setConvType(convType);
                entity.setUnreadMsgNum(0);
                refreshAdapter(entity);
                insertDatas(entity);
            }

            @Override
            public void onFaild() {

            }
        });
    }

    /**
     * 本地数据库聊天数据
     *
     * @param tid
     */
    public void refreshLocalData(String tid) {
        String name = FunctionUtil.jointTableName(tid);
        List<ChatMsgEntity> list = YouyunDbManager.getIntance().getChatMsgEntityList(name);
        msgList.clear();
        msgList.addAll(list);
        if (mView != null) {
            mView.showChatMsgList(msgList);
            mView.setChatSelection(msgList.size() - 1);
        }
    }

    /**
     * 删除本地数据
     *
     * @param tid
     */
    public void clearLocalData(String tid) {
        String name = FunctionUtil.jointTableName(tid);
        YouyunDbManager.getIntance().removeChatImageMsg(name);
        refreshLocalData(tid);
    }

    /**
     * 获取历史记录
     *
     * @param toUid
     * @param convType
     */
    public void getHistory(String toUid, ConvType convType) {
        if (msgList == null)
            return;

        long time = System.currentTimeMillis();
        if (msgList.size() > 0)
            time = msgList.get(0).getTimestamp();
        chatRequest.getChatHistory(toUid, time / 1000, 10, convType, new HttpCallback() {
            @Override
            public void onResponse(String s) {

            }

            @Override
            public void onResponseHistory(List hlist) {
                refreshComplete();
                List<HistoryMessage> list = hlist;
                if (list == null || list.size() == 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            FunctionUtil.toastMessage("没有更多数据");
                        }
                    });
                    return;
                }

                for (HistoryMessage message : list) {
                    if (NoticeType.textmessage == message.type) {
                        TextMessage textMessage = (TextMessage) message.message;
                        receiveText(textMessage);
                    } else if (NoticeType.audiomessage == message.type) {
                        AudioMessage audioMessage = (AudioMessage) message.message;
                        receiveAudio(audioMessage);
                    } else if (NoticeType.filemessage == message.type) {
                        FileMessage fileMessage = (FileMessage) message.message;
                        receiveFile(fileMessage);
                    }
                }
                historyRefreshAdapter();
            }

            @Override
            public void onError(Exception e) {
                Logger.v("error:" + e.getMessage());
                refreshComplete();
            }
        });
    }

    private void receiveFile(FileMessage fileMessage) {
        String thumbnailPath = "";
        if (null != fileMessage.thumbData) {
            thumbnailPath = FileUtil.getThumbnailPath(fileMessage.fromuid, fileMessage.msgId);
            FileUtil.saveImg(fileMessage.thumbData, thumbnailPath); //保存缩略图
        }
        ChatPicInfo chatPicInfo = new ChatPicInfo(fileMessage.fileId, null, fileMessage.fileLength, fileMessage.pieceSize);
        String chatJson = chatPicInfo.getJsonStr(chatPicInfo);

        String toUid = fileMessage.touid;
        if (ConvType.group == fileMessage.convType) {
            toUid = toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$"));
        }
        int msgType = ChatMsgEntity.CHAT_TYPE_RECV_IMAGE;
        if (FunctionUtil.uid.equals(fileMessage.fromuid))
            msgType = ChatMsgEntity.CHAT_TYPE_SEND_IMAGE;

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMsgId(fileMessage.msgId);
        entity.setFromId(fileMessage.fromuid);
        entity.setToId(toUid);
        entity.setTimestamp(fileMessage.time);
        entity.setImgThumbnail(thumbnailPath);
        entity.setImgMsg(chatJson);
        entity.setMsgType(msgType);
        entity.setConvType(fileMessage.convType);
        entity.setUnreadMsgNum(0);
        entity.setShowTime(true);

        String nickname = "";
        if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_IMAGE) {
            nickname = FunctionUtil.nickname;
        } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_IMAGE) {
            try {
                String audioTime = new String(fileMessage.padding, "utf-8");
                JSONObject object = new JSONObject(audioTime);
                nickname = object.getString("nickname");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        entity.setName(nickname);

        insertHistoryToDB(entity);
    }

    private Object audioLockObj = new Object();

    private void receiveAudio(AudioMessage audioMessage) {
        if (audioMessage == null)
            return;

        FileOutputStream fis = null;
        try {
            String toUid = audioMessage.touid;
            if (ConvType.group == audioMessage.convType) {
                toUid = toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$"));
            }
            int msgType = ChatMsgEntity.CHAT_TYPE_RECV_AUDIO;
            if (FunctionUtil.uid.equals(audioMessage.fromuid))
                msgType = ChatMsgEntity.CHAT_TYPE_SEND_AUDIO;

            String touchId = audioMessage.fromuid;
            String audioTime = new String(audioMessage.padding, "utf-8");
            JSONObject object = new JSONObject(audioTime);
            String durtion = object.getString("duration");
            String nickname = "";
            if (msgType == ChatMsgEntity.CHAT_TYPE_SEND_AUDIO) {
                nickname = FunctionUtil.nickname;
            } else if (msgType == ChatMsgEntity.CHAT_TYPE_RECV_AUDIO) {
                nickname = object.getString("nickname");
            }

            String filePath = FileUtil.getUserAudioPath(touchId);
            String audioName = filePath + audioMessage.spanId + ".amr";
            File audioNameFile = new File(audioName);
            if (audioNameFile.exists()) {
                audioNameFile.delete();
            }

            synchronized (audioLockObj) {
                fis = new FileOutputStream(audioNameFile, true);
                if (audioMessage.audioData != null)
                    fis.write(audioMessage.audioData);
            }

            Logger.v("语音存储在：" + audioName);

            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setTimestamp(audioMessage.time);
            entity.setText(audioName);
            entity.setMsgType(msgType);
            entity.setMsgId(audioMessage.spanId);
            entity.setFromId(audioMessage.fromuid);
            entity.setToId(toUid);
            entity.setAudioTime(durtion);
            entity.setName(nickname);
            entity.setConvType(audioMessage.convType);
            entity.setUnreadMsgNum(0);
            entity.setShowTime(true);

            insertHistoryToDB(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.flush();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void receiveText(TextMessage textMessage) {
        String toUid = textMessage.touid;
        if (ConvType.group == textMessage.convType) {
            toUid = toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$"));
        }
        int msgType = ChatMsgEntity.CHAT_TYPE_RECV_TEXT;
        if (FunctionUtil.uid.equals(textMessage.fromuid))
            msgType = ChatMsgEntity.CHAT_TYPE_SEND_TEXT;
        ChatMsgEntity entity = new ChatMsgEntity(textMessage.time, textMessage.text, msgType);
        entity.setMsgId(textMessage.msgId);
        entity.setFromId(textMessage.fromuid);
        entity.setToId(toUid);
        entity.setConvType(textMessage.convType);
        entity.setUnreadMsgNum(0);
        entity.setShowTime(true);

        String nickname = "";
        if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_TEXT) {
            nickname = FunctionUtil.nickname;
        } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_TEXT) {
            try {
                String audioTime = new String(textMessage.padding, "utf-8");
                JSONObject object = new JSONObject(audioTime);
                nickname = object.getString("nickname");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        entity.setName(nickname);

        insertHistoryToDB(entity);
    }

    synchronized private void insertHistoryToDB(ChatMsgEntity entity) {
        historyCount++;

        String oppositeId = "";
        if (ConvType.single == entity.getConvType()) {
            if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_TEXT ||
                    entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_IMAGE ||
                    entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_AUDIO) {
                oppositeId = entity.getToId();
            } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_TEXT ||
                    entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_IMAGE ||
                    entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_AUDIO) {
                oppositeId = entity.getFromId();
            }
        } else if (ConvType.group == entity.getConvType()) {
            oppositeId = entity.getToId();
        }
        String name = FunctionUtil.jointTableName(oppositeId);
        YouyunDbManager.getIntance().insertChatMessage(entity, name);

        msgList.add(0, entity);
        if (msgList.size() > 1) {
            if (msgList.get(1).getTimestamp() - msgList.get(0).getTimestamp() <= FunctionUtil.MSG_TIME_SEPARATE) {
                msgList.get(1).setShowTime(false);
                YouyunDbManager.getIntance().updateShowTime(msgList.get(1), name);
            }
        }

    }

    /**
     * 储存数据
     *
     * @param entity
     */
    private void insertDatas(ChatMsgEntity entity) {
        String name = FunctionUtil.jointTableName(entity.getToId());
        YouyunDbManager.getIntance().insertChatMessage(entity, name);
        YouyunDbManager.getIntance().insertRecentContact(entity);
    }

    /**
     * 刷新聊天列表
     *
     * @param entity
     */
    private void refreshAdapter(ChatMsgEntity entity) {
        int index = msgList.size();
        msgList.add(index, entity);
        int preIndex = index - 1;
        if (preIndex >= 0) {
            if (msgList.get(index).getTimestamp() - msgList.get(preIndex).getTimestamp() > FunctionUtil.MSG_TIME_SEPARATE) {
                msgList.get(index).setShowTime(true);
                msgList.set(index, entity);
            }
        } else {
            msgList.get(index).setShowTime(true);
            msgList.set(index, entity);
        }

        if (mView != null) {
            mView.showChatMsgList(msgList);
            mView.setChatSelection(msgList.size() - 1);
        }
    }

    /**
     * 刷新历史聊天列表
     */
    private void historyRefreshAdapter() {
        Logger.v("historyCount:" + historyCount);
        if (historyCount > 0) {
            final int count = historyCount;
            historyCount = 0;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mView != null) {
                        mView.showChatMsgList(msgList);
                        mView.setChatSelection(count);
                    }
                }
            });
        }
    }

    int historyCount = 0;

    /**
     * 取消下拉加载进度条
     */
    private void refreshComplete() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mView != null) {
                    mView.onCompleteLoad();
                }
            }
        });
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    private String getNowDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        return date;
    }

    /**
     * 按住说话
     *
     * @param touid
     * @param nickName
     * @param convType
     */
    synchronized public void startRealTimeRecord(String touid, String nickName, ConvType convType, Handler micHandler) {
        String spanId = FunctionUtil.genLocalMsgId();
        String audioName = FileUtil.getUserAudioPath(FunctionUtil.uid) + spanId + ".amr";
        Logger.v("audioName:" + audioName);
        RecImpl recordCallBack = new RecImpl(spanId, audioName, touid, nickName, convType, micHandler);
        MediaManager.getMediaPlayManager().setRecordCallBack(recordCallBack);
        MediaManager.getMediaPlayManager().startRealTimeRecord(null, audioName);
    }

    // 废弃此次录音
    private boolean discardRecord = false;

    synchronized public void discardRecording(boolean discardRecord) {
        this.discardRecord = discardRecord;
    }

    synchronized public void stopRealTimeRecord() {
        MediaManager.getMediaPlayManager().stopRealTimeRecord();
    }

    class RecImpl implements MediaManager.RecordCallBack {

        private String spanId;
        private String audioName; // 语音的地址+名称
        private String toUid;
        private String nickName;
        private ConvType convType;
        private Handler micHandler;

        public RecImpl(String spanId, String audioName, String toUid, String nickName, ConvType convType, Handler micHandler) {
            this.spanId = spanId;
            this.audioName = audioName;
            this.toUid = toUid;
            this.nickName = nickName;
            this.convType = convType;
            this.micHandler = micHandler;
        }

        @Override
        synchronized public void recordStartCallback(boolean bstarted) {
            Logger.v("recordStartCallback:" + bstarted);
        }

        @Override
        synchronized public void recordAudioData(byte[] buffer, int size, int seq) {
            String msgId = FunctionUtil.genLocalMsgId();
            chatRequest.sendVoiceContinue(msgId, toUid, spanId, seq, false, buffer, convType, null, null);
        }

        @Override
        synchronized public void recordStopCallback(long totalsize, int seqcount) {
            final String msgId = FunctionUtil.genLocalMsgId();

            if (discardRecord) {
                chatRequest.sendVoiceContinue(msgId, toUid, spanId, Integer.MAX_VALUE, false, new byte[]{0},
                        convType, null, null);
                FileUtil.removeFile(audioName);
                return;
            }

            int al = (int) (Math.ceil(seqcount / 2.0));
            if (al <= 0)
                return;
            final String audioLength = String.valueOf(al);
            JSONObject object = new JSONObject();
            try {
                object.put("duration", audioLength);
                if (ConvType.single == convType)
                    object.put("nickname", FunctionUtil.nickname);
                else if (ConvType.group == convType)
                    object.put("nickname", nickName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            byte[] padding = null;
            try {
                padding = object.toString().getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            chatRequest.sendVoiceContinue(msgId, toUid, spanId, seqcount + 1, true, new byte[]{0}, convType, padding, new OnChatListener() {
                @Override
                public void onSuccess() {
                    Logger.v("发送成功");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), audioName, ChatMsgEntity.CHAT_TYPE_SEND_AUDIO);
                            entity.setMsgId(msgId);
                            entity.setOppositeId(toUid);
                            entity.setFromId(FunctionUtil.uid);
                            entity.setToId(toUid);
                            entity.setName(nickName);
                            entity.setAudioTime(audioLength);
                            entity.setConvType(convType);
                            entity.setUnreadMsgNum(0);
                            refreshAdapter(entity);
                            insertDatas(entity);
                        }
                    });
                }

                @Override
                public void onFaild() {
                    FileUtil.removeFile(audioName);
                }
            });
        }

        @Override
        synchronized public void recordVolumeCallback(long value) {
            int v = (int) ((value - 30) / 2);
            if (v > 13) {
                v = 13;
            } else if (v < 0) {
                v = 0;
            }
            Message message = micHandler.obtainMessage();
            message.what = v;
            micHandler.sendMessage(message);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chatListEvent(MessageEvent.DownloadImageEvent event){
        msgList.set(event.position, event.chatMsgEntity);
    }

    class MyInnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (FunctionUtil.MSG_TYPE_RECEIVE_TEXT.equals(action)) {
                ChatMsgEntity entity = (ChatMsgEntity) intent.getSerializableExtra(FunctionUtil.TYPE_TEXT);
                refreshAdapter(entity);
            } else if (FunctionUtil.MSG_TYPE_RECEIVE_AUDIO.equals(action)) {
                ChatMsgEntity entity = (ChatMsgEntity) intent.getSerializableExtra(FunctionUtil.TYPE_AUDIO);
                refreshAdapter(entity);
            } else if (FunctionUtil.MSG_TYPE_RECEIVE_IMAGE.equals(action)) {
                ChatMsgEntity entity = (ChatMsgEntity) intent.getSerializableExtra(FunctionUtil.TYPE_IMAGE);
                refreshAdapter(entity);
            }
        }
    }

}
