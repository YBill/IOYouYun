package com.ioyouyun.group.activity;

import android.os.Bundle;
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

public class GroupMemberActivity extends BaseActivity<GroupMemberView, GroupMemberPresenter> implements GroupMemberView {

    @BindView(R.id.lv_group_member)
    ListView lvGroupMember;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private GroupMemberAdapter groupMemberAdapter;
    private String groupId;

    @Override
    protected GroupMemberPresenter initPresenter() {
        return new GroupMemberPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getGroupMember(groupId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_member;
    }

    @Override
    protected void $setToolBar() {
        super.$setToolBar();
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        $setToolBar();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        groupMemberAdapter = new GroupMemberAdapter(this);
        lvGroupMember.setAdapter(groupMemberAdapter);

        Bundle bundle = $getIntentExtra();
        if(null != bundle)
            groupId = bundle.getString(KEY_GID);
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
            Bundle bundle = new Bundle();
            bundle.putInt(KEY_FLAG, 1);
            bundle.putString(KEY_GID, groupId);
            $startActivity(InviteMemberActivity.class, bundle);
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

}
