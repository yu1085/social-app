package com.alicom.fusion.demo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

/**
 * @author: cmw01044812
 * @date: 2/22/23
 * @descript:
 */
public class PhoneUtils {


    public  static String getPhoneNum(Context mContext){
        TelephonyManager tMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber;
    }


    public static String getPackageName(Context mContext){
        String packageName="";
        try {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    mContext.getPackageName(), 0);
            packageName = packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageName;
    }
}
