package alarmproject.apps.plow.alarmproject.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by YouNesS on 18/09/2015.
 */
public class Time extends RealmObject {

    private int hour;
    private int minute;

    public Time(){}
    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }


}
