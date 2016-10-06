package com.ks.dblab.kshelper.call;

/**
 * Created by jojo on 2016-07-12.
 */
public class CallItem {

    private String name;
    private String number;

    public CallItem(String name, String number){
        this.name = name;
        this.number = number;
    }

    public String getName(){
        return this.name;
    }

    public String getNumber(){
        return this.number;
    }

}
