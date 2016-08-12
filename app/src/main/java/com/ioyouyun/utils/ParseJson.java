package com.ioyouyun.utils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/7/6.
 */
public class ParseJson {

    public static final String APISTATUS = "apistatus";
    public static final String RESULT = "result";
    public static final int REQUEST_OK = 1;

    /**
     * {
     * "apistatus": 1, //0.系统错误,1.正常
     * "result": {}
     * }
     *
     * @param json
     * @return JSONObject
     */
    public static JSONObject parseCommonObject(String json) {
        JSONObject obj = null;
        try {
            JSONObject object = new JSONObject(json);
            if (object != null && object.has(APISTATUS) && REQUEST_OK == object.getInt(APISTATUS)) {
                if (object.has(RESULT)) {
                    obj = object.getJSONObject(RESULT);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * {
     * "apistatus": 1, //0.系统错误,1.正常
     * "result": []
     * }
     *
     * @param json
     * @return JSONArray
     */
    public static JSONArray parseCommonArray(String json) {
        JSONArray array = null;
        try {
            JSONObject object = new JSONObject(json);
            if (object != null && object.has(APISTATUS) && REQUEST_OK == object.getInt(APISTATUS)) {
                if (object.has(RESULT)) {
                    array = object.getJSONArray(RESULT);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * JSONObject -> T
     *
     * @param obj
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T parseJson2T(JSONObject obj, Class<T> t) {
        T result = null;
        if (obj != null) {
            Gson gson = new Gson();
            result = gson.fromJson(obj.toString(), t);
        }
        return result;
    }

    /**
     * JSONArray -> List<T>
     *
     * @param array
     * @param t
     * @param <T>
     * @return
     */
    public static <T> List<T> parseJson2ListT(JSONArray array, Class<T> t) {
        List<T> result = new ArrayList<>();
        try {
            if (null != array) {
                if (array != null) {
                    Gson gson = new Gson();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        T tt = gson.fromJson(object.toString(), t);
                        if (tt != null)
                            result.add(tt);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * {
     * "apistatus": 1, //0.系统错误,1.正常
     * "result": 1 //1.成功  0.失败
     * }
     *
     * @param json
     * @return
     */
    public static boolean parseCommonResult(String json) {
        boolean result = false;
        try {
            JSONObject obj = new JSONObject(json);
            if (obj != null && obj.has(APISTATUS) && REQUEST_OK == obj.getInt(APISTATUS)) {
                if (obj.has(RESULT)) {
                    result = obj.getInt(RESULT) == REQUEST_OK ? true : false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ｛
     * "apistatus": 1, //0.系统错误,1.正常
     * "result": true //true:成功  false:失败
     * ｝
     *
     * @param json
     * @return
     */
    public static boolean parseCommonResult2(String json) {
        boolean result = false;
        try {
            JSONObject obj = new JSONObject(json);
            if (obj != null && obj.has(APISTATUS) && REQUEST_OK == obj.getInt(APISTATUS)) {
                if (obj.has(RESULT)) {
                    result = obj.getBoolean(RESULT);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
