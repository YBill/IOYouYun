package com.ioyouyun.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ioyouyun.YouyunApplication;

import java.lang.reflect.Method;

/**
 * PUSH sound vibration saved.
 * Created by 卫彪 on 2016/7/4.
 */
public class PushSharedUtil {

    public static final String NAME = "youyun_push_preference";
    public static final String KEY_VIBRATION = "key_vibration";
    public static final String KEY_SOUND = "key_sound";
    private static PushSharedUtil sharedPreferenceUtil;
    private SharedPreferences sharedPreferences;
    private SharedPreferencesCompat compat;

    public static synchronized PushSharedUtil getInstance() {
        if (sharedPreferenceUtil == null) {
            synchronized (PushSharedUtil.class) {
                if (sharedPreferenceUtil == null) {
                    sharedPreferenceUtil = new PushSharedUtil();
                }
            }
        }
        return sharedPreferenceUtil;
    }

    private PushSharedUtil() {
        sharedPreferences = YouyunApplication.application.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        compat = new SharedPreferencesCompat();
    }

    public void setVibration(boolean vibration) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_VIBRATION, vibration);
        compat.apply(editor);
    }

    public boolean getVibration() {
        return sharedPreferences.getBoolean(KEY_VIBRATION, false);
    }

    public void setSound(boolean sound) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SOUND, sound);
        compat.apply(editor);
    }

    public boolean getSound() {
        return sharedPreferences.getBoolean(KEY_SOUND, false);
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        private Method findApplyMethod() {
            try {
                Class<SharedPreferences.Editor> clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (Exception e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (Exception e) {
            }
            editor.commit();
        }
    }

}
