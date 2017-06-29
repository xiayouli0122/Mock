package com.zy.mock;

import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by Yuri on 2017/6/29.
 */

public class SUtils {

    public static String getString(String key) {
        XSharedPreferences pre = new XSharedPreferences("com.zy.mock", "prefs");
        String result = pre.getString(key, null);
        return result;
    }

}
