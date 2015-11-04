package app.plow.bluetooth;

/**
 * Created by Meftah on 9/20/2015.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import alarmproject.apps.plow.alarmproject.R;
import app.plow.Pairing;

public class ServiceReceiver extends BroadcastReceiver {

    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("hi","Received in ServiceReceiver");
        notify("SmartPlow","est connecté");
        Intent service = new Intent(context.getApplicationContext(), BluetoothService.class);
        service.addFlags(Intent.FLAG_FROM_BACKGROUND);
        Log.d("starter","Set");
        context.startService(service);


    }
    private void notify(String notificationTitle, String notificationMessage){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")

        Notification notification = new Notification(R.mipmap.app_logo,"Plow connecté", System.currentTimeMillis());
        Intent notificationIntent = new Intent(context,Pairing.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,notificationIntent, 0);

        notification.setLatestEventInfo(context, notificationTitle,notificationMessage, pendingIntent);
        notificationManager.notify(9999, notification);
    }
}