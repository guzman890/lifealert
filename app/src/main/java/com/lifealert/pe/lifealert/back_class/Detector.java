package com.lifealert.pe.lifealert.back_class;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lifealert.pe.lifealert.MainActivity;

/**
 * Created by pbl_8 on 04/01/2018.
 */

public class Detector extends IntentService {

    private static final String TAG = AlertReciver.class.getSimpleName();

    public static final String ACTION_FIN = "Detector.FIN";
    public int timer=0;
    public Detector() {
        super("Detector");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (true){
            if(MainActivity.flaglocate||MainActivity.flagproximity){
                timer=0;
                if (MainActivity.flagproximity)
                    MainActivity.flagproximity=false;
                if (MainActivity.flaglocate)
                    MainActivity.flaglocate=false;
            }else {
                timer++;
                tareaLarga();
            }
            if(timer==10){
                break;
            }
        }
        Log.i(TAG,"Se encontro inactividad");

        Intent bcIntent = new Intent();
        bcIntent.setAction(ACTION_FIN);
        sendBroadcast(bcIntent);
    }
    private void tareaLarga()
    {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {

        }
    }
}
