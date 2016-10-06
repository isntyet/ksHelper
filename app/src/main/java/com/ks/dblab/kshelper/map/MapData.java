package com.ks.dblab.kshelper.map;

/**
 * Created by jojo on 2016-05-30.
 */
public class MapData {
    private int num;
    private String name;
    private double x;
    private double y;

    public MapData(int num, String name, double x, double y){
        this.num = num;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
