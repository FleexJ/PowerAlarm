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
    public static final String SHARED_MIN_PERCENT = "MinPercent";
    public static final String SHARED_MIN_ACTIVE = "MinActive";
    public static final String SHARED_CHARGE_ACTIVE = "ChargeActive";
    // нужно, чтобы уведомление об одном уровне не приходило несколько раз
    public static final String SHARED_PREV_VALUE = "PrevValue";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("MainActivity", "onCreate start");

        // view and variable
        final SharedPreferences mSettings = getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);
        final TextView textViewPercent = findViewById(R.id.textVIewPercent);
        final SeekBar seekBarPercent = findViewById(R.id.seekBarPercent);
        final Switch switchOn = findViewById(R.id.switchOn);
        final Switch switchFull = findViewById(R.id.switchFull);

        stopService(
                new Intent(
                        this.getApplicationContext(),
                        RunPowerReceiver.class
                )
        );

        // if shared value exists - views set values
        if (mSettings.contains(SHARED_MIN_PERCENT)
                && mSettings.contains(SHARED_MIN_ACTIVE)
                && mSettings.contains(SHARED_CHARGE_ACTIVE)
                && mSettings.contains(SHARED_PREV_VALUE)
        ) {
            int value = mSettings.getInt(SHARED_MIN_PERCENT, 20);

            String str = value + " %";
            textViewPercent.setText(str);
            seekBarPercent.setProgress(value);
            switchOn.setChecked(mSettings.getBoolean(SHARED_MIN_ACTIVE, false));
            switchFull.setChecked(mSettings.getBoolean(SHARED_CHARGE_ACTIVE, false));
        } // if shared value not exists - set shared value and views
        else {
            String str = 20 + " %";
            textViewPercent.setText(str);
            seekBarPercent.setProgress(20);
            switchOn.setChecked(false);
            switchFull.setChecked(false);

            mSettings.edit()
                    .putInt(SHARED_MIN_PERCENT, 20)
                    .putBoolean(SHARED_MIN_ACTIVE, false)
                    .putBoolean(SHARED_CHARGE_ACTIVE, false)
                    .putInt(SHARED_PREV_VALUE, 0)
                    .apply();
        }

        // change min percent
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
                mSettings.edit()
                        .putInt(SHARED_MIN_PERCENT, seekBar.getProgress())
                        .apply();
            }
        });


        // change state for notify min percent
        switchOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    mSettings.edit()
                            .putBoolean(SHARED_MIN_ACTIVE, true)
                            .apply();
                    String msg = getString(R.string.switchOnMessage);
                    showToastLong(MainActivity.this, msg);
                }
                else {
                    mSettings.edit()
                            .putBoolean(SHARED_MIN_ACTIVE, false)
                            .apply();
                    String msg = getString(R.string.switchOffMessage);
                    showToastShort(MainActivity.this, msg);
                }
            }
        });

        // change state for notify full charge
        switchFull.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    mSettings.edit()
                            .putBoolean(SHARED_CHARGE_ACTIVE, true)
                            .apply();
                    String msg = getString(R.string.switchOnMessage);
                    showToastLong(MainActivity.this, msg);
                }
                else {
                    mSettings.edit()
                            .putBoolean(SHARED_CHARGE_ACTIVE, false)
                            .apply();
                    String msg = getString(R.string.switchOffMessage);
                    showToastShort(MainActivity.this, msg);
                }
            }
        });

        startService(
                new Intent(
                        this.getApplicationContext(),
                        RunPowerReceiver.class
                )
        );
        Log.v("MainActivity", "onCreate end");
    }

    public void showToastShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
