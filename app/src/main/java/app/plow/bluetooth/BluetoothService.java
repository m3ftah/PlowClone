package app.plow.bluetooth;

/**
 * Created by Meftah on 9/20/2015.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import alarmproject.apps.plow.alarmproject.activities.MainActivity;

public class BluetoothService extends Service implements Observer{
    private final IBinder mBinder = new MyBinder();
    private ArrayList<String> list = new ArrayList<String>();
    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "app.plow.service.receiver";
    public static boolean connected = false;
    public static BluetoothService bluetoothService;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bluetoothService = this;
        Log.d("Bluetooth Service","onStartCommand");
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                BluetoothRC blrc = BluetoothRC.getInstance(getApplicationContext());
                blrc.addObserver(BluetoothService.this);
                if (!BluetoothService.connected) while(!blrc.connect()) continue;
                BluetoothService.connected = true;
                //blrc.sendData("2");
                blrc.startListening();

                startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

                //publishResults("ready", Activity.RESULT_OK);
                return null;
            }
        }.execute();

        return START_STICKY;
    }


    private void publishResults(String outputPath, int result) {
        Intent intent = new Intent("app.plow.bluetooth.ServiceReceiver").putExtra(FILEPATH, outputPath);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void update(Observable observable, Object data) {
        publishResults((String)data,Activity.RESULT_OK);
    }

    public class MyBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    public List<String> getWordList() {
        return list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BluetoothService","onDestroy called");
        BluetoothRC.getInstance(getApplicationContext()).onPause();
        sendBroadcast(new Intent("app.plow.bluetooth.ServiceReceiver"));
    }
}