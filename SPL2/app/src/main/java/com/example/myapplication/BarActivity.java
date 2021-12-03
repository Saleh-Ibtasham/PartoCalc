package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class BarActivity extends Fragment {
    private static final String TAG = "Tab1Fragment";


    Button btn,btn2;
    EditText xInput, yInput;
    BarDataSet contractionDataSet;
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    ArrayList<BarEntry> temp = new ArrayList<>();
    MyHelper myHelper;
    SQLiteDatabase sqLiteDatabase;
    BarChart contractionGraph;
    boolean pointsAdded;
    BarData contractionData;
    ImageView imageView;
    ArrayList<BarEntry> savedEntries = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bar_chart,container,false);
        btn = (Button) view.findViewById(R.id.button);
        btn2 = (Button) view.findViewById(R.id.button2);
        xInput = (EditText) view.findViewById(R.id.editText);
        yInput = (EditText) view.findViewById(R.id.editText2);
        contractionGraph = (BarChart) view.findViewById(R.id.graph1);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        xInput.setKeyListener(null);
        yInput.setKeyListener(null);


        myHelper = new MyHelper(getContext());
        sqLiteDatabase = myHelper.getWritableDatabase();


        dataSets.clear();
        pointsAdded = false;

        contractionData = new BarData();
        xInput.setText(Integer.toString(0));
//        FetalDataSet.clear();
//        FetalDataSet.setLabel("Readings");

        execBtn();

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
                contractionData.removeDataSet(contractionDataSet);
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

                savedEntries = getData();
                if(contractionDataSet == null)
                    contractionDataSet = new BarDataSet(getData(),"bardata");
                else{
                    contractionDataSet.clear();
                    contractionDataSet.setValues(getData());
                }

                contractionData.addDataSet(contractionDataSet);
                contractionGraph.clear();
                contractionGraph.setData(contractionData);
                contractionGraph.invalidate();

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
                contractionGraph.clear();
//                lineData.clearValues();
                contractionData.removeDataSet(contractionDataSet);
                contractionGraph.setData(contractionData);
                contractionGraph.invalidate();
                myHelper.deleteAll("fetal");
                xInput.setText(Integer.toString(0));
                yInput.setText("");
            }
        });
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
    private ArrayList<BarEntry> getData() {

        ArrayList<BarEntry> dp = new ArrayList<>();
        String [] columns = {"xValues","yValues"};

        Cursor cursor = sqLiteDatabase.query("MyGraph", columns, null, null, null, null, "xValues ASC");

        for(int i=0; i<cursor.getCount(); i++)
        {
            cursor.moveToNext();
            dp.add(new BarEntry(cursor.getInt(0),cursor.getInt(1)));
        }
        return dp;
    }

    private String valueChecking(String value) {

        if(value.equals("for")||value.equals("four"))
            return "4";
        if(value.equals("sex")|| value.equals("six"))
            return "6";
        return value;
    }
    public ArrayList<BarEntry> saveData(){
        return this.savedEntries;
    }

    public void refreshData(){
        pointsAdded = false;
        contractionGraph.clear();

        contractionData.removeDataSet(contractionDataSet);
        contractionGraph.setData(contractionData);
        contractionGraph.invalidate();
        myHelper.deleteAll("fetal");
        xInput.setText(Integer.toString(0));
        yInput.setText("");
    }

}
