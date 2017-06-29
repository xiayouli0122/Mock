package com.zy.mock;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Random;

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

    private XC_LoadPackage.LoadPackageParam mLpp;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        mLpp = loadPackageParam;
        final String packageName = loadPackageParam.packageName;
        Log.d(TAG, "packageName: " + packageName);
        //这里通过包名劫持每个应用，每个类
        XposedBridge.log("handleLoadPackage:" + packageName);

        SystemPropertiesHook systemPropertiesHook = new SystemPropertiesHook();
        XposedHelpers.findAndHookMethod("android.os.SystemProperties", loadPackageParam.classLoader,
                "get", String.class, String.class, systemPropertiesHook);

//        HookTool.hookMethod(android.telephony.TelephonyManager.class, "getDeviceId", SUtils.getString("imei"));

        try {
            XposedHelpers.findAndHookMethod(TelephonyManager.class, "getDeviceId", new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    XSharedPreferences pre = new XSharedPreferences(this.getClass().getPackage().getName(), "prefs");
                    String imei = pre.getString("imei", null);
                    Log.d(TAG, "=============== IMEI: " + imei);
                    param.setResult(imei);
                }

            });
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "getDeviceId: " +  e.getMessage());
            XposedBridge.log(e);
        }


//        XposedBridge.hookAllConstructors(LocationManager.class,new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                Log.d(TAG, "LocationManager: param.args.length=" + param.args.length);
//                if (param.args.length==2) {
//                    Context context = (Context) param.args[0]; //这里的 context
//                    XposedBridge.log(" 对 "+getProgramNameByPackageName(context)+" 模拟位置");
//                    //把权限的检查 hook掉
//                    XposedHelpers.findAndHookMethod(context.getClass(), "checkCallingOrSelfPermission", String.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            if (param.args[0].toString().contains("INSTALL_LOCATION_PROVIDER")){
//                                param.setResult(PackageManager.PERMISSION_GRANTED);
//                            }
//                        }
//                    });
//                    XposedBridge.log("LocationManager : " + context.getPackageName() + " class:= " + param.args[1].getClass().toString());
//                    //获取到  locationManagerService 主动调用 对象的 reportLocation 方法  可以去模拟提供位置信息
//                    //这里代码中并没有涉及到主动调用
//                    Object   locationManagerService = param.args[1];
//                }
//            }
//        });

        //主要代码  将系统的数据替换掉
//        XposedHelpers.findAndHookMethod("com.android.server.LocationManagerService", mLpp.classLoader, "reportLocation", Location.class, boolean.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                Location location = (Location) param.args[0];
//                XposedBridge.log("实际 系统 经度"+location.getLatitude() +" 系统 纬度"+location.getLongitude() +"系统 加速度 "+location.getAccuracy());
//                XSharedPreferences pre = new XSharedPreferences(this.getClass().getPackage().getName(), "prefs");
//
//                double latitude = Double.valueOf(pre.getString("lan","39.99"))+ (double) new Random().nextInt(1000) / 1000000 ;
//                double longtitude = Double.valueOf(pre.getString("lon","116.31"))+ (double) new Random().nextInt(1000) / 1000000 ;
//                location.setLongitude(longtitude);
//                location.setLatitude(latitude);
//                XposedBridge.log("hook 系统 经度"+location.getLatitude() +" 系统 纬度"+location.getLongitude() +"系统 加速度 "+location.getAccuracy());
//
//            }
//        });

//        if (mLpp.packageName.contains("tencent") || mLpp.packageName.contains("mark")
//                || mLpp.packageName.contains("harsom") || mLpp.packageName.contains("baidu")){ //注意 不加包名过滤 容易把手机干的开不了机
//            LocationHook.HookAndChange(mLpp.classLoader,0,0);
//        }

    }

    /**
     * 通过包名获取应用程序的名称。
     * @param context
     *            Context对象。
     *            包名。
     * @return 返回包名所对应的应用程序的名称。
     */
    public static String getProgramNameByPackageName(Context context) {
        PackageManager pm = context.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
}
