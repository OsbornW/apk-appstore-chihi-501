package com.tea.store.ui.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tea.store.ui.service.StartService;
import com.tea.store.utils.PLog;

public class BootReceiver extends BroadcastReceiver {
    private static String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        PLog.i(TAG, "BootupReceiver-" + intent);
        if (intent.getAction() != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent();
            i.setAction("android.reboot.REQUEST_WEATHER");
            i.setClass(context, BootReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    (1000 * 60 * 30), pi);
            //MainActivity.disabled(context);
        }

        StartService.start(context, 0);
    }
}
