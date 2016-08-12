package com.ioyouyun.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.ioyouyun.R;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.home.fragment.ContactsFragment;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.ParseJson;
import com.ioyouyun.wchat.WeimiInstance;

import java.util.List;

public class InviteMemberActivity extends FragmentActivity {

    private ContactsFragment contactsFragment;
    private View confirmInvite;
    private GroupRequest request;
    private Handler handler = new Handler();
    private String groupId;
    private int flag; // 1：群组邀请人 2：conference邀请人
    private String invitedRoomId;
    private String invitedRoomKey;
    private String invitedGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);
        contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_contact);
        confirmInvite = findViewById(R.id.tv_confirm_invite);
        confirmInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 1) {
                    String uids = contactsFragment.getGroupInviteList();
                    if (uids != null && !"".equals(uids))
                        inviteUsers(uids);
                } else if (flag == 2) {
                    List<String> list = contactsFragment.getConferenceInviteList();
                    WeimiInstance.getInstance().conferenceInviteUsers(list, invitedGroupId, invitedRoomId, invitedRoomKey);

                    setResult(RESULT_OK);
                    InviteMemberActivity.this.finish();
                }
            }
        });
        request = new GroupRequestImpl();
        getIntentExtra();
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            flag = intent.getIntExtra("flag", 0);
            groupId = intent.getStringExtra("gid");
            invitedRoomId = intent.getStringExtra("invitedRoomId");
            invitedRoomKey = intent.getStringExtra("invitedRoomKey");
            invitedGroupId = intent.getStringExtra("invitedGroupId");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        contactsFragment.loadFragmentData(2);
    }

    private void inviteUsers(String uids) {
        request.addGroupUsers(Long.parseLong(groupId), uids, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                final boolean result = ParseJson.parseCommonResult(response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result) {
                            setResult(RESULT_OK);
                            InviteMemberActivity.this.finish();
                        } else
                            FunctionUtil.toastMessage("邀请失败");
                    }
                });
            }

            @Override
            public void onFaild() {

            }
        });
    }

}
