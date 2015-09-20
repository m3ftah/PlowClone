package alarmproject.apps.plow.alarmproject.model;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by YouNesS on 18/09/2015.
 */
public class Utilities {
    public static String fontInUse="litera-regular.ttf";
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
}