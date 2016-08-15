package com.ioyouyun.settings.biz;

import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.util.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 卫彪 on 2016/6/25.
 */
public class SettingRequestImpl implements SettingRequest {

    @Override
    public void pushCreate(String startTime, String endTime, final OnSettingListener listener) {
        WeimiInstance.getInstance().shortPushCreate(startTime, endTime, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                if (s != null) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(s);
                        String code = jsonObject.optString("code", null);
                        if (code != null && code.equals("200")) {
                            if (listener != null)
                                listener.onSuccess("");
                        }
                    } catch (JSONException e) {
                        if (listener != null)
                            listener.onFail();
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {

            }
        }, 120);
    }

    @Override
    public void getPushTime(final OnSettingListener listener) {
        WeimiInstance.getInstance().shortPushShowUser(new HttpCallback() {
            @Override
            public void onResponse(String result) {
                if (listener != null) {
                    listener.onSuccess(result);
                }
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {

            }
        }, 120);
    }

}
