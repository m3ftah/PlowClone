package alarmproject.apps.plow.alarmproject.adapters;


/**
 * Created by sony on 17/02/2015.
 */


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import alarmproject.apps.plow.alarmproject.R;

public class  SideBarListadapter extends BaseAdapter {
    private final Context context;
    private  String[] text;
    private Integer[] image;


    public SideBarListadapter(Context context, String[] text,Integer[] image) {
        this.context = context;
        this.text = text;
        this.image = image;
    }

    @Override
    public int getCount() {
        return text.length;
    }

    @Override
    public Object getItem(int i) {
        return text[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.fragment_main, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.tv_list_side);
        //Informations.setTextFont(context, textView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img_list_side);
        textView.setText(text[position]);
        imageView.setImageResource(image[position]);
        LinearLayout ln_grey = (LinearLayout) rowView.findViewById(R.id.ln_grey);
        if (position==getCount()-1) ln_grey.setVisibility(View.INVISIBLE);
        return rowView;
    }
}


