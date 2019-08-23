package com.henning.pieter.endurotimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by phenning on 2015/09/06.
 */
public class StageBase extends Activity {

    public static int selectedListItemNo;
    static String stageNo;
    static String raceName;
    static String riderId;
    static String position;
    ListView logListView;
    final Context context = this;
    static LogStorage logger;
    ArrayList<TagEvent> tagEventList = new ArrayList<>();
    public static ArrayList<RiderInfo> riderList = null;
    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_stage_end);
        intent = getIntent();
        riderList = (ArrayList<RiderInfo> ) intent.getSerializableExtra(MainActivity.RIDER_LIST);
        stageNo = intent.getStringExtra(MainActivity.STAGE_NO);
        if (stageNo.matches("")) {
            String timeStamp = new SimpleDateFormat("dd-hh-mm").format(Calendar.getInstance().getTime());
            stageNo = "Stage-"+timeStamp;
        }
        raceName = intent.getStringExtra(MainActivity.RACE);
        if (raceName.matches("")) {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            raceName = "Race-"+timeStamp;
        }
    }

    public void createViewItems(String position, int layoutActivityId, int logListViewID){
        this.position = position;
       setContentView(layoutActivityId);
        try {
                logger = new LogStorage(raceName, stageNo, position, tagEventList);
        }catch (Exception e){
            e.printStackTrace();
        }

        logListView = (ListView) findViewById(logListViewID);
        logListView.setItemsCanFocus(true);
        updateLogList();
        logListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.activity_edit_end_tag, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                selectedListItemNo = position;
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                // set dialog message
                alertDialogBuilder.setCancelable(false).
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        riderId = (userInput.getText().toString());
                                        if ( riderId.matches("") ){
                                            riderId = "unknown";
                                        }
                                        if ( doesTagExist(riderId) ) {
                                            Toast toast= Toast.makeText(context," Race No Exists",Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                                            toast.show();
                                        }else {
                                            editLogList(selectedListItemNo, riderId);
                                        }
                                    }
                                }
                        ).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                // show it
                alertDialog.show();
            }
        });


//        logListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(StageEnd.this, "Item in position " + position + " clicked", Toast.LENGTH_LONG).show();
//                // Return true to consume the click event. In this case the
//                // onListItemClick listener is not called anymore.
//
//                if (stageNo.matches("")) {
//                    String timeStamp = new SimpleDateFormat("dd-hh").format(Calendar.getInstance().getTime());
//                    stageNo = "Sage"+timeStamp;
//                }
//                return true;
//            }
//        });


    }

    public boolean doesTagExist(String raceNo){
        boolean result = false;
        Iterator<TagEvent> it = tagEventList.iterator();
        while (it.hasNext()){
            TagEvent tag = it.next();
            if (tag.id.equalsIgnoreCase(raceNo)){   // compensate for view tagEventList offset
                result = true;
                break;
            }
        }
        return result;
    }


    public void editLogList(int listId, String riderID){
        // note that lisd displayd is inverse of tagEventList in memory
        String name = "unknown";
        String surname = "unknown";
        System.out.println("editLogList " + listId + " " + riderID);


        Iterator<RiderInfo> it = riderList.iterator();
        while (it.hasNext()){
            RiderInfo rider = it.next();
            if (rider.raceNo.matches(riderID)){   // compensate for view tagEventList offset
                name = rider.name;
                surname = rider.surname;
                break;
            }
        }

        int length = tagEventList.size();
        int id = length - listId -1;
        TagEvent tag = tagEventList.get(id);
        tag.setRiderId(riderId);
        tag.setRiderName(name);
        tag.setRiderSurname(surname);
        tagEventList.remove(id);
        tagEventList.add(id, tag);
        updateLogList();
        logger.deleteLogFile();
        try {
            logger.putAttaylistIntoLog(tagEventList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void tagRider(String riderID, long time) {

        String name = "unknown";
        String surname = "unknown";

        if ( riderID.matches("") ){
            riderID = "unknown";
        } else {
            Iterator<RiderInfo> it = riderList.iterator();
            while (it.hasNext()){
                RiderInfo rider = it.next();
                if (rider.raceNo.matches(riderID)){
                    name = rider.name;
                    surname = rider.surname;
                    break;
                }
            }
        }

        TagEvent tag = new TagEvent(riderID, raceName, stageNo, position, time ,name,surname);
        tagEventList.add(tag);
        System.out.println("Tagged Rider # " + riderID);
        try {
            logger.addTagToFile(tag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateLogList();
    }



    // List is displayed in reverse
    public void updateLogList(){
        ArrayList<TagEvent> tmpList  = new ArrayList(tagEventList);
        ArrayAdapter<TagEvent> adapter = new ArrayAdapter<TagEvent>(this, android.R.layout.simple_list_item_1, tmpList);
        Collections.reverse(tmpList);
        logListView.setAdapter(adapter);
    }
}

