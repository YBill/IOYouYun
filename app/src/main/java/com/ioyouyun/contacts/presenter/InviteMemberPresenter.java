package com.ioyouyun.contacts.presenter;

import android.os.Handler;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.contacts.view.InviteMemberView;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.utils.ParseJson;

/**
 * Created by 卫彪 on 2016/8/15.
 */
public class InviteMemberPresenter extends BasePresenter<InviteMemberView> {

    private GroupRequest request;
    private Handler handler;

    public InviteMemberPresenter(){
        request = new GroupRequestImpl();
        handler = new Handler();
    }

    /**
     * 邀请群成员
     * @param gid
     * @param uids
     */
    public void inviteUsers(String gid, String uids) {
        request.addGroupUsers(Long.parseLong(gid), uids, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                final boolean result = ParseJson.parseCommonResult(response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mView != null)
                            mView.inviteResult(result);
                    }
                });
            }

            @Override
            public void onFaild() {

            }
        });
    }

}
