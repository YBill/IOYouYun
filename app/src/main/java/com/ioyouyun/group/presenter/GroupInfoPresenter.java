package com.ioyouyun.group.presenter;

import android.app.Activity;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.group.view.GroupInfoView;

/**
 * Created by 卫彪 on 2016/7/13.
 */
public class GroupInfoPresenter extends BasePresenter<GroupInfoView> {

    private Activity activity;

    public GroupInfoPresenter(Activity activity) {
        this.activity = activity;
    }

}
