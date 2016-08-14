package com.ioyouyun.group.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.contacts.InviteMemberActivity;
import com.ioyouyun.group.adapter.GroupMemberAdapter;
import com.ioyouyun.group.model.GroupMemberEntity;
import com.ioyouyun.group.presenter.GroupMemberPresenter;
import com.ioyouyun.group.view.GroupMemberView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class GroupMemberActivity extends BaseActivity<GroupMemberView, GroupMemberPresenter> implements GroupMemberView {

    @BindView(R.id.lv_group_member)
    ListView lvGroupMember;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private GroupMemberAdapter groupMemberAdapter;
    private String groupId;
    private static final int REQUEST_CODE_INVITE_MEMBER = 1001;

    @Override
    protected GroupMemberPresenter initPresenter() {
        return new GroupMemberPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_member;
    }

    @Override
    protected void setToolBar() {
        super.setToolBar();
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        getIntentExtra();
        setToolBar();
    }

    @Override
    protected void setListener() {

    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null)
            groupId = intent.getStringExtra("gid");
    }

    @Override
    protected void initData() {
        groupMemberAdapter = new GroupMemberAdapter(this);
        lvGroupMember.setAdapter(groupMemberAdapter);

        presenter.getGroupMember(groupId);
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_group_member) {
            Intent intent = new Intent(this, InviteMemberActivity.class);
            intent.putExtra("flag", 1);
            intent.putExtra("gid", groupId);
            startActivityForResult(intent, REQUEST_CODE_INVITE_MEMBER);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshAdapter(List<GroupMemberEntity> list) {
        if (list != null) {
            tvTitle.setText(getResources().getString(R.string.group_member) + "(" + list.size() + ")");
            groupMemberAdapter.setMemberList(list);
        }
        groupMemberAdapter.notifyDataSetChanged();
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
