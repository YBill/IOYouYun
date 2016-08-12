package com.ioyouyun.widgets;

import android.content.Context;
import android.widget.ProgressBar;

/**
 * Created by 卫彪 on 2016/8/12.
 */
public class LoddingProgress extends ProgressBar {

    public LoddingProgress(Context context) {
        super(context);
    }

    public void showProgress() {
        this.setVisibility(VISIBLE);
    }

    public void cancleProgress() {
        this.setVisibility(GONE);
    }

}
