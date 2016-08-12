package com.ioyouyun.contacts.view;

import com.ioyouyun.base.BaseView;
import com.ioyouyun.contacts.model.NearbyUserEntity;

import java.util.List;


/**
 * Created by luis on 16/6/29.
 */
public interface ContactView extends BaseView {

    /**
     * 刷新adapter
     *
     * @param list
     */
    void setListView(List<NearbyUserEntity> list);

}
