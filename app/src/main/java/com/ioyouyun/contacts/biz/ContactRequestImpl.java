package com.ioyouyun.contacts.biz;

import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.wchat.RequestType;

import java.util.HashMap;

/**
 * Created by 卫彪 on 2016/6/25.
 */
public class ContactRequestImpl implements ContactRequest {

    @Override
    public void addLocation(String uid, double longitude, double latitude, int type, final OnContactListener listener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("type", type);
        FunctionUtil.shortConnectRequest("/recommend/location/add", FunctionUtil.combineParamers(map), RequestType.POST, new FunctionUtil.OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                if (listener != null)
                    listener.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                if (listener != null)
                    listener.onFaild();
            }
        });
    }

    @Override
    public void getNearbyUsers(String uid, double longitude, double latitude, long range, final OnContactListener listener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("range", range);
        FunctionUtil.shortConnectRequest("/recommend/users/nearby", FunctionUtil.combineParamers(map), RequestType.GET, new FunctionUtil.OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                if (listener != null)
                    listener.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                if (listener != null)
                    listener.onFaild();
            }
        });
    }

}
