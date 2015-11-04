package alarmproject.apps.plow.alarmproject.model;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;

import alarmproject.apps.plow.alarmproject.R;

/**
 * Created by YouNesS on 18/09/2015.
 */
public class Utilities {
    public static String fontInUse="litera-regular.ttf";
    public static final String ARDUINO_HEAD_ON="1";
    public static final String ARDUINO_HEAD_OFF="0";
    public static final String ARDUINO_ALARM_TIME_HEAD_ON="3";
    public static final String ARDUINO_ALARM_TIME_HEAD_OFF="4";
    public static final String ARDUINO_ALARM_GET_STATE="t";
    public static final String ARDUINO_VIBRATE="2";
    public static void setFontText(Context c, TextView tv) {
        Typeface font = Typeface.createFromAsset(c.getAssets(),
                "AppFonts/" +fontInUse);
        tv.setTypeface(font);
    }

    public static void setFontButton(Context c, Button but) {
        Typeface font = Typeface.createFromAsset(c.getAssets(),
                "AppFonts/" +fontInUse);
        but.setTypeface(font);
    }

    public static void playMusic(Context c,int id)
    {
        MediaPlayer mp = MediaPlayer.create(c, id);
        mp.start();
    }
}
