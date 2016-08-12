package com.ioyouyun.group.view;

import com.ioyouyun.base.BaseView;
import com.ioyouyun.group.model.GroupInfoEntity;

import java.util.List;


/**
 * Created by luis on 16/6/29.
 */
public interface GroupView extends BaseView {

    /**
     * 显示Listview
     * @param list
     */
    void setListView(List<GroupInfoEntity> list);

}
