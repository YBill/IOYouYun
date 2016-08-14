package com.ioyouyun.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.contacts.InviteMemberActivity;
import com.ioyouyun.media.adapter.MemberListAdapter;
import com.ioyouyun.media.presenter.ConferencePresenter;
import com.ioyouyun.media.view.ConferenceView;
import com.ioyouyun.utils.FunctionUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConferenceActivity extends BaseActivity<ConferenceView, ConferencePresenter> implements ConferenceView {

    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.tv_user_info)
    TextView tvUserInfo;
    @BindView(R.id.tv_call_time)
    TextView tvCallTime;
    @BindView(R.id.iv_calling_mute)
    ImageView ivCallingMute;
    @BindView(R.id.iv_calling_speaker)
    ImageView ivCallingSpeaker;
    @BindView(R.id.tv_userlist_des)
    TextView tvUserlistDes;
    @BindView(R.id.lv_user_member)
    ListView lvUserMember;

    private static final int REQUEST_CODE_INVITE_MEMBER = 1001;
    private String invitedRoomId;
    private String invitedRoomKey;
    private String invitedGroupId;
    private MemberListAdapter adapter;

    @Override
    protected ConferencePresenter initPresenter() {
        return new ConferencePresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_in_call;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        getIntentExtra();
    }

    @Override
    protected void setListener() {

    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            invitedRoomId = intent.getStringExtra("invitedRoomId");
            invitedRoomKey = intent.getStringExtra("invitedRoomKey");
            invitedGroupId = intent.getStringExtra("invitedGroupId");
        }
    }

    @Override
    protected void initData() {
        tvTopTitle.setText(getResources().getString(R.string.more_conference));
        tvTopTitle.setVisibility(View.VISIBLE);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setText(getResources().getString(R.string.hangup_leave));

        setListNumber(0);

        String format2 = getResources().getString(R.string.user_info);
        String userInfo = String.format(format2, FunctionUtil.uid, FunctionUtil.uid, invitedRoomId);
        tvUserInfo.setText(userInfo);

        adapter = new MemberListAdapter(this);
        lvUserMember.setAdapter(adapter);
    }

    @Override
    public void widgetClick(View v) {

    }

    private void setListNumber(int number) {
        String format = getResources().getString(R.string.user_list);
        String member = String.format(format, number);
        tvUserlistDes.setText(member);
    }

    private void refreshAdapter(List<String> list) {
        adapter.setMemberList(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }

    @OnClick({R.id.btn_left, R.id.tv_invite_member, R.id.iv_calling_mute, R.id.iv_calling_speaker, R.id.tv_refresh_member})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                presenter.hangUp();
                finish();
                break;
            case R.id.tv_invite_member:
                Intent intent = new Intent(this, InviteMemberActivity.class);
                intent.putExtra("flag", 2);
                intent.putExtra("invitedRoomId", invitedRoomId);
                intent.putExtra("invitedRoomKey", invitedRoomKey);
                intent.putExtra("invitedGroupId", invitedGroupId);
                startActivityForResult(intent, REQUEST_CODE_INVITE_MEMBER);
                break;
            case R.id.tv_refresh_member:
                getRoomList();
                break;
            case R.id.iv_calling_mute: // 麦
                presenter.toggleMicro();
                break;
            case R.id.iv_calling_speaker: // 听筒(true开启扬声器)
                presenter.toggleSpeaker();
                break;
        }
    }

    @Override
    public void setCallTime(String time) {
        tvCallTime.setText(time);
    }

    @Override
    public void setListView(List<String> list) {
        setListNumber(list.size());
        refreshAdapter(list);
    }

    @Override
    public void getRoomList() {
        presenter.refreshMember(invitedRoomId, invitedGroupId);
    }

    @Override
    public void toggleSpeaker(boolean isSpeaker) {
        if (isSpeaker) {
            ivCallingSpeaker.setImageResource(R.drawable.speaker);
        } else {
            ivCallingSpeaker.setImageResource(R.drawable.nospeaker);
        }
    }

    @Override
    public void toggleMicro(boolean isMicMut) {
        if (isMicMut) {
            ivCallingMute.setImageResource(R.drawable.mute);
        } else {
            ivCallingMute.setImageResource(R.drawable.nomute);
        }
    }
}
