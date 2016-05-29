package com.sira.ssense;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TimerActivity extends Service {

    private String TAG ="Service";

    SharedPreferences sharedPref;
    String defaultValue;


    Timer timer;
    TimerTask timerTask;
    String deviceADDR;

    final Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startTimer();
        Log.d(TAG, "Service Started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
        Log.d(TAG, "Service Stopped");
    }

    public void startTimer() {
        timer = new Timer();

        initializeTimerTask();

        timer.schedule(timerTask, 5000, 10000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        final String strDate = simpleDateFormat.format(calendar.getTime());

                        //show the toast
                        int duration = Toast.LENGTH_SHORT;

                        Intent intent = new Intent(TimerActivity.this, Send_message.class);
                        intent.putExtra("message", "f");

                        sharedPref = getSharedPreferences("StoredSettings", Context.MODE_PRIVATE);
                        String deviceADDR = getResources().getString(R.string.client_address);

                        intent.putExtra("clientAddress", deviceADDR);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);

//                        Toast toast = Toast.makeText(getApplicationContext(), strDate, duration);
//                        toast.show();
                    }
                });
            }
        };
    }

}
