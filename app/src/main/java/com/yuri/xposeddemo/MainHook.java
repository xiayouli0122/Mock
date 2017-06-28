package com.yuri.xposeddemo;

import android.telephony.TelephonyManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Yuri on 2017/6/28.
 */

public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        hoolMethod(TelephonyManager.class, "getDeviceId", "111111111");

    }

    private void hoolMethod(Class cls, String method, final String result) {
        try {
            XposedHelpers.findAndHookMethod(cls, method, new Object[]{new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    param.setResult(result);
                }

            }});
        } catch (Throwable e) {
        }
    }
}
