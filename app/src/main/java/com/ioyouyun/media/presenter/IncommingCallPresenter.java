package com.ioyouyun.media.presenter;

import android.app.Activity;
import android.content.Intent;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.media.VoIPActivity;
import com.ioyouyun.media.view.IncommingCallView;
import com.weimi.media.WMedia;

/**
 * Created by 卫彪 on 2016/7/21.
 */
public class IncommingCallPresenter extends BasePresenter<IncommingCallView> {

    private Activity activity;

    public IncommingCallPresenter(Activity activity) {
        this.activity = activity;
    }

    /**
     * 挂断
     */
    public void decline() {
        WMedia.getInstance().decline();
    }

}
