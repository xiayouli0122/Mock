package com.zy.mock;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

/**
 * 修改系统信息
 * Created by Yuri on 2017/6/28.
 */

public class SystemPropertiesHook extends XC_MethodHook {
    public SystemPropertiesHook() {
        super();
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        XSharedPreferences pre = new XSharedPreferences(this.getClass().getPackage().getName(), "prefs");
        String methodName = param.method.getName();
        if (methodName.startsWith("get"))
        {
            Log.v("getDeviceInfo", "hook systemProperties ------>");
            XposedHelpers.setStaticObjectField(android.os.Build.class, "MODEL", pre.getString("model", "Yuri"));
            XposedHelpers.setStaticObjectField(android.os.Build.class, "MANUFACTURER", pre.getString("manufacturer", "Yuri"));
            XposedHelpers.setStaticObjectField(android.os.Build.class, "BRAND", pre.getString("brand", "Yuri"));
            XposedHelpers.setStaticObjectField(android.os.Build.class, "HARDWARE", pre.getString("hardware", "Yuri"));
            XposedHelpers.setStaticObjectField(android.os.Build.class, "RADIO", pre.getString("radio", "Yuri"));
            XposedHelpers.setStaticObjectField(android.os.Build.class, "DEVICE", pre.getString("device", "Yuri"));
            XposedHelpers.setStaticObjectField(android.os.Build.VERSION.class, "RELEASE", pre.getString("version", "10.0"));
        }
    }
}
