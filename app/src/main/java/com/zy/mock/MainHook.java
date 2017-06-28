package com.zy.mock;

import android.telephony.TelephonyManager;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Yuri on 2017/6/28.
 */

public class MainHook implements IXposedHookLoadPackage {
//http://blog.csdn.net/assicen/article/details/52719102?locationNum=12
    //拦截广告
    //http://www.cnblogs.com/czaoth/p/5643068.html
    private static final String TAG = MainHook.class.getSimpleName();

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        String packageName = loadPackageParam.packageName;
        Log.d(TAG, "packageName: " + packageName);
        //这里通过包名劫持每个应用，每个类
        Log.d(TAG, "TEST===============");

        Log.d(TAG, "1111111111111111: ");
        final XSharedPreferences pre = new XSharedPreferences(this.getClass().getPackage().getName(), "prefs");
        hoolMethod(TelephonyManager.class, "getDeviceId", pre.getString("imei", "test111111111111"));

        XposedHelpers.findAndHookMethod("com.zy.mock.MainActivity", loadPackageParam.classLoader, "save", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.d(TAG, "afterHookedMethod: ");

                hoolMethod(TelephonyManager.class, "getDeviceId", pre.getString("imei", "test111111111111"));

                SystemPropertiesHook systemPropertiesHook = new SystemPropertiesHook();
                XposedHelpers.findAndHookMethod("android.os.SystemProperties", loadPackageParam.classLoader,
                        "get", String.class, String.class, systemPropertiesHook);
            }
        });

//        if (packageName.equals("com.antutu.ABenchMark")) {
//            Log.d(TAG, "com.antutu.ABenchMark");
//            hoolMethod(TelephonyManager.class, "getDeviceId", pre.getString("imei", "test111111111111"));
//        }

//        SystemPropertiesHook systemPropertiesHook = new SystemPropertiesHook();
//        XposedHelpers.findAndHookMethod("android.os.SystemProperties", loadPackageParam.classLoader, "get", String.class, String.class, systemPropertiesHook);

    }

    private void hoolMethod(Class cls, String method, final String result) {
        Log.d(TAG, "IMEI: " + result);
        try {
            XposedHelpers.findAndHookMethod(cls, method, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    param.setResult(result);
                }

            });
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "hoolMethod: " +  e.getMessage());
            XposedBridge.log(e);
        }
    }
}
