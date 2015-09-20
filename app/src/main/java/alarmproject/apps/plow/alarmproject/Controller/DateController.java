package alarmproject.apps.plow.alarmproject.Controller;

import android.content.Context;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;

import alarmproject.apps.plow.alarmproject.Fragments.CalendarFragment;
import alarmproject.apps.plow.alarmproject.activities.MainActivity;
import alarmproject.apps.plow.alarmproject.model.Alarm;
import alarmproject.apps.plow.alarmproject.model.DateCalendar;
import alarmproject.apps.plow.alarmproject.model.RealmInteger;
import alarmproject.apps.plow.alarmproject.model.Time;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by YouNesS on 19/09/2015.
 */
public class DateController {

    Context c;
    Realm realm;

    public DateController(Context c) {
        this.c = c;
        realm = Realm.getInstance(this.c);
    }

    public void addAlarm(DateCalendar date,long alarm)
    {
        realm.beginTransaction();
        if (realm.getTable(DateCalendar.class).isEmpty() || realm.where(DateCalendar.class).equalTo("_date",date.get_date())==null || realm.where(DateCalendar.class).equalTo("_date", date.get_date()).findFirst()==null)  {
            date = realm.copyToRealm(date);
            date.setAlarms(new RealmList<RealmInteger>());
        }
        else date=realm.where(DateCalendar.class).equalTo("_date", date.get_date()).findFirst();
        if (date.getAlarms()==null) date.setAlarms(new RealmList<RealmInteger>());
        date.getAlarms().add(new RealmInteger(alarm));
        realm.commitTransaction();
    }

    public void refreshAlarms(DateCalendar date)
    {
        if (realm.getTable(DateCalendar.class).isEmpty() || realm.where(DateCalendar.class).equalTo("_date",date.get_date())==null || realm.where(DateCalendar.class).equalTo("_date", date.get_date()).findFirst()==null)  {

            return;
        }
        realm.beginTransaction();
        date=realm.where(DateCalendar.class).equalTo("_date", date.get_date()).findFirst();
        date.setAlarms(new RealmList<RealmInteger>());
        realm.commitTransaction();
    }

    public void removeAlarm(DateCalendar date,int position)
    {
        realm.beginTransaction();
        RealmQuery<DateCalendar> query = realm.where(DateCalendar.class).equalTo("_date", date.get_date());
        DateCalendar results = query.findFirst();
        results.getAlarms().remove(position);
        realm.commitTransaction();
    }

    public ArrayList<Alarm> getAllAlarmsByDay(DateCalendar date)
    {
        if (!realm.getTable(DateCalendar.class).isEmpty())
        {
            RealmQuery<DateCalendar> query = realm.where(DateCalendar.class).equalTo("_date", date.get_date());
            if (query==null) return null;
            DateCalendar results = query.findFirst();
            if (results == null) return null;
            ArrayList<Alarm> a = new ArrayList<Alarm>();
            for (RealmInteger i : results.getAlarms()) {
                a.add(MainActivity.alarmController.getAlarmById(i.getNum()));
            }
            return a;
        }
        return null;
    }

    public void pasteDay()
    {
        refreshAlarms(CalendarFragment.actualDate);
        for (Long i: DateCalendar.dayToCopy)
        {
            Alarm a = MainActivity.alarmController.getAlarmById(i);
            Alarm b = new Alarm();
            b.setTime(new Time(a.getTime().getHour(),a.getTime().getMinute()));
            b.setIsOn(a.isOn());
            b=MainActivity.alarmController.save(b);
            MainActivity.dateController.addAlarm(CalendarFragment.actualDate,b.getId());
        }
    }


    public void show()
    {
        RealmQuery<DateCalendar> query = realm.where(DateCalendar.class);

        RealmResults<DateCalendar> results = query.findAll();
        String s="";
        for (DateCalendar r:results)
        {
            s+=toString(r)+"\n";
        }
        Toast.makeText(c,s,Toast.LENGTH_LONG).show();
    }

    public String toString(DateCalendar a)
    {
        return a.get_date()+" "+a.getAlarms();
    }

    public static String getAsString(int year,int month,int day)
    {
        return year+"-"+month+"-"+day;
    }

}
