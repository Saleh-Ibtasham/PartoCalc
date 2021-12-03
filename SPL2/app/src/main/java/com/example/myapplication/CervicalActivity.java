package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
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

public class CervicalActivity extends Fragment {

    Button btn,btn2;
    EditText xInput, yInput;
    LineDataSet cervicalDataSet = new LineDataSet(null,null);
    LineDataSet cervicalDataSet1, cervicalDataSet2;
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    ArrayList<Entry> temp = new ArrayList<>();
    MyHelper myHelper;
    SQLiteDatabase sqLiteDatabase;
    LineChart cervicalGraph;
    boolean pointsAdded;
    LineData cervicalData;
    ImageView imageView;
    ArrayList<Entry> savedEntries = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cervix_chart, container, false);
        btn = (Button) view.findViewById(R.id.button);
        btn2 = (Button) view.findViewById(R.id.button2);
        xInput = (EditText) view.findViewById(R.id.editText);
        yInput = (EditText) view.findViewById(R.id.editText2);
        cervicalGraph = (LineChart) view.findViewById(R.id.graph1);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        xInput.setKeyListener(null);
        yInput.setKeyListener(null);

        myHelper = new MyHelper(getContext());
        sqLiteDatabase = myHelper.getWritableDatabase();


        cervicalDataSet1 = new LineDataSet(null,null);
        cervicalDataSet2 = new LineDataSet(null,null);

        dataSets.clear();
        cervicalDataSet1.addEntry(new Entry(0,4));
        cervicalDataSet1.addEntry(new Entry(12,10));
        cervicalDataSet1.setColor(Color.GREEN);
        cervicalDataSet1.setLineWidth(5);
        cervicalDataSet1.setLabel("Alert");

        cervicalDataSet2.addEntry(new Entry(8,4));
        cervicalDataSet2.addEntry(new Entry(20,10));
        cervicalDataSet2.setColor(Color.RED);
        cervicalDataSet2.setLineWidth(5);
        cervicalDataSet2.setLabel("Action");

        pointsAdded = false;

        xInput.setText(Integer.toString(0));
//        FetalDataSet.clear();
//        FetalDataSet.setLabel("Readings");

        cervicalData = new LineData();
        cervicalData.addDataSet(cervicalDataSet1);
        cervicalData.addDataSet(cervicalDataSet2);

        XAxis xAxis = cervicalGraph.getXAxis();
        YAxis yAxis = cervicalGraph.getAxisLeft();

        yAxis.setLabelCount(6,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(15);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(0);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        xAxis.setGranularity(1f);


        cervicalGraph.setData(cervicalData);
        cervicalGraph.invalidate();
        execBtn();


        cervicalDataSet.setLineWidth(5);
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
                cervicalData.removeDataSet(cervicalDataSet);
                String value = null;
                int xVal = Integer.parseInt(xInput.getText().toString());
                value = yInput.getText().toString();

                value = valueChecking(value);

                yInput.setText(value);

                int yVal = Integer.parseInt(value);

                if(pointsAdded == false)
                {
                    myHelper.deleteAll("fetal");
                }

                myHelper.insertData(xVal, yVal, "fetalGraph");

                cervicalDataSet.clear();

                savedEntries = getData();
                cervicalDataSet.setValues(getData());
                cervicalDataSet.setLabel("Readings");
                cervicalDataSet.setDrawCircles(true);
                cervicalDataSet.setDrawCircleHole(true);
                cervicalDataSet.setCircleColor(Color.CYAN);
                cervicalDataSet.setCircleRadius(10);
                cervicalDataSet.setCircleHoleRadius(5);

//                FetalDataSet.addEntry(new Entry(xVal,yVal));

//                lineData.clearValues();
//                lineData.addDataSet(lineDataSet1);
//                lineData.addDataSet(lineDataSet2);
                cervicalData.addDataSet(cervicalDataSet);
                cervicalGraph.clear();
                cervicalGraph.setData(cervicalData);
                cervicalGraph.invalidate();

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
                cervicalGraph.clear();
//                lineData.clearValues();
                cervicalData.removeDataSet(cervicalDataSet);
                cervicalGraph.setData(cervicalData);
                cervicalGraph.invalidate();
                myHelper.deleteAll("fetal");
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
        cervicalGraph.clear();

        cervicalData.removeDataSet(cervicalDataSet);
        cervicalGraph.setData(cervicalData);
        cervicalGraph.invalidate();
        myHelper.deleteAll("fetal");
        xInput.setText(Integer.toString(0));
        yInput.setText("");
    }
}
