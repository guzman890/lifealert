package com.lifealert.pe.lifealert.back_class;

import android.app.IntentService;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Intent;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;

/**
 * Created by pbl_8 on 27/12/2017.
 */

public class BeaconTrans extends IntentService {

    private static final String TAG = BeaconTrans.class.getSimpleName();


    public static final String ACTION_PROGRESO = "BeaconTrans.PROGRESO";
    public static final String ACTION_FIN = "BeaconTrans.FIN";

    public BeaconTrans() {

        super("BeaconTrans");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        int DNI = intent.getIntExtra("DNI", 0);
        Beacon beacon = new Beacon.Builder()
                .setId1("2f234454-cf6d-4a0f-adf2-000072050160")
                .setId2("7205")
                .setId3("0160")
                .setManufacturer(0x0118)
                .setTxPower(-59)
                .setDataFields(Arrays.asList(new Long[] {72050160L,72050160L}))
                .build();
        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
        BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "Advertisement start failed with code: "+errorCode);
            }
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i(TAG, "Advertisement start succeeded.");
            }
        });

        Log.i(TAG,"--- Beacon Activado - BeaconTrans! ---");
        tareaLarga();
        Intent bcIntent = new Intent();
        bcIntent.setAction(ACTION_FIN);
        sendBroadcast(bcIntent);
    }

    private void tareaLarga()
    {
        try {
            Thread.sleep(10000);
        } catch(InterruptedException e) {

        }
    }
}
