package com.henning.pieter.endurotimer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by phenning on 2015/09/17.
 */
public class TagEventBase {

    public String id;
    public long timeStamp;
    public String stage;
    public String location;
    public String name = "unknown";
    public String surname = "unknown";
    public String race;


    public TagEventBase(String id, String race, String stage, String location, long ts, String name, String surname) {
        this.id = id;
        this.timeStamp = ts;
        this.stage = stage;
        this.location = location;
        this.name = name;
        this.surname = surname;
        this.race = race;
    }


    public void setRiderId(String riderId) {
        id = riderId;
    }

    public void setRiderName(String name) {
        this.name = name;
    }

    public void setRiderSurname(String surname) {
        this.surname = surname;
    }

    public long getTimeStamp() {return this.timeStamp; }

    @Override
    public String toString() {

        long th =    timeStamp / 1000 / 60 / 60;
        long tm =    timeStamp / 1000 / 60 ;
        long ts =    timeStamp / 1000;
        long m = tm - th*60;
        long s = ts - (tm*60);
        long ms =  timeStamp % 1000;
        return id + " | "  + name + "   |   "  +  th + "h:" +   m + "m:" +  s + "s." +  ms;
    }

}
