package alarmproject.apps.plow.alarmproject.model;

import io.realm.RealmObject;

/**
 * Created by YouNesS on 20/09/2015.
 */
public class RealmInteger extends RealmObject {
    private long num;

    public RealmInteger(long num) {
        this.num = num;
    }

    public RealmInteger() {
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }
}
