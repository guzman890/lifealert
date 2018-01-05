package com.lifealert.pe.lifealert.back_class;

/**
 * Created by pbl_8 on 25/12/2017.
 */

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


public class AlertReciver extends IntentService {
    private static final String TAG = AlertReciver.class.getSimpleName();

    public static final String ACTION_PROGRESO = "AlertReciver.PROGRESO";
    public static final String ACTION_FIN = "AlertReciver.FIN";

    public AlertReciver() {

        super("AlertReciver");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        boolean flag=false;
        boolean flagbucle=true;
        while(flagbucle) {
            Cursor cur = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
            Log.i(TAG,"buscando mensaje de alerta");
            if (cur.moveToFirst()) { /* false = no sms */
                do {
                    String msgInfo = "";
                    for (int i = 0; i < cur.getColumnCount(); i++) {
                        msgInfo += "/" + cur.getColumnName(i) + ":" + cur.getString(i);
                    }
                    String[] smm = msgInfo.split("/");
                    String[] campos;
                    for (int i = 0; i < smm.length; i++) {
                        campos = smm[i].split(":");
                        if (campos.length == 2) {
                            if (campos[0].contains("address") && campos[1].contains("+51930274827")) {
                                flag = true;
                            }
                            if (campos[0].contains("body") && campos[1].contains("Terremoto") && flag) {
                                flagbucle = false;
                                Log.i(TAG,"Mensaje encontrado");
                            }
                        }
                    }
                    //Log.i(TAG,msgInfo);
                } while (cur.moveToNext());
            }
            tareaLarga();
            cur.close();
        }
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
