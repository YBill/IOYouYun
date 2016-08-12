package com.ioyouyun.login.biz;

import android.app.Activity;

import com.ioyouyun.YouyunApplication;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.login.UserInfoEntity;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.wchat.RequestType;
import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.data.AuthResultData;
import com.ioyouyun.wchat.message.WChatException;
import com.weimi.media.WMedia;

import java.util.HashMap;

/**
 * Created by 卫彪 on 2016/6/6.
 */
public class LoginRequestImpl implements LoginRequest {

    @Override
    public void login(final Activity activity, final OnRequestListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AuthResultData authResultData;
                    if (FunctionUtil.isOnlinePlatform) {
                        authResultData = WeimiInstance.getInstance().registerApp(
                                YouyunApplication.application,
                                FunctionUtil.generateOpenUDID(activity),
                                "1-20046-8c351702e261c7f607ea8020dcb80f41-android",
                                "db25eb5508619a34c0ce38b63b4b4c0a",
                                120);
                    } else {
                        authResultData = WeimiInstance.getInstance().testRegisterApp(
                                YouyunApplication.application,
                                FunctionUtil.generateOpenUDID(activity),
                                "1-20122-fd379ae976ede4c2ea1526cef20f2c0d-android",
                                "56715b563c03682d4dd7c19501ec99a9",
                                120);
                    }
                    if (authResultData.success)
                        listener.onSuccess(authResultData.userInfo);
                    else
                        listener.onFaild();
                } catch (WChatException e) {
                    listener.onFaild();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void setNickName(String nickName, final OnRequestListener listener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("nickname", nickName);
        FunctionUtil.shortConnectRequest("/users/update", FunctionUtil.combineParamers(map), RequestType.POST, new FunctionUtil.OnRequestListener() {
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
    public UserInfoEntity getUserInfo() {
        return YouyunDbManager.getIntance().getUserInfo();
    }

    @Override
    public void saveUserInfo(UserInfoEntity entity) {
        if (entity != null)
            YouyunDbManager.getIntance().insertUserInfo(entity);
    }


    @Override
    public boolean initMediaSDK(Activity activity) {
        try {
            WMedia.getInstance().initWMediaSDK(activity.getApplicationContext(), true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getUid() {
        return WeimiInstance.getInstance().getUID();
    }

}
