package com.lifealert.pe.lifealert;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.lifealert.pe.lifealert.back_class.AlertReciver;
import com.lifealert.pe.lifealert.back_class.BeaconTrans;
import com.lifealert.pe.lifealert.back_class.Detector;
import com.lifealert.pe.lifealert.back_class.RAlertReciver;
import com.lifealert.pe.lifealert.back_class.RBeaconTrans;
import com.lifealert.pe.lifealert.back_class.RDetector;
import com.lifealert.pe.lifealert.back_class.RSendGPSUbication;
import com.lifealert.pe.lifealert.back_class.SendGPSUbication;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    //varaibles banderas
    public static boolean flagproximity=false;
    public static boolean flaglocate=false;
    public static MainActivity main;

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    public static Location mLastLocation;
    public static boolean enviarDatos = false;
    public static boolean beaconActivar = false;
    private GoogleApiClient mGoogleApiClient;

    private boolean mRequestLocationUpdates = true;

    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;

    // view
    private Button btnShowLocation, btnStartLocationUpdates;

    //Intent
    private static Intent msgIntent;
    private static Intent msgIntent2;
    private static Intent msgIntent3;
    private static Intent msgIntent4;
    private static SensorManager sensorManager;
    private static Sensor proximitySensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(proximitySensor == null) {
            Log.e(TAG, "Proximity sensor not available.");
        }

        //Verificar permisos
        if(checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        //botones
        btnShowLocation = (Button) findViewById(R.id.buttonShowLocation);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocation();
            }
        });

        // intent
        msgIntent4 = new Intent(this, Detector.class);
        msgIntent3 = new Intent(this, BeaconTrans.class);
        msgIntent2 = new Intent(this, SendGPSUbication.class);
        msgIntent = new Intent(this, AlertReciver.class);

        // intent Alert Reciver
        //msgIntent.putExtra("iteraciones", 20);
        startService(msgIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        main=this;
        IntentFilter filter = new IntentFilter();
        filter.addAction(AlertReciver.ACTION_PROGRESO);
        filter.addAction(AlertReciver.ACTION_FIN);
        RAlertReciver rcv = new RAlertReciver();
        registerReceiver(rcv, filter);

        Log.i(TAG, "Comenzo APP ");

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        ActivarSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        if(mGoogleApiClient.isConnected() && mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void displayLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longtitude = mLastLocation.getLongitude();
            Log.i(TAG,"Bool: "+enviarDatos);
            //if GPS
            if(enviarDatos){

                IntentFilter filterSend = new IntentFilter();
                filterSend.addAction(SendGPSUbication.ACTION_FIN);
                RSendGPSUbication rcvSend = new RSendGPSUbication();
                registerReceiver(rcvSend, filterSend);

                msgIntent2.putExtra("long",longtitude);
                msgIntent2.putExtra("lati",latitude);
                startService(msgIntent2);

                enviarDatos =false;
                Log.i(TAG,"---- Enviar Data ----");

            }
            //if beacon
            if(beaconActivar){
                beaconActivar =false;
                IntentFilter filterBeaon = new IntentFilter();
                filterBeaon.addAction(BeaconTrans.ACTION_FIN);
                RBeaconTrans rcvBeacon = new RBeaconTrans();
                registerReceiver(rcvBeacon, filterBeaon);
                int DNI = 7205;
                msgIntent3.putExtra("DNI", DNI);
                startService(msgIntent3);
            }
            Toast.makeText(getApplicationContext(), latitude + ", " + longtitude, Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(getApplicationContext(), "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_LONG)
                    .show();
        }
        Log.i(TAG, " ---- APP ---- Display Location");

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS) {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();

        if(mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();

        displayLocation();
        Log.i(TAG,"location change");

        flaglocate=true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }

    public static void ActivarSensor(){
        SensorEventListener proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // More code goes here
                flagproximity=true;
                Log.i(TAG,"Sensor proximity change");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        sensorManager.registerListener(proximitySensorListener, proximitySensor, 2 * 1000 * 1000);
    }
    public void ActivateDetector(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Detector.ACTION_FIN);
        RDetector rcv = new RDetector();
        registerReceiver(rcv, filter);
        startService(msgIntent4);
        Log.i(TAG,"--- Start Detector ---");
    }
    public static void Alerta(){
        enviarDatos =true;
        beaconActivar =true;
        main.displayLocation();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
}
