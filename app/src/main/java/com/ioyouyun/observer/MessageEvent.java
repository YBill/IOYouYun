package com.ioyouyun.observer;

import com.ioyouyun.chat.model.ChatMsgEntity;

/**
 * Created by 卫彪 on 2016/8/2.
 *
 * EventBus事件类
 */
public class MessageEvent {

    /**
     * 群组列表Event
     */
    public static class GroupListEvent {
        public String groupId;
    }

    /**
     * 下载大图后更新聊天列表数据
     */
    public static class DownloadImageEvent{
        public ChatMsgEntity chatMsgEntity;
        public int position;
    }

}
