package com.example.airplanemode2;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Switch;
import android.widget.Toast;

public class AirplaneModeReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean state = intent.getBooleanExtra("state", false);
        if(state) {
            Toast.makeText(context, "on", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "off", Toast.LENGTH_SHORT).show();
        }
    }
}
