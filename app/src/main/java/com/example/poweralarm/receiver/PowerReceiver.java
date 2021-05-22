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
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.poweralarm.R;
import com.example.poweralarm.activity.MainActivity;

public class PowerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MainActivity.ACTION)) {
            Log.v("PowerReceiver", "onReceive - " + MainActivity.ACTION);

            final SharedPreferences mSettings = context.getSharedPreferences(MainActivity.SHARED_FILE, Context.MODE_PRIVATE);

            if (!mSettings.contains(MainActivity.SHARED_MIN_ACTIVE)
                    || !mSettings.contains(MainActivity.SHARED_MIN_PERCENT)
                    || !mSettings.contains(MainActivity.SHARED_CHARGE_ACTIVE)
                    || !mSettings.contains(MainActivity.SHARED_PREV_VALUE))
                return;

            int curValue = intent.getIntExtra("level", -1);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                                 ||
                                 status == BatteryManager.BATTERY_STATUS_FULL;
            Log.v("PowerReceiver", "CurrentPowerLevel: " + curValue);

            boolean active = mSettings.getBoolean(MainActivity.SHARED_MIN_ACTIVE, false);
            int minValue = mSettings.getInt(MainActivity.SHARED_MIN_PERCENT, 20);
            boolean chargeActive = mSettings.getBoolean(MainActivity.SHARED_CHARGE_ACTIVE, false);
            int prevValue = mSettings.getInt(MainActivity.SHARED_PREV_VALUE, 0);

            if (active
                    && !isCharging
                    && minValue == curValue
                    && prevValue != minValue) {
                showNotification(context);
            } else if (chargeActive
                    && isCharging
                    && curValue == 100
                    && prevValue != 100) {
                    showNotification(context);
            }

            mSettings.edit()
                    .putInt(MainActivity.SHARED_PREV_VALUE, curValue)
                    .apply();
        }
    }

    private void showNotification(Context context) {
        Log.v("PowerReceiver", "showNotification");
        Intent intent_new = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), MainActivity.ID_NOTIF, intent_new, PendingIntent.FLAG_CANCEL_CURRENT);
        int currentPower = (
                (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE))
                .getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY
        );

        if(Build.VERSION.SDK_INT >= 26 ){
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context.getApplicationContext(),MainActivity.CHANNEL_ID);
            NotificationChannel notificationChannel = new NotificationChannel(MainActivity.CHANNEL_ID, MainActivity.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(1);
            notificationChannel.enableLights(true);

            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.battery)
                    .setContentTitle(context.getString(R.string.notifPowerInfo))
                    .setContentText(currentPower + "%")
                    .setShowWhen(true)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            nm.createNotificationChannel(notificationChannel);
            nm.cancel(MainActivity.ID_NOTIF);
            nm.notify(MainActivity.ID_NOTIF, notification);
        }
        else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), MainActivity.CHANNEL_ID)
                            .setSmallIcon(R.drawable.battery)
                            .setContentTitle(context.getString(R.string.notifPowerInfo))
                            .setContentText(currentPower + "%")
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context.getApplicationContext());
            notificationManager.cancel(MainActivity.ID_NOTIF);
            notificationManager.notify(MainActivity.ID_NOTIF, builder.build());
        }
    }
}
