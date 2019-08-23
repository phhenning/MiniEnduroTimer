package com.henning.pieter.endurotimer;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by phenning on 2015/08/25.
 */
public class TagEvent extends TagEventBase{


    public TagEvent(String id, String race, String stage, String location, long ts, String name, String surname) {
        super(id, race, stage, location,ts,name,surname);
    }

    @Override
    public String toString() {
        return id + " | " + name + "   |   " + (new SimpleDateFormat("hh:mm:ss:SSS")).format(new Date(timeStamp));
    }

}