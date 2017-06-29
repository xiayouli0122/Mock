package com.zy.mock;

import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by Yuri on 2017/6/29.
 */

public class HookTool {

    private static final String TAG = HookTool.class.getSimpleName();

    public static void hookMethod(Class cls, String method, final String result) {
        Log.d(TAG, "hookMethod.result:" + result);
        try {
            XposedHelpers.findAndHookMethod(cls, method, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    String imei = SUtils.getString("imei");
                    Log.d(TAG, "*****************************.imei=" + imei);
                    Log.d(TAG, "=============================.result=" + result);
                    param.setResult(result);
                }

            });
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "hookMethod: " +  e.getMessage());
            XposedBridge.log(e);
        }
    }

    /**
     * 不带参数的方法拦截
     * @param className 类名
     * @param classLoader
     * @param methodName 方法名
     */
    public static void hookMethod(final String className, ClassLoader classLoader, final String methodName){
        try {
            XposedHelpers.findAndHookMethod(className, classLoader, methodName, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Log.d(TAG, "hookMethod.className:" + className + ",methondName:" + methodName);
                    param.setResult(null);
                }
            });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    /**
     * 带参数的方法拦截
     */
    public static void hookMethods(String className, String methodName, XC_MethodHook xmh){
        try {
            Class<?> clazz = Class.forName(className);
            for (Method method : clazz.getDeclaredMethods())
                if (method.getName().equals(methodName)
                        && !Modifier.isAbstract(method.getModifiers())
                        && Modifier.isPublic(method.getModifiers())) {
                    XposedBridge.hookMethod(method, xmh);
                }
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

}
