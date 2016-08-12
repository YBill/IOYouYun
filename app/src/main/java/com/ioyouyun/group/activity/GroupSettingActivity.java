package com.ioyouyun.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.presenter.GroupSettingPresenter;
import com.ioyouyun.group.view.GroupSettingView;
import com.ioyouyun.home.HomeActivity;
import com.ioyouyun.observer.MessageEvent;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.widgets.LoddingDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupSettingActivity extends BaseActivity<GroupSettingView, GroupSettingPresenter> implements GroupSettingView {

    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.btn_left)
    Button btnLeft;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting);
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
        tvTopTitle.setText(getResources().getString(R.string.string_setting));
        tvTopTitle.setVisibility(View.VISIBLE);
        btnLeft.setText(getResources().getString(R.string.btn_back));
        btnLeft.setVisibility(View.VISIBLE);

        loddingDialog = new LoddingDialog(this);

        presenter.getGroupInfo(groupId);
    }

    @Override
    protected GroupSettingPresenter initPresenter() {
        return new GroupSettingPresenter(this);
    }

    @OnClick({R.id.btn_left, R.id.ll_group_name, R.id.ll_group_intra, R.id.ll_group_member, R.id.tv_clear_chat, R.id.tv_exit_group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.ll_group_name:
                break;
            case R.id.ll_group_intra:
                break;
            case R.id.ll_group_member:
                Intent intent = new Intent(this, GroupMemberActivity.class);
                intent.putExtra("gid", groupId);
                this.startActivity(intent);
                break;
            case R.id.tv_clear_chat:
                String name = FunctionUtil.jointTableName(groupId);
                if (YouyunDbManager.getIntance().removeChatImageMsg(name))
                    FunctionUtil.toastMessage("清空成功");
                break;
            case R.id.tv_exit_group:
                if (role == 4) {
                    FunctionUtil.toastMessage("您是群主，不可退群");
                    /*Snackbar.make(tvExitGroup, "确认删除?", Snackbar.LENGTH_LONG).setAction("删除", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.deleteGroup(groupId);
                        }
                    }).show();*/
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

            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra(FunctionUtil.INTENT_HOME_TYPE, FunctionUtil.REFRESH_GROUP);
            startActivity(intent);
        } else
            FunctionUtil.toastMessage("退群失败");
    }

    @Override
    public void delGroup(String gid, boolean result) {
        if (result) {
            FunctionUtil.toastMessage("解散群成功");

            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra(FunctionUtil.INTENT_HOME_TYPE, FunctionUtil.REFRESH_GROUP);
            startActivity(intent);
        } else
            FunctionUtil.toastMessage("解散群失败");
    }

    private void notifyGroupList(String gid){
        MessageEvent.GroupListEvent event = new MessageEvent.GroupListEvent();
        event.groupId = gid;
        EventBus.getDefault().post(event);
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
