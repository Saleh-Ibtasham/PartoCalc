package com.example.myapplication;

import java.util.Date;

public class PreviousEntry {
   private String graphName;
   private Object xValue;
   private Date date;

    public PreviousEntry(String graphName, Object xValue, Date date) {
        this.graphName = graphName;
        this.xValue = xValue;
        this.date = date;
    }

    public String getGraphName() {
        return graphName;
    }

    public Object getxValue() {
        return xValue;
    }

    public Date getDate() {
        return date;
    }
}
