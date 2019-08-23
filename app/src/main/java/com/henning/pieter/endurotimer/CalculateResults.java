package com.henning.pieter.endurotimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by phenning on 2015/09/06.
 */
public class CalculateResults extends StageBase {

    private static final int FILE_START = 11;
    private static final int FILE_END = 13;
    private static final int FILE_RESULTS = 17;
    public TextView textStageFinish;
    public TextView textStageStart;
    public Button butCalculate;
    String startFilePath = null;
    String finishFilePath = null;
    String startFileName = null;
    String finishFileName = null;
    String resultsFilePath = null;
    String resultsFileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewItems("Results", R.layout.activity_results, R.id.listResultsView);
        butCalculate = (Button) findViewById(R.id.butCalulateResults);
        textStageFinish = (TextView) findViewById(R.id.textResStageEnd);
        textStageStart = (TextView) findViewById(R.id.textResStageStart);
    }

    @Override
    public void createViewItems(String position, int layoutActivityId, int logListViewID){
        this.position = position;
        setContentView(layoutActivityId);
        logListView = (ListView) findViewById(logListViewID);
        logListView.setItemsCanFocus(true);
        System.out.println("DONE - createViewItems");
    }



    public void loadResults(View view){
        Intent intent = new Intent(this, FileChooser.class);
        ArrayList<String> extensions = new ArrayList<String>();
        extensions.add(".csv");
        intent.putStringArrayListExtra("filterFileExtension", extensions);
        intent.putExtra("filterFileStartPath", raceName);
        startActivityForResult(intent, FILE_RESULTS);

    }

    public void calulateResults(View view) {

        if ( (startFilePath == null) || (finishFilePath == null) ){
            textStageFinish.setText("need finish file !");
            textStageStart.setText ("need start file !");
        } else {
            try {
                int x = startFileName.lastIndexOf(".");
                String resFileName = "result_";
                String st = startFileName.substring(0, x);
                x = finishFileName.lastIndexOf(".");
                String fn = finishFileName.substring(0, x);
                resFileName = resFileName.concat(st);
                resFileName = resFileName.concat("-");
                resFileName = resFileName.concat(fn);
                logger = new LogStorage(raceName, resFileName, "result", tagEventList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<TagEvent> startTagEventList = new ArrayList<>();
            ArrayList<TagEvent> finishTagEventList = new ArrayList<>();
            File startLog = new File(startFilePath);
            File finishLog = new File(finishFilePath);

            logger.readRiderlog(startLog, startTagEventList);
            logger.readRiderlog(finishLog, finishTagEventList);

            int countS = startTagEventList.size();
            int countF = finishTagEventList.size();
            if (countS >= countF){
                getResults(startTagEventList,finishTagEventList );
            } else {
                getResults(finishTagEventList, startTagEventList );
            }
            logger.deleteLogFile();
            try {
                logger.putAttaylistIntoLog(tagEventList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateLogList();
        }
    }

//    run throught longsest list. may be start or finish
    public void getResults( ArrayList<TagEvent> one ,  ArrayList<TagEvent> two){
        Iterator<TagEvent> it_start = one.iterator();

        while (it_start.hasNext()) {
            boolean foundIdMatch = false;
            TagEvent tag = it_start.next();
            String id = tag.id;

            if (!id.equalsIgnoreCase("unknown")) {
                long startTime = tag.timeStamp;
                Iterator<TagEvent> it_finish = two.iterator();
                while (it_finish.hasNext()) {
                    TagEvent tagEnd = it_finish.next();
                    if (id.matches(tagEnd.id)) {
                        System.out.println("got " + id);
                        long finishTime = tagEnd.timeStamp;
                        tag.timeStamp = Math.abs(startTime - finishTime);
                        tag.location = "result";
                        if (tag.timeStamp == 0) {
                            tag.timeStamp = (long) (99 * 60 * 60 * 1000);
                        }
                        tagEventList.add(tag);
                        foundIdMatch = true;
                    }
                }
            }
            if (!foundIdMatch){
                tag.location =  "Unmathced";
                tagEventList.add(tag);
            }
        }

        //All the entries from first list is tagged
        //now look for unmathed entries in list 2
        Iterator<TagEvent> it_finish = two.iterator();
        while (it_finish.hasNext()) {
            boolean foundIdMatch = false;
            TagEvent checkTag = it_finish.next();
            String checkId = checkTag.id;
            if (!checkId.equalsIgnoreCase("unknown")) {
                Iterator<TagEvent> it_res = tagEventList.iterator();
                while (it_res.hasNext()) {
                    TagEvent checkEventTag = it_res.next();
                    if (checkId.matches(checkEventTag.id)){
                        foundIdMatch = true;
                    }
                }
            }
            if (!foundIdMatch){
                checkTag.location =  "Unmathced";
                tagEventList.add(checkTag);
            }
        }

    }



    public void choseStartFile(View view){
        Intent intent = new Intent(this, FileChooser.class);
        ArrayList<String> extensions = new ArrayList<String>();
        extensions.add(".csv");
//        extensions.add(".txt");
        intent.putStringArrayListExtra("filterFileExtension", extensions);
        intent.putExtra("filterFileStartPath", raceName);
        startActivityForResult(intent, FILE_START);
    }


    public void choseFinishFile(View view){
        Intent intent = new Intent(this, FileChooser.class);
        ArrayList<String> extensions = new ArrayList<String>();
        extensions.add(".csv");
//        extensions.add(".txt");
        intent.putStringArrayListExtra("filterFileExtension", extensions);
        intent.putExtra("filterFileStartPath", raceName);
        startActivityForResult(intent, FILE_END);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == FILE_START) && (resultCode == -1)) {
            startFilePath = data.getStringExtra("fileSelected");
            startFileName = data.getStringExtra("fileName");
            textStageStart.setText(startFileName);
        } else if ((requestCode == FILE_END) && (resultCode == -1)) {
            finishFilePath = data.getStringExtra("fileSelected");
            finishFileName = data.getStringExtra("fileName");
            textStageFinish.setText(finishFileName);
        } else if ((requestCode == FILE_RESULTS) && (resultCode == -1)) {
            resultsFilePath = data.getStringExtra("fileSelected");
            resultsFileName = data.getStringExtra("fileName");
            textStageFinish.setText(resultsFileName);

            File resultLog = new File(resultsFilePath);
            tagEventList.clear();
            logger = new LogStorage (resultLog, tagEventList);
            updateLogList();
         }
    }


    @Override
    public void updateLogList(){
        ArrayList<TagEventBase> tmpList  =  new ArrayList();
        Iterator<TagEvent> it = tagEventList.iterator();
        while (it.hasNext()) {
            TagEvent tag = it.next();

            String id = tag.id;
            String location = tag.location;
            String stage = tag.stage;
            String name  = tag.name;
            String surname  = tag.surname;
            long time = tag.timeStamp;
            tmpList.add( new TagEventBase(id, tag.race, stage,location, time, name, surname) );
        }

        ArrayAdapter<TagEventBase> adapter = new ArrayAdapter<TagEventBase>(this, android.R.layout.simple_list_item_1, tmpList);
        Comparator<TagEventBase> comp = new TagComparator<TagEventBase>();
        Collections.sort(tmpList, comp);
        logListView.setAdapter(adapter);
    }


    private class TagComparator<T> implements Comparator<TagEventBase> {
        public int compare(TagEventBase emp1, TagEventBase emp2) {
            if (emp1.getTimeStamp() < emp2.getTimeStamp()) {
                return -1;
            }
            if (emp1.getTimeStamp() == emp2.getTimeStamp()) {
                return 0;
            }
            return 1;
        }
    }
}



