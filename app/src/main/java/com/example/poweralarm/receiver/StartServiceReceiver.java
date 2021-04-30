package com.example.poweralarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.poweralarm.service.StartService;

class StartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("StartServiceReceiver", "onReceive");
        context.startService(new Intent(context.getApplicationContext(), StartService.class));
    }
}
