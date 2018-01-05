package com.lifealert.pe.lifealert.back_class;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.lifealert.pe.lifealert.MainActivity;

/**
 * Created by pbl_8 on 25/12/2017.
 */

public class RSendGPSUbication  extends BroadcastReceiver {
    private static final String TAG = RSendGPSUbication.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SendGPSUbication.ACTION_FIN)) {
            String response = intent.getStringExtra("response");

            Toast.makeText(context, "Tarea finalizada: "+ response, Toast.LENGTH_SHORT).show();

            MainActivity.enviarDatos=true;
        }
    }
}
