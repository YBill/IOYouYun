package com.ioyouyun.contacts;

import android.os.Bundle;
import android.view.View;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.contacts.presenter.InviteMemberPresenter;
import com.ioyouyun.contacts.view.InviteMemberView;
import com.ioyouyun.home.fragment.ContactsFragment;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.wchat.WeimiInstance;

import java.util.List;

public class InviteMemberActivity extends BaseActivity<InviteMemberView, InviteMemberPresenter> implements InviteMemberView {

    private ContactsFragment contactsFragment;
    private View confirmInvite;
    private String groupId;
    private int flag; // 1：群组邀请人 2：conference邀请人
    private String invitedRoomId;
    private String invitedRoomKey;

    @Override
    protected InviteMemberPresenter initPresenter() {
        return new InviteMemberPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_invite_member;
    }

    @Override
    protected void $setToolBar() {
        super.$setToolBar();
    }

    @Override
    protected void initView() {
        $setToolBar();

        contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_contact);
        confirmInvite = $findViewById(R.id.tv_confirm_invite);
    }

    @Override
    protected void setListener() {
        confirmInvite.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Bundle bundle = $getIntentExtra();
        flag = bundle.getInt(KEY_FLAG);
        groupId = bundle.getString(KEY_GID);
        invitedRoomId = bundle.getString(KEY_ROOMID);
        invitedRoomKey = bundle.getString(KEY_KEY);
    }

    @Override
    public void widgetClick(View v) {
        if (flag == 1) {
            String uids = contactsFragment.getGroupInviteList();
            if (uids != null && !"".equals(uids))
                presenter.inviteUsers(groupId, uids);
        } else if (flag == 2) {
            List<String> list = contactsFragment.getConferenceInviteList();
            WeimiInstance.getInstance().conferenceInviteUsers(list, groupId, invitedRoomId, invitedRoomKey);

            setResult(RESULT_OK);
            InviteMemberActivity.this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        contactsFragment.loadFragmentData(2);
    }

    @Override
    public void inviteResult(boolean result) {
        if (result) {
            InviteMemberActivity.this.finish();
        } else
            FunctionUtil.toastMessage("邀请失败");
    }
}
