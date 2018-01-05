package com.lifealert.pe.lifealert.back_class;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.lifealert.pe.lifealert.MainActivity;

/**
 * Created by pbl_8 on 25/12/2017.
 */

public class RAlertReciver extends BroadcastReceiver {
    private static final String TAG = RAlertReciver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(AlertReciver.ACTION_PROGRESO)) {
            int prog = intent.getIntExtra("progreso", 0);
            Log.i(TAG, "--- progreso:"+prog);
        }
        else if(intent.getAction().equals(AlertReciver.ACTION_FIN)) {
            Log.i(TAG, "--- Tarea finalizada AlertReciver! ---");
            Toast.makeText(context, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
            MainActivity.main.ActivateDetector();
        }
    }
}