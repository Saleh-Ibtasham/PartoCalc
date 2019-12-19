package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyHelper extends SQLiteOpenHelper {

    private Context con;

    public MyHelper(Context context) {
        super(context, "GraphDB", null, 1);
        con = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable1 = "create table if not exists fetalGraph (xValues INTEGER, yValues INTEGER)";
        db.execSQL(createTable1);

        String createTable2 = "create table if not exists cervicalGraph (xValues INTEGER, yValues INTEGER)";
        db.execSQL(createTable2);

        String createTable3 = "create table if not exists descendGraph (xValues INTEGER, yValues INTEGER)";
        db.execSQL(createTable3);

        String createTable4 = "create table if not exists contractionGraph (xValues INTEGER, yValues INTEGER)";
        db.execSQL(createTable4);

        String createTable5 = "create table if not exists maternalGraph (xValues INTEGER, yValues INTEGER)";
        db.execSQL(createTable5);

        String createTable6 = "create table if not exists pressureGraph (xValues REAL, yValues1 INTEGER, yValues2 INTEGER)";
        db.execSQL(createTable6);

        Toast.makeText(con,"Table Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertData(int x, int y, String graph) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("xValues", x);
        contentValues.put("yValues", y);

        db.insert(graph, null, contentValues);
        Toast.makeText(con, "Data inserted", Toast.LENGTH_LONG).show();
    }

    public void deleteAll(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table,null,null);

    }

    public void insertDataForPressure(double pressureX, int sysTol, int dysTol, String pressureGraph) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("xValues", pressureX);
        contentValues.put("yValues1", sysTol);
        contentValues.put("yValues2", dysTol);

        db.insert(pressureGraph, null, contentValues);

        Toast.makeText(con, "Data inserted", Toast.LENGTH_LONG).show();
    }

    public void deleteEntry(String graph) {
        SQLiteDatabase db = this.getWritableDatabase();

//        String query = "( select MAX(xValues) from "+graph + " )";
//        db.delete(graph, "xValues = ?", new String[]{query});
        String query = "DELETE FROM " + graph +" WHERE xValues IN(SELECT MAX(xValues) FROM " + graph+" );";
        db.execSQL(query);


    }
}
