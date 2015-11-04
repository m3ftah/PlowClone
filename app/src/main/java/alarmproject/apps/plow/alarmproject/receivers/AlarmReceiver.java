package alarmproject.apps.plow.alarmproject.receivers;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import alarmproject.apps.plow.alarmproject.Controller.DateController;
import alarmproject.apps.plow.alarmproject.R;
import alarmproject.apps.plow.alarmproject.activities.AskActivity;
import alarmproject.apps.plow.alarmproject.activities.MainActivity;
import alarmproject.apps.plow.alarmproject.model.Alarm;
import alarmproject.apps.plow.alarmproject.model.DateCalendar;
import alarmproject.apps.plow.alarmproject.model.Utilities;
import app.plow.bluetooth.BluetoothRC;
import app.plow.PrincipalActivity;
import app.plow.bluetooth.BluetoothService;

public class AlarmReceiver extends BroadcastReceiver{
	AlarmManager am;
	PendingIntent pi;
	final static private long ONE_MINUTE = 60000;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);


		pi = PendingIntent.getBroadcast(context, 0, new Intent(
				context.getResources().getString(R.string.service_alarm_manager)), 0);
		am = (AlarmManager) (context.getSystemService(Context.ALARM_SERVICE));

		boolean one_min = Boolean.parseBoolean(prefs.getString("one_minute",
				"false"));
		if (!one_min) {
			Calendar cal = Calendar.getInstance();
			int sec = cal.get(Calendar.SECOND);

			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("one_minute", "true");
			editor.commit();
			am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + (ONE_MINUTE - sec*1000), pi);
			
		} else {
			am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + ONE_MINUTE, pi);
		}

			DateController dt= new DateController(context);
			if (dt.isNowFirstAlarm()) {
				BluetoothRC.getInstance(context).sendData(Utilities.ARDUINO_ALARM_GET_STATE);
			}



		}



	class ConnectingTask extends AsyncTask {
		Context context;
		BluetoothRC blrc;
		public ConnectingTask(Context c)
		{
			this.context=c;
		}
		@Override
		protected Object doInBackground(Object[] params) {
			blrc = BluetoothRC.getInstance(context);
			while (!blrc.connect()) continue;
			blrc.sendData("2");

			return null;
		}
	};

}
