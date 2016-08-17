package com.ioyouyun.receivemsg;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.chat.model.ChatPicInfo;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.media.BeInviteActivity;
import com.ioyouyun.media.IncomingCallActivity;
import com.ioyouyun.receivemsg.msg.MsgBodyTemplate;
import com.ioyouyun.receivemsg.msg.MsgUtil;
import com.ioyouyun.utils.FileUtil;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.utils.ParseJson;
import com.ioyouyun.wchat.message.AudioMessage;
import com.ioyouyun.wchat.message.CmdType;
import com.ioyouyun.wchat.message.ConvType;
import com.ioyouyun.wchat.message.FileMessage;
import com.ioyouyun.wchat.message.NoticeType;
import com.ioyouyun.wchat.message.NotifyCenter;
import com.ioyouyun.wchat.message.SendBackMessage;
import com.ioyouyun.wchat.message.SystemMessage;
import com.ioyouyun.wchat.message.TextMessage;
import com.ioyouyun.wchat.message.WChatException;
import com.ioyouyun.wchat.message.WeimiNotice;

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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 卫彪 on 2016/6/6.
 */
public class ReceiveRunnable implements Runnable {

    private static Object audioLockObj = new Object();
    public static Map<String, List<AudioMessage>> audioMessageReceive = new ConcurrentHashMap<>();
    public static Map<String, List<Integer>> fileSend = new ConcurrentHashMap<>();
    public static Map<String, Integer> fileSendCount = new ConcurrentHashMap<>();
    private Context context;
    private Handler handler = new Handler();

