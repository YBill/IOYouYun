package com.ioyouyun.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.hzmc.audioplugin.MediaManager;
import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.chat.biz.ChatRequest;
import com.ioyouyun.chat.biz.ChatRequestImpl;
import com.ioyouyun.chat.biz.OnChatListener;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.chat.model.ChatPicInfo;
import com.ioyouyun.datamanager.YouyunDbManager;
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
    private String uid; // 自己的Id
    private Handler handler;
    public List<ChatMsgEntity> msgList = new ArrayList<>();

    public ChatPresenter() {
        handler = new Handler(Looper.getMainLooper());
        chatRequest = new ChatRequestImpl();
        registerReceiver();
        uid = FunctionUtil.uid;
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

    public void onDestory() {
        unregisterReceiver();
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
                ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), text, ChatMsgEntity.Chat_Msg_Type.TYPE_TEXT, false);
                entity.setMsgId(msgId);
                entity.setOppositeId(toUid);
                entity.setFromId(FunctionUtil.uid);
                entity.setToId(toUid);
                entity.setName(nickName);
                entity.setConvType(convType);
                entity.setUnreadMsgNum(0);
                refreshAdapter(entity);
                if (mView != null)
                    mView.clearChatContent();
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
                thumbnailPath = FileUtil.getThumbnailPath(uid, msgId);
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
                ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), "", ChatMsgEntity.Chat_Msg_Type.TYPE_IMAGE, false);
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
        if (mView != null)
            mView.showChatMsgList(msgList);
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
                        Logger.v("msgId:" + textMessage.msgId + "|fromId:" + textMessage.fromuid + "|toId:" + textMessage.touid + "|time:" + textMessage.time);
                        receiveText(textMessage);
                    } else if (NoticeType.audiomessage == message.type) {
                        AudioMessage audioMessage = (AudioMessage) message.message;
                        Logger.v("msgId:" + audioMessage.msgId + "|fromId:" + audioMessage.fromuid + "|toId:" + audioMessage.touid + "|time:" + audioMessage.time);
                        receiveAudio(audioMessage);
                    } else if (NoticeType.filemessage == message.type) {
                        FileMessage fileMessage = (FileMessage) message.message;
                        Logger.v("msgId:" + fileMessage.msgId + "|fromId:" + fileMessage.fromuid + "|toId:" + fileMessage.touid + "|time:" + fileMessage.time);
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
        boolean direct = true;
        if (FunctionUtil.uid.equals(fileMessage.fromuid))
            direct = false;

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMsgId(fileMessage.msgId);
        entity.setFromId(fileMessage.fromuid);
        entity.setToId(toUid);
        entity.setTimestamp(fileMessage.time);
        entity.setImgThumbnail(thumbnailPath);
        entity.setImgMsg(chatJson);
        entity.setDirect(direct);
        entity.setMsgType(ChatMsgEntity.Chat_Msg_Type.TYPE_IMAGE);
        entity.setConvType(fileMessage.convType);

        msgList.add(0, entity);
        historyCount++;
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
            boolean direct = true;
            if (FunctionUtil.uid.equals(audioMessage.fromuid))
                direct = false;

            String touchId = audioMessage.fromuid;
            String audioTime = new String(audioMessage.padding, "utf-8");
            JSONObject object = new JSONObject(audioTime);
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
            entity.setMsgType(ChatMsgEntity.Chat_Msg_Type.TYPE_AUDIO);
            entity.setDirect(direct);
            entity.setMsgId(audioMessage.spanId);
            entity.setFromId(audioMessage.fromuid);
            entity.setToId(toUid);
            entity.setAudioTime(object.getString("duration"));
            entity.setConvType(audioMessage.convType);
            entity.setUnreadMsgNum(0);

            msgList.add(0, entity);
            historyCount++;
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
        boolean direct = true;
        if (FunctionUtil.uid.equals(textMessage.fromuid))
            direct = false;
        ChatMsgEntity entity = new ChatMsgEntity(textMessage.time, textMessage.text, ChatMsgEntity.Chat_Msg_Type.TYPE_TEXT, direct);
        entity.setMsgId(textMessage.msgId);
        entity.setFromId(textMessage.fromuid);
        entity.setToId(toUid);
        entity.setConvType(textMessage.convType);
        entity.setUnreadMsgNum(0);

        msgList.add(0, entity);
        historyCount++;
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
        msgList.add(entity);
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
     * @param touid
     * @param nickName
     * @param convType
     */
    synchronized public void startRealTimeRecord(String touid, String nickName, ConvType convType) {
        String spanId = FunctionUtil.genLocalMsgId();
        String audioName = FileUtil.getUserAudioPath(uid) + spanId + ".amr";
        Logger.v("audioName:" + audioName);
        RecImpl recordCallBack = new RecImpl(spanId, audioName, touid, nickName, convType);
        MediaManager.getMediaPlayManager().setRecordCallBack(recordCallBack);
        MediaManager.getMediaPlayManager().startRealTimeRecord(null, audioName);
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

        public RecImpl(String spanId, String audioName, String toUid, String nickName, ConvType convType) {
            this.spanId = spanId;
            this.audioName = audioName;
            this.toUid = toUid;
            this.nickName = nickName;
            this.convType = convType;
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
            int al = (int) (Math.ceil(seqcount / 2.0));
            if (al <= 0)
                return;
            final String audioLength = String.valueOf(al);
            JSONObject object = new JSONObject();
            try {
                object.put("duration", audioLength);
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
                            ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), audioName, ChatMsgEntity.Chat_Msg_Type.TYPE_AUDIO, false);
                            entity.setMsgId(msgId);
                            entity.setOppositeId(toUid);
                            entity.setFromId(FunctionUtil.uid);
                            entity.setToId(toUid);
                            entity.setName(nickName);
                            entity.setAudioTime(audioLength + "\"");
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
            Logger.v("recordVolumeCallback:" + value);
        }
    }

    class MyInnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (FunctionUtil.MSG_TYPE_RECEIVE_TEXT.equals(action)) {
                Log.v("Bill", "ChatPresenter receive");
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
