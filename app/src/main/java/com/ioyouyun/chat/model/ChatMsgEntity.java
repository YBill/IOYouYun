package com.ioyouyun.chat.model;

import com.ioyouyun.wchat.message.ConvType;

import java.io.Serializable;

/**
 * Created by 卫彪 on 2016/6/14.
 */
public class ChatMsgEntity implements Serializable{

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
    private boolean direct; // 消息方向：true:recv false:send
    private ConvType convType;
    private Chat_Msg_Type msgType; // 消息类型
    public enum Chat_Msg_Type{
        TYPE_TEXT,
        TYPE_AUDIO,
        TYPE_IMAGE
    }
    private int unreadMsgNum; // 未读消息数

    public ChatMsgEntity() {
    }

    public ChatMsgEntity(long timestamp, String text, Chat_Msg_Type msgType, boolean direct) {
        this.timestamp = timestamp;
        this.text = text;
        this.msgType = msgType;
        this.direct = direct;
    }

    public int getDirectInt(ChatMsgEntity entity){
        int direct = 0; // send
        if(entity.isDirect())
            direct = 1; // recv
        return direct;
    }

    public boolean getDirectBoolean(int direct){
        boolean isDirect = false;
        if(direct == 1)
            isDirect = true;
        return isDirect;
    }

    public int getMsgTypeInt(ChatMsgEntity entity){
        int msgType = 0;
        if(entity.getMsgType() == ChatMsgEntity.Chat_Msg_Type.TYPE_TEXT)
            msgType = 1;
        else if(entity.getMsgType() == ChatMsgEntity.Chat_Msg_Type.TYPE_AUDIO)
            msgType = 2;
        else if(entity.getMsgType() == ChatMsgEntity.Chat_Msg_Type.TYPE_IMAGE)
            msgType = 3;
        return msgType;
    }

    public Chat_Msg_Type getMsgTypeEnum(int type){
        Chat_Msg_Type chat_msg_type = Chat_Msg_Type.TYPE_TEXT;
        if(type == 1)
            chat_msg_type = Chat_Msg_Type.TYPE_TEXT;
        else if(type == 2)
            chat_msg_type = Chat_Msg_Type.TYPE_AUDIO;
        else if(type == 3)
            chat_msg_type = Chat_Msg_Type.TYPE_IMAGE;
        return chat_msg_type;
    }

    public int getConvTypeInt(ChatMsgEntity entity){
        int convType = 0;
        if(entity.getConvType() == ConvType.single)
            convType = 1;
        else if(entity.getConvType() == ConvType.group)
            convType = 2;
        return convType;
    }

    public ConvType getConvTypeEnum(int type){
        ConvType convType = ConvType.unknown;
        if(type == 1)
            convType = ConvType.single;
        else if(type == 2)
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

    public Chat_Msg_Type getMsgType() {
        return msgType;
    }

    public void setMsgType(Chat_Msg_Type msgType) {
        this.msgType = msgType;
    }

    public boolean isDirect() {
        return direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }

    public ConvType getConvType() {
        return convType;
    }

    public void setConvType(ConvType convType) {
        this.convType = convType;
    }
}
