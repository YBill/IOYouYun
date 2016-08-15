package com.ioyouyun.group.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.presenter.GroupSettingPresenter;
import com.ioyouyun.group.view.GroupSettingView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.widgets.LoddingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupSettingActivity extends BaseActivity<GroupSettingView, GroupSettingPresenter> implements GroupSettingView {

    @BindView(R.id.tv_group_name)
    TextView tvGroupName;
    @BindView(R.id.tv_group_intra)
    TextView tvGroupIntra;
    @BindView(R.id.tv_exit_group)
    TextView tvExitGroup;
    @BindView(R.id.tbtn_msg_push)
    ToggleButton tbtnMsgPush;
    @BindView(R.id.tbtn_msg_no_disturb)
    ToggleButton tbtnMsgNoDisturb;

    private LoddingDialog loddingDialog;
    private String groupId;
    private int role; // 群权限

    @Override
    protected GroupSettingPresenter initPresenter() {
        return new GroupSettingPresenter(this);
    }

    @Override
    protected void $setToolBar() {
        super.$setToolBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_setting;
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
        loddingDialog = new LoddingDialog(this);

        Bundle bundle = $getIntentExtra();
        if(null != bundle)
            groupId = bundle.getString(KEY_GID);

        presenter.getGroupInfo(groupId);
    }

    @Override
    public void widgetClick(View v) {

    }

    @OnClick({R.id.ll_group_name, R.id.ll_group_intra, R.id.ll_group_member, R.id.tv_clear_chat, R.id.tv_exit_group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_group_name:
                break;
            case R.id.ll_group_intra:
                break;
            case R.id.ll_group_member:
                Bundle bundle = new Bundle();
                bundle.putString(KEY_GID, groupId);
                $startActivity(GroupMemberActivity.class, bundle);
                break;
            case R.id.tv_clear_chat:
                String name = FunctionUtil.jointTableName(groupId);
                if (YouyunDbManager.getIntance().removeChatImageMsg(name))
                    FunctionUtil.toastMessage("清空成功");
                break;
            case R.id.tv_exit_group:
                if (role == 4) {
//                    FunctionUtil.toastMessage("您是群主，不可退群");
                    Snackbar.make(tvExitGroup, "确认解散群?", Snackbar.LENGTH_LONG).setAction("解散", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.deleteGroup(groupId);
                        }
                    }).show();
                } else {
                    presenter.exitGroup(groupId);
                }
                break;
        }
    }

    @Override
    public void setGroupInfo(GroupInfoEntity entity) {
        role = entity.getRole();
        tvGroupName.setText(entity.getName());
        tvGroupIntra.setText(entity.getIntra());
    }

    @Override
    public void exitGroup(String gid, boolean result) {
        if (result) {
            FunctionUtil.toastMessage("退群成功");
            setResult(RESULT_OK);
            finish();
        } else
            FunctionUtil.toastMessage("退群失败");
    }

    @Override
    public void delGroup(String gid, boolean result) {
        if (result) {
            FunctionUtil.toastMessage("解散群成功");
            setResult(RESULT_OK);
            finish();
        } else
            FunctionUtil.toastMessage("解散群失败");
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
