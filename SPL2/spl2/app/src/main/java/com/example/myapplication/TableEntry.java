package com.example.myapplication;

public class TableEntry {
   private int xValue = 0;
   private String yValue;

    public TableEntry(int xValue, String yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public int getxValue() {
        return xValue;
    }

    public void setxValue(int xValue) {
        this.xValue = xValue;
    }

    public String getyValue() {
        return yValue;
    }

    public void setyValue(String yValue) {
        this.yValue = yValue;
    }
}
