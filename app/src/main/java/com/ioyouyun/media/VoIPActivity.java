package com.ioyouyun.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.media.presenter.VoIPPresenter;
import com.ioyouyun.media.view.VoIPView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoIPActivity extends BaseActivity<VoIPView, VoIPPresenter> implements VoIPView {

    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_uid)
    TextView tvUid;
    @BindView(R.id.tv_tips)
    TextView tvTips;

    private boolean isReceive; // true:接电话 false:打电话
    private String uid;
    private String nickName;

    @Override
    protected VoIPPresenter initPresenter() {
        return new VoIPPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vo_ip;
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
            uid = intent.getStringExtra("toUid");
            nickName = intent.getStringExtra("nickname");
            isReceive = intent.getBooleanExtra("isReceive", false);
        }
    }

    @Override
    protected void initData() {
        btnLeft.setVisibility(View.VISIBLE);
        tvUid.setText(uid);
        tvNickname.setText(nickName);

        if (isReceive) {
            presenter.startTime();
        } else {
            setTipsText("等待对方接听，请稍后...");
        }
    }

    @Override
    public void widgetClick(View v) {

    }

    private void hangUpVoIP() {
        presenter.hangUp();
        finish();
    }

    @OnClick({R.id.btn_left, R.id.tv_hangup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
            case R.id.tv_hangup:
                hangUpVoIP();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }

    @Override
    public void setTipsText(String time) {
        tvTips.setText(time);
    }
}