    public ReceiveRunnable(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        WeimiNotice weimiNotice = null;
        while (true) {
            try {
                weimiNotice = (WeimiNotice) NotifyCenter.clientNotifyChannel.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            NoticeType type = weimiNotice.getNoticeType();
            Logger.v("消息类型:" + type);

//            handleExtendActionForMsgReceive(weimiNotice);

            if (NoticeType.textmessage == type) {
                textMessageMethod(weimiNotice);
            } else if (NoticeType.audiomessage == type) {
                audioMessageMethod(weimiNotice);
            } else if (NoticeType.filemessage == type) {
                fileMessageMethod(weimiNotice);
            } else if (NoticeType.downloadfile == type) {
                downloadMethod(weimiNotice);
            } else if (NoticeType.wmediastate == type) {
                callMethod(weimiNotice);
            } else if (NoticeType.conferenceResources == type) {
                conferenceMethod(weimiNotice);
            } else if (NoticeType.exception == type) {
                exceptionMethod(weimiNotice);
            } else if (NoticeType.logging == type) {
                loggingMethod(weimiNotice);
            } else if (NoticeType.sendfinished == type) {
                Log.v("Tag", "消息发送到网关成功,msgId:" + weimiNotice.getWithtag());
            } else if (NoticeType.sendback == type) {
                SendBackMessage sendBackMessage = (SendBackMessage) weimiNotice.getObject();
                Log.v("Tag", "发送消息成功,msgId:" + weimiNotice.getWithtag() + "|id:" + sendBackMessage.withtag);
            } else {
                Logger.v("noticeType:" + type);
            }

        }

    }

    /**
     * log
     *
     * @param weimiNotice
     */
    private void loggingMethod(WeimiNotice weimiNotice) {
        String log = weimiNotice.getObject().toString();
        Logger.v("logging:" + log);
    }

    /**
     * 异常处理
     *
     * @param weimiNotice
     */
    private void exceptionMethod(WeimiNotice weimiNotice) {
        WChatException wChatException = (WChatException) weimiNotice.getObject();
        int statusCode = wChatException.getStatusCode();
        switch (statusCode) {
            case WChatException.InputParamError: // 输入参数错误
                Logger.v("输入参数错误:" + wChatException.getMessage());
                break;
            case WChatException.CodeException: // 编码处理错误
                Logger.v("编码处理错误:" + wChatException.getMessage());
                Logger.v("编码处理错误:" + wChatException.getCause());
                break;
            case WChatException.InterruptedException: // 请求被中断
                Logger.v("请求被中断:" + wChatException.getMessage());
                Logger.v("请求被中断:" + wChatException.getCause());
                break;
            case WChatException.RequestTimeout: // 会话请求超时
                Logger.v("会话请求超时:" + wChatException.getMessage());
                break;
            case WChatException.BadFileRead: // 文件读取失败
                Logger.v("文件读取失败:" + wChatException.getMessage());
                Logger.v("文件读取失败:" + wChatException.getCause());
                break;
            case WChatException.ResponseError: // 返回结果错误
                Logger.v("返回结果错误:" + wChatException.getMessage());
                Logger.v("返回结果错误:" + wChatException.getCause());
                break;
            case WChatException.AuthFailed: // 认证失败
                Logger.v("认证失败:" + wChatException.getMessage());
                break;
            case WChatException.NetworkInterruption: // 网络中断
                Logger.v("网络中断:" + "NetworkInterruption");
                Logger.v("网络中断:" + wChatException.getMessage());
                break;
            case WChatException.MediaHeartBeatError: // mediaSDK心跳失败
                Logger.v("mediaSDK心跳失败:" + wChatException.getMessage());
                break;
            case WChatException.DataDecodeError: // 数据解析异常
                Logger.v("数据解析异常:" + wChatException.getMessage());
                break;
            case WChatException.SOCKET_CHANNEL_CLOSED: // 网络socket端开
                Logger.v("网络socket端开:" + wChatException.getMessage());
                break;
            default:
                Logger.v("WChatException:" + wChatException.getMessage());
                Logger.v("WChatException:" + wChatException.getStatusCode());
        }
    }

    /**
     * Conference
     *
     * @param weimiNotice
     */
    private void conferenceMethod(WeimiNotice weimiNotice) {
        String content = (String) weimiNotice.getObject();
        try {
            JSONObject jsonObject = new JSONObject(content);
            if (jsonObject == null || !jsonObject.has("cmd"))
                return;
            switch (jsonObject.getInt("cmd")) {
                case CmdType.requesRoom:
                    Logger.v("requesRoom");
                    Intent intent = new Intent();
                    intent.setAction(FunctionUtil.CONFERENCE_REQUEST_ROOM);
                    intent.putExtra(FunctionUtil.CONTENT, content);
                    intent.setPackage(context.getPackageName());
                    BroadCastCenter.getInstance().broadcast(intent);
                    break;
                case CmdType.beingInvited:
                    Logger.v("beingInvited");
                    String currentActivity = FunctionUtil.getCurrentActivity();
                    if (FunctionUtil.BEINVITEACTIVITYACTIVITY_PATH.equals(currentActivity) ||
                            FunctionUtil.CONFERENCEACTIVITY_PATH.equals(currentActivity) ||
                            FunctionUtil.VOIPACTIVITY_PATH.equals(currentActivity)) {
                        return;
                    }
                    String invitedFrom = jsonObject.getString("from");
                    String invitedGroupId = jsonObject.getString("groupid");
                    String invitedTo = jsonObject.getString("to");
                    JSONObject room2 = new JSONObject(jsonObject.getString("room"));
                    String invitedRoomId = room2.getString("id");
                    String invitedRoomKey = room2.getString("key");

                    Intent beInviteIntent = new Intent(context, BeInviteActivity.class);
                    beInviteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    beInviteIntent.putExtra("invitedFrom", invitedFrom);
                    beInviteIntent.putExtra("invitedRoomId", invitedRoomId);
                    beInviteIntent.putExtra("invitedRoomKey", invitedRoomKey);
                    beInviteIntent.putExtra("invitedGroupId", invitedGroupId);
                    context.startActivity(beInviteIntent);
                    break;
                case CmdType.inviteUsers:
                    Logger.v("inviteUsers");
                    break;
                case CmdType.list:
                    Logger.v("list");
                    Intent listIntent = new Intent();
                    listIntent.setAction(FunctionUtil.CONFERENCE_LIST);
                    listIntent.putExtra(FunctionUtil.CONTENT, content);
                    listIntent.setPackage(context.getPackageName());
                    BroadCastCenter.getInstance().broadcast(listIntent);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * VoIP
     *
     * @param weimiNotice
     */
    private void callMethod(WeimiNotice weimiNotice) {
        String mediaType = weimiNotice.getObject().toString();
        if (FunctionUtil.MSG_TYPE_INCOMINGRECEIVED.equals(mediaType)) {
            String currentActivity = FunctionUtil.getCurrentActivity();
            if (FunctionUtil.VOIPACTIVITY_PATH.equals(currentActivity) ||
                    FunctionUtil.CONFERENCEACTIVITY_PATH.equals(currentActivity)) {
                return;
            }
            Intent intent = new Intent(context, IncomingCallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(FunctionUtil.INCOMINGNAME, weimiNotice.getWithtag());
            context.startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setAction(FunctionUtil.MSG_TYPE_MEDIA_CALL_HEAD + mediaType);
            intent.putExtra(FunctionUtil.INCOMINGNAME, weimiNotice.getWithtag());
            intent.setPackage(context.getPackageName());
            BroadCastCenter.getInstance().broadcast(intent);
        }
    }

    /**
     * 处理额外事件
     *
     * @param weimiNotice
     */
    private void handleExtendActionForMsgReceive(WeimiNotice weimiNotice) {
        long time = System.currentTimeMillis();
        String msg = null;
        int msgType = 0;
        if (weimiNotice.getNoticeType() == NoticeType.system) {
            SystemMessage message = (SystemMessage) weimiNotice.getObject();
            msg = message.content;
            time = message.time;
            msgType = MsgUtil.getSysMsgType(msg);
        } else if (weimiNotice.getNoticeType() == NoticeType.textmessage
                || weimiNotice.getNoticeType() == NoticeType.mixedtextmessage) {
            TextMessage message = (TextMessage) weimiNotice.getObject();
            msg = message.text;
            time = message.time;
            if(msg != null)
                msgType = MsgUtil.getSysMsgType(msg);
        }
        if (msgType != 0 && msg != null) {
            if (msgType == MsgUtil.MSGT_SYS_NOTI) {// 50002
                String subType = MsgUtil.getSysMsgBodyType(msg);
                if (subType.equals(MsgUtil.MSN_TYPE_APPLY_JOIN_GROUP)) {
                    // 申请加入群
                    JSONObject obj = MsgUtil.getSysMsgBody(msg);
                    MsgBodyTemplate entity = ParseJson.parseJson2T(obj, MsgBodyTemplate.class);
                    if (entity != null) {
                        entity.setDate(getDate(time));

                        String currentActivity = FunctionUtil.getCurrentActivity();
                        if (FunctionUtil.NOTIFYACTIVITYACTIVITY_PATH.equals(currentActivity)) {
                            entity.setIsLook(0);
                        } else {
                            entity.setIsLook(1);
                        }
                        YouyunDbManager.getIntance().insertNotify(entity);

                        Intent intent = new Intent();
                        intent.setAction(FunctionUtil.MSG_TYPE_NOTIFY);
//                    intent.putExtra(FunctionUtil.TYPE_NOTIFY, entity);
                        intent.setPackage(context.getPackageName());
                        BroadCastCenter.getInstance().broadcast(intent);
                    }
                }
            }
        }
    }

    /**
     * 下载大图
     *
     * @param weimiNotice
     */
    private void downloadMethod(WeimiNotice weimiNotice) {
        FileMessage fileMessage = (FileMessage) weimiNotice.getObject();
        String fileId = weimiNotice.getWithtag();
        double completed = (fileMessage.hasReveive.size() / (double) fileMessage.limit);
        int progress = (int) (completed * 100);
        Intent intent = new Intent();
        intent.setAction(FunctionUtil.MSG_TYPE_DOWNLOAD_IMAGE);
        intent.putExtra(FunctionUtil.DOWNLOAD_FILEID, fileId);
        intent.putExtra(FunctionUtil.DOWNLOAD_PROGRESS, progress);
        intent.setPackage(context.getPackageName());
        BroadCastCenter.getInstance().broadcast(intent);
    }

    /**
     * 图片
     *
     * @param weimiNotice
     */
    private void fileMessageMethod(WeimiNotice weimiNotice) {
        FileMessage fileMessage = (FileMessage) weimiNotice.getObject();

        String nickName = "";
        try {
            String audioTime = new String(fileMessage.padding, "utf-8");
            JSONObject object = new JSONObject(audioTime);
            nickName = object.getString("nickname");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String thumbnailPath = "";
        if (null != fileMessage.thumbData) {
            thumbnailPath = FileUtil.getThumbnailPath(fileMessage.fromuid, fileMessage.msgId);
            FileUtil.saveImg(fileMessage.thumbData, thumbnailPath); //保存缩略图
        }
        ChatPicInfo chatPicInfo = new ChatPicInfo(fileMessage.fileId, null, fileMessage.fileLength, fileMessage.pieceSize);
        String chatJson = chatPicInfo.getJsonStr(chatPicInfo);

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMsgId(fileMessage.msgId);
        entity.setName(nickName);
        entity.setFromId(fileMessage.fromuid);
        String toUid = fileMessage.touid;
        if (ConvType.group == fileMessage.convType)
            entity.setToId(toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$")));
        else
            entity.setToId(toUid);
        entity.setTimestamp(fileMessage.time);
        entity.setImgThumbnail(thumbnailPath);
        entity.setImgMsg(chatJson);
        entity.setMsgType(ChatMsgEntity.CHAT_TYPE_RECV_IMAGE);
        entity.setConvType(fileMessage.convType);

        notify(FunctionUtil.MSG_TYPE_RECEIVE_IMAGE, FunctionUtil.TYPE_IMAGE, entity);
    }

    /**
     * 语音消息
     *
     * @param weimiNotice
     */
    private void audioMessageMethod(WeimiNotice weimiNotice) {
        AudioMessage audioMessage = (AudioMessage) weimiNotice.getObject();
        if (audioMessage.isSpan) {
            receiveContinue(audioMessage);
        }
    }

    /**
     * 文本消息
     *
     * @param weimiNotice
     */
    private void textMessageMethod(WeimiNotice weimiNotice) {
        TextMessage textMessage = (TextMessage) weimiNotice.getObject();
        Log.v("Bill", "textMessageMethod:" + textMessage.text + "|fromId:" + textMessage.fromuid + "|toId:" + textMessage.touid);

        String nickName = "";
        try {
            String audioTime = new String(textMessage.padding, "utf-8");
            JSONObject object = new JSONObject(audioTime);
            nickName = object.getString("nickname");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMsgId(textMessage.msgId);
        entity.setName(nickName);
        entity.setFromId(textMessage.fromuid);
        String toUid = textMessage.touid;
        if (ConvType.group == textMessage.convType) {
            entity.setToId(toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$")));
        }
        else {
            entity.setToId(toUid);
        }
        entity.setTimestamp(textMessage.time);
        entity.setText(textMessage.text);
        entity.setMsgType(ChatMsgEntity.CHAT_TYPE_RECV_TEXT);
        entity.setConvType(textMessage.convType);

        notify(FunctionUtil.MSG_TYPE_RECEIVE_TEXT, FunctionUtil.TYPE_TEXT, entity);
    }

    private void notify(String action, String key, ChatMsgEntity entity) {
        String toid = entity.getFromId();
        if (ConvType.group == entity.getConvType())
            toid = entity.getToId();
        entity.setOppositeId(toid);

        String name = FunctionUtil.jointTableName(toid);

        String currentActivity = FunctionUtil.getCurrentActivity();
        if (FunctionUtil.CHATACTIVITYACTIVITY_PATH.equals(currentActivity)) {
            entity.setUnreadMsgNum(0);
        } else {
            ChatMsgEntity chatMsgEntity = YouyunDbManager.getIntance().getRecentContactById(toid);
            if (chatMsgEntity == null) {
                entity.setUnreadMsgNum(1);
            } else {
                entity.setUnreadMsgNum(chatMsgEntity.getUnreadMsgNum() + 1);
            }

            ChatMsgEntity lastEntity = YouyunDbManager.getIntance().getLastChatMsgEntity(name);
            if(lastEntity == null || lastEntity.getTimestamp() <= 0){
                entity.setShowTime(true);
            }else{
                if(entity.getTimestamp() - lastEntity.getTimestamp() > FunctionUtil.MSG_TIME_SEPARATE){
                    entity.setShowTime(true);
                }
            }

        }

        setBroadCast(action, key, entity);

        YouyunDbManager.getIntance().insertChatMessage(entity, name);
        YouyunDbManager.getIntance().insertRecentContact(entity);
    }

    /**
     * 发送广播
     *
     * @param action
     * @param key
     * @param entity
     */
    private void setBroadCast(String action, String key, ChatMsgEntity entity) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(key, entity);
        intent.setPackage(context.getPackageName());
        BroadCastCenter.getInstance().broadcast(intent);
    }

    private String getDate(long date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date));
    }

    private void receiveContinue(AudioMessage audioMessage) {
        FileOutputStream fis = null;
        try {
            List<AudioMessage> spanList;
            if (audioMessageReceive.containsKey(audioMessage.spanId)) {
                spanList = audioMessageReceive.get(audioMessage.spanId);
            } else {
                spanList = new ArrayList<>();
            }
            spanList.add(audioMessage);
            audioMessageReceive.put(audioMessage.spanId, spanList);
            if(audioMessage.spanSequenceNo == Integer.MAX_VALUE){
                audioMessageReceive.remove(audioMessage.spanId);
            } else if (audioMessage.spanSequenceNo == -1) {
                Logger.v("语音接收结束");

                String touchId = audioMessage.fromuid;
                String audioTime = new String(audioMessage.padding, "utf-8");
                JSONObject object = new JSONObject(audioTime);
                String duration = object.getString("duration");
                String nickName = object.getString("nickname");

                String filePath = FileUtil.getUserAudioPath(touchId);
                String audioName = filePath + audioMessage.spanId + ".amr";

                synchronized (audioLockObj) {
                    fis = new FileOutputStream(new File(audioName), true);
                    for (Map.Entry<String, List<AudioMessage>> entry : audioMessageReceive.entrySet()) {
                        for (AudioMessage am : entry.getValue()) {
                            if (am != null && am.audioData != null)
                                fis.write(am.audioData);
                        }

                    }
                }

                audioMessageReceive.remove(audioMessage.spanId);

                Logger.v("语音存储在：" + audioName);

                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setMsgId(audioMessage.spanId);
                entity.setFromId(audioMessage.fromuid);
                entity.setName(nickName);
                String toUid = audioMessage.touid;
                if (ConvType.group == audioMessage.convType)
                    entity.setToId(toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$")));
                else
                    entity.setToId(toUid);
                entity.setText(audioName);
                entity.setTimestamp(audioMessage.time);
                entity.setAudioTime(duration);
                entity.setMsgType(ChatMsgEntity.CHAT_TYPE_RECV_AUDIO);
                entity.setConvType(audioMessage.convType);

                notify(FunctionUtil.MSG_TYPE_RECEIVE_AUDIO, FunctionUtil.TYPE_AUDIO, entity);
            }
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

}
