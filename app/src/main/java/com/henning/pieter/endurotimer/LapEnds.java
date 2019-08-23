package com.henning.pieter.endurotimer;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by phenning on 2015/09/06.
 */
public class LapEnds extends StageBase {


    public EditText textLapNo;
    public TextView textStartTime;
    Button butTagLap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewItems("Lap", R.layout.activity_lap_race, R.id.listLapView);
        butTagLap = (Button) findViewById(R.id.butTagLap);
        textLapNo = (EditText) findViewById(R.id.textLapNo);
        textStartTime = (TextView) findViewById(R.id.textLapRaceStartTime);

        long time_ms = System.currentTimeMillis();
        textStartTime.setText(new SimpleDateFormat("hh:mm:ss").format(new Date(time_ms)));
        TagEvent startTime = new TagEvent("00", raceName, super.stageNo, "Lap", time_ms, "Lap Race", "Start Time");

        try {
            logger.addTagToFile(startTime);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void tagRiderLap(View view) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        long time_ms = System.currentTimeMillis();
        String raceNo = textLapNo.getText().toString();
        tagRider(raceNo, time_ms);
        textLapNo.setText("");
    }

}