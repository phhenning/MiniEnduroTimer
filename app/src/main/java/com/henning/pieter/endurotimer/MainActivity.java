package com.henning.pieter.endurotimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.os.Environment;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

public class MainActivity extends Activity {

    private static final int START_COUNT_DOWN = 23;
    private static final int FIND_RACE = 33;
    private static final int FIND_STAGE = 34;


    public static String STAGE_NO = "com.henning.pieter.endurotimer.MESSAGE_STAGE_NO";
    public static String RACE = "com.henning.pieter.endurotimer.MESSAGE_RACE";
    public static String RIDER_LIST =  "com.henning.pieter.endurotimer.MESSAGE_RIDER_LIST";
    public static String COUNT_VALUE =  "com.henning.pieter.endurotimer.MESSAGE_COUNT_VALUE";
    public static ArrayList<RiderInfo> riders = null;
    public static String raceName = null;
    public static EditText stageNoInput;
    public static EditText raceInput;
    public static int countDownTime = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stageNoInput  = (EditText) findViewById(R.id.editStage);
        raceInput  = (EditText) findViewById(R.id.editRace);
        if (raceName != null){
            raceInput.setText(raceName);
        }
        makeRiders();
        readRiderList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        switch (item.getItemId()) {
            case R.id.action_help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
//            case R.id.action_donate:
//                Intent intent = new Intent(this, HelpActivity.class);
//                startActivity(intent);
////                https://developer.paypal.com/docs/classic/mobile/ht_mpl-itemPayment-Android/
//                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == START_COUNT_DOWN ) && (resultCode == -1)) {
            countDownTime = data.getIntExtra("NewCountDownTime", 11) ;
        } else if ((requestCode == FIND_RACE) && (resultCode == -1)) {
//            startFilePath = data.getStringExtra("fileSelected");
//            textStageStart.setText(startFileName);
            String path = data.getStringExtra("folderName");
            int x = path.lastIndexOf("/");
            raceName = path.substring(x+1);
            raceInput.setText(raceName);
            System.out.println("RACE : " + raceName);

            String stageName = data.getStringExtra("fileName");
            x = stageName.lastIndexOf("-");
            stageName = stageName.substring(0,x);
            stageNoInput.setText(stageName);


        } else if ((requestCode == FIND_STAGE) && (resultCode == -1)) {
            String stageName = data.getStringExtra("fileName");
            int x = stageName.lastIndexOf("-");
            stageName = stageName.substring(0,x);
            stageNoInput.setText(stageName);
            System.out.println("STAGE : " + data.getStringExtra("fileName"));
        }

    }



    public void calculateResults(View view) {
        Intent intent = new Intent(this, CalculateResults.class);
        String stageNo   = stageNoInput.getText().toString();
        raceName  = raceInput.getText().toString();

        if (stageNo.matches("")) {
            String timeStamp = new SimpleDateFormat("dd-hh").format(Calendar.getInstance().getTime());
            stageNo = "Sage"+timeStamp;
        }
        intent.putExtra(STAGE_NO, stageNo);
        if (raceName.matches("")) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            raceName = "Race_"+timeStamp;
        }
        intent.putExtra(RACE, raceName);
        intent.putExtra(RIDER_LIST, riders);
        startActivity(intent);
    }


    public void findRace(View view){
        Intent intent = new Intent(this, FileChooser.class);
        ArrayList<String> extensions = new ArrayList<String>();
        extensions.add(".csv");
        intent.putStringArrayListExtra("filterFileExtension", extensions);
        startActivityForResult(intent, FIND_RACE);
    }

    public void findStage(View view){
        Intent intent = new Intent(this, FileChooser.class);
        ArrayList<String> extensions = new ArrayList<String>();
        extensions.add(".csv");
        intent.putStringArrayListExtra("filterFileExtension", extensions);
        intent.putExtra("filterFileStartPath", raceName);
        //startActivityForResult(intent, FIND_STAGE);
        startActivityForResult(intent, FIND_RACE);
    }


    public void addStageStart(View view) {
        Intent intent = new Intent(this, StageStart.class);
        String stageNo   = stageNoInput.getText().toString();
        raceName  = raceInput.getText().toString();

        if (stageNo.matches("")) {
            String timeStamp = new SimpleDateFormat("dd-hh").format(Calendar.getInstance().getTime());
            stageNo = "Sage"+timeStamp;
        }
        intent.putExtra(STAGE_NO, stageNo);
        if (raceName.matches("")) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            raceName = "Race_"+timeStamp;
        }
        intent.putExtra(RACE, raceName);
        intent.putExtra(RIDER_LIST, riders);
        intent.putExtra(COUNT_VALUE, countDownTime);
        startActivityForResult(intent, START_COUNT_DOWN);
