package com.ioyouyun.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.media.presenter.IncommingCallPresenter;
import com.ioyouyun.media.view.IncommingCallView;
import com.ioyouyun.utils.FunctionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IncomingCallActivity extends BaseActivity<IncommingCallView, IncommingCallPresenter> implements IncommingCallView {

    @BindView(R.id.tv_uid)
    TextView tvUid;

    private String callName;

    @Override
    protected IncommingCallPresenter initPresenter() {
        return new IncommingCallPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_incoming_call;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        getIntentExtra();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void widgetClick(View v) {

    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            callName = intent.getStringExtra(FunctionUtil.INCOMINGNAME);
            tvUid.setText(callName);
        }
    }

    private void answer() {
        presenter.answer(callName);
    }

    private void decline() {
        presenter.decline();
        finish();
    }

    @OnClick({R.id.tv_answer, R.id.tv_hangup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_answer:
                answer();
                break;
            case R.id.tv_hangup:
                decline();
                break;
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
