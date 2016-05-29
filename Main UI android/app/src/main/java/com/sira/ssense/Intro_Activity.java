package com.sira.ssense;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Intro_Activity extends AppCompatActivity {

    Button connectButton;
    ImageView connectionStatus, settingsIcon;
    Boolean device_connected;
    TextView startScreenInfo;
    int requestCode = 1;
    Vibrator vibrator;


    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;

    String deviceADDR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        device_connected = false;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro_1);

        connectionStatus = (ImageView)findViewById(R.id.status);
        settingsIcon = (ImageView)findViewById(R.id.settings_icon);

        connectButton = (Button)findViewById(R.id.btn_connect);
        startScreenInfo = (TextView)findViewById(R.id.startScreenInfo);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!device_connected){
                    startScreenInfo.setText("Trying to connect...");
                    Intent getDeviceStatus = new Intent(Intro_Activity.this, CheckBluetoothConnectivity.class);
                    startActivityForResult(getDeviceStatus, requestCode);
                } else {
                    Intent showProfile = new Intent(Intro_Activity.this, MainActivity.class);
                    startActivity(showProfile);
                }
            }
        });
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(Intro_Activity.this, ControlPanel.class);
                startActivity(settingsIntent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Boolean i_connectivity;
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                i_connectivity = data.getBooleanExtra("connected", false);

                vibrator.vibrate(100);

//              startService(new Intent(getBaseContext(), Bluetooth_listener.class));
                Intent intent = new Intent(Intro_Activity.this, Bluetooth_listener.class);
                startActivity(intent);

                String message;
                if(i_connectivity){
                    connectionStatus.setImageResource(R.drawable.device_online);
                    message = "Device connected successfully";

                    mBuilder= new NotificationCompat.Builder(Intro_Activity.this);
                    mBuilder.setSmallIcon(R.drawable.icon_small);
                    mBuilder.setContentTitle("S-Sense");
                    mBuilder.setTicker("Your S-Sense device is ready!");
                    i_connectivity = data.getBooleanExtra("connected", false);
                    mBuilder.setContentText("Your S-Sense is ready! \n" + data.getStringExtra("device_name") + " [" + data.getStringExtra("device_address")+ "] ");
                    deviceADDR = data.getStringExtra("device_address");
                    mNotificationManager.notify(0, mBuilder.build());


                    connectButton.setText("View Profile");
                    startScreenInfo.setText("Tap to View your Profile.");

                } else {
                    connectionStatus.setImageResource(R.drawable.device_online);
                    message = "Device is not ready";
                    startScreenInfo.setText("Tap to Try Again");

                }
                device_connected=  i_connectivity;

                Toast.makeText(Intro_Activity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendMessage(String msg){

        Intent intent = new Intent(Intro_Activity.this, Bluetooth_communication.class);
        intent.putExtra("message", msg);
        intent.putExtra("clientAddress", deviceADDR);
        startService(intent);
    }

}
