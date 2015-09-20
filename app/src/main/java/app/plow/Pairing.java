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
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Pairing extends Activity {
    @Bind(R.id.retry)    Button retry;
    @OnClick(R.id.retry)
    public void retry(){
        Toast.makeText(this,"hello, from retry",Toast.LENGTH_SHORT).show();
        new ConnectingTask().execute();
    }
    class ConnectingTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] params) {
            BluetoothRC blrc = BluetoothRC.getInstance(Pairing.this);
            if (blrc.connect()){
                startActivity(new Intent(Pairing.this, PrincipalActivity.class));
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pairing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
