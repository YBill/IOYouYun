package com.ioyouyun.group.view;


import com.ioyouyun.base.BaseView;
import com.ioyouyun.group.model.GroupInfoEntity;

/**
 * Created by 卫彪 on 2016/7/6.
 */
public interface CreateGroupView extends BaseView {

    /**
     * 创建群成功
     * @param entity
     */
    void createSuccess(GroupInfoEntity entity);

}
