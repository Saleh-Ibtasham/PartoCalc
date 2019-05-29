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
    LineDataSet lineDataSet = new LineDataSet(null,null);
    LineDataSet lineDataSet1,lineDataSet2;
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    ArrayList<Entry> temp = new ArrayList<>();
    MyHelper myHelper;
    SQLiteDatabase sqLiteDatabase;
    LineChart graph;
    boolean pointsAdded;
    LineData lineData;
    ImageView imageView;
    ArrayList<Entry> savedEntries = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cervix_chart, container, false);
        btn = (Button) view.findViewById(R.id.button);
        btn2 = (Button) view.findViewById(R.id.button2);
        xInput = (EditText) view.findViewById(R.id.editText);
        yInput = (EditText) view.findViewById(R.id.editText2);
        graph = (LineChart) view.findViewById(R.id.graph1);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        xInput.setKeyListener(null);
        yInput.setKeyListener(null);

        myHelper = new MyHelper(getContext());
        sqLiteDatabase = myHelper.getWritableDatabase();


        lineDataSet1 = new LineDataSet(null,null);
        lineDataSet2 = new LineDataSet(null,null);

        dataSets.clear();
        lineDataSet1.addEntry(new Entry(0,4));
        lineDataSet1.addEntry(new Entry(12,10));
        lineDataSet1.setColor(Color.GREEN);
        lineDataSet1.setLineWidth(5);
        lineDataSet1.setLabel("Alert");

        lineDataSet2.addEntry(new Entry(8,4));
        lineDataSet2.addEntry(new Entry(20,10));
        lineDataSet2.setColor(Color.RED);
        lineDataSet2.setLineWidth(5);
        lineDataSet2.setLabel("Action");

        pointsAdded = false;

        xInput.setText(Integer.toString(0));
//        lineDataSet.clear();
//        lineDataSet.setLabel("Readings");

        lineData = new LineData();
        lineData.addDataSet(lineDataSet1);
        lineData.addDataSet(lineDataSet2);

        XAxis xAxis = graph.getXAxis();
        YAxis yAxis = graph.getAxisLeft();

        yAxis.setLabelCount(6,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(15);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(0);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        xAxis.setGranularity(1f);


        graph.setData(lineData);
        graph.invalidate();
        execBtn();


        lineDataSet.setLineWidth(5);
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
                lineData.removeDataSet(lineDataSet);
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

                lineDataSet.clear();

                savedEntries = getData();
                lineDataSet.setValues(getData());
                lineDataSet.setLabel("Readings");
                lineDataSet.setDrawCircles(true);
                lineDataSet.setDrawCircleHole(true);
                lineDataSet.setCircleColor(Color.CYAN);
                lineDataSet.setCircleRadius(10);
                lineDataSet.setCircleHoleRadius(5);

//                lineDataSet.addEntry(new Entry(xVal,yVal));

//                lineData.clearValues();
//                lineData.addDataSet(lineDataSet1);
//                lineData.addDataSet(lineDataSet2);
                lineData.addDataSet(lineDataSet);
                graph.clear();
                graph.setData(lineData);
                graph.invalidate();

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
                graph.clear();
//                lineData.clearValues();
                lineData.removeDataSet(lineDataSet);
                graph.setData(lineData);
                graph.invalidate();
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
        graph.clear();

        lineData.removeDataSet(lineDataSet);
        graph.setData(lineData);
        graph.invalidate();
        myHelper.deleteAll();
        xInput.setText(Integer.toString(0));
        yInput.setText("");
    }
}
