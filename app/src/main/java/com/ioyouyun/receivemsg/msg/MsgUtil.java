package com.ioyouyun.receivemsg.msg;

import com.ioyouyun.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 卫彪 on 2016/7/14.
 */
public class MsgUtil {
    /**************************************
     * 消息类型以及子类型
     *********************************************/

    /*
     * 系统事件消息 50001 由系统用户下发的事件消息，表示和用户或者群组相关的状态变更，客户端可能需要根据该消息进行逻辑处理。
	 * 比如用户加入群组，用户退出群组。客户端可能需要根据该事件更新用户的联系人列表等。这种消息一般不显示，如果显示也只是在消
	 * 息界面显示一条文本提示信息。客户端的处理新旧版本兼容策略是该消息如果有desc文本内容则显示desc，然后根据事件类型判断，
	 * 识别则处理，不识别则抛弃。
	 */
    public static final int MSGT_SYS_EVENT = 50001;
    public static final String MSE_TYPE_JOIN_GROUP_EVENT = "join_group_event";
    public static final String MSE_TYPE_JOIN_GROUP_EVENT2 = "join_group_event_msg";
    public static final String MSE_TYPE_QUIT_GROUP_EVENT = "quit_group_event";
    public static final String MSE_TYPE_CREATE_GROUP_EVENT = "create_group_event";
    public static final String MSE_TYPE_FRIEND_FEED_EVENT = "friends_timeline_unread_event";
    public static final String MSE_TYPE_REALTIME_RECOMMEND_USER = "realtime_recommend_user";
    public static final String MSE_TYPE_REALTIME_RECOMMEND_GROUP = "realtime_recommend_group";
    public static final String MSE_TYPE_GROUP_SETTING_CHANGE_EVENT = "group_setting_change_event";
    public static final String MSE_TYPE_NEW_FRIEND_EVENT = "new_friend_event";
    public static final String MSE_TYPE_APPROVE_GROUP_JOIN_GROUPUNION = "approve_group_join_groupUnion";
    public static final String MSE_TYPE_GROUP_UPDATE_TYPE = "group_update_type";
    public static final String MSE_TYPE_ADD_FRIEND_EVENT = "add_friend_event";
    public static final String MSE_TYPE_REMOVE_FRIEND_EVENT = "remove_friend_event";

    /*
     * 系统提醒消息 50002 由系统用户下发的提醒信息，用于提醒用户某个信息或者提醒用户进行一些行为。比如 有人评论了用户的动态，
	 * 有人申请加入用户管理的群，有人加用户为好友等等。这种消息的关注点是提醒，客户端只需展示并且支持用户进行下一步行为，不需
	 * 要根据消息进行特殊的逻辑操作。这种消息的关注点是展示，所以使用统一的格式。
	 */
    public static final int MSGT_SYS_NOTI = 50002;
    public static final String MSN_TYPE_INVITE_JOIN_GROUP = "invite_join_group";
    public static final String MSN_TYPE_APPLY_JOIN_GROUP = "apply_join_group";
    public static final String MSN_TYPE_SET_GROUP_MANAGE = "set_group_manage";
    public static final String MSN_TYPE_UNSET_GROUP_MANAGE = "unset_group_manage";
    public static final String MSN_TYPE_GROUP_MEMBER_OVER = "group_member_over";
    public static final String MSN_TYPE_DEL_GROUP = "del_group";
    public static final String MSN_TYPE_QUIT_GROUP = "quit_group";
    public static final String MSN_TYPE_DEL_STATUES = "del_status";
    public static final String MSN_TYPE_COMMENT = "comment";
    public static final String MSN_TYPE_F_COMMENT = "friend_comment";
    public static final String MSN_TYPE_LIKE = "like";
    public static final String MSN_TYPE_MENTION = "mention";
    public static final String MSN_TYPE_SHARE = "share";
    public static final String MSN_TYPE_NEW_FRIEND = "new_friend";
    public static final String MSN_TYPE_KICKED_OUT_GROUP = "kicked_out_group";

    /*
     * 内容消息 50003 显示为内容的消息，包括普通的 文本语音图片，以及富文本信息（图文混排的新闻，地理位置，用户名片，
	 * feed进chat）。这种消息发送方可能是系统用户，也可能是普通用户。如果是普通用户，则显示用户头像，表现为一个用户发出的
	 * 消息。如果是系统用户则不显示用户头像，表现为系统信息。富文本信息也通过模板方式定义格式。客户端的兼容策略是如果识别则显示， 不识别则提示用户升级。
	 * 注意：50003里面的json "body"后面的json以字符串保存
	 */
    public static final int MSGT_CONTENT = 50003;
    public static final String MC_TYPE_WHISPER = "whisper"; // 密语
    public static final String MC_TYPE_WHISPER_EMOJI = "whisper_emoji";// 表情密语
    public static final String MC_TYPE_WHISPER_VOICE = "whisper_voice"; // 语音密语
    public static final String MC_TYPE_SHARE_FEED = "share_feed"; // 分享feed，客户端组装 包含动态和活动
    public static final String MC_TYPE_SHARE_LINK = "share_link"; // 分享链接，客户端组装
    public static final String MC_TYPE_SHARE = "share"; // 服务器下发的分享字段 ，包含3种类型 链接 动态 活动
    public static final String MC_TYPE_SHARE_ACTIVITY = "activity"; // 发布活动
    public static final String MC_TYPE_EMOJI_DUAB = "emoji_duab"; // 贴图表情
    public static final String MC_TYPE_EMOJI_DUAB2 = "emoji_duab2"; // 贴图表情(新版)
    public static final String MC_TYPE_RICH_CONTENT = "rich_content"; // 微群微女郎微刊轻博客导入
    public static final String MC_TYPE_FEED = "status"; // 小组FEED进聊天
    public static final String MC_TYPE_COMMENT = "comment";//评论进chat

    /*
	 * 类似50002
	 */
    public static final int MSGT_SYS_NOTI2 = 50004;
    public static final String MSN2_TYPE_NEARBY_USER_ADD = "nearby_user_add";// 附近的人打招呼
    public static final String MSN2_TYPE_NEW_FRIEND_ADD_REPLY = "new_friend_add_reply";//新的朋友回复
    public static final String MSN2_TYPE_USER_ADD_REPLY = "user_add_reply";//打招呼回复

    /******************************************************************************************/

    public static int getSysMsgType(String msg) {
        int type = 0;
        try {
            JSONObject msgJson = new JSONObject(msg);
            if (msgJson != null && msgJson.has("id"))
                type = msgJson.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return type;
    }

    public static JSONObject getSysMsgBody(String msg) {
        JSONObject bodyJson = null;
        try {
            JSONObject msgJson = new JSONObject(msg);
            bodyJson = new JSONObject(msgJson.optString("body"));
        } catch (JSONException e) {
            Logger.e(e.toString());
        }
        return bodyJson;
    }

    public static String getSysMsgBodyType(String msg) {
        String type = null;
        JSONObject bodyJson = getSysMsgBody(msg);
        if (bodyJson != null) {
            type = bodyJson.optString("type", null);
        }
        return type;
    }
}
