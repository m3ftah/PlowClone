package app.plow.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * Created by Meftah on 9/17/2015.
 */
public class BluetoothRC extends Observable  {
    private static final String TAG = "BluetoothRC";
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private Context context = null;
    private static BluetoothRC instance;
    private AsyncTask task;
    private ArrayList<Observer> observers = new ArrayList<>();
    private boolean connected = false;
    // Well known SPP UUID
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Insert your bluetooth devices MAC address
    private static String address = "20:14:05:06:21:16";


    public static BluetoothRC getInstance(Context context){
        if (BluetoothRC.instance == null){
            BluetoothRC.instance = new BluetoothRC(context);
        }else{
            BluetoothRC.instance.context = context;
        }
        return BluetoothRC.instance;
    }

    private BluetoothRC(Context context){
        this.context = context;
        openBluetooth();
    }
    public void openBluetooth(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()){
            btAdapter.enable();
            long ref = System.currentTimeMillis();
            while( System.currentTimeMillis() - ref < 6000) continue;
        }
    }

    public void onResume(){
        Log.d(TAG, "...In onResume - Attempting client connect...");
        //checkBTState();
        if (!connected)
            connect();

    }

    public boolean connect() {
        boolean problem = false;
        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.d(TAG, "In onResume() and socket create failed: ");
            problem = true;
        }
        // Discovery is resource intensive. Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();
        // Establish the connection. This will block until it connects.
        Log.d(TAG, "...Connecting to Remote...");

        try {
            btSocket.connect();
            Log.d(TAG, "...Connection established and data link opened...");
        } catch (IOException e) {
            problem = true;
            Log.d(TAG,"close socket during connection failure");
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.d(TAG,"In onResume() and unable to close socket during connection failure");
            }
        }
        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Creating Socket...");
        try {
            inStream = btSocket.getInputStream();
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.d(TAG,"In onResume() and output stream creation failed:");
            problem = true;
        }
        this.connected = true;
        return !problem;
    }

    public void onPause(){
        Log.d(TAG, "...In onPause()...");
        connected = false;
        /*if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                Log.d(TAG, "In onPause() and failed to flush output stream: ");
            }
        }*/

        try {
            btSocket.close();
        } catch (IOException e2) {
            Log.d(TAG, "In onPause() and failed to close socket.");
        }
    }

    public  void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Sending data: " + message + "...");
        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            Log.d(TAG,"erreur sending Data");
        }
    }
    public String receiveData() {
        String str = null;
        try {
            int a = inStream.read();
            a -= 48;
            str = String.valueOf(a);
            Log.d(TAG, "received " + str);
        } catch (IOException e) {
            Log.d(TAG,"erreur receiving Data");
        }
        return str;
    }
    public void startListening(){
        task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {

                    while (btSocket.isConnected()) {
                        while (inStream.available() == 0) {
                            if (!isConnected()){
                                Log.d("stateConnection", "Disconnected");
                                onPause();
                                    /*context.startService(new Intent(
                                            context.getApplicationContext(),BluetoothService.class
                                    ));
                                    long ref = System.currentTimeMillis();
                                    while( System.currentTimeMillis() - ref < 2000) continue;*/
                                BluetoothService.connected = false;
                                BluetoothRC blrc = BluetoothRC.getInstance(context);
                                if (!BluetoothService.connected) while(!blrc.connect()) continue;
                            }

                        }
                        final String str = receiveData();
                        Log.d(TAG, "Data Received: " + str);
                        BluetoothRC.this.notifyObservers(str);

                    }
                }catch(IOException e){
                    // e.printStackTrace();
                }

                return null;
            }

        };
        task.execute();
    }

    @Override
    public void notifyObservers(Object str) {
        for (Observer ob : observers) {
            ob.update(this, str);
        }
    }

    @Override
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }


    public boolean isConnected() {
        long ref = System.currentTimeMillis();
        while( System.currentTimeMillis() - ref < 2000) continue;
        try {
            outStream.write("0".getBytes());
        } catch (IOException e) {
            Log.d(TAG,"erreur sending Data");
            return false;
        }
        return true;
    }
}