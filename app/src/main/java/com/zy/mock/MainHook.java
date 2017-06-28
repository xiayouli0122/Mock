package com.zy.mock;

import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
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
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        String packageName = loadPackageParam.packageName;
        Log.d(TAG, "packageName: " + packageName);
        //这里通过包名劫持每个应用，每个类
        Log.d(TAG, "TEST===============");
        if (packageName.equals("com.zy.mock")) {
            hoolMethod(TelephonyManager.class, "getDeviceId", "2222222222");
        }

        SystemPropertiesHook systemPropertiesHook = new SystemPropertiesHook();
        XposedHelpers.findAndHookMethod("android.os.SystemProperties", loadPackageParam.classLoader, "get", String.class, String.class, systemPropertiesHook);

    }

    private void hoolMethod(Class cls, String method, final String result) {
        try {
            XposedHelpers.findAndHookMethod(cls, method, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    param.setResult(result);
                    Log.d(TAG, "model 33333333333: " + Build.MODEL);
                }

            });
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "hoolMethod: " +  e.getMessage());
            XposedBridge.log(e);
        }
    }
}
