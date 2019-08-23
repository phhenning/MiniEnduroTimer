package com.henning.pieter.endurotimer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by phenning on 2015/08/27.
 */
public class LogStorage {

    static File stageLog;
    static File dir;
    static String raceName;
    static String stageNo;
    static String location;

    public LogStorage(File logFile, ArrayList<TagEvent> tagEventList){
        stageLog = logFile;
        if (stageLog.exists()) {
            readRiderlog(stageLog, tagEventList);
        }
    }

    public LogStorage(String raceName, String stageNo, String location, ArrayList<TagEvent> tagEventList) throws IOException {
        this.raceName = raceName;
        this.stageNo = stageNo;
        this.location = location;
        boolean storeExternal = isExternalStorageWritable();
        if (storeExternal){
            createLogFileWithHeader(tagEventList);
        } else {
            //// TODO: 2015/08/27
        }
    }

    public void createFileHeader(File logFile)throws IOException{
        FileWriter fileWriter = new FileWriter(logFile, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        String logEntry = "Race No, Name , Surname, TimeStamp(ms), Event, Location , Stage, Duration \n";
        bufferedWriter.write(logEntry);
        bufferedWriter.close();
        fileWriter.close();
    }

    public void createLogFileWithHeader(ArrayList<TagEvent> tagEventList) throws IOException {
        dir = getLogStorageDir("EnduroTimer/"+raceName);
        stageLog = new File(dir, (stageNo + "-" + location + ".csv"));
//        if (location.equalsIgnoreCase("result")){
//            stageLog = new File(dir, (location + "-" + stageNo + ".csv"));
//            FileWriter fileWriter = new FileWriter(stageLog, true);
//            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//            String logEntry = "Race No, Name , Surname, Time(ms) , Event, Location , Stage, Time \n";
//            bufferedWriter.write(logEntry);
//            bufferedWriter.close();
//            fileWriter.close();
//        } else {
//            stageLog = new File(dir, (stageNo + "-" + location + ".csv"));
//        }

        if (!stageLog.exists()) {
            createFileHeader(stageLog);
        } else {
            readRiderlog(stageLog, tagEventList);
        }
    }

    public void readRiderlog(File logFile, ArrayList<TagEvent> tagEventList){
        System.out.println("Read   - "+ logFile);

        Scanner scan = null;
        try {
            scan = new Scanner(logFile);
            while (scan.hasNextLine()) {
                TagEvent tag = null;
                String line = scan.nextLine();
                String[] lineArray = line.split(",");
                if (lineArray.length >= 7) {
                    String id       = lineArray[0];

                    if (!id.equalsIgnoreCase("Race No")) {
                        String name = lineArray[1];
                        String surname = lineArray[2];
                        long time_ms = 0;
                        try {
                            time_ms = Long.valueOf(lineArray[3]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        String race = lineArray[4];
                        String location = lineArray[5];
                        String stage = lineArray[6];
                        tag = new TagEvent(id, race, stage, location, time_ms, name, surname);
                    }
                }
                if (tag != null) {
                    tagEventList.add(tag);
                    System.out.println(tag);
                }
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
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public File getLogStorageDir(String raceName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS), raceName);
//        File file = new File("/mnt/sdcard/Documents/EnduroTimer");
        if (!file.mkdirs()) {
            System.out.println("Directory not created");
        }
        return file;
    }

    public void deleteLogFile(){
        stageLog.delete();
        try {
            stageLog.createNewFile();
            createFileHeader(stageLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putAttaylistIntoLog(ArrayList<TagEvent> list)throws IOException {
        Iterator<TagEvent> iterator = list.iterator();
            while (iterator.hasNext()) {
                addTagToFile(iterator.next());
            }
    }

    public void addTagToFile(TagEvent event) throws IOException {

        String id = event.id;
        String location = event.location;
        String stage = event.stage;
        String name  = event.name;
        String surname  = event.surname;
        String time = String.valueOf(event.timeStamp);
        String logEntry;
        if (location.equalsIgnoreCase("result")) {
            long th =    event.timeStamp / 1000 / 60 / 60;
            long tm =    event.timeStamp / 1000 / 60 ;
            long ts =    event.timeStamp / 1000;

            long m = tm - th*60;
            long s = ts - (tm*60);
            long ms =  event.timeStamp % 1000;
            System.out.println ( (int)th + "h:" +   (int)m + "m:" +  (int)s + "s." +  (int)ms);



            //  return id + " | "  + name + "   |   ","  +  h + "h:" +  m + "m:" + s + "s." + ms;
            logEntry = id + ", " + name + ", " + surname + "," + time + ", " + event.race + ", " + location + ", " + stage + ","  +  th + ":" +  m + ":" + s + "." + ms + "\n";
        } else {
            logEntry = id + ", " + name + ", " + surname + "," + time + ", " + event.race + ", " + location + ", " + stage + "\n";
        }
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(stageLog, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(logEntry);
            bufferedWriter.close();
            fileWriter.close();

        }finally {
            if (bufferedWriter != null ) {
                bufferedWriter.close();
            }
            if (fileWriter != null ) {
                fileWriter.close();
            }
        }
    }
}
