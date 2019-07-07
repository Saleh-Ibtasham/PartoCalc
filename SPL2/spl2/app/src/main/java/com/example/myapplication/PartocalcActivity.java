package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;


public class PartocalcActivity extends AppCompatActivity implements RecognitionListener {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SectionsPageAdapter mSectionsPageAdapter;
    private DroidSpeech droidSpeech;
    private OnDSListener onDSListener;

    private ViewPager mViewPager;
    private Toolbar toolbar;
    private String graphId;
    private Button cont;

    private FirebaseFirestore firebaseFirestore;

    private static final String KWS_SEARCH = "wakeup";
    private static final String MENU_SEARCH = "menu";
    private static final String KEYPHRASE = "hey computer";

    private LineChart fetalGraph, cervicalGraph, maternalGraph;
    private BarChart contractionGraph;

    private LineDataSet fetalDataSet = new LineDataSet(null,null);
    private LineDataSet cervicalDataSet = new LineDataSet(null,null);
//    private BarDataSet contractionDataSet = new BarDataSet(null,null);
    private LineDataSet maternalDataSet = new LineDataSet(null,null);

    private LineDataSet fetalDataSet1,fetalDataSet2,cervicalDataSet1,cervicalDataSet2;

    private LineData fetalData, cervicalData, maternalData;
    private BarData contractionData;

    private int fetalX = 0, fetalY, cervicalX = 0, cervicalY, contractionX = 0, contractionY, maternalX = 0, maternalY;


    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer;

    private TextToSpeech tts;

    private SpeechRecognizer speechRecognizer;

    MyHelper fetalHelper,cervicalHelper,contractionHelper,maternalHelper;
    SQLiteDatabase fetalDB, cervicalDB, contractionDB, maternalDB;

    private boolean fetalPointsAdded = false , cervicalPointsAdded = false, contractionPointsAdded = false, maternalPointsAdded = false;
    private String[] charts = {"fetal heart rate", "cervical", "bar chart", "patient heart rate"};
    private String[] commands = {"input", "remove"};
    private HashMap<String,Chart> chartHashMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_partocalc);

