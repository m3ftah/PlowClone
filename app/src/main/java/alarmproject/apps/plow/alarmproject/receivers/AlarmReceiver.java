package alarmproject.apps.plow.alarmproject.receivers;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import alarmproject.apps.plow.alarmproject.Controller.DateController;
import alarmproject.apps.plow.alarmproject.R;
import alarmproject.apps.plow.alarmproject.model.Alarm;
import alarmproject.apps.plow.alarmproject.model.DateCalendar;
import app.plow.BluetoothRC;

public class AlarmReceiver extends BroadcastReceiver {
	AlarmManager am;
	PendingIntent pi;
	final static private long ONE_MINUTE = 60000;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		Toast.makeText(context,"Im Up",Toast.LENGTH_LONG).show();
		

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

		DateController dt = new DateController(context);
		DateCalendar date =dt.getFirstAlarmDate();
		if (date!=null)
		{
			String today="";
			Calendar now= Calendar.getInstance();
			today +=now.get(Calendar.YEAR)+"-"+now.get(Calendar.MONTH)+"-"+now.get(Calendar.DAY_OF_MONTH);

			if (today.equals(date.get_date()))
			{
				Alarm a = dt.getFirstAlarmTime(date);
				if (a.isOn() && a.getTime().getHour()==now.get(Calendar.HOUR) && a.getTime().getMinute()==now.get(Calendar.MINUTE))
				{
					BluetoothRC bl = BluetoothRC.getInstance(context);
					bl.sendData("2");
					Toast.makeText(context,"Time to wake up",Toast.LENGTH_LONG).show();
				}
			}
		}


	}

}