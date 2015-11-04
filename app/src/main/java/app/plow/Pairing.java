package app.plow;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rey.material.widget.Button;

import java.util.Observable;
import java.util.Observer;

import alarmproject.apps.plow.alarmproject.R;
import alarmproject.apps.plow.alarmproject.activities.AskActivity;
import alarmproject.apps.plow.alarmproject.activities.MainActivity;
import app.plow.bluetooth.BluetoothRC;
import app.plow.bluetooth.BluetoothService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Pairing extends Activity implements Observer {
    public static String TAG = Pairing.class.getSimpleName();
    @Bind(R.id.retry)    Button retry;

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof BluetoothRC){
            final String str = (String) data;
            Log.d(TAG, "observed");
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (str.equals("1"))
                    {
                        startActivity(new Intent(getApplicationContext(), AskActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    Toast.makeText(Pairing.this, " received : " + str, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private BluetoothRC blrc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        ButterKnife.bind(this);
        retry.setVisibility(View.INVISIBLE);
        blrc = BluetoothRC.getInstance(this);
        blrc.addObserver(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sendBroadcast(new Intent("app.plow.bluetooth.ServiceReceiver"));
    }
}
