package com.hyman.notification;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ztk.demo.lockdemo.R;

import com.hyman.notification.utils.NotificationUtil;
import com.hyman.notification.service.PlayService;

/**
 * author : Hyman
 * date   : 2020/5/26
 * desc   :
 */
public class PlayMusic extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_music);
        final Intent intent = new Intent(this, PlayService.class);
        startService(intent);
        //开启通知栏
        NotificationUtil mNotificationUtils = new NotificationUtil(this);
        mNotificationUtils.showNotification();
    }
}
