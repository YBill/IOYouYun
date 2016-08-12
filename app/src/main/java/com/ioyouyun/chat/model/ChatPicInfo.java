package com.ioyouyun.chat.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 卫彪 on 2016/6/17.
 */
public class ChatPicInfo {

    public String fileId;
    public String local;
    public int fileLength;
    public int pieceSize;

    public ChatPicInfo(String fileId, String local, int fileLength, int pieceSize){
        super();
        this.fileId = fileId;
        this.local = local;
        this.fileLength = fileLength;
        this.pieceSize = pieceSize;
    }

    public ChatPicInfo() {
        super();
    }

    public static String getJsonStr(ChatPicInfo chatPicInfo){
        JSONObject json = new JSONObject();
        try {
            json.put("fileId", chatPicInfo.fileId);
            json.put("local", chatPicInfo.local);
            json.put("fileLength", chatPicInfo.fileLength);
            json.put("pieceSize", chatPicInfo.pieceSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static ChatPicInfo getInfo(String jsonStr){
        ChatPicInfo chatPicInfo = null;
        if(jsonStr != null){
            try {
                JSONObject json = new JSONObject(jsonStr);
                if(json != null){
                    chatPicInfo = new ChatPicInfo();
                    chatPicInfo.fileId = json.optString("fileId",null);
                    chatPicInfo.local = json.optString("local",null);
                    chatPicInfo.fileLength = json.optInt("fileLength",0);
                    chatPicInfo.pieceSize = json.optInt("pieceSize",0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return chatPicInfo;
    }

}
