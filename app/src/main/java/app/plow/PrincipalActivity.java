package app.plow;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import alarmproject.apps.plow.alarmproject.R;
import app.plow.bluetooth.BluetoothRC;
import app.plow.bluetooth.BluetoothService;


public class PrincipalActivity extends Activity implements Observer{
    private static final String TAG = "LEDOnOff";

    ImageButton lamp_yellow,microphone_btn;
    public static boolean lamp_yellow_on = false;
    static String on="allumer la lampe";
    static String off="éteindre la lampe";
    BluetoothRC blrc = null;
    GoogleVoice gvc = null;
    Button b;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "In onCreate()");

        setContentView(R.layout.activity_principal);

        lamp_yellow = (ImageButton) findViewById(R.id.lamp_yellow);
        microphone_btn=(ImageButton)findViewById(R.id.micro_btn);
        blrc = BluetoothRC.getInstance(this);
        blrc.addObserver(this);
        gvc = new GoogleVoice(this);

        lamp_yellow.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                lamp_yellow_on = !lamp_yellow_on;

                if (lamp_yellow_on) {
                    sendData("2");
                } else {
                    sendData("3");
                }
                Log.d(TAG, "lampe clicked");
                if (!lamp_yellow_on)
                    lamp_yellow.setImageResource(R.drawable.lamp_off);
                else
                    lamp_yellow.setImageResource(R.drawable.lamp_yellow);
            }
        });
        microphone_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                gvc.openMicrophone();

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        sendBroadcast(new Intent("app.plow.bluetooth.ServiceReceiver").putExtra(BluetoothService.FILEPATH,"restart"));
        //startService(new Intent(this, BluetoothService.class));
        //blrc.onResume();
        //blrc.startListening();
        lamp_yellow_on  = getPreferences(MODE_PRIVATE).getBoolean("led",true);
        //registerReceiver(
                new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                switch (b.getInt(BluetoothAdapter.EXTRA_STATE)){
                    case BluetoothAdapter.STATE_TURNING_OFF:Log.d("stateBluetooth","turning off");
                        stopService(new Intent(context,BluetoothService.class));
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:Log.d("stateBluetooth", "turning on");
                        break;
                    case BluetoothAdapter.STATE_OFF:Log.d("stateBluetooth","off");
                        break;
                    case BluetoothAdapter.STATE_ON:Log.d("stateBluetooth","on");
                        context.startService(new Intent(context,BluetoothService.class));
                        break;
                }
                unregisterReceiver(this);
            }
        };//,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        //);

    }

    @Override
    public void onPause() {
        super.onPause();
        //blrc.onPause();
        getPreferences(MODE_PRIVATE).edit().putBoolean("led",lamp_yellow_on).commit();

    }

    private void sendData(String message) {
        blrc.sendData(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GoogleVoice.check && resultCode==RESULT_OK){
            String result = gvc.onActivityForResult(data);
            String str2 ="";
            if(on.contains(result)){
                sendData("3");
            }else
                if(off.contains(result)){
                sendData("2");
            }else
                Toast.makeText(getBaseContext(), "Commande Inexistante",Toast.LENGTH_SHORT).show();

            if (str2.equals("2")){
                lamp_yellow_on = false;
                lamp_yellow.setImageResource(R.drawable.lamp_off);
            }
            else if (str2.equals("3")){
                lamp_yellow_on = true;
                lamp_yellow.setImageResource(R.drawable.lamp_yellow);
            }
        }

    }


    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof BluetoothRC){
            final String str = (String) data;
            Log.d(TAG, "observed");
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PrincipalActivity.this, " received : " + str, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        //sendBroadcast(new Intent("app.plow.bluetooth.ServiceReceiver"));
        Log.d(TAG, "broadcast sent");
    }
}