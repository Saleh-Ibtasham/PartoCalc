package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class FetalHeartRate extends Fragment {

    Button btn,btn2;
    EditText xInput, yInput;
    LineDataSet fetalDataSet = new LineDataSet(null,null);
    LineDataSet fetalDataSet1, fetalDataSet2;
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    ArrayList<Entry> temp = new ArrayList<>();
    MyHelper myHelper;
    SQLiteDatabase sqLiteDatabase;
    LineChart fetalGraph;
    boolean pointsAdded;
    LineData fetalData;
    ImageView imageView;
    ArrayList<Entry> savedEntries = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fetal_heart_rate, container, false);
        btn = (Button) view.findViewById(R.id.button);
        btn2 = (Button) view.findViewById(R.id.button2);
        xInput = (EditText) view.findViewById(R.id.editText);
        yInput = (EditText) view.findViewById(R.id.editText2);
        fetalGraph = (LineChart) view.findViewById(R.id.graph1);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        xInput.setKeyListener(null);
        yInput.setKeyListener(null);

        myHelper = new MyHelper(getContext());
        sqLiteDatabase = myHelper.getWritableDatabase();

        fetalDataSet1 = new LineDataSet(null,null);
        fetalDataSet2 = new LineDataSet(null,null);

        dataSets.clear();
        fetalDataSet1.addEntry(new Entry(0,100));
        fetalDataSet1.addEntry(new Entry(24,100));
        fetalDataSet1.setColor(Color.GRAY);
        fetalDataSet1.setLineWidth(5);
        fetalDataSet1.setLabel("lower-limit");

        fetalDataSet2.addEntry(new Entry(0,180));
        fetalDataSet2.addEntry(new Entry(24,180));
        fetalDataSet2.setColor(Color.GRAY);
        fetalDataSet2.setLineWidth(5);
        fetalDataSet2.setLabel("upper-limit");

        pointsAdded = false;

        xInput.setText(Integer.toString(0));
//        FetalDataSet.clear();
//        FetalDataSet.setLabel("Readings");

        fetalData = new LineData();
        fetalData.addDataSet(fetalDataSet1);
        fetalData.addDataSet(fetalDataSet2);

        XAxis xAxis = fetalGraph.getXAxis();
        YAxis yAxis = fetalGraph.getAxisLeft();

        yAxis.setLabelCount(13,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(200);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(80);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        xAxis.setGranularity(1f);

        fetalGraph.setData(fetalData);
        fetalGraph.invalidate();
        execBtn();


        fetalDataSet.setLineWidth(5);
        return view;
    }
    private void execBtn() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetalData.removeDataSet(fetalDataSet);
                String value = null;
                int xVal = Integer.parseInt(xInput.getText().toString());
                value = yInput.getText().toString();

                value = valueChecking(value);

                yInput.setText(value);

                int yVal = Integer.parseInt(value);

                if(pointsAdded == false)
                {
                    myHelper.deleteAll();
                }

                myHelper.insertData(xVal, yVal);

                fetalDataSet.clear();

                savedEntries = getData();
                fetalDataSet.setValues(getData());
                fetalDataSet.setLabel("Readings");
                fetalDataSet.setDrawCircles(true);
                fetalDataSet.setDrawCircleHole(true);
                fetalDataSet.setCircleColor(Color.CYAN);
                fetalDataSet.setCircleRadius(6);
                fetalDataSet.setCircleHoleRadius(3);

                fetalData.addDataSet(fetalDataSet);
                fetalGraph.clear();
                fetalGraph.setData(fetalData);
                fetalGraph.invalidate();

                pointsAdded = true;

                int x = Integer.parseInt(xInput.getText().toString());
                x += 4;
                xInput.setText(Integer.toString(x));
                yInput.setText("");

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointsAdded = false;
                fetalGraph.clear();
//                lineData.clearValues();
                fetalData.removeDataSet(fetalDataSet);
                fetalGraph.setData(fetalData);
                fetalGraph.invalidate();
                myHelper.deleteAll();
                xInput.setText(Integer.toString(0));
                yInput.setText("");
            }
        });
    }

    private String valueChecking(String value) {

        if(value.equals("for")||value.equals("four"))
            return "4";
        if(value.equals("sex")|| value.equals("six"))
            return "6";
        return value;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    yInput.setText(result.get(0));
                }
                break;
        }
    }
    private ArrayList<Entry> getData() {

        ArrayList<Entry> dp = new ArrayList<>();
        String [] columns = {"xValues","yValues"};

        Cursor cursor = sqLiteDatabase.query("MyGraph", columns, null, null, null, null, "xValues ASC");

        for(int i=0; i<cursor.getCount(); i++)
        {
            cursor.moveToNext();
            dp.add(new Entry(cursor.getInt(0),cursor.getInt(1)));
        }
        return dp;
    }

    public ArrayList<Entry> saveData(){

        return this.savedEntries;
    }

    public void refreshData(){
        pointsAdded = false;
        fetalGraph.clear();

        fetalData.removeDataSet(fetalDataSet);
        fetalGraph.setData(fetalData);
        fetalGraph.invalidate();
        myHelper.deleteAll();
        xInput.setText(Integer.toString(0));
        yInput.setText("");
    }
}
