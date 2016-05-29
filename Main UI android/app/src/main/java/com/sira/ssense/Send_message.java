package com.sira.ssense;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Send_message extends AppCompatActivity {

    String deviceADDR, msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent thisIntent = getIntent();

        String FILENAME = "device_address";

        try{
            FileInputStream fis = openFileInput(FILENAME);
            byte[] bytes = new byte[17];
            fis.read(bytes);

            deviceADDR = new String(bytes, "UTF-8");

            fis.close();

        } catch (IOException e){
            Log.e("ERROR", "No internal storage");
        }

        msg = thisIntent.getStringExtra("message");


        Intent intent = new Intent(getBaseContext(), Bluetooth_communication.class);
        intent.putExtra("message", msg);
        intent.putExtra("clientAddress", deviceADDR);
        startActivity(intent);

    }
}
