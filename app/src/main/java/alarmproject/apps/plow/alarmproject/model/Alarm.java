package alarmproject.apps.plow.alarmproject.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by YouNesS on 18/09/2015.
 */
public class Alarm extends RealmObject {
    @PrimaryKey
    private long id;

    private Time time;
    private boolean isOn;

    public Alarm(){}
    public Alarm(Time time,boolean isOn) {
        this.time = time;
        this.isOn = isOn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setIsOn(boolean isOn) {
        this.isOn = isOn;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }


}
