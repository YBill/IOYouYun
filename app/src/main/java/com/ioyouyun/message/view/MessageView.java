package com.ioyouyun.message.view;

import com.ioyouyun.chat.model.ChatMsgEntity;

import java.util.List;


/**
 * Created by 卫彪 on 2016/6/20.
 */
public interface MessageView {

    /**
     * 刷新list
     *
     * @param list
     */
    void refreshList(List<ChatMsgEntity> list);

    /**
     * 显示通知
     *
     * @param count
     */
    void showNotify(long count);

    /**
     * 刷新MessageFragment listview
     *
     * @param entity
     */
    void refreshMessageList(ChatMsgEntity entity);

    /**
     * 刷新消息栏的通知
     */
    void refreshMessageNotify();
}
