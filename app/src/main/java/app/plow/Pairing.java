package app.plow;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rey.material.widget.Button;

import alarmproject.apps.plow.alarmproject.R;
import alarmproject.apps.plow.alarmproject.activities.MainActivity;
import app.plow.bluetooth.BluetoothRC;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Pairing extends Activity {
    @Bind(R.id.retry)    Button retry;
    @OnClick(R.id.retry)
    public void retry(){

        new ConnectingTask().execute();
    }
    class ConnectingTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] params) {
            BluetoothRC blrc = BluetoothRC.getInstance(Pairing.this);
            if (blrc.connect()){
                startActivity(new Intent(Pairing.this, MainActivity.class));
            }else{
                Pairing.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Pairing.this, "Connection echoue", Toast.LENGTH_SHORT).show();
                        retry.setVisibility(View.VISIBLE);
                        //Pairing.this.finish();
                    }
                });
            }
            return null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        ButterKnife.bind(this);
        retry.setVisibility(View.INVISIBLE);
        new ConnectingTask().execute();
    }
}
