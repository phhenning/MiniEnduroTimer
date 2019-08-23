package com.henning.pieter.endurotimer;


import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class StageEnd extends StageBase {


    public EditText textFinishNo;
    public TextView textEndStageNo;
    Button butTagRider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewItems("Finish", R.layout.activity_stage_end, R.id.listEndView);
        butTagRider = (Button) findViewById(R.id.butTagRider);
        textFinishNo = (EditText) findViewById(R.id.textViewFinisNo);
        textEndStageNo = (TextView) findViewById(R.id.textEndStageNo);
        textEndStageNo.setText(super.stageNo);

     }


    public void tagRiderFinish(View view) {
//        InputMethodManager inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
//        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        long time_ms =  System.currentTimeMillis();
        String raceNo = textFinishNo.getText().toString();
        tagRider(raceNo, time_ms);
        textFinishNo.setText("");
    }


}
