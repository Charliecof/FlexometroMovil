package com.example.flexometromovil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;

public class ActivityUx extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maybeEnableArButton();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    void maybeEnableArButton() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            // Continue to query availability at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            Toast.makeText(this, "Es compatible", Toast.LENGTH_LONG).show();
        } else { // The device is unsupported or unknown.
            Toast.makeText(this, "No es compatible", Toast.LENGTH_LONG).show();
        }
    }

}