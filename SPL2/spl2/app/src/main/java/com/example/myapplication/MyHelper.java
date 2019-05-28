package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.widget.Toast;

public class MyHelper extends SQLiteOpenHelper {

    private Context con;

    public MyHelper(Context context) {
        super(context, "GraphDB", null, 1);
        con = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table MyGraph (xValues INTEGER, yValues INTEGER)";
        db.execSQL(createTable);
        Toast.makeText(con,"Table Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertData(int x, int y) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("xValues", x);
        contentValues.put("yValues", y);

        db.insert("MyGraph", null, contentValues);

        Toast.makeText(con, "Data inserted", Toast.LENGTH_LONG).show();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("MyGraph",null,null);

    }

}
