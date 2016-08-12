package com.ioyouyun.datamanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ioyouyun.YouyunApplication;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.login.UserInfoEntity;
import com.ioyouyun.receivemsg.msg.MsgBodyTemplate;
import com.ioyouyun.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/6/21.
 */
public class YouyunDbManager {

    private static YouyunDbManager youyunDbManager;
    private YouyunDbHelper youyunDbHelper;

    private YouyunDbManager(Context context) {
        if (youyunDbHelper == null)
            youyunDbHelper = YouyunDbHelper.getInstance(context);
    }

    synchronized public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase database = youyunDbHelper.getReadableDatabase();
        return database;
    }

    synchronized public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase database = youyunDbHelper.getWritableDatabase();
        return database;
    }

    public static YouyunDbManager getIntance() {
        if (youyunDbManager == null) {
            synchronized (YouyunDbManager.class) {
                if (youyunDbManager == null)
                    youyunDbManager = new YouyunDbManager(YouyunApplication.application);
            }
        }
        return youyunDbManager;
    }

    /////////////////////////////////聊天表//////////////////////////////////

    /**
     * 新建聊天表
     *
     * @param tableName
     * @return
     */
    synchronized private boolean createChatMsgTable(String tableName) {
        String sql = "CREATE TABLE " + tableName + "(" +
                YouyunDbHelper.MSG_ID + " VARCHAR PRIMARY KEY," +
                YouyunDbHelper.FROM_ID + " VARCHAR," +
                YouyunDbHelper.TO_ID + " VARCHAR," +
                YouyunDbHelper.NAME + " VARCHAR," +
                YouyunDbHelper.DATE + " VARCHAR," +
                YouyunDbHelper.TEXT + " VARCHAR," +
                YouyunDbHelper.AUDIOTIME + " VARCHAR," +
                YouyunDbHelper.IMGTHUMBNAIL + " VARCHAR," +
                YouyunDbHelper.IMGMSG + " VARCHAR," +
                YouyunDbHelper.DIRECT + " INTEGER," +
                YouyunDbHelper.MSGTYPE + " INTEGER," +
                YouyunDbHelper.CONVTYPE + " INTEGER" +
                ")";
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 查询表是否存在
     *
     * @param tableName
     * @return
     */
    private boolean ifExistTable(String tableName) {
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT 1 FROM sqlite_master WHERE type='table' and name = ?";
            cursor = db.rawQuery(sql, new String[]{tableName});
            if (cursor.moveToNext()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    /////////////////////////////////消息表//////////////////////////////////////

    /**
     * 插入IM消息
     *
     * @param entity
     * @param name
     * @return
     */
    synchronized public boolean insertChatMessage(ChatMsgEntity entity, String name) {
        String tableName = YouyunDbHelper.CHAT_MSG_TABLE + name;
        Logger.v("table:" + tableName);
        if (!ifExistTable(tableName)) {
            Logger.v(tableName + "不存在");
            createChatMsgTable(tableName);
        }

        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        String sql = "insert into " + tableName +
                "(" + YouyunDbHelper.MSG_ID + "," +
                YouyunDbHelper.FROM_ID + "," +
                YouyunDbHelper.TO_ID + "," +
                YouyunDbHelper.NAME + "," +
                YouyunDbHelper.DATE + "," +
                YouyunDbHelper.TEXT + "," +
                YouyunDbHelper.AUDIOTIME + "," +
                YouyunDbHelper.IMGTHUMBNAIL + "," +
                YouyunDbHelper.IMGMSG + "," +
                YouyunDbHelper.DIRECT + "," +
                YouyunDbHelper.MSGTYPE + "," +
                YouyunDbHelper.CONVTYPE +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            db.execSQL(sql, new Object[]{entity.getMsgId(), entity.getFromId(), entity.getToId(), entity.getName(),
                    entity.getTimestamp(), entity.getText(), entity.getAudioTime(), entity.getImgThumbnail(),
                    entity.getImgMsg(), entity.getDirectInt(entity), entity.getMsgTypeInt(entity),
                    entity.getConvTypeInt(entity)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 修改图片消息
     *
     * @param imMsg
     * @param msgId
     * @param name
     * @return
     */
    synchronized public boolean updateChatImageMsg(String imMsg, String msgId, String name) {
        String tableName = YouyunDbHelper.CHAT_MSG_TABLE + name;
        Logger.v("table:" + tableName);
        if (!ifExistTable(tableName)) {
            Logger.v(tableName + "不存在");
            return false;
        }
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(YouyunDbHelper.IMGMSG, imMsg);
            String whereClause = YouyunDbHelper.MSG_ID + "=?";//修改条件
            String[] whereArgs = {msgId};//修改条件的参数
            int update = db.update(tableName, values, whereClause, whereArgs);
            if (update != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 查询会话聊天
     *
     * @param name
     * @return
     */
    public List<ChatMsgEntity> getChatMsgEntityList(String name) {
        String tableName = YouyunDbHelper.CHAT_MSG_TABLE + name;
        Logger.v("table:" + tableName);
        List<ChatMsgEntity> lists = new ArrayList<>();
        if (!ifExistTable(tableName)) {
            Logger.v(tableName + "不存在");
            return lists;
        }
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT * FROM " + tableName;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setMsgId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.MSG_ID)));
                entity.setFromId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.FROM_ID)));
                entity.setToId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.TO_ID)));
                entity.setName(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.NAME)));
                entity.setTimestamp(Long.parseLong(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.DATE))));
                entity.setText(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.TEXT)));
                entity.setAudioTime(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.AUDIOTIME)));
                entity.setImgThumbnail(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.IMGTHUMBNAIL)));
                entity.setImgMsg(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.IMGMSG)));
                entity.setDirect(entity.getDirectBoolean(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.DIRECT))));
                entity.setMsgType(entity.getMsgTypeEnum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.MSGTYPE))));
                entity.setConvType(entity.getConvTypeEnum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.CONVTYPE))));
                lists.add(entity);
            }
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 删除聊天，这里直接把表删除了
     *
     * @param name
     * @return
     */
    synchronized public boolean removeChatImageMsg(String name) {
        String tableName = YouyunDbHelper.CHAT_MSG_TABLE + name;
        Logger.v("table:" + tableName);
        if (!ifExistTable(tableName)) {
            Logger.v(tableName + "不存在");
            return false;
        }
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            String sql = "DROP TABLE IF EXISTS " + tableName;
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /////////////////////////////////最近联系人表//////////////////////////////////////

    /**
     * 插入最近联系人
     *
     * @param entity
     * @return
     */
    synchronized public boolean insertRecentContact(ChatMsgEntity entity) {
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        // replace into 不存在插入，存在修改
        String sql = "replace into " + YouyunDbHelper.TABLE_NAME_RECENT_CONTACT +
                "(" + YouyunDbHelper.FROM_ID + "," +
                YouyunDbHelper.NAME + "," +
                YouyunDbHelper.DATE + "," +
                YouyunDbHelper.TEXT + "," +
                YouyunDbHelper.MSGTYPE + "," +
                YouyunDbHelper.CONVTYPE + "," +
                YouyunDbHelper.UNREAD_MSG_NUM +
                ") VALUES(?,?,?,?,?,?,?)";
        try {
            db.execSQL(sql, new Object[]{entity.getOppositeId(), entity.getName(), entity.getTimestamp(),
                    entity.getText(), entity.getMsgTypeInt(entity), entity.getConvTypeInt(entity),
                    entity.getUnreadMsgNum()});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 修改联系人未读消息数,也可以用insertRecentContact(entity)修改
     *
     * @param oppositeId
     * @param number  未读消息数
     * @return
     */
    synchronized public boolean updateUnreadNumber(String oppositeId, int number) {
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(YouyunDbHelper.UNREAD_MSG_NUM, number);
            String whereClause = YouyunDbHelper.FROM_ID + "=?";//修改条件
            String[] whereArgs = {oppositeId};//修改条件的参数
            int update = db.update(YouyunDbHelper.TABLE_NAME_RECENT_CONTACT,
                    values, whereClause, whereArgs);
            if (update != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 根据用户Id查询最近联系人
     *
     * @param tid
     * @return
     */
    public ChatMsgEntity getRecentContactById(String tid) {
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT * FROM " + YouyunDbHelper.TABLE_NAME_RECENT_CONTACT
                + " WHERE " + YouyunDbHelper.FROM_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{tid});
            if (cursor.moveToNext()) {
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setOppositeId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.FROM_ID)));
                entity.setName(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.NAME)));
                entity.setTimestamp(Long.parseLong(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.DATE))));
                entity.setText(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.TEXT)));
                entity.setMsgType(entity.getMsgTypeEnum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.MSGTYPE))));
                entity.setConvType(entity.getConvTypeEnum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.CONVTYPE))));
                entity.setUnreadMsgNum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.UNREAD_MSG_NUM)));
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 查询最近联系人
     *
     * @return
     */
    public List<ChatMsgEntity> getRecentContact() {
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT * FROM " + YouyunDbHelper.TABLE_NAME_RECENT_CONTACT;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            List<ChatMsgEntity> lists = new ArrayList<>();
            while (cursor.moveToNext()) {
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setOppositeId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.FROM_ID)));
                entity.setName(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.NAME)));
                entity.setTimestamp(Long.parseLong(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.DATE))));
                entity.setText(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.TEXT)));
                entity.setMsgType(entity.getMsgTypeEnum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.MSGTYPE))));
                entity.setConvType(entity.getConvTypeEnum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.CONVTYPE))));
                entity.setUnreadMsgNum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.UNREAD_MSG_NUM)));
                lists.add(entity);
            }
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /////////////////////////////////通知表//////////////////////////////////////

    /**
     * 插入通知表信息
     *
     * @param entity
     * @return
     */
    synchronized public boolean insertNotify(MsgBodyTemplate entity) {
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        String sql = "replace into " + YouyunDbHelper.TABLE_NAME_NOTIFY +
                "(" + YouyunDbHelper.BUSINESS_ID + "," +
                YouyunDbHelper.NOTIFY_TYPE + "," +
                YouyunDbHelper.USERID + "," +
                YouyunDbHelper.USERNAME + "," +
                YouyunDbHelper.GROUPID + "," +
                YouyunDbHelper.GROUPNAME + "," +
                YouyunDbHelper.TEXT + "," +
                YouyunDbHelper.DATE + "," +
                YouyunDbHelper.ISLOOK + "," +
                YouyunDbHelper.ENTER_GROUP_TYPE +
                ") VALUES(?,?,?,?,?,?,?,?,?,?)";
        try {
            db.execSQL(sql, new Object[]{entity.getBusiness_id(), entity.getType(), entity.getSource().getId(),
                    entity.getSource().getDesc(), entity.getObject().getId(), entity.getObject().getDesc(), entity.getExt().getMsg(),
                    entity.getDate(), entity.getIsLook(), 0});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 查询通知表列表
     *
     * @return
     */
    public List<MsgBodyTemplate> getNotifyList() {
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT * FROM " + YouyunDbHelper.TABLE_NAME_NOTIFY;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            List<MsgBodyTemplate> lists = new ArrayList<>();
            while (cursor.moveToNext()) {
                MsgBodyTemplate entity = new MsgBodyTemplate();

                entity.setBusiness_id(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.BUSINESS_ID)));
                entity.setType(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.NOTIFY_TYPE)));

                MsgBodyTemplate.SourceEntity sourceEntity = new MsgBodyTemplate.SourceEntity();
                sourceEntity.setId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.USERID)));
                sourceEntity.setDesc(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.USERNAME)));
                entity.setSource(sourceEntity);

                MsgBodyTemplate.ObjectEntity objectEntity = new MsgBodyTemplate.ObjectEntity();
                objectEntity.setId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.GROUPID)));
                objectEntity.setDesc(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.GROUPNAME)));
                entity.setObject(objectEntity);

                MsgBodyTemplate.ExtEntity extEntity = new MsgBodyTemplate.ExtEntity();
                extEntity.setMsg(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.TEXT)));
                entity.setExt(extEntity);

                entity.setDate(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.DATE)));
