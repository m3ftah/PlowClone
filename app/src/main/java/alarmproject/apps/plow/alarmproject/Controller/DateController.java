package alarmproject.apps.plow.alarmproject.Controller;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.ArrayRes;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    public DateCalendar getFirstAlarmDate()
    {
        RealmResults<DateCalendar> query=null;
        if (!realm.getTable(DateCalendar.class).isEmpty())
        query = realm.where(DateCalendar.class).findAllSorted("_date");
        else return null;
        Calendar now = Calendar.getInstance();
        for (DateCalendar d:query)
        {
            if (d.getAlarms()!=null && !d.getAlarms().isEmpty() && compareDates(d.get_date(),now.get(Calendar.YEAR)+"-"+now.get(Calendar.MONTH)+"-"+now.get(Calendar.DAY_OF_MONTH))>=0) return d;
        }
        return null;
    }

    public Alarm getFirstAlarmTime(DateCalendar d)
    {
        ArrayList<Alarm> alarms = getAllAlarmsByDay(d);
        Time t=new Time(24,61);
        int index=0;
        int i=0;
        Calendar now = Calendar.getInstance();
        for (Alarm al:alarms)
        {
            if (TimeController.compareTime(new Time(al.getTime().getHour(),al.getTime().getMinute()),new Time(now.get(Calendar.HOUR),now.get(Calendar.MINUTE)))>=0)
            {
                if (al.getTime().getHour() < t.getHour() || (al.getTime().getHour() == t.getHour() && al.getTime().getMinute() < t.getMinute())) {
                    index = i;
                }
            }
            i++;
        }
        return alarms.get(index);
    }

    public static int compareDates(String date1,String date2)
    {
        int year1 = Integer.parseInt(date1.split("-")[0]);
        int year2 = Integer.parseInt(date2.split("-")[0]);
        int month1 = Integer.parseInt(date1.split("-")[1]);
        int month2 = Integer.parseInt(date2.split("-")[1]);
        int day1 = Integer.parseInt(date1.split("-")[2]);
        int day2 = Integer.parseInt(date2.split("-")[2]);

        if (year1>year2) return 1;
        if (year1<year2) return -1;
        if (month1>month2) return 1;
        if (month1<month2) return -1;
        if (day1>day2) return 1;
        if (day1<day2) return -1;
        return 0;
    }

}
