package alarmproject.apps.plow.alarmproject.model;

import java.util.ArrayList;

/**
 * Created by YouNesS on 18/09/2015.
 */
public class Date {
    private int day;
    private int month;
    private int year;
    ArrayList<Alarm> alarms;
    public static ArrayList<Alarm> dayToCopy=null;

    public Date(int day, int month, int year,ArrayList<Alarm> alarms) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.alarms=alarms;
    }

    public ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean addAlarm(Time ti)
    {
        if (isExist(ti)) return false;
        else
        {
            alarms.add(new Alarm(ti,true));
            return true;
        }
    }

    public void removeAlarm(int index)
    {
        alarms.remove(index);
    }

    public boolean isExist(Time ti)
    {
        for (int i=0;i<alarms.size();i++)
        {
            if (ti.toString().equals(alarms.get(i).toString())) return true;
        }
        return false;
    }

    public void copyday()
    {
        this.dayToCopy=new ArrayList<>(this.alarms);
    }

    public void pasteDay()
    {
        this.alarms = new ArrayList<>(dayToCopy);
    }
}
