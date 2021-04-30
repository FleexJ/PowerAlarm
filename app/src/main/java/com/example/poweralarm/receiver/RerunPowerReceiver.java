package com.example.poweralarm.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.poweralarm.service.RunPowerReceiver;

class RerunPowerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.v("StartServiceReceiver", "onReceive - ACTION_BOOT_COMPLETED");
            context.startService(new Intent(context.getApplicationContext(), RunPowerReceiver.class));
        }
    }
}
