package com.ioyouyun.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.chat.ChatActivity;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.presenter.CreateGroupPresenter;
import com.ioyouyun.group.view.CreateGroupView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.widgets.LoddingDialog;

public class CreateGroupActivity extends BaseActivity<CreateGroupView, CreateGroupPresenter> implements CreateGroupView, View.OnClickListener {

    private TextView topTitleText; // title
    private Button backBtn; // 返回
    private View createBtn;
    private LoddingDialog loddingDialog;
    private EditText groupNameEdit;
    private EditText groupIntraEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
//        initView();
//        addListener();
        initDatas();
    }

    @Override
    protected void initView() {
        topTitleText = (TextView) findViewById(R.id.tv_top_title);
        backBtn = (Button) findViewById(R.id.btn_left);
        createBtn = findViewById(R.id.tv_confirm_create);
        groupNameEdit = (EditText) findViewById(R.id.et_group_name);
        groupIntraEdit = (EditText) findViewById(R.id.et_group_intra);
    }

    @Override
    protected void setListener() {
        backBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);
    }

    private void initDatas() {
        topTitleText.setText(getResources().getString(R.string.create_group));
        topTitleText.setVisibility(View.VISIBLE);
        backBtn.setText(getResources().getString(R.string.btn_back));
        backBtn.setVisibility(View.VISIBLE);

        loddingDialog = new LoddingDialog(this);
    }

   /* private void addListener() {
        backBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);
    }*/

  /*  private void initView() {
        topTitleText = (TextView) findViewById(R.id.tv_top_title);
        backBtn = (Button) findViewById(R.id.btn_left);
        createBtn = findViewById(R.id.tv_confirm_create);
        groupNameEdit = (EditText) findViewById(R.id.et_group_name);
        groupIntraEdit = (EditText) findViewById(R.id.et_group_intra);
    }*/

    @Override
    protected CreateGroupPresenter initPresenter() {
        return new CreateGroupPresenter(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.tv_confirm_create:
                String name = groupNameEdit.getText().toString().trim();
                String intra = groupIntraEdit.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    FunctionUtil.toastMessage("请输入群名称");
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
