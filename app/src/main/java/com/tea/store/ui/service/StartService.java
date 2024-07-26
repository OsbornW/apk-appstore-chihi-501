package com.tea.store.ui.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import com.tea.store.R;
import com.tea.store.utils.PLog;

public class StartService extends Service {
    private static String TAG = StartService.class.getSimpleName();

    private static boolean gServiceInstance = false;

    public static void start(Context context, int index) {
        if (gServiceInstance) {
            return;
        }

        Intent i = new Intent();
        i.setClass(context, StartService.class);
        i.putExtra("index", index);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        } else {
            try {
                context.startService(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;
        String CHANNEL_ID = getString(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //Android 8.0适配
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);//如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道， //通知才能正常弹出
            manager.createNotificationChannel(channel);
            builder = new Notification.Builder(this, String.valueOf(CHANNEL_ID));
        } else {
            builder = new Notification.Builder(this);
        }
        builder.setContentTitle(" ")            //指定通知栏的标题内容
                .setContentText(" ")            //通知的正文内容
                .setWhen(System.currentTimeMillis())  //通知创建的时间
                .setSmallIcon(R.mipmap.ic_launcher)        //通知显示的小图标，只能用alpha图层的图片进行设置
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        Notification notification = builder.build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        gServiceInstance = true;
        int index = intent.getIntExtra("index", 0);
        PLog.i(TAG, "StartService-" + intent + "==" + index);
        com.hs.App.init(this);
        return START_STICKY;
    }
}
