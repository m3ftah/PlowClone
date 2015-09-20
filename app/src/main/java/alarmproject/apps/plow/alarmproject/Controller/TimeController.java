package alarmproject.apps.plow.alarmproject.Controller;

import android.content.Context;
import android.widget.Toast;

import alarmproject.apps.plow.alarmproject.model.Time;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by YouNesS on 19/09/2015.
 */
public class TimeController {

    Context c;
    Realm realm;

    public TimeController(Context c) {
        this.c = c;
        realm = Realm.getInstance(this.c);
    }

    public boolean save(Time time)
    {
        realm.beginTransaction();
        Time time1=realm.copyToRealm(time);
        realm.commitTransaction();
        return time1!=null;
    }


    public void show()
    {
        RealmQuery<Time> query = realm.where(Time.class);
        RealmResults<Time> results = query.findAll();
        String s="";
        for (Time t:results)
        {
            s+=toString(t)+"\n";
        }
        Toast.makeText(c,s,Toast.LENGTH_LONG).show();
    }
    public static String toString(Time t)
    {
        String hours=""+t.getHour();
        String minutes=""+t.getMinute();
        if (t.getHour()<10) hours="0"+hours;
        if (t.getMinute()<10) minutes="0"+t.getMinute();
        return hours+":"+minutes;
    }

    public static int compareTime(Time t1,Time t2)
    {
        if (t1.getHour()>t2.getHour()) return 1;
        if (t1.getHour()<t2.getHour()) return -1;
        if (t1.getMinute()>t2.getMinute()) return 1;
        if (t1.getMinute()<t2.getMinute()) return -1;
        return 0;
    }

}
