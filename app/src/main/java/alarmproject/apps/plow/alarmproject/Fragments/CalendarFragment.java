package alarmproject.apps.plow.alarmproject.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import alarmproject.apps.plow.alarmproject.Controller.DateController;
import alarmproject.apps.plow.alarmproject.R;
import alarmproject.apps.plow.alarmproject.activities.MainActivity;
import alarmproject.apps.plow.alarmproject.adapters.AlarmAdapter;
import alarmproject.apps.plow.alarmproject.model.Alarm;
import alarmproject.apps.plow.alarmproject.model.DateCalendar;
import alarmproject.apps.plow.alarmproject.model.Time;
import me.nlmartian.silkcal.DayPickerView;
import me.nlmartian.silkcal.SimpleMonthAdapter;

/**
 * Created by sony on 18/02/2015.
 */
public class CalendarFragment extends Fragment {

    public static RecyclerView recyclerView;
    public static AlarmAdapter adapter;

    public static  ArrayList<Alarm> alarms = new ArrayList<>();

    public static DateCalendar actualDate;

    private DayPickerView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_calendar, container, false);


        calendarView = (DayPickerView) rootView.findViewById(R.id.calendar_view);
        calendarView.setController(new me.nlmartian.silkcal.DatePickerController() {
            @Override
            public int getMaxYear() {
                return 0;
            }

            @Override
            public void onDayOfMonthSelected(int year, int month, int day) {
                actualDate = new DateCalendar(DateController.getAsString(year, month, day));
                alarms = MainActivity.dateController.getAllAlarmsByDay(actualDate);
                if (alarms==null) alarms=new ArrayList<Alarm>();
                setNotify();
            }

            @Override
            public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {

            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        init(recyclerView);
        Calendar now = Calendar.getInstance();
        actualDate = new DateCalendar(DateController.getAsString(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)));
        initAlarms();

        return rootView;
    }




    public void initAlarms()
    {
        alarms = MainActivity.dateController.getAllAlarmsByDay(actualDate);
        if (alarms==null) alarms=new ArrayList<Alarm>();
    }

    private void init(RecyclerView recyclerView) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new AlarmAdapter(alarms);
        recyclerView.setAdapter(adapter);
        final SwipeToDismissTouchListener<RecyclerViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewAdapter(recyclerView),
                        new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerViewAdapter view, int position) {
                                adapter.remove(position);
                            }
                        });

        recyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        recyclerView.setOnScrollListener((RecyclerView.OnScrollListener) touchListener.makeScrollListener());
        recyclerView.addOnItemTouchListener(new SwipeableItemClickListener(getActivity(),
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        if (view.getId() == R.id.txt_delete) {
                            MainActivity.dateController.removeAlarm(actualDate, position);
                            MainActivity.alarmController.remove(alarms.get(position).getId());
                            MainActivity.alarmController.show();
                            touchListener.processPendingDismisses();
                        } else if (view.getId() == R.id.txt_undo) {
                            touchListener.undoPendingDismiss();
                        } else { // R.id.txt_data
                            TimePickerDialog tpd = TimePickerDialog.newInstance(
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i1) {
                                            alarms.set(position, MainActivity.alarmController.updateTime(new Time(i, i1), alarms.get(position).getId()));
                                            MainActivity.alarmController.show();
                                            setNotify();
                                        }
                                    },
                                    alarms.get(position).getTime().getHour(),
                                    alarms.get(position).getTime().getMinute(),
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
                            tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
                        }
                    }
                }));
    }

    public static void setNotify()
    {
        adapter.notifyDataSetChanged();
        adapter = new AlarmAdapter(alarms);
        recyclerView.setAdapter(adapter);
    }

}
