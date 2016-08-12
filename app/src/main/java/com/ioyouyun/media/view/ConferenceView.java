package com.ioyouyun.media.view;

import java.util.List;

/**
 * Created by 卫彪 on 2016/7/19.
 */
public interface ConferenceView {

    /**
     * 通话时间
     *
     * @param time
     */
    void setCallTime(String time);

    /**
     * 显示成员列表
     *
     * @param list
     */
    void setListView(List<String> list);

    /**
     * 获取房间列表
     */
    void getRoomList();

    /**
     * 扬声器
     * @param isSpeaker
     */
    void toggleSpeaker(boolean isSpeaker);

    /**
     * 麦克风
     * @param isMicMut
     */
    void toggleMicro(boolean isMicMut);

}
