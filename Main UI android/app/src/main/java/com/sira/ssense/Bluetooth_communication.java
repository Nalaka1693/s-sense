package com.sira.ssense;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class Bluetooth_communication extends AppCompatActivity {

    private String TAG;

    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothSocket btSocket;

    private SendMessage_BT sendMessage;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG = "bluetooth connectivity";
        super.onCreate(savedInstanceState);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        String msg, address;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                msg = null;
                address = null;
            } else {
                msg = extras.getString("message");
                address = extras.getString("clientAddress");
            }
        } else {
            msg = (String) savedInstanceState.getSerializable("message");
            address = (String) savedInstanceState.getSerializable("clientAddress");
        }


        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        btAdapter.cancelDiscovery();

        try {
            btSocket.connect();
            Log.d(TAG, "Connected");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        sendMessage = new SendMessage_BT(btSocket, msg);
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord",UUID.class);
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private void checkBTState() {
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class SendMessage_BT {
        private final OutputStream mmOutStream;

        public SendMessage_BT(BluetoothSocket socket, String msg) {
            OutputStream tmpOut = null;

            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) { }

            mmOutStream = tmpOut;
            write(msg);
        }

        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "Error data send: " + e.getMessage() + "...");
            }
        }
    }
}
