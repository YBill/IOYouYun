package com.ioyouyun.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.presenter.GroupInfoPresenter;
import com.ioyouyun.group.view.GroupInfoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupInfoActivity extends BaseActivity<GroupInfoView, GroupInfoPresenter> implements GroupInfoView {

    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.group_name)
    TextView groupName;
    @BindView(R.id.group_manager)
    TextView groupManager;
    @BindView(R.id.group_id)
    TextView groupId;
    @BindView(R.id.group_intra)
    TextView groupIntra;

    public static final int REQUEST_CODE_APPLY = 1001;
    private GroupInfoEntity groupInfoEntity;

    @Override
    protected GroupInfoPresenter initPresenter() {
        return new GroupInfoPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_info;
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
            groupInfoEntity = intent.getParcelableExtra("groupinfo");
        }
    }

    @Override
    protected void initData() {
        if (groupInfoEntity != null)
            tvTopTitle.setText(groupInfoEntity.getName());
        tvTopTitle.setVisibility(View.VISIBLE);
        btnLeft.setVisibility(View.VISIBLE);
        setGroupInfo(groupInfoEntity);
    }

    @Override
    public void widgetClick(View v) {

    }

    private void setGroupInfo(GroupInfoEntity entity) {
        if (entity != null) {
            groupName.setText(entity.getName());
            groupManager.setText("假数据");
            groupId.setText(entity.getGid());
            if (TextUtils.isEmpty(entity.getIntra()))
                groupIntra.setText(getResources().getString(R.string.group_intra_msg));
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @OnClick({R.id.btn_left, R.id.tv_confirm_apply})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.tv_confirm_apply:
                Intent intent = new Intent(this, ApplyAddGroupActivity.class);
                intent.putExtra("gid", groupInfoEntity.getGid());
                startActivityForResult(intent, REQUEST_CODE_APPLY);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_APPLY) {
                finish();
            }
        }
    }
}
