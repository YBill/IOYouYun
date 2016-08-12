package com.ioyouyun.group.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.view.ApplyAddGroupView;
import com.ioyouyun.utils.ParseJson;

/**
 * Created by 卫彪 on 2016/7/13.
 */
public class ApplyAddGroupPresenter extends BasePresenter<ApplyAddGroupView> {

    private Activity activity;
    private GroupRequest request;
    private Handler handler;

    public ApplyAddGroupPresenter(Activity activity) {
        this.activity = activity;
        handler = new Handler(Looper.getMainLooper());
        request = new GroupRequestImpl();
    }

    public void applyAddGroup(String gid, String intra) {
        if (mView != null) {
            mView.showLoading();
        }
        request.applyAddGroup(Long.parseLong(gid), intra, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();

                final boolean result = ParseJson.parseCommonResult2(response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null)
                            mView.addGroupResult(result);
                    }
                });

            }

            @Override
            public void onFaild() {
                closeLoading();
            }
        });
    }

    private void closeLoading() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mView != null)
                    mView.hideLoading();
            }
        });
    }

}
