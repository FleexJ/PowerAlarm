package com.example.poweralarm.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.poweralarm.R;
import com.example.poweralarm.activity.MainActivity;

public class PowerMonitorReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "PowerAlarmIDChannel";
    public static final String CHANNEL_NAME = "PowerAlarmNameChannel";
    public static final String ACTION = Intent.ACTION_BATTERY_LOW;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Log.v("CheckBattery\t", "work");
            final SharedPreferences mSettings = context.getSharedPreferences(MainActivity.SHARED_FILE, Context.MODE_PRIVATE);

            boolean active = false;
            int minValue = 20;

            if (mSettings.contains(MainActivity.SHARED_ACTIVE) && mSettings.contains(MainActivity.SHARED_PERCENT)) {
                active = mSettings.getBoolean(MainActivity.SHARED_ACTIVE, active);
                minValue = mSettings.getInt(MainActivity.SHARED_PERCENT, minValue);
            }

            int curValue = intent.getIntExtra("level", 0);

            if (active /*&& minValue >= curValue*/) {
                Log.v("CheckBattery\t", "true");
                Intent intent_new = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent_new, PendingIntent.FLAG_CANCEL_CURRENT);
                BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

                if(Build.VERSION.SDK_INT >= 26 ){
                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification.Builder builder = new Notification.Builder(context.getApplicationContext(), CHANNEL_ID);
                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setLockscreenVisibility(1);
                    notificationChannel.enableLights(true);

                    builder.setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("Уровень заряда!")
//                            .setContentText("Опустился до " + minValue + "%")
                            .setContentText(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "%")
                            .setShowWhen(true)
                            .setAutoCancel(true);
                    Notification notification = builder.build();
                    notification.defaults = NotificationCompat.DEFAULT_ALL;
                    nm.createNotificationChannel(notificationChannel);
                    nm.cancel(0);
                    nm.notify(0, notification);
                }
                else {
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                                    .setContentTitle("Уровень заряда!")
//                                    .setContentText("Опустился до " + minValue + "%")
                                    .setContentText(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "%")
                                    .setContentIntent(pendingIntent)
                                    .setDefaults(Notification.DEFAULT_SOUND)
                                    .setAutoCancel(true);

                    NotificationManagerCompat notificationManager =
                            NotificationManagerCompat.from(context.getApplicationContext());
                    notificationManager.cancel(0);
                    notificationManager.notify(0, builder.build());
                }
            }
            context.registerReceiver(new PowerMonitorReceiver(), new IntentFilter(ACTION));
        }
    }
}
