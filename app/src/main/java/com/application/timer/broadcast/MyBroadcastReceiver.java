package com.application.timer.broadcast;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyBroadcastReceiver extends BroadcastReceiver {

    //  receiver
    public static final String ACTION_SNOOZE = "OK";
    public static final String EXTRA_NOTIFICATION_ID = "notification-id";

    private static final String TAG = "receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_SNOOZE.equals(intent.getAction())) {
            int notificationId = intent.getExtras().getInt(EXTRA_NOTIFICATION_ID);
            Log.e(TAG, "Cancel notification with id " + notificationId);
            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationmanager.cancel(notificationId);
        }
    }
}