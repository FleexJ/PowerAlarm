package com.example.poweralarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.poweralarm.service.RunPowerReceiver;

public class RerunPowerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.v("RerunPowerReceiver", "onReceive - ACTION_BOOT_COMPLETED");
            context.startService(
                    new Intent(
                            context.getApplicationContext(),
                            RunPowerReceiver.class
                    )
            );
        }
    }
}
