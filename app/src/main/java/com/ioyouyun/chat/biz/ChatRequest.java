package com.ioyouyun.chat.biz;


import com.ioyouyun.wchat.message.ConvType;
import com.ioyouyun.wchat.util.HttpCallback;

/**
 * Created by YWB on 2016/6/5.
 */
public interface ChatRequest {

    /**
     * 发送文本
     * @param msgId 消息Id
     * @param tid touchId
     * @param text 消息
     * @param type 聊天类型
     * @param padding 额外消息，选填
     * @param listener
     */
    void sendText(String msgId, String tid, String text, ConvType type, byte[] padding, OnChatListener listener);

    /**
     * 发送语音
     * @param msgId 消息Id
     * @param tid uid or gid
     * @param spanId 分片Id
     * @param spanSequenceNo 分片序号
     * @param endTag 结束标记
     * @param audioData 语音数据
     * @param convType 聊天类型
     * @param padding 额外消息
     * @param listener
     */
    void sendVoiceContinue(String msgId, String tid, String spanId, int spanSequenceNo,
                           boolean endTag, byte[] audioData, ConvType convType, byte[] padding, OnChatListener listener);

    /**
     * 发送图片
     * @param msgId 消息Id
     * @param tid uid or gid
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param convType 聊天类型
     * @param padding 额外消息
     * @param thumbnail 缩略图
     * @param listener
     */
    void sendImage(String msgId, String tid, String filePath, String fileName, ConvType convType, byte[] padding, byte[] thumbnail, OnChatListener listener);

    /**
     * 下载大图
     * @param fileId 文件Id
     * @param downloadPath 下载到
     * @param fileLength 文件大小
     * @param pieceSize 分片大小
     * @param listener
     */
    void downloadImage(String fileId, String downloadPath, int fileLength, int pieceSize, OnChatListener listener);

    void getChatHistory(String toUid, long timestamp, int num, ConvType convType, HttpCallback listener);

}
