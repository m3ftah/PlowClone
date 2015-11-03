package app.plow.bluetooth;

/**
 * Created by Meftah on 9/20/2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyStartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("starter","Server Starter has received a broadcast");
        Intent service = new Intent(context.getApplicationContext(), BluetoothService.class);
        context.startService(service);
        Toast.makeText(context,"Service Restarted",Toast.LENGTH_SHORT).show();
    }
}