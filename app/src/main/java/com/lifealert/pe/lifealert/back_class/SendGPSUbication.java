package com.lifealert.pe.lifealert.back_class;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lifealert.pe.lifealert.utils.CustomJSONObjectRequest;
import com.lifealert.pe.lifealert.utils.CustomVolleyRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pbl_8 on 25/12/2017.
 */

public class SendGPSUbication extends IntentService implements Response.Listener, Response.ErrorListener {
    private static final String TAG = SendGPSUbication.class.getSimpleName();
    public static final String REQUEST_TAG = SendGPSUbication.class.getSimpleName();
    public static final String ACTION_FIN = "SendGPSUbication.FIN";
    public static String responseWeb = "";

    private RequestQueue mQueue;
    public double ubiLong = 0;
    public double ubiLati = 0;

    public SendGPSUbication() {
        super("SendGPSUbication");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ubiLong = intent.getDoubleExtra("long", 0);
        ubiLati = intent.getDoubleExtra("lati", 0);
        Intent bcIntent = new Intent();
        Log.i(TAG,"Data listo para enviar");
        //Comunicaion http
        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                .getRequestQueue();
        String url = "https://viv0-busqueda.herokuapp.com/service.php?dni=72050160&lati="+ubiLati+"&lon="+ubiLong;
        final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(
                Request.Method.GET,
                url,
                new JSONObject(),
                this,
                this);
        jsonRequest.setTag(REQUEST_TAG);
        mQueue.add(jsonRequest);
        //Fin de comunicaicon
        espera();
        bcIntent.setAction(ACTION_FIN);
        bcIntent.putExtra("response",responseWeb);
        sendBroadcast(bcIntent);
        Log.i(TAG, "--- Tarea finalizada - SendGPSUbication! ---");
    }
    private void espera()
    {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResponse(Object response) {
        responseWeb = response.toString();
        Log.i(TAG,"Response : " + response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.i(TAG,"--- Fin de Consulta ---");
    }

}