//                entity.set(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.ENTER_GROUP_TYPE)));
                lists.add(entity);
            }
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 查询未读通知
     *
     * @return
     */
    public long getUnreadNotifyNum() {
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + YouyunDbHelper.TABLE_NAME_NOTIFY
                + " WHERE " + YouyunDbHelper.ISLOOK + " = 1";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            long count = cursor.getLong(0);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    /**
     * 清空通知未读数
     **/
    synchronized public boolean clearNotifyUnreadNumber() {
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            String sql = "UPDATE " + YouyunDbHelper.TABLE_NAME_NOTIFY + " SET " + YouyunDbHelper.ISLOOK + "=0"
                    + " WHERE " + YouyunDbHelper.ISLOOK + "= 1";
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /////////////////////////////////用户表//////////////////////////////////////

    /**
     * 查询用户表
     *
     * @return
     */
    public UserInfoEntity getUserInfo() {
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT * FROM " + YouyunDbHelper.TABLE_NAME_USER_INFO;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                UserInfoEntity entity = new UserInfoEntity();
                entity.setId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.USERID)));
                entity.setNickname(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.NICKNAME)));
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 插入用户信息
     *
     * @param entity
     * @return
     */
    synchronized public boolean insertUserInfo(UserInfoEntity entity) {
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        String sql = "insert into " + YouyunDbHelper.TABLE_NAME_USER_INFO +
                "(" + YouyunDbHelper.USERID + "," +
                YouyunDbHelper.NICKNAME +
                ") VALUES(?,?)";
        try {
            db.execSQL(sql, new Object[]{entity.getId(), entity.getNickname()});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

}
