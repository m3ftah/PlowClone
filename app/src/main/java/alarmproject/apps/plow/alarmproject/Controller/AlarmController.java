package alarmproject.apps.plow.alarmproject.Controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import alarmproject.apps.plow.alarmproject.model.Alarm;
import alarmproject.apps.plow.alarmproject.model.Time;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by YouNesS on 19/09/2015.
 */
public class AlarmController  {

    Context c;
    Realm realm;

    public AlarmController(Context c) {
        this.c = c;
        realm = Realm.getInstance(this.c);
    }

    public Alarm save(Alarm alarm)
    {
        realm.beginTransaction();
        alarm.setId(nextInt());
        Alarm alarm1=realm.copyToRealm(alarm);
        realm.commitTransaction();
        return alarm1;
    }

    public Alarm updateTime(Time t,long id)
    {
        realm.beginTransaction();
        RealmQuery<Alarm> query = realm.where(Alarm.class).equalTo("id",id);
        Alarm results = query.findFirst();
        results.getTime().setHour(t.getHour());
        results.getTime().setMinute(t.getMinute());
        realm.commitTransaction();
        return results;
    }

    public Alarm updateIsOn(boolean b,long id)
    {
        realm.beginTransaction();
        RealmQuery<Alarm> query = realm.where(Alarm.class).equalTo("id", id);
        Alarm results = query.findFirst();
        results.setIsOn(b);
        realm.commitTransaction();
        return results;
    }

    public void remove(long id)
    {
        realm.beginTransaction();
        RealmQuery<Alarm> query = realm.where(Alarm.class).equalTo("id", id);
        Alarm results = query.findFirst();
        results.removeFromRealm();
        realm.commitTransaction();
    }

    public Alarm getAlarmById(long id)
    {
        RealmQuery<Alarm> query = realm.where(Alarm.class).equalTo("id", id);
        return query.findFirst();
    }


    public long nextInt()
    {
        long a;
        try {
            a=realm.where(Alarm.class).maximumInt("id") + 1;
            if (a<0) return 0;
            return a;
        }catch (Exception e) {
            return 0;
        }
    }

    public void show()
    {
        RealmQuery<Alarm> query = realm.where(Alarm.class);

        RealmResults<Alarm> results = query.findAll();
        String s="";
        for (Alarm r:results)
        {
            s+=toString(r)+"\n";
        }
        Toast.makeText(c,s,Toast.LENGTH_LONG).show();
    }
    public String toString(Alarm a)
    {
        return a.getId()+" "+a.getTime().toString()+" "+a.isOn();
    }
    public static String day(Time t){
        if (t.getHour()<12) return "am";
        else return "pm";
    }


}
