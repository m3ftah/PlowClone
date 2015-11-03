package alarmproject.apps.plow.alarmproject.model;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by YouNesS on 18/09/2015.
 */
public class DateCalendar extends RealmObject {
    @PrimaryKey
    private String _date;

    private RealmList<RealmInteger> alarms=new RealmList<RealmInteger>();

    @Ignore
    public static ArrayList<Long> dayToCopy=null;

    public DateCalendar(){}
    public DateCalendar(String date) {
        this._date = date;
    }

    public RealmList<RealmInteger> getAlarms() {
        return alarms;
    }

    public void setAlarms(RealmList<RealmInteger> alarms) {
        this.alarms = alarms;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }
}
