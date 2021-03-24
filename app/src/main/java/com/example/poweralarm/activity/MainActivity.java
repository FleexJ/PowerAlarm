package com.example.poweralarm.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.poweralarm.R;
import com.example.poweralarm.service.StartService;

import java.util.Calendar;

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
    private TextView textView;
    private Switch switchFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);
        textViewPercent = findViewById(R.id.textVIewPercent);
        seekBarPercent = findViewById(R.id.seekBarPercent);
        switchOn = findViewById(R.id.switchOn);
        textView = findViewById(R.id.textView);
        switchFull = findViewById(R.id.switchFull);

        start();
    }

    public void start() {
        stopService(new Intent(this.getApplicationContext(), StartService.class));

        final int currentPower = ((BatteryManager) getSystemService(BATTERY_SERVICE)).getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
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
            final int value = 20;
            final boolean active = false;
            final boolean fullActive = false;
            String str = value + " %";
            textViewPercent.setText(str);
            seekBarPercent.setProgress(value);
            switchOn.setChecked(active);
            switchFull.setChecked(fullActive);
            editor.putInt(SHARED_PERCENT, value);
            editor.putBoolean(SHARED_ACTIVE, active);
            editor.putBoolean(SHARED_FULL_ACTIVE, fullActive);
            editor.apply();
        }
        updateTextView(textView);

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
                updateTextView(textView);
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
                updateTextView(textView);
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
                updateTextView(textView);
            }
        });

        startService(new Intent(this.getApplicationContext(), StartService.class));
    }

    public void showToastShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public void updateTextView(TextView textView) {
        String str = "Shared value:\nPercent: " +  mSettings.getInt(SHARED_PERCENT, 20) +
                "%\nPercentActive: " + mSettings.getBoolean(SHARED_ACTIVE, false) +
                "\nFullActive: " + mSettings.getBoolean(SHARED_FULL_ACTIVE, false);
        textView.setText(str);
    }
}
