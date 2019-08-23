package com.henning.pieter.endurotimer;

import java.io.Serializable;

/**
 * Created by phenning on 2015/08/28.
 */
public class RiderInfo implements Serializable{

    public String raceNo;
    public String name;
    public String surname;
    public String cat;
    public String sex;


    public RiderInfo (String raceNo, String name, String surname, String cat, String sex){
        this.raceNo = raceNo;
        this.name = name;
        this.surname = surname;
        this.cat = cat;
        this.sex = sex;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(raceNo);
        builder.append(",");
        builder.append( name);
        builder.append(",");
        builder.append(surname);
        builder.append(",");
        builder.append(cat);
        builder.append( ",");
        builder.append(sex);
        return builder.toString();
    }
}
