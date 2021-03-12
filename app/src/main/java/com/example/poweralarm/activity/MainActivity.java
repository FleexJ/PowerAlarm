package com.example.poweralarm.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poweralarm.R;
import com.example.poweralarm.receiver.PowerMonitorReceiver;

public class MainActivity extends Activity {

    public static final String SHARED_FILE = "PowerAlarmShared";
    public static final String SHARED_PERCENT = "PowerAlarmPercent";
    public static final String SHARED_ACTIVE = "PowerAlarmActive";

    private SharedPreferences mSettings;
    private SharedPreferences.Editor editor;

    private TextView textViewPercent;
    private SeekBar seekBarPercent;
    private Switch switchOn;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);
        editor = mSettings.edit();
        textViewPercent = findViewById(R.id.textVIewPercent);
        seekBarPercent = findViewById(R.id.seekBarPercent);
        switchOn = findViewById(R.id.switchOn);
        textView = findViewById(R.id.textView);

        start();
    }

    public void start() {
        final Context myContext = this;
        final int currentPower = ((BatteryManager) getSystemService(BATTERY_SERVICE)).getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        if (mSettings.contains(SHARED_PERCENT) && mSettings.contains(SHARED_ACTIVE)) {
            int value = mSettings.getInt(SHARED_PERCENT, 20);
            textViewPercent.setText(value + " %");
            seekBarPercent.setProgress(value);
            switchOn.setChecked(mSettings.getBoolean(SHARED_ACTIVE, false));
        }
        else {
            final int value = 20;
            final boolean active = false;
            textViewPercent.setText(value + " %");
            seekBarPercent.setProgress(value);
            switchOn.setChecked(active);
            editor.putInt(SHARED_PERCENT, value);
            editor.putBoolean(SHARED_ACTIVE, active);
            editor.apply();
        }
        updateTextView(textView);

        seekBarPercent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewPercent.setText(progress + " %");
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
                    Toast.makeText(MainActivity.this,"Уведомление активировано", Toast.LENGTH_SHORT).show();
                    myContext.registerReceiver(new PowerMonitorReceiver(), new IntentFilter(PowerMonitorReceiver.ACTION));
                }
                else {
                    editor.putBoolean(SHARED_ACTIVE, false);
                    editor.apply();
                    Toast.makeText(MainActivity.this,"Уведомление отключено", Toast.LENGTH_SHORT).show();
                }
                updateTextView(textView);
            }
        });
    }

    public void updateTextView(TextView textView) {
        textView.setText("Shared value: \n" +  mSettings.getInt(SHARED_PERCENT, 20) + "%\n" + mSettings.getBoolean(SHARED_ACTIVE, false));
    }
}
