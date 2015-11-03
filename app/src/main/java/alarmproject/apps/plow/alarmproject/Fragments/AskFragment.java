package alarmproject.apps.plow.alarmproject.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import alarmproject.apps.plow.alarmproject.Controller.DateController;
import alarmproject.apps.plow.alarmproject.R;
import alarmproject.apps.plow.alarmproject.activities.MainActivity;
import alarmproject.apps.plow.alarmproject.model.Alarm;
import alarmproject.apps.plow.alarmproject.model.DateCalendar;
import alarmproject.apps.plow.alarmproject.model.Time;
import alarmproject.apps.plow.alarmproject.model.Utilities;
import alarmproject.apps.plow.alarmproject.model.VoiceAlarmCommand;
import alarmproject.apps.plow.alarmproject.model.VoiceCommand;
import butterknife.Bind;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by sony on 18/02/2015.
 */
public class AskFragment extends Fragment {
    TextView tv_ask;
    ImageButton btn_voice;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    public VoiceCommand calendar;
    public VoiceCommand stats;
    public VoiceAlarmCommand alarmCommand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_ask, container, false);
        tv_ask =(TextView) rootView.findViewById(R.id.tv_help);
        btn_voice =(ImageButton) rootView.findViewById(R.id.btn_voice);

        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        Utilities.setFontText(getActivity(), tv_ask);

        Utilities.playMusic(getActivity(), R.raw.what_can_i_help_you);

        calendar = new VoiceCommand(getString(R.string.speech_show_calendar)) {
            @Override
            public void action() {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(1))
                        .commit();;
            }
        };
        stats = new VoiceCommand(getString(R.string.speech_show_stats)) {
            @Override
            public void action() {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(2))
                        .commit();;
            }
        };

        alarmCommand = new VoiceAlarmCommand(getString(R.string.speech_add_alarm)) {
            @Override
            public void action(int a,int b) {
                Alarm al = new Alarm(new Time(a,b), true);
                Alarm alarm=MainActivity.alarmController.save(al);
                Calendar now = Calendar.getInstance();
                DateCalendar actualDate = new DateCalendar(DateController.getAsString(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)));
                MainActivity.dateController.addAlarm(actualDate,alarm.getId());
            }
        };
        return rootView;
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity().getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (calendar.tryParse(result.get(0)))
                    {
                        tv_ask.setText(getString(R.string.speech_okey));
                        Utilities.playMusic(getActivity(), R.raw.okey);
                        calendar.action();
                    }
                    else if (stats.tryParse(result.get(0)))
                    {
                        tv_ask.setText(getString(R.string.speech_okey));
                        Utilities.playMusic(getActivity(), R.raw.okey);
                        stats.action();
                    }
                    else if(alarmCommand.tryParse(result.get(0)))
                    {
                        if (alarmCommand.getState()==1)
                        {
                            tv_ask.setText(getString(R.string.speech_time));
                            Utilities.playMusic(getActivity(), R.raw.speech_time);
                        }
                        else {
                            tv_ask.setText(getString(R.string.speech_alarm_added));
                            Utilities.playMusic(getActivity(), R.raw.alarm_added);
                            alarmCommand.action(Integer.parseInt(result.get(0).split(":")[0]),Integer.parseInt(result.get(0).split(":")[1]));
                        }
                    }
                    else
                    {
                        tv_ask.setText(getString(R.string.speech_command_insupported));
                        Utilities.playMusic(getActivity(), R.raw.sorry_unsupported_command);
                    }
                }
                break;
            }

        }
    }

}
