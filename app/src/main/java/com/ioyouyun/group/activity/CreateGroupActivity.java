package com.ioyouyun.group.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.chat.ChatActivity;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.presenter.CreateGroupPresenter;
import com.ioyouyun.group.view.CreateGroupView;

public class CreateGroupActivity extends BaseActivity<CreateGroupView, CreateGroupPresenter> implements CreateGroupView {

    private Button createBtn;
    private EditText groupNameEdit;
    private EditText groupIntraEdit;

    @Override
    protected CreateGroupPresenter initPresenter() {
        return new CreateGroupPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_group;
    }

    @Override
    protected void setToolBar() {
        super.setToolBar();
    }

    @Override
    protected void initView() {
        setToolBar();

        createBtn = findView(R.id.tv_confirm_create);
        groupNameEdit = findView(R.id.et_group_name);
        groupIntraEdit = findView(R.id.et_group_intra);
    }

    @Override
    protected void setListener() {
        createBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        loddingDialog.setMessage("创建中...");
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm_create:
                String name = groupNameEdit.getText().toString().trim();
                String intra = groupIntraEdit.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    groupNameEdit.setError("请输入群名称");
                    return;
                }
                presenter.createGroup(name, intra);
                break;
        }
    }

    @Override
    public void createSuccess(GroupInfoEntity entity) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("toUid", entity.getGid());
        intent.putExtra("nickName", entity.getName());
        intent.putExtra("chatType", 1);
        startActivity(intent);
        CreateGroupActivity.this.finish();
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
