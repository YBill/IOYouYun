package com.ioyouyun.login;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.login.biz.LoginRequest;
import com.ioyouyun.login.biz.LoginRequestImpl;
import com.ioyouyun.login.biz.OnRequestListener;
import com.ioyouyun.receivemsg.PushMsgReceiver;
import com.ioyouyun.settings.biz.OnSettingListener;
import com.ioyouyun.settings.biz.SettingRequest;
import com.ioyouyun.settings.biz.SettingRequestImpl;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.utils.ParseJson;

import org.json.JSONObject;

/**
 * Created by 卫彪 on 2016/8/11.
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    private LoginRequest request;
    private Activity activity;
    private Handler handler;
    private SettingRequest settingRequest;

    public LoginPresenter(Activity activity) {
        this.activity = activity;
        handler = new Handler(Looper.getMainLooper());
        request = new LoginRequestImpl();
        settingRequest = new SettingRequestImpl();
    }

    /**
     * 设置用户昵称
     */
    public void setNickName(String nickName) {
        request.setNickName(nickName, new OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject object = ParseJson.parseCommonObject(response);
                UserInfoEntity entity = ParseJson.parseJson2T(object, UserInfoEntity.class);
                if (entity == null || TextUtils.isEmpty(entity.getNickname())) {
                    setNickNameFaild();
                } else {
                    FunctionUtil.nickname = entity.getNickname();
                    UserInfoEntity userInfoEntity = new UserInfoEntity();
                    userInfoEntity.setId(FunctionUtil.uid);
                    userInfoEntity.setNickname(entity.getNickname());
                    request.saveUserInfo(userInfoEntity);
                }
            }

            @Override
            public void onFaild() {
                setNickNameFaild();
            }
        });
    }

    /**
     * 登录
     */
    public void login() {
        request.login(activity, new OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                UserInfoEntity userInfoEntity = null;
                try {
                    Logger.v(response);
                    if (response != null) {
                        JSONObject object = new JSONObject(response);
                        userInfoEntity = ParseJson.parseJson2T(object, UserInfoEntity.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (userInfoEntity != null) {
                    FunctionUtil.uid = userInfoEntity.getId();
                    FunctionUtil.nickname = userInfoEntity.getNickname();
                } else {
                    FunctionUtil.uid = request.getUid();
                    FunctionUtil.nickname = request.getUid();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        UserInfoEntity entity = request.getUserInfo();
                        if (entity == null || TextUtils.isEmpty(entity.getNickname())) {
                            if (mView != null)
                                mView.showDialog();
                        } else {
                            FunctionUtil.nickname = entity.getNickname();
                            if (mView != null)
                                mView.loginSuccess();
                        }
                    }
                });
            }

            @Override
            public void onFaild() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null)
                            mView.loginFaild();
                    }
                });
            }
        });
    }

    /**
     * 初始化Push和Media SDK
     */
    public void init() {
        initMediaSDK();
        startPush();
    }

    private void setNickNameFaild() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                FunctionUtil.toastMessage("设置昵称失败");
            }
        });
    }

    private void initMediaSDK() {
        boolean result = request.initMediaSDK(activity);
        if (!result) {
            FunctionUtil.toastMessage("初始化Media_SDK失败");
        }
    }

    private void startPush() {
        settingRequest.pushCreate(null, null, new OnSettingListener() {
            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onFail() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FunctionUtil.toastMessage("初始化PUSH失败");
                    }
                });
            }
        });
        PushMsgReceiver.startPush();
    }

}
