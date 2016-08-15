package com.ioyouyun.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.home.HomeActivity;
import com.ioyouyun.utils.FunctionUtil;

public class LoginActivity extends BaseActivity<LoginView, LoginPresenter> implements LoginView {

    private Button loginBtn;

    @Override
    protected LoginPresenter initPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void $setToolBar() {
        super.$setToolBar();
    }

    @Override
    protected void initView() {
        $setToolBar();

        loginBtn = $findViewById(R.id.btn_login);
    }

    @Override
    protected void setListener() {
        loginBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void widgetClick(View v) {
        presenter.login();
    }

    @Override
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setHint("请输入昵称");
        builder.setTitle("登录").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nickname = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(nickname))
                    presenter.setNickName(nickname);

                loginSuccess();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                loginSuccess();
            }
        }).show();
    }

    @Override
    public void loginSuccess() {
        presenter.init();

        FunctionUtil.toastMessage("登录成功");
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFaild() {
        FunctionUtil.toastMessage("登录失败");
    }

}
