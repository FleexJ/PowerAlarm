package com.example.poweralarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.poweralarm.activity.MainActivity;

public class StartPowerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("Start power\t", "");
        context.getApplicationContext().registerReceiver(new PowerReceiver(), new IntentFilter(MainActivity.ACTION));
    }
}
