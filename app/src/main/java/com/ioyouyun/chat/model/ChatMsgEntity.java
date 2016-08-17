package com.ioyouyun.chat.model;

import com.ioyouyun.wchat.message.ConvType;

import java.io.Serializable;

/**
 * Created by 卫彪 on 2016/6/14.
 */
public class ChatMsgEntity implements Serializable {

    public static final int CHAT_TYPE_RECV_TEXT = 0; // 接收文本
    public static final int CHAT_TYPE_SEND_TEXT = 1; // 发送文本
    public static final int CHAT_TYPE_RECV_AUDIO = 2; // 接收语音
    public static final int CHAT_TYPE_SEND_AUDIO = 3; // 发送语音
    public static final int CHAT_TYPE_RECV_IMAGE = 4; // 接收图片
    public static final int CHAT_TYPE_SEND_IMAGE = 5; // 发送图片

    private String msgId; // msg标记唯一一条数据
    private String fromId; // 消息from
    private String toId; // 消息to
    private String oppositeId; // 对方Id,IM其他人uid或gid,用于充当联系人表主键
    private String name; // 昵称
    private long timestamp; // 消息事件,13位
    private String text; // 消息内容
    private String audioTime; // 语音时间
    private String imgThumbnail; // 缩略图
    private String imgMsg; // json
    private ConvType convType;
    private int msgType; // 消息类型，取值为上面static final定义的常量
    private boolean isShowTime; // 是否显示时间标签
    private int unreadMsgNum; // 未读消息数
    private boolean isAudioPlaying; // 是否正在播放语音

    public ChatMsgEntity() {
    }

    public ChatMsgEntity(long timestamp, String text, int msgType) {
        this.timestamp = timestamp;
        this.text = text;
        this.msgType = msgType;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public boolean isAudioPlaying() {
        return isAudioPlaying;
    }

    public void setAudioPlaying(boolean audioPlaying) {
        isAudioPlaying = audioPlaying;
    }

    public boolean isShowTime() {
        return isShowTime;
    }

    public void setShowTime(boolean showTime) {
        isShowTime = showTime;
    }

    public int getShowTimeInt(ChatMsgEntity entity) {
        int showTime = 0; // 不显示
        if (entity.isShowTime)
            showTime = 1; // 显示
        return showTime;
    }

    public boolean getShowTimeBoolean(int showTime) {
        boolean isShowTime = false;
        if (showTime == 1)
            isShowTime = true;
        return isShowTime;
    }

    public int getConvTypeInt(ChatMsgEntity entity) {
        int convType = 0;
        if (entity.getConvType() == ConvType.single)
            convType = 1;
        else if (entity.getConvType() == ConvType.group)
            convType = 2;
        return convType;
    }

    public ConvType getConvTypeEnum(int type) {
        ConvType convType = ConvType.unknown;
        if (type == 1)
            convType = ConvType.single;
        else if (type == 2)
            convType = ConvType.group;
        return convType;
    }

    public int getUnreadMsgNum() {
        return unreadMsgNum;
    }

    public void setUnreadMsgNum(int unreadMsgNum) {
        this.unreadMsgNum = unreadMsgNum;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getOppositeId() {
        return oppositeId;
    }

    public void setOppositeId(String oppositeId) {
        this.oppositeId = oppositeId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAudioTime() {
        return audioTime;
    }

    public void setAudioTime(String audioTime) {
        this.audioTime = audioTime;
    }

    public String getImgThumbnail() {
        return imgThumbnail;
    }

    public void setImgThumbnail(String imgThumbnail) {
        this.imgThumbnail = imgThumbnail;
    }

    public String getImgMsg() {
        return imgMsg;
    }

    public void setImgMsg(String imgMsg) {
        this.imgMsg = imgMsg;
    }

    public ConvType getConvType() {
        return convType;
    }

    public void setConvType(ConvType convType) {
        this.convType = convType;
    }
}
