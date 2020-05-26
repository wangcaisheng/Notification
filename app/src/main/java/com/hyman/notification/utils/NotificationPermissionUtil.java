package com.hyman.notification.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;

/**
 * author : Hyman
 * date   : 2020/5/26
 * desc   :
 * 权限分为：
 * 普通权限  （清单文件申请）
 * 危险权限   （清单文件申请+代码动态申请 6.0以上）
 * 特殊权限    （清单文件申请+intent跳转系统页面申请）；
 * <p>
 * 通知栏权限属于特殊权限
 */
public class NotificationPermissionUtil {

    private static NotificationPermissionUtil mInstance;

    public static NotificationPermissionUtil getInstance() {
        if (mInstance == null) {
            mInstance = new NotificationPermissionUtil();
        }
        return mInstance;
    }

    private NotificationPermissionUtil() {
    }

    public void openPsermission(Context context) {
        boolean enabled = isNotificationEnabled(context);
        if (!enabled) {
            /**
             * 跳到通知栏设置界面
             * @param context
             */
            Intent localIntent = new Intent();
            //直接跳转到应用通知设置的代码：
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//8.0以上
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            }else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", context.getPackageName());
                localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
            } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {//4.4
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + context.getPackageName()));
            } else {
                //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.setAction(Intent.ACTION_VIEW);
                    localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                    localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                }
            }
            context.startActivity(localIntent);
        }

    }

    /**
     * 判断是否有通知栏权限
     * 4.4以上生效；以下返回的一直是true
     * @param context
     * @return
     */
    public boolean isNotificationEnabled(Context context){
        return NotificationManagerCompat.from(context).areNotificationsEnabled();

    }
}
