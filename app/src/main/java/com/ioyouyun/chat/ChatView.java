package com.ioyouyun.chat;

import com.ioyouyun.base.BaseView;
import com.ioyouyun.chat.model.ChatMsgEntity;

import java.util.List;


/**
 * Created by YWB on 2016/6/5.
 */
public interface ChatView extends BaseView {

    /**
     * listview显示数据
     *
     * @param list
     */
    void showChatMsgList(List<ChatMsgEntity> list);

    /**
     * 设置ListView光标位置
     *
     * @param position
     */
    void setChatSelection(int position);

    /**
     * 清空聊天输入框
     */
    void clearChatContent();

    /**
     * 加载完成
     */
    void onCompleteLoad();
}
