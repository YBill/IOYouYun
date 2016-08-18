package com.ioyouyun.contacts;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.chat.ChatActivity;
import com.ioyouyun.contacts.presenter.ContactDetailPresenter;
import com.ioyouyun.contacts.view.ContactDetailView;
import com.ioyouyun.media.VoIPActivity;
import com.weimi.media.WMedia;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactDetailActivity extends BaseActivity<ContactDetailView, ContactDetailPresenter> implements ContactDetailView {

    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_uid)
    TextView tvUid;

    private Dialog dialog;
    private String uid;
    private String nickName;

    @Override
    protected ContactDetailPresenter initPresenter() {
        return new ContactDetailPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_detail;
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
        Bundle bundle = $getIntentExtra();
        uid = bundle.getString(KEY_UID);
        nickName = bundle.getString(KEY_NICKNAME);

        tvNickname.setText(nickName);
        tvUid.setText(uid);
    }

    @Override
    public void widgetClick(View v) {

    }

    /**
     * VoIP
     *
     * @param uid
     * @param nickName
     */
    public void callVoIP(String uid, String nickName) {
        if (TextUtils.isEmpty(uid))
            return;

        Bundle bundle = new Bundle();
        bundle.putString(KEY_UID, uid);
        bundle.putString(KEY_NICKNAME, nickName);
        bundle.putBoolean(KEY_FLAG, false);
        $startActivity(VoIPActivity.class, bundle);

        WMedia.getInstance().call(uid);
    }

    /**
     * 选着对话框
     */
    private void showSwitchDialog() {
        if (dialog != null) {
            dialog.show();
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.layout_call_choose_dialog, null);
        Button btnVoip = (Button) view.findViewById(R.id.btn_voip);
        Button btnConference = (Button) view.findViewById(R.id.btn_conference);
        Button btnCancle = (Button) view.findViewById(R.id.btn_cancle);
        btnVoip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.cancel();
                callVoIP(uid, nickName);
            }
        });
        btnConference.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.cancel();
                presenter.applyRoom(uid);
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.cancel();
            }
        });
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @OnClick({R.id.tv_contact_im, R.id.tv_contact_voip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_contact_im:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("toUid", uid);
                intent.putExtra("nickName", nickName);
                intent.putExtra("chatType", 0);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_contact_voip:
                showSwitchDialog();
                break;
        }
    }
}
