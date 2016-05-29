package com.sira.ssense;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

public class CheckBluetoothConnectivity extends AppCompatActivity {


    private static final String TAG = "BluetoothConnectivity";

    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;

    private Boolean device_found = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_check_bluetooth_connectivity);

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        try {
            Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();
            //BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            if(bondedDevices.isEmpty()) {
                Toast.makeText(getApplicationContext(),"Please Pair the Device first",Toast.LENGTH_SHORT).show();
            }
            else {
                for (BluetoothDevice iterator : bondedDevices) {
                    if(iterator.getName().startsWith("HC-")){
                        device = iterator;
                        device_found = true;
                        break;
                    }
                }
            }
        } catch (NullPointerException e){
            Toast.makeText(getApplicationContext(),"No devices found!!!",Toast.LENGTH_SHORT).show();

            Intent intent=new Intent();
            intent.putExtra("device_name","");
            intent.putExtra("device_address", "");
            intent.putExtra("connected",device_found);

            setResult(RESULT_OK,intent);
            finish();
        }

        Intent intent=new Intent();
        intent.putExtra("device_name",device.getName());
        intent.putExtra("device_address",device.getAddress());
        intent.putExtra("connected",device_found);
        setResult(RESULT_OK,intent);
        finish();

    }

    private void checkBTState() {
        if(btAdapter==null) {
            Log.d("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "Bluetooth Enabled");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }
        }
    }
}
