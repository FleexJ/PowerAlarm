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
            final SharedPreferences mSettings = context.getSharedPreferences(MainActivity.SHARED_FILE, Context.MODE_PRIVATE);

            boolean active = false;
            int minValue = 20;

            if (mSettings.contains(MainActivity.SHARED_ACTIVE) && mSettings.contains(MainActivity.SHARED_PERCENT)) {
                active = mSettings.getBoolean(MainActivity.SHARED_ACTIVE, active);
                minValue = mSettings.getInt(MainActivity.SHARED_PERCENT, minValue);
            }

            int curValue = intent.getIntExtra("level", -1);

            Log.v("Receiver\t", String.valueOf(curValue));
            if (active) {
                Log.v("Receiver active\t", String.valueOf(curValue));
                BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
                if (curValue == 100 && batteryManager.getIntProperty(BatteryManager.BATTERY_STATUS_CHARGING) == BatteryManager.BATTERY_STATUS_CHARGING) {
                    Log.v("Receiver, charge 100%\t", String.valueOf(curValue));
                    showNotification(context);
                }
                else
                if (minValue == curValue) {
                    Log.v("Receiver, minVal\t", String.valueOf(curValue));
                    showNotification(context);
                }
            }
        }
    }

    private void showNotification(Context context) {
        Intent intent_new = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), MainActivity.ID_NOTIF, intent_new, PendingIntent.FLAG_CANCEL_CURRENT);
        int currentPower = ((BatteryManager) context.getSystemService(Context.BATTERY_SERVICE)).getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        if(Build.VERSION.SDK_INT >= 26 ){
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context.getApplicationContext(),MainActivity.CHANNEL_ID);
            NotificationChannel notificationChannel = new NotificationChannel(MainActivity.CHANNEL_ID, MainActivity.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(1);
            notificationChannel.enableLights(true);

            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.battery)
                    .setContentTitle("Уровень заряда!")
                    .setContentText(currentPower + "%")
                    .setShowWhen(true)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            notification.defaults = NotificationCompat.DEFAULT_ALL;
            nm.createNotificationChannel(notificationChannel);
            nm.cancel(MainActivity.ID_NOTIF);
            nm.notify(MainActivity.ID_NOTIF, notification);
        }
        else
        if (Build.VERSION.SDK_INT >= 21) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context.getApplicationContext(), MainActivity.CHANNEL_ID)
                            .setSmallIcon(R.drawable.battery)
                            .setContentTitle("Уровень заряда!")
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
