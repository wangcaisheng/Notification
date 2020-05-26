package com.hyman.notification;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.ztk.demo.lockdemo.R;
import com.hyman.notification.utils.*;
import com.hyman.notification.utils.NotificationPermissionUtil.*;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //开启通知栏权限
        NotificationPermissionUtil.getInstance().openPsermission(this);

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(MainActivity.this,PlayMusic.class);
                startActivity(intent1);
            }
        });

        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(MainActivity.this,NotificationActivity.class);
                startActivity(intent1);
            }
        });

    }
}
