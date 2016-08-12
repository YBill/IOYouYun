package com.ioyouyun.group.view;


import com.ioyouyun.base.BaseView;
import com.ioyouyun.group.model.GroupInfoEntity;

/**
 * Created by 卫彪 on 2016/7/7.
 */
public interface GroupSettingView extends BaseView {

    /**
     * 设置群信息
     * @param entity
     */
    void setGroupInfo(GroupInfoEntity entity);

    /**
     * 退群
     * @param gid
     * @param result
     */
    void exitGroup(String gid, boolean result);

    /**
     * 解散群
     * @param gid
     * @param result
     */
    void delGroup(String gid, boolean result);

}
