package com.ioyouyun.group.biz;

import com.ioyouyun.utils.Logger;
import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.util.HttpCallback;

import java.util.List;

/**
 * Created by 卫彪 on 2016/6/25.
 */
public class GroupRequestImpl implements GroupRequest {

    @Override
    public void getGroupList(long uid, String cat1, String cat2, final OnGroupListener listener) {
        WeimiInstance.getInstance().getGroupList(uid, cat1, cat2, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("getGroupList success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("getGroupList:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void createGroup(String name, String intra, int cat1, int cat2, final OnGroupListener listener) {
        WeimiInstance.getInstance().createGroup(name, intra, cat1, cat2, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("createGroup success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("createGroup error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void getGroupInfo(long gid, final OnGroupListener listener) {
        WeimiInstance.getInstance().getGroupInfo(gid, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("getGroupInfo success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("getGroupInfo error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void getGroupMembers(long gid, int role, int count, final OnGroupListener listener) {
        WeimiInstance.getInstance().groupuserList(gid, role, count, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("getGroupMembers success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("getGroupMembers error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void exitGroup(long gid, final OnGroupListener listener) {
        WeimiInstance.getInstance().shortExitGroup(gid, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("exitGroup success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("exitGroup error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void deleteGroup(String groupId, final OnGroupListener listener) {
        WeimiInstance.getInstance().shortDeleteRoom(groupId, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("deleteGroup success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("deleteGroup error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void addGroupUsers(long gid, String uids, final OnGroupListener listener) {
        WeimiInstance.getInstance().shortGroupuserAdd(gid, uids, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("addGroupUsers success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("addGroupUsers error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void applyAddGroup(long gid, String intra, final OnGroupListener listener) {
        WeimiInstance.getInstance().applyAddGroup(gid, intra, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("applyAddGroup success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("applyAddGroup error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

}
