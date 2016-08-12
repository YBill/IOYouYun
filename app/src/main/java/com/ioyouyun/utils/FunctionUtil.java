package com.ioyouyun.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.ioyouyun.YouyunApplication;
import com.ioyouyun.wchat.RequestType;
import com.ioyouyun.wchat.ServerType;
import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.message.WChatException;
import com.ioyouyun.wchat.util.HttpCallback;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by 卫彪 on 2016/6/6.
 */
public class FunctionUtil {

    public static final String MSG_TYPE_INCOMINGRECEIVED = "IncomingReceived"; // 来电消息 内容固定不可改
    public static final String MSG_TYPE_MEDIA_CALL_HEAD = "MediaSDK_"; // Media 头部 内容固定不可改
    public static final String MEDIA_CALL_CONNECTED = MSG_TYPE_MEDIA_CALL_HEAD + "Connected"; // 接通 内容固定不可改
    public static final String MEDIA_CALL_END = MSG_TYPE_MEDIA_CALL_HEAD + "CallEnd"; // 结束 内容固定不可改
    public static final String MEDIA_CALL_ERROR = MSG_TYPE_MEDIA_CALL_HEAD + "Error"; // 错误 内容固定不可改
    public static final String MSG_TYPE_CONFERENCE = "conference_"; // 来电消息
    public static final String CONFERENCE_REQUEST_ROOM = MSG_TYPE_CONFERENCE + "request_room"; // 申请房间
    public static final String CONFERENCE_LIST = MSG_TYPE_CONFERENCE + "list"; // 用户列表

    public static final String MSG_TYPE_NOTIFY = "msg_type_notify"; // 通知

    public static final String MSG_TYPE_RECEIVE_TEXT = "msg_type_receive_text"; // 文本
    public static final String MSG_TYPE_RECEIVE_AUDIO = "msg_type_receive_audio"; // 语音
    public static final String MSG_TYPE_RECEIVE_IMAGE = "msg_type_receive_image"; // 图片
    public static final String MSG_TYPE_DOWNLOAD_IMAGE = "msg_type_download_image"; // 下载图片

    /**
     * Media
     */
    public static final String INCOMINGNAME = "incomingname"; // 来电人
    public static final String CONTENT = "content"; // 内容

    /**
     * notify
     */
    public static final String TYPE_NOTIFY = "type_notify";
    public static final String TYPE_TEXT = "type_text";
    public static final String TYPE_AUDIO = "type_audio";
    public static final String TYPE_IMAGE = "type_image";

    /**
     * im
     */
    public static final String FROM_UID = "from_uid";
    public static final String RECV_CONTENT = "recv_content";
    public static final String RECV_DATE = "recv_date";
    public static final String RECV_AUDIO_TIME = "recv_audio_time";
    public static final String RECV_IMAGE_THUMB_PATH = "recv_image_thumb_path";
    public static final String RECV_IMAGE_INFO = "recv_image_info";
    public static final String DOWNLOAD_FILEID = "download_fileid";
    public static final String DOWNLOAD_PROGRESS = "download_progress";

    /**
     * HomeActivity的启动模式是singleTask,表示进入HomeActivity后的操作
     */
    public static final String INTENT_HOME_TYPE = "intent_home_type";
    public static final String REFRESH_GROUP = "refresh_group"; // 刷新group

    public static final String CONFERENCEACTIVITY_PATH = "cn.youyunsample.media.ConferenceActivity";
    public static final String VOIPACTIVITY_PATH = "cn.youyunsample.media.VoIPActivity";
    public static final String BEINVITEACTIVITYACTIVITY_PATH = "cn.youyunsample.media.BeInviteActivity";
    public static final String NOTIFYACTIVITYACTIVITY_PATH = "cn.youyunsample.home.NotifyActivity";
    public static final String CHATACTIVITYACTIVITY_PATH = "cn.youyunsample.chat.ChatActivity";



    private static Toast toast;
    public static boolean isOnlinePlatform = true;

    public static String uid; // 用户ID
    public static String nickname; // 昵称

    /**
     * 获取Android Id
     *
     * @return
     */
    public static String generateOpenUDID(Activity activity) {
        // Try to get the ANDROID_ID
        String OpenUDID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (OpenUDID == null || OpenUDID.equals("9774d56d682e549c") | OpenUDID.length() < 15) {
            // if ANDROID_ID is null, or it's equals to the GalaxyTab generic
            // ANDROID_ID or bad, generates a new one
            final SecureRandom random = new SecureRandom();
            OpenUDID = new BigInteger(64, random).toString(16);
        }
        return OpenUDID;
    }

    /**
     * Toast
     *
     * @param msg
     */
    public static void toastMessage(String msg) {
        if (toast == null)
            toast = Toast.makeText(YouyunApplication.application, msg, Toast.LENGTH_SHORT);
        else
            toast.setText(msg);
        toast.show();
    }

    public static final String MSG_ID_PRE = UUID.randomUUID() + "";
    public static int msg_p = 0;

    public static String genLocalMsgId() {
        msg_p++;
        String msgId = MSG_ID_PRE + msg_p;
        return msgId;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null)
                return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static String getVersion() {
        try {
            PackageManager manager = YouyunApplication.application.getPackageManager();
            PackageInfo info = manager.getPackageInfo(YouyunApplication.application.getPackageName(), 0);
            String version = info.versionName;
            return "v " + version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 短链请求
     *
     * @param path
     * @param param
     * @param requestType
     * @param listener
     */
    public static void shortConnectRequest(String path, String param, RequestType requestType, final OnRequestListener listener) {
        Logger.i("path = " + path);
        try {
            WeimiInstance.getInstance().commonInterface(path, param, requestType, ServerType.weimiPlatform, null, null,
                    null, new HttpCallback() {
                        @Override
                        public void onResponse(String s) {
                            Logger.i("request success:" + s);
                            if (listener != null)
                                listener.onSuccess(s);
                        }

                        @Override
                        public void onResponseHistory(List list) {

                        }

                        @Override
                        public void onError(Exception e) {
                            Logger.i("request error:" + e.getMessage());
                            if (listener != null)
                                listener.onError(e.getMessage());
                        }
                    }, 120);
        } catch (WChatException e) {
            e.printStackTrace();
        }
    }

    public interface OnRequestListener {
        void onSuccess(String response);

        void onError(String error);
    }

    /**
     * Map -> "a=?"+"&b=?"
     *
     * @param params
     * @return
     */
    public static String combineParamers(Map<String, Object> params) {
        String param = "";
        if (params != null) {
            Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                param += entry.getKey() + "=" + entry.getValue() + "&";
            }
            if (param.length() >= 1)
                param = param.substring(0, param.length() - 1);
            Logger.i("param = " + param);
        }
        return param;
    }

    /**
     * 拼接表名称
     * @param toId 对方Id或gid
     * @return
     */
    public static String jointTableName(String toId) {
        return uid + "_" + toId;
    }

    public static String getCurrentActivity() {
        ActivityManager manager = (ActivityManager) YouyunApplication.application.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = manager.getRunningTasks(1);
        ComponentName componentName = taskInfos.get(0).topActivity;
        String name = componentName.getClassName();
        Log.v("Bill", "currentActivity:" + name);
        return name;
    }

}
