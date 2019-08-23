package com.henning.pieter.endurotimer;


import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class StageStart extends StageBase {

    static EditText raceNoInput;
    static EditText interval_s;
    public TextView stageNo;
    public int countDownTime_s;
    Button butCount;
    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewItems("Start", R.layout.activity_stage_start, R.id.listStartView);
        butCount = (Button) findViewById(R.id.butCountDown);
        stageNo = (TextView) findViewById(R.id.textStartStageNo);
        stageNo.setText(super.stageNo);
        raceNoInput  = (EditText) findViewById(R.id.editID);
        raceNoInput.requestFocus();
        interval_s  = (EditText) findViewById(R.id.editInterval);
        countDownTime_s = intent.getIntExtra(MainActivity.COUNT_VALUE, 5);
        interval_s.setText(Integer.toString(countDownTime_s));
     }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("NewCountDownTime", countDownTime_s);
        setResult(Activity.RESULT_OK, intent);
//        System.out.println("XXX Return results");
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    public void updateCountDown(View view){
        countDownTime_s = Integer.parseInt(interval_s.getText().toString());
//        System.out.println("time b: " + countDownTime_s);
    }


    public void editIdClick(View view){
        raceNoInput.setTextColor(0xff000000);
        raceNoInput.setText("");
        butCount.setClickable(true);
    }

    public void startCountDown(View view) {
//        InputMethodManager inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
//        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        butCount.setClickable(false);
        String raceNo = raceNoInput.getText().toString();
        raceNoInput.setText("");
//        raceNoInput.setTextColor(0xff000000);
        boolean noExists = false;
        if (!raceNo.equalsIgnoreCase("")) {
            noExists = doesTagExist(raceNo);
        }

        if (noExists) {
            raceNoInput.setText(raceNo + " Exists !!!");
            raceNoInput.setTextColor(0xffff0000);
            butCount.setClickable(false);
        } else {

            long startTime_ms = 0;
            long now_ms = System.currentTimeMillis();
            int interval_ms = 0;

            countDownTime_s = Integer.parseInt(interval_s.getText().toString());
//        System.out.println("time b: " + countDownTime_s);
            try {
                interval_ms = countDownTime_s * 1000;
                interval_ms = interval_ms + 1000;   //add sec for display
            } catch (NumberFormatException e) {
                interval_ms = 11000;
            }
            startTime_ms = (now_ms) + (interval_ms);
            tagRider(raceNo, startTime_ms);
            new CountDownTimer(interval_ms, 250) {
                public void onTick(long millisUntilFinished) {
//                System.out.println("tick =  " + millisUntilFinished);

                    butCount.setClickable(false);
                    if (millisUntilFinished <= 650) {
                        butCount.setText("0");
//                    System.out.println("beep ");
                        tg.startTone(ToneGenerator.MAX_VOLUME);
                        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                    } else {
                        butCount.setText("" + millisUntilFinished / 1000);
                        //cd.setText("" + millisUntilFinished / 1000);
                    }
                }

                public void onFinish() {
                    tg.startTone(ToneGenerator.MAX_VOLUME);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                    //cd.setText("0!");
                    try {
                        wait(500);
                    } catch (Exception e) {

                    }
                    butCount.setText("Start Countdown");
                    butCount.setClickable(true);
                }
            }.start();
        }
        //butCount.setClickable(true);
    }

}
