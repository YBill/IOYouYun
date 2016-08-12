package com.ioyouyun.widgets;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by 卫彪 on 2016/7/29.
 */
public class LoddingDialog extends ProgressDialog {

    private String message = "正在加载...";

    public LoddingDialog(Context context) {
        super(context);
        setMessage(message);
    }

    public void setMessage(String msg) {
        if (null != msg)
            super.setMessage(msg);
    }

    public void showProgress() {
        if (!this.isShowing()) {
            this.show();
        }
    }

    public void cancleProgress() {
        if (this.isShowing()) {
            this.dismiss();
        }
    }

}
