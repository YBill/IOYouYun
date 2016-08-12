package com.ioyouyun.group.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.model.GroupMemberEntity;
import com.ioyouyun.group.view.GroupMemberView;
import com.ioyouyun.utils.ParseJson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/7/8.
 */
public class GroupMemberPresenter extends BasePresenter<GroupMemberView> {

    private GroupRequest request;
    private Handler handler;
    private Activity activity;
    private List<GroupMemberEntity> groupMemberEntityList = new ArrayList<>();

    public GroupMemberPresenter(Activity activity) {
        request = new GroupRequestImpl();
        handler = new Handler(Looper.getMainLooper());
        this.activity = activity;
    }

    /**
     * 获取群成员
     *
     * @param gid
     */
    public void getGroupMember(String gid) {
        if(mView != null){
            mView.showLoading();
        }
        request.getGroupMembers(Long.parseLong(gid), -1, -1, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();

                JSONObject obj = ParseJson.parseCommonObject(response);
                JSONArray array = null;
                if(obj != null){
                    array = obj.optJSONArray("roles");
                }
                groupMemberEntityList.clear();
                List list = ParseJson.parseJson2ListT(array, GroupMemberEntity.class);
                if(list != null)
                    groupMemberEntityList.addAll(list);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null) {
                            mView.setListView(groupMemberEntityList);
                        }
                    }
                });
            }

            @Override
            public void onFaild() {
                closeLoading();
            }
        });
    }

    private void closeLoading(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(mView != null)
                    mView.hideLoading();
            }
        });
    }

}