//        startActivity(intent);
    }

    public void addStagEnd(View view) {
        Intent intent = new Intent(this, StageEnd.class);
        EditText stageNoInput = (EditText) findViewById(R.id.editStage);
        EditText raceInput = (EditText) findViewById(R.id.editRace);

        String stageNo = stageNoInput.getText().toString();
        String raceName = raceInput.getText().toString();

        if (stageNo.matches("")) {
            String timeStamp = new SimpleDateFormat("dd-hh").format(Calendar.getInstance().getTime());
            stageNo = "Sage" + timeStamp;
        }
        intent.putExtra(STAGE_NO, stageNo);
        if (raceName.matches("")) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            raceName = "Race_" + timeStamp;
        }
        intent.putExtra(RACE, raceName);
        intent.putExtra(RIDER_LIST, riders);
        startActivity(intent);
    }



    public void lapRaceStart(View view) {
        Intent intent = new Intent(this, LapEnds.class);
        EditText stageNoInput = (EditText) findViewById(R.id.editStage);
        EditText raceInput = (EditText) findViewById(R.id.editRace);

        String stageNo = stageNoInput.getText().toString();
        String raceName = raceInput.getText().toString();

        if (stageNo.matches("")) {
            String timeStamp = new SimpleDateFormat("dd-hh").format(Calendar.getInstance().getTime());
            stageNo = "Lap" + timeStamp;
        }
        intent.putExtra(STAGE_NO, stageNo);
        if (raceName.matches("")) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            raceName = "Lap_Race_" + timeStamp;
        }
        intent.putExtra(RACE, raceName);
        intent.putExtra(RIDER_LIST, riders);
        startActivity(intent);
    }

    public void makeRiders() {
        File riderFile;
        File dir = new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS), "EnduroTimer");


        //File dir = new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS), "EnduroTimer");
        riderFile = new File(dir, ("riderList.csv"));
        riders = new ArrayList<>();
        //TODO remove
        riders.add(new RiderInfo("10", "Pieter", "Henning", "SV", "M"));
        riders.add(new RiderInfo("9", "Sune", "Henning", "EW", "F"));
        riders.add(new RiderInfo("8", "John", "Dalton", "SV", "M"));
        riders.add(new RiderInfo("4", "Hanri", "Dalton", "EW", "F"));
        riders.add(new RiderInfo("5", "Werner", "Hatting", "SV", "M"));
        riders.add(new RiderInfo("6", "Adi", "van Der Merwe", "EM", "M"));
        riders.add(new RiderInfo("7", "Mark", "Hopkins", "EM", "M"));
        riders.add(new RiderInfo("3", "Darryn", "Stow", "EM", "M"));
        riders.add(new RiderInfo("2", "Danny", "Dobinson", "EM", "M"));
        riders.add(new RiderInfo("1", "Rupert", "van Go Fast", "SV", "M"));

//        riderFile.delete();
//        riderFile = new File(dir, ("riderList.csv"));

        if (riderFile.exists()) {
//            System.out.println("rider tagEventList template present");
        } else {
            BufferedOutputStream outputStream = null;
            String entry = " Race No , Name , Surname , Cat , Sex \n";
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(riderFile, true));
                outputStream.write(entry.getBytes());

                Iterator<RiderInfo> iterator = riders.iterator();
            while (iterator.hasNext()) {
                String data = iterator.next().toString() + "\n";
                System.out.println("written : " + data);
                outputStream.write(data.getBytes());
            }
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
//            System.out.println("rider lit template crated");
        }
    }


    public void readRiderList(){
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "EnduroTimer");
        dir.deleteOnExit();
        System.out.println("sdk ver: "+ Build.VERSION.SDK_INT);
        File riderFile = new File(dir, ("riderList.csv"));


        if(riderFile.exists()){
            riders = new ArrayList<>();
            Scanner scan = null;
            try {
                scan = new Scanner(riderFile);
                int x = 1;
                while (scan.hasNextLine()) {
                    RiderInfo rider;
                    String line = scan.nextLine();
                    String[] lineArray = line.split(",");
                    if (lineArray.length == 5) {
                        rider = new RiderInfo(lineArray[0], lineArray[1], lineArray[2], lineArray[3], lineArray[4]);
                    } else {
                        rider = new RiderInfo("bad ", "rider ", "info ", "line ", Integer.toString(x));
                    }
                    riders.add(rider);
                    x++;
//                    System.out.println(rider);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if ( scan != null) {
                        scan.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ListView logListView = (ListView) findViewById(R.id.listRiderView);
            ArrayAdapter<RiderInfo> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, riders);
            logListView.setAdapter(adapter);
        }else{
            System.out.println("File not found!");
            TextView logViewText = (TextView) findViewById(R.id.textLog);
            logViewText.setText("No rider file found in " + dir.toString() +
            "\n You can carry on and add riderList later.");
            riders = null;
        }
    }
}
