package com.ioyouyun.group.view;

import com.ioyouyun.base.BaseView;
import com.ioyouyun.group.model.GroupMemberEntity;

import java.util.List;


/**
 * Created by 卫彪 on 2016/7/8.
 */
public interface GroupMemberView extends BaseView {

    void setListView(List<GroupMemberEntity> list);

}
