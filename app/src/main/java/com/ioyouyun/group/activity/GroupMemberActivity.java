package com.ioyouyun.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.contacts.InviteMemberActivity;
import com.ioyouyun.group.adapter.GroupMemberAdapter;
import com.ioyouyun.group.model.GroupMemberEntity;
import com.ioyouyun.group.presenter.GroupMemberPresenter;
import com.ioyouyun.group.view.GroupMemberView;
import com.ioyouyun.widgets.LoddingDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class GroupMemberActivity extends BaseActivity<GroupMemberView, GroupMemberPresenter> implements GroupMemberView {

    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.lv_group_member)
    ListView lvGroupMember;

    private LoddingDialog loddingDialog;

    private GroupMemberAdapter groupMemberAdapter;
    private String groupId;
    private static final int REQUEST_CODE_INVITE_MEMBER = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        ButterKnife.bind(this);
        getIntentExtra();
        initData();
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null)
            groupId = intent.getStringExtra("gid");
    }

    private void initData() {
        tvTopTitle.setText(getResources().getString(R.string.group_member));
        tvTopTitle.setVisibility(View.VISIBLE);
        btnLeft.setText(getResources().getString(R.string.btn_back));
        btnLeft.setVisibility(View.VISIBLE);
        btnRight.setText(getResources().getString(R.string.invite));
        btnRight.setVisibility(View.VISIBLE);

        loddingDialog = new LoddingDialog(this);

        groupMemberAdapter = new GroupMemberAdapter(this);
        lvGroupMember.setAdapter(groupMemberAdapter);

        presenter.getGroupMember(groupId);
    }

    private void refreshAdapter(List<GroupMemberEntity> list) {
        if (list != null) {
            tvTopTitle.setText(getResources().getString(R.string.group_member) + "(" + list.size() + ")");
            groupMemberAdapter.setMemberList(list);
        }
        groupMemberAdapter.notifyDataSetChanged();
    }

    @Override
    protected GroupMemberPresenter initPresenter() {
        return new GroupMemberPresenter(this);
    }

    @Override
    public void setListView(List<GroupMemberEntity> list) {
        refreshAdapter(list);
    }

    @Override
    public void showLoading() {
        loddingDialog.showProgress();
    }

    @Override
    public void hideLoading() {
        loddingDialog.cancleProgress();
    }

    @OnClick({R.id.btn_left, R.id.btn_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_right:
                Intent intent = new Intent(this, InviteMemberActivity.class);
                intent.putExtra("flag", 1);
                intent.putExtra("gid", groupId);
                startActivityForResult(intent, REQUEST_CODE_INVITE_MEMBER);
                break;
        }
    }

    @OnItemClick(R.id.lv_group_member)
    public void onItemClick(int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_INVITE_MEMBER) {
                presenter.getGroupMember(groupId);
            }
        }
    }
}
