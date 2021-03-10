package com.example.poweralarm.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.poweralarm.R;
import com.example.poweralarm.activity.MainActivity;

class PowerMonitorReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "PowerAlarmIDChannel";
    public static final String CHANNEL_NAME = "PowerAlarmNameChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        final SharedPreferences mSettings = context.getSharedPreferences(MainActivity.SHARED_FILE, Context.MODE_PRIVATE);

        boolean active = false;
        int minValue = 20;

        if (mSettings.contains(MainActivity.SHARED_ACTIVE) && mSettings.contains(MainActivity.SHARED_PERCENT)) {
            active = mSettings.getBoolean(MainActivity.SHARED_ACTIVE, false);
            minValue = mSettings.getInt(MainActivity.SHARED_PERCENT, 20);
        }

        int curValue = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

        if (active && minValue >= curValue) {
            Intent intent_new = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent_new, PendingIntent.FLAG_CANCEL_CURRENT);

            if(Build.VERSION.SDK_INT >= 26 ){
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(context.getApplicationContext(), CHANNEL_ID);
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableVibration(true);
                notificationChannel.setLockscreenVisibility(1);
                notificationChannel.enableLights(true);

                builder.setContentIntent(pendingIntent)
                        .setContentTitle("Уровень заряда!")
                        .setContentText("Опустился до " + minValue + "%")
                        .setShowWhen(true)
                        .setAutoCancel(true);
                Notification notification = builder.build();
                nm.createNotificationChannel(notificationChannel);
                nm.cancel(0);
                nm.notify(0, notification);
            }
            else {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(context.getApplicationContext(), "channel_id_mynote")
                                .setContentTitle("Уровень заряда!")
                                .setContentText("Опустился до " + minValue + "%")
                                .setContentIntent(pendingIntent)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setAutoCancel(true);

                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(context.getApplicationContext());
                notificationManager.cancel(0);
                notificationManager.notify(0, builder.build());
            }
        }
    }
}
