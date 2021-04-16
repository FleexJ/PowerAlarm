package com.example.poweralarm.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.poweralarm.activity.MainActivity;
import com.example.poweralarm.receiver.PowerReceiver;

public class StartService extends Service {
    final BroadcastReceiver powerReceiver = new PowerReceiver();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("StartService", "onCreate");
        try {
            unregisterReceiver(powerReceiver);
        } catch (IllegalArgumentException e) {
            Log.v("StartService", "Catching IllegalArgumentException,\tDo nothing");
        }
        registerReceiver(powerReceiver, new IntentFilter(MainActivity.ACTION));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(powerReceiver);
        Log.v("StartService", "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("StartService", "onStartCommand");
        return START_STICKY;
    }
}
