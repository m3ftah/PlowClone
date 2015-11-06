package alarmproject.apps.plow.alarmproject.activities;
import android.app.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import alarmproject.apps.plow.alarmproject.Controller.AlarmController;
import alarmproject.apps.plow.alarmproject.Controller.DateController;
import alarmproject.apps.plow.alarmproject.Controller.TimeController;
import alarmproject.apps.plow.alarmproject.Fragments.AskFragment;
import alarmproject.apps.plow.alarmproject.Fragments.CalendarFragment;
import alarmproject.apps.plow.alarmproject.Fragments.StatsFragment;
import alarmproject.apps.plow.alarmproject.R;
import alarmproject.apps.plow.alarmproject.model.Alarm;
import alarmproject.apps.plow.alarmproject.model.DateCalendar;
import alarmproject.apps.plow.alarmproject.model.NavigationDrawerFragment;
import alarmproject.apps.plow.alarmproject.model.Time;
import alarmproject.apps.plow.alarmproject.model.Utilities;
import app.plow.bluetooth.BluetoothRC;
import app.plow.bluetooth.BluetoothService;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,Observer {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static DateController dateController;
    public static AlarmController alarmController;
    public static TimeController timeController;
    PendingIntent pi;
    AlarmManager am;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public static MainActivity ac;
    public static Menu m;
    final static private long FIVE_SECONDS = 5000;
    BluetoothRC blrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ac=this;

        dateController = new DateController(MainActivity.this);
        alarmController = new AlarmController(MainActivity.this);
        timeController = new TimeController(MainActivity.this);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        onNavigationDrawerItemSelected(0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        blrc = BluetoothRC.getInstance(this);
        blrc.addObserver(this);
        /*final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean is_first = prefs.getBoolean("is_first",
                true);
        if (is_first)
        {*/
            setup();
           /* SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("is_first", false);
            editor.commit();
        }*/

    }

    @Override
    public  void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof BluetoothRC){
            final String str = (String) data;
            Log.d("obs", "observed");
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (str)
                    {
                        case (Utilities.ARDUINO_HEAD_OFF):
                            blrc.sendData(Utilities.ARDUINO_ALARM_SEND_TIME+getCurrentTimeStamp());
                            blrc.disconnect();
                            break;
                        case (Utilities.ARDUINO_HEAD_ON):

                            // search according to time if there is an alarm in the peiode after (N/D)
                            startActivity(new Intent(getApplicationContext(), AskActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            blrc.sendData(Utilities.ARDUINO_ALARM_SEND_TIME+getCurrentTimeStamp());
                            blrc.disconnect();
                            break;
                        case (Utilities.ARDUINO_ALARM_TIME_HEAD_OFF) :
                            Utilities.playMusic(getBaseContext(),R.raw.alarm_song);
                            //BluetoothRC.getInstance(getBaseContext()).sendData(Utilities.ARDUINO_ALARM_GET_STATISTCS);
                            break;
                        case (Utilities.ARDUINO_ALARM_TIME_HEAD_ON) :
                            Toast.makeText(getBaseContext(), "Time to wake up", Toast.LENGTH_LONG).show();
                            //sendBroadcast(new Intent("app.plow.bluetooth.ServiceReceiver").putExtra(BluetoothService.FILEPATH, "restart"));
                            BluetoothRC.getInstance(getBaseContext()).sendData(Utilities.ARDUINO_VIBRATE);
                            //BluetoothRC.getInstance(getBaseContext()).sendData(Utilities.ARDUINO_ALARM_GET_STATISTCS);
                            break;
                        default:
                            //fill the stats
                            break;
                    }
                    Toast.makeText(MainActivity.this, " received : " + str, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        menu.getItem(2).setVisible(false);
        m=menu;
        if (PlaceholderFragment.section_number==0) setMenuVisible(true);
        else setMenuVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add) {
            Calendar now = Calendar.getInstance();
            TimePickerDialog tpd = TimePickerDialog.newInstance(
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i1) {

                            Alarm al = new Alarm(new Time(i, i1), true);
                            CalendarFragment.alarms.add(MainActivity.alarmController.save(al));
                            MainActivity.dateController.addAlarm(CalendarFragment.actualDate,CalendarFragment.alarms.get(CalendarFragment.alarms.size()-1).getId());
                            CalendarFragment.setNotify();
                        }
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
            );
            tpd.setThemeDark(false);
            tpd.vibrate(true);
            tpd.dismissOnPause(true);
            tpd.setAccentColor(getResources().getColor(R.color.primary));
            tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {

                }
            });
            tpd.show(getFragmentManager(), "Timepickerdialog");
            return true;
        }
        if (id==R.id.menu_copy)
        {
            if (CalendarFragment.alarms==null || CalendarFragment.alarms.isEmpty())
            {
                Toast.makeText(getBaseContext(), getString(R.string.toast_date_nothing_copied), Toast.LENGTH_LONG).show();
                return true;
            }
            DateCalendar.dayToCopy = new ArrayList<Long>();
            for (Alarm a:CalendarFragment.alarms)
            DateCalendar.dayToCopy.add(a.getId());

            m.getItem(2).setVisible(true);
            Toast.makeText(getBaseContext(), getString(R.string.toast_date_copied), Toast.LENGTH_LONG).show();
            return true;
        }
        if (id==R.id.menu_past)
        {
            dateController.pasteDay();
            CalendarFragment.alarms=dateController.getAllAlarmsByDay(CalendarFragment.actualDate);
            CalendarFragment.setNotify();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        static int section_number=0;

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            section_number=sectionNumber-1;
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            FragmentManager fragmentManager;
            switch (section_number)
            {
                case 0: {
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new CalendarFragment())
                            .commit();
                }
                break;
                case 1: {
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new StatsFragment())
                            .commit();
                }
                break;
                case 2: {
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new AskFragment())
                            .commit();
                }
                break;
                case 3:
                    Intent j = new Intent(getActivity().getBaseContext(),SettingsActivity.class);
                    startActivity(j);
                    getActivity().finish();
                    break;
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void setup()
    {

        pi = PendingIntent.getBroadcast(this, 0, new Intent(
                getString(R.string.service_alarm_manager)), 0);
        am = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        // cette instruction est pour activer le premier appel du
        // broadcastReciever
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + FIVE_SECONDS, pi);

    }

    public static void setMenuVisible(boolean b)
    {
        m.getItem(0).setVisible(b);
        m.getItem(1).setVisible(b);
        m.getItem(2).setVisible(false);
    }
}
