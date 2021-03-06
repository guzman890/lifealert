package com.lifealert.pe.lifealert.back_class;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.lifealert.pe.lifealert.MainActivity;

/**
 * Created by pbl_8 on 04/01/2018.
 */

public class RDetector extends BroadcastReceiver {
    private static final String TAG = RDetector.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Detector.ACTION_FIN)) {
            Log.i(TAG, "--- Tarea finalizada Detector! ---");
            Toast.makeText(context, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
            MainActivity.Alerta();
        }
    }
}
