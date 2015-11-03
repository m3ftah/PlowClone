package alarmproject.apps.plow.alarmproject.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import alarmproject.apps.plow.alarmproject.Controller.DateController;
import alarmproject.apps.plow.alarmproject.R;
import alarmproject.apps.plow.alarmproject.model.Alarm;
import alarmproject.apps.plow.alarmproject.model.DateCalendar;
import alarmproject.apps.plow.alarmproject.model.Time;
import alarmproject.apps.plow.alarmproject.model.Utilities;
import alarmproject.apps.plow.alarmproject.model.VoiceAlarmCommand;
import alarmproject.apps.plow.alarmproject.model.VoiceCommand;

/**
 * Created by sony on 18/02/2015.
 */
public class AskActivity extends ActionBarActivity {
    TextView tv_ask;
    ImageButton btn_voice;


    private MediaPlayer mp;
    private int state=0;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        tv_ask =(TextView) findViewById(R.id.tv_help);
        btn_voice =(ImageButton) findViewById(R.id.btn_voice);

        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        Utilities.setFontText(getBaseContext(), tv_ask);
        tv_ask.setText(getString(R.string.text_ask_alarm));

       Utilities.playMusic(this,R.raw.text_ask_alarm);

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
            Toast.makeText(getApplicationContext(),
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
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if (state==0)
                    {
                        if (result.get(0).equalsIgnoreCase(getString(R.string.speech_add_no)))
                        {
                            finish();
                        }
                        else if (result.get(0).equalsIgnoreCase(getString(R.string.speech_add_yes)))
                        {
                            state=1;
                            tv_ask.setText(getString(R.string.speech_time));
                            Utilities.playMusic(this, R.raw.speech_time);
                        }
                        else
                        {
                            tv_ask.setText(getString(R.string.speech_command_insupported));
                            Utilities.playMusic(this, R.raw.sorry_unsupported_command);
                        }
                    }
                    else if (state==1)
                    {
                        if (result.get(0).toLowerCase().split(":").length==2)
                        {
                            action(Integer.parseInt(result.get(0).toLowerCase().split(":")[0]), Integer.parseInt(result.get(0).toLowerCase().split(":")[1]));
                            tv_ask.setText(getString(R.string.speech_okey));
                            finish();
                        }
                        else {
                            tv_ask.setText(getString(R.string.speech_command_insupported));
                            Utilities.playMusic(this, R.raw.sorry_unsupported_command);
                        }
                    }
                }
                break;
            }

        }
    }

    public void action(int a,int b) {
        Alarm al = new Alarm(new Time(a,b), true);
        Alarm alarm=MainActivity.alarmController.save(al);
        Calendar now = Calendar.getInstance();
        DateCalendar actualDate = new DateCalendar(DateController.getAsString(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)));
        MainActivity.dateController.addAlarm(actualDate, alarm.getId());
    }

}
