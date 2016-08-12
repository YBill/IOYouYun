package com.ioyouyun.group.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.view.CreateGroupView;
import com.ioyouyun.utils.ParseJson;

/**
 * Created by 卫彪 on 2016/7/5.
 */
public class CreateGroupPresenter extends BasePresenter<CreateGroupView> {

    private GroupRequest request;
    private Handler handler;
    private Activity activity;

    public CreateGroupPresenter(Activity activity) {
        request = new GroupRequestImpl();
        handler = new Handler(Looper.getMainLooper());
        this.activity = activity;
    }

    public void createGroup(String groupName, String intra) {
        if (mView != null) {
            mView.showLoading();
        }
        request.createGroup(groupName, intra, 2, 0, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();

                final GroupInfoEntity entity = ParseJson.parseJson2T(ParseJson.parseCommonObject(response), GroupInfoEntity.class);

                if (entity != null && entity.getGid() != null && !"".equals(entity.getGid())) {
                    // 创建群成功
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null)
                                mView.createSuccess(entity);
                        }
                    });
                }
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
