package com.hyman.notification; /**
 * author : Hyman
 * date   : 2020/5/25
 * desc   :
 * 关于通知栏的API
 *  Notification.Builder builder;
 *  builder.setCustomContentView(remoteViews)//自定义View，7.0以上
 *  NotificationManager.createNotificationChannel(channel) ;//创建渠道，8.0以上
 *  NotificationManager.notify(1,notification);// 展示通知栏
 *
 *
 *  androidX提供的API：
 *  NotificationCompat.Builder(mContext, id);//这个builder可以设置自定义View。
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.ztk.demo.lockdemo.R;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener{
    private Button normal_notification;
    private Button progress_notification;
    private Button custom_notification;
    private Button detete_notification;
    private Button delete_progress_notification;
    private boolean exitNotification=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        normal_notification = (Button) findViewById(R.id.normal_notification);
        normal_notification.setOnClickListener(this);
        progress_notification = (Button) findViewById(R.id.progress_notification);
        progress_notification.setOnClickListener(this);
        custom_notification = (Button) findViewById(R.id.custom_notification);
        custom_notification.setOnClickListener(this);
        detete_notification = (Button) findViewById(R.id.detete_notification);
        detete_notification.setOnClickListener(this);
        delete_progress_notification=findViewById(R.id.detete_progress_notification);
        delete_progress_notification.setOnClickListener(this);

    }

    //发一个普通通知，新增一个通知
    private void sendNormalNotification(){
        Notification notification = getNotificationBuilder().build();
        getNotificationManager().notify(1,notification);
    }

    //模仿一个带下载进度的通知，对通知的更新
    private void sendProgressNotification(){
        final NotificationCompat.Builder builder = getNotificationBuilder();
        //发起Notification后，铃声和震动均只执行一次
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        getNotificationManager().notify(2,builder.build());
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(1000);
                        builder.setProgress(100,i,false);
                        //虽然加了FLAG_ONLY_ALERT_ONCE，但是每过一秒就发次通知震动一次还是挺烦的，
                        //所以把下面设置震动模式的代码注释掉了
                        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
                        if(exitNotification){
                            break;
                        }
                        getNotificationManager().notify(2,builder.build());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //发一个自定义通知
    private void sendCustomNotification(){
        //自定义通知也是在Android N之后才出现的，所以要加上版本号判断
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){ //7.0
            NotificationCompat.Builder builder = getNotificationBuilder();
            //自定义通知栏视图初始化
            RemoteViews remoteViews =
                    new RemoteViews(getPackageName(),R.layout.layout_custom_notification);
            remoteViews.setTextViewText(R.id.notification_title,"custom_title");
            remoteViews.setTextViewText(R.id.notification_content,"custom_content");
            //PendingIntent即将要发生的意图，可以被取消、更新
            Intent intent = new Intent(this,MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this,-1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.turn_next,pendingIntent);
            //绑定自定义视图
            builder.setCustomContentView(remoteViews);
            getNotificationManager().notify(3,builder.build());
//        }
    }

    //获取系统服务
    private NotificationManager mNotificationManager;
    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null){
            mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    //兼容android8.0以及之前版本获取Notification.Builder方法
    private NotificationCompat.Builder getNotificationBuilder(){
        String id="channel_id";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id)
                .setAutoCancel(true)//是否自动取消，设置为true，点击通知栏 ，移除通知
                .setContentTitle("通知栏消息标题")
                .setContentText("通知栏消息具体内容")
                .setSmallIcon(R.mipmap.ic_launcher)//通知栏消息小图标，不设置是不会显示通知的
                //ledARGB 表示灯光颜色、ledOnMs 亮持续时间、ledOffMs 暗的时间
                .setLights(Color.RED, 3000, 3000)
                //.setVibrate(new long[]{100,100,200})//震动的模式，第一次100ms，第二次100ms，第三次200ms
                //.setStyle(new Notification.BigTextStyle())
                ;
        //没加版本判断会报Call requires API level 26 (current min is 16):android.app.Notification.Builder#Builder）错误
        //builder.setChannelId("channel_id");
        //通过版本号判断兼容了低版本没有通知渠道方法的问题，只有当版本号大于26（Build.VERSION_CODES.O）时才使用渠道相关方法
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            //builder的channelId需和下面channel的保持一致；
            builder.setChannelId("channel_id");
            NotificationChannel channel = new
                    NotificationChannel("channel_id","channel_name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setBypassDnd(true);//设置可以绕过请勿打扰模式
            channel.canBypassDnd();//可否绕过请勿打扰模式
            //锁屏显示通知
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            channel.shouldShowLights();//是否会闪光
            channel.enableLights(true);//闪光
            //指定闪光时的灯光颜色，为了兼容低版本在上面builder上通过setLights方法设置了
            //channel.setLightColor(Color.RED);
            channel.canShowBadge();//桌面launcher消息角标
            channel.enableVibration(true);//是否允许震动
            //震动模式，第一次100ms，第二次100ms，第三次200ms，为了兼容低版本在上面builder上设置了
            //channel.setVibrationPattern(new long[]{100,100,200});
            channel.getAudioAttributes();//获取系统通知响铃声音的配置
            channel.getGroup();//获取通知渠道组
            //绑定通知渠道
            getNotificationManager().createNotificationChannel(channel);
        }
        return builder;
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.normal_notification:
                sendNormalNotification();
                break;
            case R.id.progress_notification:
                sendProgressNotification();
                break;
            case R.id.custom_notification:
                sendCustomNotification();
                break;
            case R.id.detete_notification:
                getNotificationManager().cancel(2);//通知管理——删除
                break;
            case R.id.detete_progress_notification:
                exitNotification=true;
                getNotificationManager().cancel(2);//通知管理——删除
                break;
        }
    }

}