//        graphId = getIntent().getStringExtra("graphId");
//        firebaseFirestore = FirebaseFirestore.getInstance();

        toolbar = (Toolbar) findViewById(R.id.partoToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Graph");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }


        createFetal();
        createCervical();
        createContraction();
        createMaternal();
        createGraphMaps();

//        initializeTextToSpeech();
        runRecognizerSetup();

//        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.container);
//        setupViewPager(mViewPager);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(mViewPager);

//        droidSpeech = new DroidSpeech(this,null);
//        onDSListener = new OnDSListener() {
//            @Override
//            public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages) {
//
//            }
//
//            @Override
//            public void onDroidSpeechRmsChanged(float rmsChangedValue) {
//
//            }
//
//            @Override
//            public void onDroidSpeechLiveResult(String liveSpeechResult) {
//                Toast.makeText(PartocalcActivity.this,liveSpeechResult,Toast.LENGTH_LONG).show();
//                processResult(liveSpeechResult);
//            }
//
//            @Override
//            public void onDroidSpeechFinalResult(String finalSpeechResult) {
//                Toast.makeText(PartocalcActivity.this,finalSpeechResult,Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onDroidSpeechClosedByUser() {
//
//            }
//
//            @Override
//            public void onDroidSpeechError(String errorMsg) {
//                Toast.makeText(PartocalcActivity.this,errorMsg,Toast.LENGTH_LONG).show();
//            }
//        };
//
//        droidSpeech.setOnDroidSpeechListener(onDSListener);
//        droidSpeech.setContinuousSpeechRecognition(true);
//
//        mViewPager.setCurrentItem(1);
//
//        initializeTextToSpeech();
//
//        droidSpeech.startDroidSpeechRecognition();
//        initializeSpeechRecognizer();
//
//        openSpeechRecog();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                openSpeechRecog();
//            }
//        }, 2000);
//        cont.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openSpeechRecog();
//            }
//        });
    }

    private void createGraphMaps() {
        chartHashMap = new HashMap<>();
        chartHashMap.put(charts[0],fetalGraph);
        chartHashMap.put(charts[1],cervicalGraph);
        chartHashMap.put(charts[2],contractionGraph);
        chartHashMap.put(charts[3],maternalGraph);
    }

    private void createFetal() {
        fetalGraph = findViewById(R.id.fetal_graph);

        fetalHelper = new MyHelper(getApplicationContext());
        fetalDB = fetalHelper.getWritableDatabase();

        fetalDataSet1 = new LineDataSet(null,null);
        fetalDataSet2 = new LineDataSet(null,null);

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

        fetalData = new LineData();
        fetalData.addDataSet(fetalDataSet1);
        fetalData.addDataSet(fetalDataSet2);

        XAxis xAxis = fetalGraph.getXAxis();
        YAxis yAxis = fetalGraph.getAxisLeft();
        YAxis yAxis2 = fetalGraph.getAxisRight();

        yAxis.setLabelCount(13,true);
        yAxis2.setLabelCount(13,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(200);
        yAxis2.setAxisMaximum(200);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(80);
        yAxis2.setAxisMaximum(80);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        yAxis2.setGranularity(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        fetalGraph.setData(fetalData);
        fetalGraph.invalidate();
    }

    private void createCervical(){
        cervicalGraph = findViewById(R.id.cervical_graph);

        cervicalHelper = new MyHelper(getApplicationContext());
        cervicalDB = cervicalHelper.getWritableDatabase();

        cervicalDataSet1 = new LineDataSet(null,null);
        cervicalDataSet2 = new LineDataSet(null,null);

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

        cervicalData = new LineData();
        cervicalData.addDataSet(cervicalDataSet1);
        cervicalData.addDataSet(cervicalDataSet2);

        XAxis xAxis = cervicalGraph.getXAxis();
        YAxis yAxis = cervicalGraph.getAxisLeft();
        YAxis yAxis2 = cervicalGraph.getAxisRight();

        yAxis.setLabelCount(13,true);
        yAxis2.setLabelCount(13,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(12);
        yAxis2.setAxisMaximum(12);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(0);
        yAxis2.setAxisMinimum(0);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        yAxis2.setGranularity(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        cervicalGraph.setData(cervicalData);
        cervicalGraph.invalidate();
    }

    private void createContraction(){
        contractionGraph = findViewById(R.id.contraction_graph);

        contractionHelper = new MyHelper(getApplicationContext());
        contractionDB = contractionHelper.getWritableDatabase();

        contractionData = new BarData();

        XAxis xAxis = contractionGraph.getXAxis();
        YAxis yAxis = contractionGraph.getAxisLeft();

        yAxis.setLabelCount(6,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(5);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(0);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        contractionGraph.setData(contractionData);
        contractionGraph.invalidate();
    }

    private void createMaternal(){
        maternalGraph = findViewById(R.id.maternal_graph);

        maternalHelper = new MyHelper(getApplicationContext());
        maternalDB = maternalHelper.getWritableDatabase();

        maternalData = new LineData();

        XAxis xAxis = maternalGraph.getXAxis();
        YAxis yAxis = maternalGraph.getAxisLeft();
        YAxis yAxis2 = maternalGraph.getAxisRight();

        yAxis.setLabelCount(13,true);
        yAxis2.setLabelCount(13,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(180);
        yAxis2.setAxisMaximum(180);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(60);
        yAxis2.setAxisMinimum(60);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        yAxis2.setGranularity(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        maternalGraph.setData(maternalData);
        maternalGraph.invalidate();

    }

    private void runRecognizerSetup() {
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(PartocalcActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }
            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    System.out.println(result.getMessage());
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
//                new SetupTask(this).execute();
                runRecognizerSetup();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
//        recognizer.getDecoder().hyp().getHypstr();
        String text = hypothesis.getHypstr();
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

        if (text.equals(KEYPHRASE))
            switchSearch(MENU_SEARCH);
        else
            Toast.makeText(getApplicationContext(), hypothesis.getHypstr(), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onResult(Hypothesis hypothesis) {
//        ((TextView) findViewById(R.id.result_text)).setText(hypothesis.getHypstr());
        if (hypothesis != null) {
            Toast.makeText(getApplicationContext(), hypothesis.getHypstr(), Toast.LENGTH_LONG).show();
            if(hypothesis.getHypstr().contains("input")){
                String s = getChart(hypothesis.getHypstr());
                String s2 = getNumber(hypothesis.getHypstr());
                int yVal = convertWordsToNum(s2);
                if(s.equals(charts[0])){
                    updateLineChart(fetalData,fetalHelper,fetalDataSet,fetalGraph,yVal, fetalX, fetalPointsAdded, fetalDB);
                }
                else if(s.equals(charts[1])){
                    updateLineChart(cervicalData,cervicalHelper,cervicalDataSet,cervicalGraph,yVal, cervicalX, cervicalPointsAdded, cervicalDB);
                }
//                else if(s.equals(charts[2])){
//                    updateBarChart(contractionData,contractionHelper,contractionDataSet,contractionGraph,yVal, contractionX, contractionPointsAdded, contractionDB);
//                }
                else if(s.equals(charts[3])){
                    updateLineChart(maternalData,maternalHelper,maternalDataSet,maternalGraph,yVal, maternalX, maternalPointsAdded, maternalDB);
                }

            }
        }
    }

    private void updateBarChart(BarData barData, MyHelper myHelper, BarDataSet barDataSet, BarChart barChart, int yVal, int xVal, boolean pointsAdded, SQLiteDatabase sqLiteDatabase) {
        barData.removeDataSet(barDataSet);

        if(pointsAdded == false)
        {
            myHelper.deleteAll();
        }

        myHelper.insertData(xVal, yVal);

        barDataSet.setValues(getBarData(sqLiteDatabase));

        barData.addDataSet(barDataSet);
        barChart.clear();
        barChart.setData(contractionData);
        barChart.invalidate();

        pointsAdded = true;

        xVal += 4;
    }

    private List<BarEntry> getBarData(SQLiteDatabase sqLiteDatabase) {
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

    private String getNumber(String hypstr) {
        String ans = null;
        for(String s : commands){
            ans = hypstr.replace(s,"");
        }
        for(String s : charts){
            ans = ans.replace(s,"");
        }
        String result = ans.trim();
        return result;
    }

    private int convertWordsToNum(String s) {

        String[] words = s.split("\\s");
        int finalResult = 0;
        int intermediateResult = 0;
        for (String str : words) {
            // clean up string for easier processing
            str = str.toLowerCase().replaceAll("[^a-zA-Z\\s]", "");
            if (str.equalsIgnoreCase("zero")) {
                intermediateResult += 0;
            } else if (str.equalsIgnoreCase("one")) {
                intermediateResult += 1;
            } else if (str.equalsIgnoreCase("two")) {
                intermediateResult += 2;
            } else if (str.equalsIgnoreCase("three")) {
                intermediateResult += 3;
            } else if (str.equalsIgnoreCase("four")) {
                intermediateResult += 4;
            } else if (str.equalsIgnoreCase("five")) {
                intermediateResult += 5;
            } else if (str.equalsIgnoreCase("six")) {
                intermediateResult += 6;
            } else if (str.equalsIgnoreCase("seven")) {
                intermediateResult += 7;
            } else if (str.equalsIgnoreCase("eight")) {
                intermediateResult += 8;
            } else if (str.equalsIgnoreCase("nine")) {
                intermediateResult += 9;
            } else if (str.equalsIgnoreCase("ten")) {
                intermediateResult += 10;
            } else if (str.equalsIgnoreCase("eleven")) {
                intermediateResult += 11;
            } else if (str.equalsIgnoreCase("twelve")) {
                intermediateResult += 12;
            } else if (str.equalsIgnoreCase("thirteen")) {
                intermediateResult += 13;
            } else if (str.equalsIgnoreCase("fourteen")) {
                intermediateResult += 14;
            } else if (str.equalsIgnoreCase("fifteen")) {
                intermediateResult += 15;
            } else if (str.equalsIgnoreCase("sixteen")) {
                intermediateResult += 16;
            } else if (str.equalsIgnoreCase("seventeen")) {
                intermediateResult += 17;
            } else if (str.equalsIgnoreCase("eighteen")) {
                intermediateResult += 18;
            } else if (str.equalsIgnoreCase("nineteen")) {
                intermediateResult += 19;
            } else if (str.equalsIgnoreCase("twenty")) {
                intermediateResult += 20;
            } else if (str.equalsIgnoreCase("thirty")) {
                intermediateResult += 30;
            } else if (str.equalsIgnoreCase("forty")) {
                intermediateResult += 40;
            } else if (str.equalsIgnoreCase("fifty")) {
                intermediateResult += 50;
            } else if (str.equalsIgnoreCase("sixty")) {
                intermediateResult += 60;
            } else if (str.equalsIgnoreCase("seventy")) {
                intermediateResult += 70;
            } else if (str.equalsIgnoreCase("eighty")) {
                intermediateResult += 80;
            } else if (str.equalsIgnoreCase("ninety")) {
                intermediateResult += 90;
            } else if (str.equalsIgnoreCase("hundred")) {
                intermediateResult *= 100;
            } else if (str.equalsIgnoreCase("thousand")) {
                intermediateResult *= 1000;
                finalResult += intermediateResult;
                intermediateResult = 0;
            } else if (str.equalsIgnoreCase("million")) {
                intermediateResult *= 1000000;
                finalResult += intermediateResult;
                intermediateResult = 0;
            } else if (str.equalsIgnoreCase("billion")) {
                intermediateResult *= 1000000000;
                finalResult += intermediateResult;
                intermediateResult = 0;
            } else if (str.equalsIgnoreCase("trillion")) {
                intermediateResult *= 1000000000000L;
                finalResult += intermediateResult;
                intermediateResult = 0;
            }
        }

        finalResult += intermediateResult;
        intermediateResult = 0;
        return finalResult;
    }

    private void updateLineChart(LineData lineData, MyHelper myHelper, LineDataSet lineDataSet, LineChart lineChart, int yVal, int xVal, boolean pointsAdded, SQLiteDatabase sqLiteDatabase) {
        lineData.removeDataSet(lineDataSet);

        myHelper.insertData(xVal, yVal);

        lineDataSet.clear();

        lineDataSet.setValues(getLineData(sqLiteDatabase));
        lineDataSet.setLabel("Readings");
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleColor(Color.CYAN);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(3);

        lineData.addDataSet(lineDataSet);
        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.invalidate();

        pointsAdded = true;

        xVal += 4;
    }

    private ArrayList<Entry> getLineData(SQLiteDatabase sqLiteDatabase) {
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


    private String getChart(String hypstr) {
        String ans = null;
        for(String s: charts){
            if(hypstr.contains(s)){
                ans = s;
                return ans;
            }
        }
        return ans;
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
//        recognizer.getDecoder().hyp().getHypstr();
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 20000);

    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

//                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // Create grammar-based search for selection between demos
//        File menuGrammar = new File(assetsDir, "menu.gram");
//        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

//        // Create grammar-based search for digit recognition
//        File digitsGrammar = new File(assetsDir, "digits.gram");
//        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
//
//        // Create language model search
//        File languageModel = new File(assetsDir, "weather.dmp");
//        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
//
//        // Phonetic search
//        File phoneticModel = new File(assetsDir, "en-phone.dmp");
//        recognizer.addAllphoneSearch(PHONE_SEARCH, phoneticModel);

//        recognizer.addNgramSearch(MENU_SEARCH, new File(assetsDir, "en-us.lm.bin"));

        recognizer.addGrammarSearch(MENU_SEARCH, new File(assetsDir, "menu.gram"));
    }

    @Override
    public void onError(Exception error) {

    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }

//    private void processResult(String result_message) {
//        result_message = result_message.toLowerCase();
//
//        Toast.makeText(PartocalcActivity.this, result_message, Toast.LENGTH_LONG).show();
//
////        Handle at least four sample cases
//
////        First: What is your Name?
////        Second: What is the time?
////        Third: Is the earth flat or a sphere?
////        Fourth: Open a browser and open url
//        if(result_message.indexOf("what") != -1){
//            if(result_message.indexOf("your name") != -1){
//                speak("My Name is Mr.Android. Nice to meet you!");
//            }
//            if (result_message.indexOf("time") != -1){
//                speak("The time is now: " );
//            }
//        } else if (result_message.indexOf("earth") != -1){
//            speak("Don't be silly, The earth is a sphere. As are all other planets and celestial bodies");
//        } else if (result_message.indexOf("browser") != -1){
//            speak("Opening a browser right away master.");
//        }
//    }

    private void initializeTextToSpeech() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (tts.getEngines().size() == 0 ){
                    Toast.makeText(PartocalcActivity.this, "There were no Text to Speech Engines located in the device",Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    tts.setLanguage(Locale.US);
                    speak("Hello there, I am ready to start our conversation");
                }
            }
        });
    }

    private void speak(String message) {
        if(Build.VERSION.SDK_INT >= 21){
            tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH,null);
        }
    }





//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.partocalc_activity,container,false);
//
//        mSectionsPageAdapter = new SectionsPageAdapter(getActivity().getSupportFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) view.findViewById(R.id.container);
//        setupViewPager(mViewPager);
//
//        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(mViewPager);
//        return view;
//    }

//    private void setupViewPager(ViewPager viewPager) {
//        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
//        adapter.addFragment(new CervicalActivity(), "Cervical Dialatation");
//        adapter.addFragment(new BarActivity(), "Contractions");
//        adapter.addFragment(new FetalHeartRate(),"Fetal Heart Rate");
//
//        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(2);
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.partocalc_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            onBackPressed();
            finish();
            return true;
        }
        if (id == R.id.save_graphs) {
            saveGraphs();
            return true;
        }
        else if (id == R.id.refresh_grpahs){
            refreshGraphs();
            return true;
        }
        else if( id == R.id.delete_graphs){
            deleteGraphs();
            return true;
        }

        return true;
    }

    private void deleteGraphs() {
        firebaseFirestore.collection("graphs").document(graphId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(PartocalcActivity.this,"Graphs have been deleted",Toast.LENGTH_LONG).show();
                    deletePatient();
                }
            }
        });
    }

    private void deletePatient() {
        firebaseFirestore.collection("patients").document(graphId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(PartocalcActivity.this,"Patient entry has also been deleted",Toast.LENGTH_LONG).show();
                    onBackPressed();
                    finish();
                }
            }
        });
    }

    private void refreshGraphs() {

        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for(Fragment f: fragments){
            if(f instanceof CervicalActivity){
                ((CervicalActivity) f).refreshData();
            }
            else if(f instanceof BarActivity){
                ((BarActivity) f).refreshData();
            }
            else if(f instanceof FetalHeartRate){
                ((FetalHeartRate) f).refreshData();
            }
        }
    }

    private void saveGraphs() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        Map<String , Object> graphMap = new HashMap<>();
        for(Fragment f: fragments){
            if(f instanceof CervicalActivity){
                graphMap.put("cervicalData",((CervicalActivity) f).saveData());
            }
            else if(f instanceof BarActivity){
                graphMap.put("contractions",((BarActivity) f).saveData());
            }
            else if(f instanceof FetalHeartRate){
                graphMap.put("fetalRate",((FetalHeartRate) f).saveData());
            }
        }
        storeFirestore(graphMap);
    }

    private void storeFirestore(Map<String, Object> graphMap) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null)
        {
            firebaseFirestore.collection("graphs").document(graphId).update(graphMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(PartocalcActivity.this,"Graph data added",Toast.LENGTH_LONG).show();
                        onBackPressed();
                        finish();
                    }
                }
            });

        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (recognizer != null) {
//            recognizer.cancel();
//            recognizer.shutdown();
//        }
//        tts.shutdown();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        runRecognizerSetup();
//        initializeTextToSpeech();
//    }

//    private void regMatcher(){
//        final String regex = "^(input|remove)\\s(cervical|barchart)\\s?(twenty|thirty)?\\s?(one|two)?$";
//        final String string = "remove barchart\n";
//
//        final Pattern pattern = Pattern.compile(regex);
//        final Matcher matcher = pattern.matcher(string);
//
//        while (matcher.find()) {
//            System.out.println("Full match: " + matcher.group(0));
//            for (int i = 1; i <= matcher.groupCount(); i++) {
//                System.out.println("Group " + i + ": " + matcher.group(i));
//            }
//        }
//    }

}
