package com.ioyouyun.utils;

import android.util.Log;

/**
 * Log管理类
 * <p>
 * Created by 卫彪 on 2016/6/6.
 */
public class Logger {

    /**
     * 是否开启debug
     */
    public static boolean isDebug = true;
    /**
     * 默认Log名
     */
    private static final String Default_Tag = "Youyun_Tag";

    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    private static final int ASSERT = 6;

    public static void v(String msg) {
        log(Default_Tag, msg, VERBOSE);
    }

    public static void d(String msg) {
        log(Default_Tag, msg, DEBUG);
    }

    public static void i(String msg) {
        log(Default_Tag, msg, INFO);
    }

    public static void w(String msg) {
        log(Default_Tag, msg, WARN);
    }

    public static void e(String msg) {
        log(Default_Tag, msg, ERROR);
    }

    public static void wtf(String msg) {
        log(Default_Tag, msg, ASSERT);
    }

    public static void v(String tag, String msg) {
        log(tag, msg, VERBOSE);
    }

    public static void d(String tag, String msg) {
        log(tag, msg, DEBUG);
    }

    public static void i(String tag, String msg) {
        log(tag, msg, INFO);
    }

    public static void w(String tag, String msg) {
        log(tag, msg, WARN);
    }

    public static void e(String tag, String msg) {
        log(tag, msg, ERROR);
    }

    public static void wtf(String tag, String msg) {
        log(tag, msg, ASSERT);
    }

    private static void log(String tag, String msg, int level) {
        if (!isDebug || msg == null)
            return;

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        int index = 4;
        String className = stackTraceElements[index].getFileName();
        if (className.equals(Logger.class.getName())) {
            index = 3;
            Log.e(Default_Tag, "className old:" + className);
            className = stackTraceElements[index].getFileName();
            Log.e(Default_Tag, "className new:" + className);
        }

        String methodName = stackTraceElements[index].getMethodName();
        int lineNumber = stackTraceElements[index].getLineNumber();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodName).append(" ] -->");

        stringBuilder.append(msg);

        String logStr = stringBuilder.toString();

        switch (level) {
            case VERBOSE:
                Log.v(tag, "##" + Thread.currentThread().getName() + "###### " + logStr);
                break;
            case DEBUG:
                Log.d(tag, "##" + Thread.currentThread().getName() + "###### " + logStr);
                break;
            case INFO:
                Log.i(tag, "##" + Thread.currentThread().getName() + "###### " + logStr);
                break;
            case WARN:
                Log.w(tag, "##" + Thread.currentThread().getName() + "###### " + logStr);
                break;
            case ERROR:
                Log.e(tag, "##" + Thread.currentThread().getName() + "###### " + logStr);
                break;
            case ASSERT:
                Log.wtf(tag, "##" + Thread.currentThread().getName() + "###### " + logStr);
                break;
        }

    }


}
