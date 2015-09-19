package alarmproject.apps.plow.alarmproject.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.rey.material.widget.Switch;

import alarmproject.apps.plow.alarmproject.Controller.AlarmController;
import alarmproject.apps.plow.alarmproject.Controller.TimeController;
import alarmproject.apps.plow.alarmproject.R;
import alarmproject.apps.plow.alarmproject.model.Alarm;
import alarmproject.apps.plow.alarmproject.model.Utilities;

/**
 * Created by YouNesS on 18/09/2015.
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {


     private ArrayList<Alarm> mlist = new ArrayList<>();

    public AlarmAdapter(ArrayList<Alarm> list) {
        this.mlist=list;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_time.setText(TimeController.toString(mlist.get(position).getTime()));
        holder.tv_day.setText(AlarmController.day(mlist.get(position).getTime()));
        holder.isOn.setChecked(mlist.get(position).isOn());
            if (holder.isOn.isChecked()) holder.isOn.setAlpha(1);
            else holder.isOn.setAlpha(0.3f);
        holder.isOn.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch aSwitch, boolean b) {
                mlist.get(position).setIsOn(b);
                if (b) aSwitch.setAlpha(1);
                else aSwitch.setAlpha(0.3f);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public void remove(int position) {
        mlist.remove(position);
        notifyItemRemoved(position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_time;
        TextView tv_day;
        Switch isOn;
        MyViewHolder(View view) {
            super(view);
            tv_time = ((TextView) view.findViewById(R.id.tv_time));
            tv_day = ((TextView) view.findViewById(R.id.tv_day));
            isOn = ((Switch) view.findViewById(R.id.is_on));
            Utilities.setFontText(view.getContext(), tv_time);
            Utilities.setFontText(view.getContext(),tv_day);
        }
    }
}