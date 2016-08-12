package com.ioyouyun.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.group.presenter.ApplyAddGroupPresenter;
import com.ioyouyun.group.view.ApplyAddGroupView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.widgets.LoddingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplyAddGroupActivity extends BaseActivity<ApplyAddGroupView, ApplyAddGroupPresenter> implements ApplyAddGroupView {

    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.et_search_id)
    EditText etSearchId;
    private String groupId;
    private LoddingDialog loddingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_add_group);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        tvTopTitle.setText(getResources().getString(R.string.add_group_apply));
        tvTopTitle.setVisibility(View.VISIBLE);
        btnLeft.setVisibility(View.VISIBLE);

        loddingDialog = new LoddingDialog(this);
    }

    @Override
    protected ApplyAddGroupPresenter initPresenter() {
        return new ApplyAddGroupPresenter(this);
    }

    @Override
    public void showLoading() {
        loddingDialog.showProgress();
    }

    @Override
    public void hideLoading() {
        loddingDialog.cancleProgress();
    }

    @Override
    public void addGroupResult(boolean result) {
        if (result) {
            FunctionUtil.toastMessage("申请成功");
            setResult(RESULT_OK);
            finish();
        } else
            FunctionUtil.toastMessage("申请失败");
    }

    @OnClick({R.id.btn_left, R.id.tv_confirm_apply})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.tv_confirm_apply:
                String text = etSearchId.getText().toString().trim();
                presenter.applyAddGroup(groupId, text);
                break;
        }
    }
}
