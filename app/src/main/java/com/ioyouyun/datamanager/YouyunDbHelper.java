package com.ioyouyun.datamanager;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 卫彪 on 2016/6/21.
 */
public class YouyunDbHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "youyun_db";
    public final static int DATABASE_VERSION = 1;

    public final static String CHAT_MSG_TABLE = "chat_msg_t_"; // 一个会话一张表，动态生成
    public static final String TABLE_NAME_RECENT_CONTACT = "youyun_recent_contact"; // 最近联系人表
    public static final String TABLE_NAME_NOTIFY = "youyun_notify"; // 通知表
    public static final String TABLE_NAME_USER_INFO = "youyun_user_info"; // 用户表

    public static final String ID = "_id";
    public static final String MSG_ID = "msg_id";
    public static final String FROM_ID = "from_id";
    public static final String TO_ID = "to_id";
    public static final String NAME = "name";
    public static final String NICKNAME = "nickname";
    public static final String DATE = "date";
    public static final String TEXT = "text";
    public static final String AUDIOTIME = "audiotime";
    public static final String IMGTHUMBNAIL = "imgthumbnail";
    public static final String IMGMSG = "imgmsg"; // 图片信息,JSON
    public static final String ISSHOWTIME = "isshowtime";
    public static final String MSGTYPE = "msgtype"; // 消息类型,取值见ChatMsgEntity类
    public static final String CONVTYPE = "convtype"; // 1:单聊 2:群聊
    public static final String UNREAD_MSG_NUM = "unread_msg_num"; // 未读消息数

    public static final String BUSINESS_ID = "business_id"; // 通知返回type-uid-gid ，充当主键
    public static final String NOTIFY_TYPE = "notify_type"; // 通知类型
    public static final String USERID = "userid"; // 用户ID
    public static final String USERNAME = "username"; // 用户名
    public static final String GROUPID = "groupid"; // 群组ID
    public static final String GROUPNAME = "groupname"; // 群名称
    public static final String ISLOOK = "islook"; // 是否看过该通知 0：看过 1：没看过
    public static final String ENTER_GROUP_TYPE = "enter_group_type"; // 申请入群处理  0:未处理 1：已同意 2：已拒绝

    private YouyunDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static YouyunDbHelper youyunDbHelper = null;

    public static YouyunDbHelper getInstance(Context context) {
        if (youyunDbHelper == null) {
            synchronized (YouyunDbHelper.class) {
                if (youyunDbHelper == null) {
                    youyunDbHelper = new YouyunDbHelper(context);
                }
            }
        }
        return youyunDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(createRecentContactTable());
            db.execSQL(createNotifyTable());
            db.execSQL(createUserInfoTable());
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            // TODO 消息表删除？

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RECENT_CONTACT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NOTIFY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_INFO);
            onCreate(db);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 创建最近联系人表
     *
     * @return
     */
    private String createRecentContactTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_RECENT_CONTACT + "(" +
                FROM_ID + " VARCHAR PRIMARY KEY," +
                NAME + " VARCHAR," +
                DATE + " VARCHAR," +
                TEXT + " VARCHAR," +
                MSGTYPE + " INTEGER," +
                CONVTYPE + " INTEGER," +
                UNREAD_MSG_NUM + " INTEGER" +
                ")";
        return sql;
    }

    /**
     * 创建通知表
     *
     * @return
     */
    private String createNotifyTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_NOTIFY + "(" +
                BUSINESS_ID + " VARCHAR PRIMARY KEY," +
                NOTIFY_TYPE + " VARCHAR," +
                USERID + " VARCHAR," +
                USERNAME + " VARCHAR," +
                GROUPID + " VARCHAR," +
                GROUPNAME + " VARCHAR," +
                TEXT + " VARCHAR," + // 验证内容
                DATE + " VARCHAR," +
                ISLOOK + " INTEGER," +
                ENTER_GROUP_TYPE + " INTEGER" +
                ")";
        return sql;
    }

    /**
     * 创建用户表
     *
     * @return
     */
    private String createUserInfoTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_USER_INFO + "(" +
                USERID + " VARCHAR PRIMARY KEY," +
                NICKNAME + " VARCHAR" +
                ")";
        return sql;
    }

}
