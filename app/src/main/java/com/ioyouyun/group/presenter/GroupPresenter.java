package com.ioyouyun.group.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.chat.ChatActivity;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.view.GroupView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.ParseJson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 卫彪 on 2016/7/5.
 */
public class GroupPresenter extends BasePresenter<GroupView> {

    private GroupRequest request;
    private Handler handler;
    private List<GroupInfoEntity> groupInfoEntityList = new ArrayList<>();
    private Activity activity;

    public GroupPresenter(Activity activity) {
        this.activity = activity;
        request = new GroupRequestImpl();
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取群组列表
     */
    public void getGroupList() {
        if(mView != null){
            mView.showLoading();
        }

        request.getGroupList(Long.parseLong(FunctionUtil.uid), "1,2", "0", new OnGroupListener() {

            @Override
            public void onSuccess(String response) {
                closeLoading();

                JSONObject obj = ParseJson.parseCommonObject(response);
                JSONArray array = null;
                if (obj != null) {
                    array = obj.optJSONArray("groups");
                }
                groupInfoEntityList.clear();
                List list = ParseJson.parseJson2ListT(array, GroupInfoEntity.class);
                if (list != null)
                    groupInfoEntityList.addAll(list);
               handler.post(new Runnable() {
                   @Override
                   public void run() {
                       if (mView != null)
                           mView.setListView(groupInfoEntityList);
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

    public void onItemClick(int position) {
        Toast.makeText(activity, groupInfoEntityList.get(position).getGid(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("toUid", groupInfoEntityList.get(position).getGid());
        intent.putExtra("nickName", groupInfoEntityList.get(position).getName());
        intent.putExtra("chatType", 1);
        activity.startActivity(intent);
    }

}
