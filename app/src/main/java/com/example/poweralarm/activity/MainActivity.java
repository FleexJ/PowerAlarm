package com.example.poweralarm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.poweralarm.R;
import com.example.poweralarm.service.RunPowerReceiver;

public class MainActivity extends Activity {
    public static final String CHANNEL_ID = "PowerAlarmIDChannel";
    public static final String CHANNEL_NAME = "PowerAlarmNameChannel";
    public static final String ACTION = Intent.ACTION_BATTERY_CHANGED;
    public static final int ID_NOTIF = 0;

    public static final String SHARED_FILE = "PowerAlarmShared";
    public static final String SHARED_PERCENT = "PowerAlarmPercent";
    public static final String SHARED_ACTIVE = "PowerAlarmActive";
    public static final String SHARED_FULL_ACTIVE = "PowerAlarmFullActive";

    private SharedPreferences mSettings;

    private TextView textViewPercent;
    private SeekBar seekBarPercent;
    private Switch switchOn;
    private Switch switchFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);
        textViewPercent = findViewById(R.id.textVIewPercent);
        seekBarPercent = findViewById(R.id.seekBarPercent);
        switchOn = findViewById(R.id.switchOn);
        switchFull = findViewById(R.id.switchFull);

        start();
    }

    public void start() {
        Log.v("MainActivity", "start() begin");
        stopService(new Intent(this.getApplicationContext(), RunPowerReceiver.class));

        final SharedPreferences.Editor editor = mSettings.edit();

        if (mSettings.contains(SHARED_PERCENT) && mSettings.contains(SHARED_ACTIVE) && mSettings.contains(SHARED_FULL_ACTIVE)) {
            int value = mSettings.getInt(SHARED_PERCENT, 20);

            String str = value + " %";
            textViewPercent.setText(str);
            seekBarPercent.setProgress(value);
            switchOn.setChecked(mSettings.getBoolean(SHARED_ACTIVE, false));
            switchFull.setChecked(mSettings.getBoolean(SHARED_FULL_ACTIVE, false));
        }
        else {
            String str = 20 + " %";
            textViewPercent.setText(str);
            seekBarPercent.setProgress(20);
            switchOn.setChecked(false);
            switchFull.setChecked(false);

            editor.putInt(SHARED_PERCENT, 20);
            editor.putBoolean(SHARED_ACTIVE, false);
            editor.putBoolean(SHARED_FULL_ACTIVE, false);
            editor.apply();
        }

        seekBarPercent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String str = progress + " %";
                textViewPercent.setText(str);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 0)
                    seekBar.setProgress(1);
                editor.putInt(SHARED_PERCENT, seekBar.getProgress());
                editor.apply();
            }
        });


        switchOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Switch sw = (Switch) buttonView;
                if (sw.isChecked()) {
                    editor.putBoolean(SHARED_ACTIVE, true);
                    editor.apply();
                    String msg = getString(R.string.switchOnMessage);
                    showToastLong(MainActivity.this, msg);
                }
                else {
                    editor.putBoolean(SHARED_ACTIVE, false);
                    editor.apply();
                    String msg = getString(R.string.switchOffMessage);
                    showToastShort(MainActivity.this, msg);
                }
            }
        });

        switchFull.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Switch sw = (Switch) buttonView;
                if (sw.isChecked()) {
                    editor.putBoolean(SHARED_FULL_ACTIVE, true);
                    editor.apply();
                    String msg = getString(R.string.switchOnMessage);
                    showToastLong(MainActivity.this, msg);
                }
                else {
                    editor.putBoolean(SHARED_FULL_ACTIVE, false);
                    editor.apply();
                    String msg = getString(R.string.switchOffMessage);
                    showToastShort(MainActivity.this, msg);
                }
            }
        });

        startService(new Intent(this.getApplicationContext(), RunPowerReceiver.class));
        Log.v("MainActivity", "start() end");
    }

    public void showToastShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
