package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MicrophoneInfo;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.speech.RecognitionListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Soundex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pocketsphinx.SpeechRecognizer;

import static android.speech.RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS;

public class TestingPartograph extends AppCompatActivity implements RecognitionListener {
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private int SCREEN_HEIGHT;
    private int SCREEN_WIDTH;

    private SectionsPageAdapter mSectionsPageAdapter;
    private DroidSpeech droidSpeech;
    private OnDSListener onDSListener;

    private ViewPager mViewPager;
    private Toolbar toolbar;
    private String graphId;
    private Button cont;
    private ScrollView scrollView,scrollViewB;

    private FirebaseFirestore firebaseFirestore;

    private static final String KWS_SEARCH = "wakeup";
    private static final String MENU_SEARCH = "menu";
    private static final String KEYPHRASE = "hey mobile";

    private LineChart fetalGraph, cervicalGraph, maternalGraph;
    private BarChart contractionGraph;
    private TableLayout fluid,time,oxytocin,medicine,temperature,urine;

    private int tableColumnCount;

//    private List<RowHeader> liquorRowHeaderList;
//    private List<>;

    private LineDataSet fetalDataSet = new LineDataSet(null,null);
    private LineDataSet cervicalDataSet = new LineDataSet(null,"cervical");
    private LineDataSet descendDataSet = new LineDataSet(null,"head descend");
    private BarDataSet contractionDataSet;
    private LineDataSet maternalDataSet = new LineDataSet(null,null);

    private ArrayList<ILineDataSet> cervicalDataSets = new ArrayList<>();
    private ArrayList<Entry> cervicalList = new ArrayList<>();
    private ArrayList<Entry> descendList = new ArrayList<>();

    private LineDataSet fetalDataSet1,fetalDataSet2,cervicalDataSet1,cervicalDataSet2;

    private LineData fetalData, cervicalData, maternalData, descendData;
    private BarData contractionData;

    private int fetalX = 0, fetalY, cervicalX = 1, cervicalY, contractionX = 0, contractionY, maternalX = 0, maternalY, descendX = 1;
    private int fluidX=0, mouldingX=0, oxyAmX = 0, oxyDrX = 0, tempX=0, proteanX =0, acetoneX=0, amountX = 0;


    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer;

    private TextToSpeech tts;

    private SpeechRecognizer speechRecognizer;

    private MediaPlayer okSound, invalidSound;

    MyHelper fetalHelper,cervicalHelper,contractionHelper,maternalHelper,descendHelper,myHelper;
    SQLiteDatabase fetalDB, cervicalDB, contractionDB, maternalDB, descendDB,sqLiteDatabase;

    private boolean fetalPointsAdded = false , cervicalPointsAdded = false, contractionPointsAdded = false, maternalPointsAdded = false, descendPointAdded = false;
//    private String[] charts = {"fetal heart rate", "cervical dilatation", "contraction", "maternal pulse", "head descend five by","amniotic fluid","moulding","oxytocin amount", "oxytocin drops","temperature","protean","acetone","amount"};
    private String[] charts = {"fetal", "cervical", "contraction", "madonna", "descend","fluid","morning","temperature","protein","acetone","everton","urine"};
    private String[] commands = {"input", "delete"};
    private HashMap<String, Chart> chartHashMap;
    private int[] counters = {0,0,0,0};
    private String[] xAxisCervical = new String[24];

    private String[] tens = {"twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};
    private String[] hundreds = {"hundred"};
    private String[] digits = {"zero", "one", "two", "three", "four", "five", "six",
            "seven", "eight", "nine", "ten"};
    private String[] digitsPlus = {"absent", "one", "two", "three"};
    private String[] teens = {"eleven", "twelve", "thirteen", "fourteen", "fifteen",
            "sixteen", "seventeen", "eighteen", "nineteen"};

    private List<Integer> barColors = new ArrayList<>();

    private BluetoothHeadset bluetoothHeadset;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothProfile.ServiceListener profileListener;

    private BluetoothDevice btDevice;

    private RelativeLayout fluidLayout,timeLayout,oxytocinLayout,tempLayout,urineLayout;
    private LinearLayout medicineLayout;

    private TableRow tableRow, fluidRow;
    private AudioRecord audioRecord;
    private List<MicrophoneInfo> microphoneInfos = new ArrayList<>();

    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private android.speech.SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private String context = "command";
    private AudioManager am;

    private String graphingChart=null;
    private int selectedChart=-1;

    private void resetSpeechRecognizer() {

        if(speech != null)
            speech.destroy();
        speech = android.speech.SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + android.speech.SpeechRecognizer.isRecognitionAvailable(this));
        if(android.speech.SpeechRecognizer.isRecognitionAvailable(this))
            speech.setRecognitionListener(this);
        else
            finish();
    }

    private void setRecogniserIntent() {

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en-IN");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,7000);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,7000);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,7000);
    }



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

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        scrollView = findViewById(R.id.contents);
        scrollViewB = findViewById(R.id.scrollViewB);

        fluidLayout = findViewById(R.id.fluid_container);
        timeLayout = findViewById(R.id.time_container);
        oxytocinLayout = findViewById(R.id.oxytocin_container);
        medicineLayout = findViewById(R.id.medicine_container);
        tempLayout = findViewById(R.id.temp_container);
        urineLayout = findViewById(R.id.urine_container);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int x = scrollView.getScrollX();
                int y = scrollView.getScrollY();
                scrollViewB.scrollTo(x, y);
            }
        });

        scrollViewB.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int x = scrollViewB.getScrollX();
                int y = scrollViewB.getScrollY();
                scrollView.scrollTo(x, y);
            }
        });

        setupBluetooth();
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, 2024);
        int x = audioRecord.getAudioSessionId();

        NoiseSuppressor.create(x);
        Log.i("audioSession", "AfterBluetooth: " + x);
        getScreenDimension();

        createHelper();
        createFetal();
        createCervical();
        createContraction();
        createMaternal();
        craeteTables();
        createGraphMaps();
//        clearDatabase();

        okSound = MediaPlayer.create(getApplicationContext(), R.raw.right);
        invalidSound = MediaPlayer.create(getApplicationContext(), R.raw.case_closed);

        resetSpeechRecognizer();
        initializeTextToSpeech();
        setRecogniserIntent();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        am.setStreamMute(AudioManager.STREAM_MUSIC,true);

        speech.startListening(recognizerIntent);
    }

    private void createHelper() {
        myHelper = new MyHelper(getApplicationContext());
        sqLiteDatabase = myHelper.getWritableDatabase();
        myHelper.onCreate(sqLiteDatabase);
    }

    private void getScreenDimension() {

        WindowManager wm= (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH= size.x;
        SCREEN_HEIGHT = size.y;

        Log.i("screent width", "getScreenDimension: " + SCREEN_WIDTH + " " + SCREEN_HEIGHT);
    }

    private void craeteTables() {

        createFluid();
        createTime();
        createOxytocin();
        createMedicine();
        createTemp();
        createUrine();
    }

    private void createFluid() {
        fluid = new TableLayout(getApplicationContext());
        fluid.setPadding(0, 0 ,0 ,0);
        fluid.setLayoutParams(new TableLayout.LayoutParams(fluidLayout.getWidth(), SCREEN_HEIGHT/10));

        fluidLayout.addView(fluid);

        for(int i=0; i< 2; i++){
            initializeFluid(i);
        }

        for(int i=0; i<2; i++){
            for(int j=0; j<24; j++){
                addColumnsToFluid(i);
            }
        }
    }

    private synchronized void initializeFluid(int pos) {
        TableRow fluidRow= new TableRow(getApplicationContext());
        fluidRow.setPadding(0,0,0,0);
        fluidRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, SCREEN_HEIGHT/20));
        this.fluid.addView(fluidRow,pos);
    }

    private synchronized void addColumnsToFluid(int id) {
        TableRow tableAdd = (TableRow) fluid.getChildAt(id);
        tableRow= new TableRow(getApplicationContext());
        Log.i("Fluid", "Fluid Width: " + fluid.getWidth());
        TableRow.LayoutParams layoutParamsTableRow= new TableRow.LayoutParams(27, SCREEN_HEIGHT/22);
        tableRow.setPadding(3,3,3,3);
        tableRow.setBackground(getDrawable(R.drawable.cell_bacground));
        tableRow.setLayoutParams(layoutParamsTableRow);
        TextView label_date = new TextView(getApplicationContext());
        label_date.setText("");
        label_date.setTextSize(12);
        this.tableRow.addView(label_date);
        this.tableRow.setTag("yolo");
        tableAdd.addView(tableRow);
        tableColumnCount++;
    }

    private void createTime(){
        time = new TableLayout(getApplicationContext());
        time.setPadding(0, 0 ,0 ,0);
        time.setLayoutParams(new TableLayout.LayoutParams(timeLayout.getWidth(), SCREEN_HEIGHT/6));

        timeLayout.addView(time);

        for(int i=0; i< 2; i++){
            initializeTime(i);
        }

        for(int i=0; i<2; i++){
            for(int j=0; j<12; j++){
                addColumnsToTime(i);
            }
        }
    }

    private void addColumnsToTime(int id) {
        TableRow tableAdd = (TableRow) time.getChildAt(id);
        tableRow= new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow;
        if(id == 0)
        {
            layoutParamsTableRow= new TableRow.LayoutParams(54, SCREEN_HEIGHT/18);
        }
        else{
            layoutParamsTableRow= new TableRow.LayoutParams(54, SCREEN_HEIGHT/12);
        }

        tableRow.setPadding(3,3,3,3);
        tableRow.setBackground(getDrawable(R.drawable.cell_bacground));
        tableRow.setLayoutParams(layoutParamsTableRow);
        TextView label_date = new TextView(getApplicationContext());
        label_date.setText("10:30");
        label_date.setTextSize(10);
        this.tableRow.addView(label_date);
        this.tableRow.setTag("yolo");
        tableAdd.addView(tableRow);
    }

    private void initializeTime(int pos) {
        TableRow timeRow = new TableRow(getApplicationContext());
        timeRow.setPadding(0,0,0,0);
        timeRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, SCREEN_HEIGHT/10));
        this.time.addView(timeRow,pos);
    }

    private void createOxytocin(){
        oxytocin = new TableLayout(getApplicationContext());
        oxytocin.setPadding(0, 0 ,0 ,0);
        oxytocin.setLayoutParams(new TableLayout.LayoutParams(oxytocinLayout.getWidth(), SCREEN_HEIGHT/10));

        oxytocinLayout.addView(oxytocin);

        for(int i=0; i< 2; i++){
            initializeOxytocin(i);
        }

        for(int i=0; i<2; i++){
            for(int j=0; j<24; j++){
                addColumnsToOxytocin(i);
            }
        }
    }

    private void addColumnsToOxytocin(int id) {
        TableRow tableAdd = (TableRow) oxytocin.getChildAt(id);
        tableRow= new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow;
        layoutParamsTableRow= new TableRow.LayoutParams(27, SCREEN_HEIGHT/20);
        tableRow.setPadding(3,3,3,3);
        tableRow.setBackground(getDrawable(R.drawable.cell_bacground));
        tableRow.setLayoutParams(layoutParamsTableRow);
        TextView label_date = new TextView(getApplicationContext());
        label_date.setText("");
        label_date.setTextSize(10);
        this.tableRow.addView(label_date);
        this.tableRow.setTag("yolo");
        tableAdd.addView(tableRow);
    }


    private void initializeOxytocin(int pos) {
        TableRow oxytocinRow = new TableRow(getApplicationContext());
        oxytocinRow.setPadding(0,0,0,0);
        oxytocinRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, SCREEN_HEIGHT/10));
        this.oxytocin.addView(oxytocinRow,pos);
    }


    private void createMedicine(){

        for(int j=0; j<12; j++){
            addColumnsToMedicine(0);
        }

        medicineLayout.setRotation(-90);

    }

    private void addColumnsToMedicine(int id) {
        EditText label_date = new EditText(getApplicationContext());
        label_date.setTextSize(getResources().getDimension(R.dimen.cell_text_size));
        label_date.setWidth(SCREEN_HEIGHT/3);
        label_date.setHeight(55);
        label_date.setText("");
        label_date.setPadding(10,0,0,0);
        label_date.setBackground(getDrawable(R.drawable.cell_bacground));
        medicineLayout.addView(label_date);
    }

    private void createTemp(){
        temperature = new TableLayout(getApplicationContext());
        temperature.setPadding(0, 0 ,0 ,0);
        temperature.setLayoutParams(new TableLayout.LayoutParams(tempLayout.getWidth(), SCREEN_HEIGHT/20));

        tempLayout.addView(temperature);

        initializeTemperature(0);

        for(int j=0; j<12; j++){
            addColumnsToTemperature(0);
        }
    }

    private void addColumnsToTemperature(int id) {
        TableRow tableAdd = (TableRow) temperature.getChildAt(id);
        tableRow= new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow;
        layoutParamsTableRow= new TableRow.LayoutParams(53, SCREEN_HEIGHT/20);
        tableRow.setPadding(3,3,3,3);
        tableRow.setBackground(getDrawable(R.drawable.cell_bacground));
        tableRow.setLayoutParams(layoutParamsTableRow);
        TextView label_date = new TextView(getApplicationContext());
        label_date.setText("");
        label_date.setTextSize(10);
        this.tableRow.addView(label_date);
        this.tableRow.setTag("yolo");
        tableAdd.addView(tableRow);
    }

    private void initializeTemperature(int pos) {

        TableRow tempRow = new TableRow(getApplicationContext());
        tempRow.setPadding(0,0,0,0);
        tempRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, SCREEN_HEIGHT/20));
        this.temperature.addView(tempRow,pos);
    }

    private void createUrine(){
        urine = new TableLayout(getApplicationContext());
        urine.setPadding(0, 0 ,0 ,0);
        urine.setLayoutParams(new TableLayout.LayoutParams(urineLayout.getWidth(), SCREEN_HEIGHT/5));

        urineLayout.addView(urine);

        for(int i=0; i< 3; i++){
            initializeUrine(i);
        }

        for(int i=0; i<3; i++){
            for(int j=0; j<12; j++){
                addColumnsToUrine(i);
            }
        }
    }

    private void addColumnsToUrine(int id) {
        TableRow tableAdd = (TableRow) urine.getChildAt(id);
        tableRow= new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow;
        layoutParamsTableRow= new TableRow.LayoutParams(53, SCREEN_HEIGHT/20);
        tableRow.setPadding(3,3,3,3);
        tableRow.setBackground(getDrawable(R.drawable.cell_bacground));
        tableRow.setLayoutParams(layoutParamsTableRow);
        TextView label_date = new TextView(getApplicationContext());
        label_date.setText("");
        label_date.setTextSize(12);
        this.tableRow.addView(label_date);
        this.tableRow.setTag("yolo");
        tableAdd.addView(tableRow);
    }

    private void initializeUrine(int pos) {
        TableRow urineRow = new TableRow(getApplicationContext());
        urineRow.setPadding(0,0,0,0);
        urineRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, SCREEN_HEIGHT/20));
        this.urine.addView(urineRow,pos);
    }

    private void setupBluetooth() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        profileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HEADSET) {
                    bluetoothHeadset = (BluetoothHeadset) proxy;
                }
                Log.d("Bluetooth connected", "onServiceConnected: here");

                List<BluetoothDevice> devices = bluetoothHeadset.getConnectedDevices();
                if(devices.size() != 1){
                    Toast.makeText(getApplicationContext(),"Please connect only 1 bluetooth headset",Toast.LENGTH_LONG).show();
                    return;
                }
                btDevice = devices.get(0);

                bluetoothHeadset.startVoiceRecognition(btDevice);

                if(bluetoothHeadset.isAudioConnected(btDevice)){
                    Toast.makeText(getApplicationContext(),"Bluetooth Audio is connected",Toast.LENGTH_LONG).show();
                }
            }
            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.HEADSET) {
                    bluetoothHeadset = null;
                }
                Log.d("Bluetooth disconnected", "onServiceDisconnected: here");
            }
        };

        if(bluetoothAdapter.getProfileProxy(getApplicationContext(), profileListener, BluetoothProfile.HEADSET)){
            Log.d("profile proxy", "setupBluetooth: enabled");
        }

    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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

//        fetalHelper = new MyHelper(getApplicationContext());
//        fetalDB = fetalHelper.getWritableDatabase();

        fetalDataSet1 = new LineDataSet(null,null);
        fetalDataSet2 = new LineDataSet(null,null);

        fetalDataSet1.addEntry(new Entry(0,120));
        fetalDataSet1.addEntry(new Entry(24,120));
        fetalDataSet1.setColor(Color.GRAY);
        fetalDataSet1.setLineWidth(5);
        fetalDataSet1.setLabel("lower-limit");

        fetalDataSet2.addEntry(new Entry(0,160));
        fetalDataSet2.addEntry(new Entry(24,160));
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
        yAxis2.setAxisMinimum(80);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        yAxis2.setGranularity(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        yAxis2.setDrawLabels(false);

        fetalGraph.setData(fetalData);
        fetalGraph.invalidate();
    }

    private void createCervical(){
        cervicalGraph = findViewById(R.id.cervical_graph);

//        cervicalHelper = new MyHelper(getApplicationContext());
//        cervicalDB = cervicalHelper.getWritableDatabase();

//        descendHelper = new MyHelper(getApplicationContext());
//        descendDB = descendHelper.getWritableDatabase();

        cervicalDataSet1 = new LineDataSet(null,null);
        cervicalDataSet2 = new LineDataSet(null,null);

        initializeXAxisCervical();

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

        cervicalDataSets.add(cervicalDataSet1);
        cervicalDataSets.add(cervicalDataSet2);

        cervicalData = new LineData(cervicalDataSets);
        descendData = new LineData();
//        cervicalData.addDataSet(cervicalDataSet1);
//        cervicalData.addDataSet(cervicalDataSet2);

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
        yAxis2.setDrawLabels(false);

        cervicalGraph.setData(cervicalData);
        cervicalGraph.invalidate();
    }

    private void initializeXAxisCervical() {
        int x =1;
        for(int i=0; i<24; i++){
            xAxisCervical[i] = Integer.toString(x);
            x++;
        }
    }

    private void createContraction(){
        contractionGraph = findViewById(R.id.contraction_graph);

//        contractionHelper = new MyHelper(getApplicationContext());
//        contractionDB = contractionHelper.getWritableDatabase();

        contractionData = new BarData();

        XAxis xAxis = contractionGraph.getXAxis();
        YAxis yAxis = contractionGraph.getAxisLeft();
        YAxis yAxis2 = contractionGraph.getAxisRight();

        yAxis.setLabelCount(6,true);
        yAxis2.setLabelCount(6,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(5);
        yAxis2.setAxisMaximum(5);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(0);
        yAxis2.setAxisMinimum(0);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        yAxis2.setGranularity(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        yAxis2.setDrawLabels(false);

        contractionGraph.setData(contractionData);
        contractionGraph.invalidate();
    }

    private void createMaternal(){
        maternalGraph = findViewById(R.id.maternal_graph);

//        maternalHelper = new MyHelper(getApplicationContext());
//        maternalDB = maternalHelper.getWritableDatabase();

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
        yAxis2.setDrawLabels(false);

        maternalGraph.setData(maternalData);
        maternalGraph.invalidate();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speech.startListening(recognizerIntent);
            } else {
                Toast.makeText(TestingPartograph.this, "Permission Denied!", Toast
                        .LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void initializeTextToSpeech() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (tts.getEngines().size() == 0 ){
                    Toast.makeText(TestingPartograph.this, "There were no Text to Speech Engines located in the device",Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    tts.setLanguage(Locale.US);
                    tts.setSpeechRate(1f);
                    speak("Hello there, I am ready to start our conversation");;
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
//            saveGraphs();
            return true;
        }
        else if (id == R.id.refresh_grpahs){
//            refreshGraphs();
            return true;
        }
        else if( id == R.id.delete_graphs){
//            deleteGraphs();
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
                    Toast.makeText(TestingPartograph.this,"Graphs have been deleted",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(TestingPartograph.this,"Patient entry has also been deleted",Toast.LENGTH_LONG).show();
                    onBackPressed();
                    finish();
                }
            }
        });
    }


    private void updateChart12(int yVal) {
        tableRow = (TableRow) urine.getChildAt(2);

        TableRow temp = (TableRow) tableRow.getChildAt(amountX);
        TextView tempText = (TextView) temp.getChildAt(0);
        tempText.setText(Integer.toString(yVal));

        amountX++;
        okSound.start();
    }

    private void updateChart11(String yVal) {
        tableRow = (TableRow) urine.getChildAt(1);

        TableRow temp = (TableRow) tableRow.getChildAt(acetoneX);
        TextView tempText = (TextView) temp.getChildAt(0);
        String ans = null;
        if(yVal.contains(digitsPlus[0])){
            ans = "0";
        }
        else if(yVal.contains(digitsPlus[1]))
        {
            ans = "+";
        }
        else if(yVal.contains(digitsPlus[2]))
        {
            ans = "++";
        }
        else if(yVal.contains(digitsPlus[3]))
        {
            ans = "+++";
        }
        else{
            invalidSound.start();
            return;
        }
        tempText.setTextSize(8);
        tempText.setText(ans);

        acetoneX++;
        okSound.start();
    }

    private void updateChart10(String yVal) {
        tableRow = (TableRow) urine.getChildAt(0);

        TableRow temp = (TableRow) tableRow.getChildAt(proteanX);
        TextView tempText = (TextView) temp.getChildAt(0);
        String ans = null;
        if(yVal.contains(digitsPlus[0])){
            ans = "0";
        }
        else if(yVal.contains(digitsPlus[1]))
        {
            ans = "+";
        }
        else if(yVal.contains(digitsPlus[2]))
        {
            ans = "++";
        }
        else if(yVal.contains(digitsPlus[3]))
        {
            ans = "+++";
        }
        else{
            invalidSound.start();
            return;
        }
        tempText.setTextSize(8);
        tempText.setText(ans);

        proteanX++;
        okSound.start();
    }

    private void updateChart9(double yVal, int local) {
        tableRow = (TableRow) temperature.getChildAt(0);

        TableRow temp = (TableRow) tableRow.getChildAt(tempX);
        TextView tempText = (TextView) temp.getChildAt(0);
        if(local == 1){
            tempText.setText(Double.toString(yVal)+" C");
        }
        else{
            tempText.setText(Double.toString(yVal)+" F");
        }
        tempX++;
        okSound.start();
    }

    private void updateChart8(int yVal) {
        tableRow = (TableRow) oxytocin.getChildAt(1);

        TableRow temp = (TableRow) tableRow.getChildAt(oxyDrX);
        TextView tempText = (TextView) temp.getChildAt(0);
        tempText.setText(Integer.toString(yVal));

        oxyDrX++;
        okSound.start();
    }

    private void updateChart7(int yVal) {
        tableRow = (TableRow) oxytocin.getChildAt(0);

        TableRow temp = (TableRow) tableRow.getChildAt(oxyAmX);
        TextView tempText = (TextView) temp.getChildAt(0);
        tempText.setText(Integer.toString(yVal));

        oxyAmX++;
        okSound.start();
    }

    private void updateChart6(String yVal) {
        tableRow = (TableRow) fluid.getChildAt(1);

        TableRow temp = (TableRow) tableRow.getChildAt(mouldingX);
        TextView tempText = (TextView) temp.getChildAt(0);
        String ans = null;
        if(yVal.contains(digitsPlus[0])){
            ans = "0";
        }
        else if(yVal.contains(digitsPlus[1]))
        {
            ans = "+";
        }
        else if(yVal.contains(digitsPlus[2]))
        {
            ans = "++";
        }
        else if(yVal.contains(digitsPlus[3]))
        {
            ans = "+++";
        }
        else{
            invalidSound.start();
            return;
        }
        tempText.setTextSize(8);
        tempText.setText(ans);

        mouldingX++;
        okSound.start();
    }

    private void updateChart5(String yVal) {
        tableRow = (TableRow) fluid.getChildAt(0);

        TableRow temp = (TableRow) tableRow.getChildAt(fluidX);
        TextView tempText = (TextView) temp.getChildAt(0);
        tempText.setText(yVal.toUpperCase());

        fluidX++;
        okSound.start();
    }

    private boolean checkNumberValidity(String s2) {
        String []words = s2.split(" ");

        Log.i("S2", "checkNumberValidity: "+s2);

//        Toast.makeText(getApplicationContext(), "input here", Toast.LENGTH_LONG).show();

        if(s2.equals("")){
            return false;
        }
        if(words.length > 4)
        {
            return false;
        }
        String prunedNumber = pruneNumber(s2);

        Log.i("PrunedNumber", "checkNumberValidity: "+prunedNumber);

        if(!tensTest(prunedNumber)){
            Log.i("tensTest", "invalid");
            return false;
        }

        if(!teensTest(prunedNumber)){
            Log.i("teensTest", "invalid");
            return false;
        }

        if(!onesTest(prunedNumber)){
            Log.i("onesTest", "invalid");
            return false;
        }

        if(!invalidityOfTens(prunedNumber)){
            Log.i("invalidity of Tens", "invalid");
            return false;
        }

        if(!invalidityOfTeens(prunedNumber)){
            Log.i("invalidity of Teens", prunedNumber);
            return false;
        }

        return true;
    }

    private boolean invalidityOfTeens(String prunedNumber) {
        boolean answer = true;
        for(String s: teens){
            if(prunedNumber.contains(s)){
                for(String string: digits){
                    Pattern pattern = Pattern.compile("\\b"+string+"\\b");
                    Matcher matcher = pattern.matcher(prunedNumber);
                    if(matcher.find()){
                        answer = false;
                    }
                }
            }
        }
        return answer;
    }

    private boolean onesTest(String prunedNumber) {
        int count = 0;
        for(String s: digits){
            Pattern p = Pattern.compile("\\b"+s+"\\b");
            Matcher m = p.matcher(prunedNumber);
            while (m.find()) {
                count++;
            }
        }

        if(count > 1)
            return false;
        else
            return true;
    }

    private boolean invalidityOfTens(String prunedNumber) {
        boolean answer = true;
        for(String s: tens){
            if(prunedNumber.contains(s)){
                for(String string: teens){
                    Pattern pattern = Pattern.compile("\\b"+string+"\\b");
                    Matcher matcher = pattern.matcher(prunedNumber);
                    Log.i("MatcherString", "invalidityOfTens: "+pattern);
                    if(matcher.find()){
                        answer = false;
                    }
                }
            }
        }

        return answer;
    }

    private boolean teensTest(String prunedNumber) {
        int count = 0;
        for(String s: teens){
            Pattern p = Pattern.compile("\\b"+s+"\\b");
            Matcher m = p.matcher(prunedNumber);
            while (m.find()) {
                count++;
            }
        }
        if(count > 1)
            return false;
        else
            return true;
    }

    private boolean tensTest(String prunedNumber) {
        int count = 0;
        for(String s: tens){
            Pattern p = Pattern.compile("\\b"+s+"\\b");
            Matcher m = p.matcher(prunedNumber);
            while (m.find()) {
                count++;
            }
        }
        if(count > 1)
            return false;
        else
            return true;
    }

    private void updateChart3(String s2) {
        contractionData.removeDataSet(contractionDataSet);

        if(contractionPointsAdded == false)
        {
            myHelper.deleteAll("contractionGraph");
        }

        int last = s2.indexOf("seconds");
        String number = s2.substring(0,last);
        String yValString = number.trim();
        if(!checkNumberValidity(yValString)){
            Log.i("yvalString", "updateChart3: "+yValString);
            Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_LONG).show();
            invalidSound.start();
            return;
        }
        int seconds = convertWordsToNum(yValString);
        Log.i("value", "updateChart3: " + seconds);

        int lastIn = s2.length();
        String secondsPhrase = s2.substring(last,lastIn);
        String secondsNumber = secondsPhrase.replace("seconds", "");
        secondsNumber = secondsNumber.replace("times","");
        secondsNumber = secondsNumber.replace("time","");
        String valueSeconds = secondsNumber.trim();
        if(!checkNumberValidity(valueSeconds)){
            Log.i("valueSeconds", "updateChart3: "+valueSeconds+"hello");
            Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_LONG).show();
            invalidSound.start();
            return;
        }
        int yVal = convertWordsToNum(valueSeconds);
        Log.i("seconds", "updateChart3: " + yVal);

        if((yVal < 1) || (yVal > 5)){
            Toast.makeText(getApplicationContext(),"Input out of Contraction range", Toast.LENGTH_LONG).show();
            invalidSound.start();
            invalidSound.start();
            return;
        }

        myHelper.insertData(contractionX, yVal, "contractionGraph");

        if(contractionDataSet == null)
            contractionDataSet = new BarDataSet(getBarData(),"bardata");
        else{
            contractionDataSet.clear();
            contractionDataSet.setValues(getBarData());
        }

        if(seconds > 40)
            barColors.add(getResources().getColor(R.color.contraction3));
        else if((seconds <= 40) && (seconds >= 20))
            barColors.add(getResources().getColor(R.color.contraction2));
        else if(seconds < 20)
            barColors.add(getResources().getColor(R.color.contraction1));

        contractionData.setBarWidth(1f);
        contractionGraph.getLegend().setEnabled(false);
        contractionGraph.setFitBars(true);
        contractionDataSet.setColors(barColors);
        contractionData.addDataSet(contractionDataSet);
        contractionGraph.clear();
        contractionGraph.setData(contractionData);
        contractionGraph.invalidate();

        contractionPointsAdded = true;


        contractionX += 1;
        okSound.start();
    }

    private List<BarEntry> getBarData() {
        ArrayList<BarEntry> dp = new ArrayList<>();
        String [] columns = {"xValues","yValues"};

        Cursor cursor = sqLiteDatabase.query("contractionGraph", columns, null, null, null, null, "xValues ASC");

        for(int i=0; i<cursor.getCount(); i++)
        {
            cursor.moveToNext();
            dp.add(new BarEntry(cursor.getInt(0),cursor.getInt(1)));
        }
        return dp;
    }

    private String getNumber(String hypstr) {
        String ans = hypstr;
        for(String s : commands){
            ans = ans.replace(s,"");
        }
        for(String s : charts){
            ans = ans.replace(s,"");
        }
        String result = ans.trim();

        return result;
    }

    private String pruneNumber(String string){
        String str1, str2;
        str1 = string.replace("one hundred", "");
        str2 = str1.replace("two hundred", "");

        return str2.trim();
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

    private void updateChart1(int yVal) {
        fetalData.removeDataSet(fetalDataSet);

        if(fetalPointsAdded == false)
        {
            myHelper.deleteAll("fetalGraph");
        }

        if((yVal < 80) || (yVal > 200)){
            Toast.makeText(getApplicationContext(),"Input out of Fetal heart rate range", Toast.LENGTH_LONG).show();
            invalidSound.start();
            return;
        }

        myHelper.insertData(fetalX, yVal,"fetalGraph");

        fetalDataSet.clear();

        fetalDataSet.setValues(getData1());
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

        fetalPointsAdded = true;

        fetalX += 4;
        okSound.start();

    }
    //    https://brightinventions.pl/blog/charts-on-android-2/
    private void updateChart2(int yVal, int x){
        if(x == 1){

            if(cervicalPointsAdded == false)
            {
                myHelper.deleteAll("cervicalGraph");
            }

            if((yVal < 0) || (yVal > 10)){
                Toast.makeText(getApplicationContext(),"Input out of cervical dialation range", Toast.LENGTH_LONG).show();
                invalidSound.start();
                return;
            }
            myHelper.insertData(cervicalX, yVal, "cervicalGraph");

//            Entry newEntry = new Entry(cervicalX,yVal);
//            newEntry.setIcon();
//            cervicalDataSet.setDrawIcons(true);
//            cervicalList.add(newEntry);
//
//            printListCervical();
            cervicalPointsAdded = true;
            cervicalX += 4;
        }
        else{
            cervicalData.removeDataSet(descendDataSet);

            if(descendPointAdded == false)
            {
                myHelper.deleteAll("descendGraph");
            }

            if((yVal < 1) || (yVal > 5)){
                Toast.makeText(getApplicationContext(),"Input out of feotal head descend range", Toast.LENGTH_LONG).show();
                invalidSound.start();
                return;
            }
            myHelper.insertData(descendX, yVal, "descendGraph");

            descendPointAdded = true;

            descendX += 4;
        }

        cervicalDataSet = new LineDataSet(getData2(),"Cervical Dialation");
        cervicalDataSet.setDrawCircles(true);
        cervicalDataSet.setDrawCircleHole(true);
        cervicalDataSet.setCircleColor(Color.CYAN);
        cervicalDataSet.setCircleRadius(7);
        cervicalDataSet.setCircleHoleRadius(2);



        descendDataSet = new LineDataSet(getData5(),"Foetal Head Decent");
        descendDataSet.setLabel("Foetal Head Readings");
        descendDataSet.setDrawCircles(true);
        descendDataSet.setDrawCircleHole(true);
        descendDataSet.setCircleColor(R.color.descend);
        descendDataSet.setCircleRadius(7);
        descendDataSet.setCircleHoleRadius(2);


        cervicalDataSets.clear();
        cervicalDataSets.add(cervicalDataSet1);
        cervicalDataSets.add(cervicalDataSet2);
        cervicalDataSets.add(cervicalDataSet);
        cervicalDataSets.add(descendDataSet);

        Log.i("cervicalDatasets", "updateChart2: "+cervicalDataSets.size());
        Log.i("cervicalDataset", "updateChart2: "+cervicalDataSet.getEntryCount());
        Log.i("descendDataset", "updateChart2:"+descendDataSet.getEntryCount());

        cervicalGraph.clear();

//            cervicalData.clearValues();

//            cervicalData.addDataSet(cervicalDataSet1);
//            cervicalData.addDataSet(cervicalDataSet2);
//            cervicalData.addDataSet(cervicalDataSet);
//            cervicalData.addDataSet(descendDataSet);
        cervicalData = new LineData(cervicalDataSets);

        cervicalGraph.setData(cervicalData);
//            cervicalGraph.setData(descendData);
        cervicalGraph.invalidate();
        okSound.start();

    }

    private void printListCervical() {
        for(Entry e: cervicalList){
            Log.i("CervicalList", "printListCervical: "+e.toString());
        }
    }

    private void updateChart4(int yVal){
        maternalData.removeDataSet(maternalDataSet);

        if(maternalPointsAdded == false)
        {
            myHelper.deleteAll("maternalGraph");
        }

        if((yVal < 60) || (yVal > 180)){
            Toast.makeText(getApplicationContext(),"Input out of Patient heart rate range", Toast.LENGTH_LONG).show();
            invalidSound.start();
            return;
        }

        myHelper.insertData(maternalX, yVal, "maternalGraph   ");

        maternalDataSet.clear();

        maternalDataSet.setValues(getData4());
        maternalDataSet.setLabel("Readings");
        maternalDataSet.setDrawCircles(true);
        maternalDataSet.setDrawCircleHole(true);
        maternalDataSet.setCircleColor(Color.CYAN);
        maternalDataSet.setCircleRadius(10);
        maternalDataSet.setCircleHoleRadius(5);

        maternalData.addDataSet(maternalDataSet);
        maternalGraph.clear();
        maternalGraph.setData(maternalData);
        maternalGraph.invalidate();

        maternalPointsAdded = true;

        maternalX += 4;
        okSound.start();
    }

    private ArrayList<Entry> getData1() {
        ArrayList<Entry> dp = new ArrayList<>();
        String [] columns = {"xValues","yValues"};

        Cursor cursor = sqLiteDatabase.query("fetalGraph", columns, null, null, null, null, "xValues ASC");

        Log.i("FetalGetData", "getData1: "+ cursor.getCount());
        if(cursor.getCount() == 0)
            return dp;

        for(int i=0; i<cursor.getCount(); i++)
        {
            cursor.moveToNext();
            dp.add(new Entry(cursor.getInt(0),cursor.getInt(1)));
        }
        return dp;
    }

    private ArrayList<Entry> getData2() {
        ArrayList<Entry> dp = new ArrayList<>();
        String [] columns = {"xValues","yValues"};

        Cursor cursor = sqLiteDatabase.query("cervicalGraph", columns, null, null, null, null, "xValues ASC");

        Log.i("CervicalGetData", "getData2: "+ cursor.getCount());
        if(cursor.getCount() == 0)
            return dp;
        for(int i=0; i<cursor.getCount(); i++)
        {
            cursor.moveToNext();
            dp.add(new Entry(cursor.getInt(0),cursor.getInt(1)));
        }
        return dp;
    }

    private ArrayList<Entry> getData4() {
        ArrayList<Entry> dp = new ArrayList<>();
        String [] columns = {"xValues","yValues"};

        Cursor cursor = sqLiteDatabase.query("maternalGraph", columns, null, null, null, null, "xValues ASC");

        for(int i=0; i<cursor.getCount(); i++)
        {
            cursor.moveToNext();
            dp.add(new Entry(cursor.getInt(0),cursor.getInt(1)));
        }
        return dp;
    }

    private ArrayList<Entry> getData5() {
        ArrayList<Entry> dp = new ArrayList<>();
        String [] columns = {"xValues","yValues"};

        Cursor cursor = sqLiteDatabase.query("descendGraph", columns, null, null, null, null, "xValues ASC");

        Log.i("DescendGetData", "getData5: "+ cursor.getCount());
        if(cursor.getCount() == 0)
            return dp;
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
    public void onResume() {
        Log.i(LOG_TAG, "resume");
        super.onResume();
        resetSpeechRecognizer();
        initializeTextToSpeech();
        speech.startListening(recognizerIntent);
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "pause");
        super.onPause();
        speech.stopListening();
        tts.shutdown();
    }

    @Override
    protected void onStop() {
        Log.i(LOG_TAG, "stop");
        super.onStop();
        if (speech != null) {
            speech.destroy();
        }
        tts.shutdown();
        if(bluetoothHeadset != null)
            bluetoothHeadset.stopVoiceRecognition(btDevice);
        bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET,bluetoothHeadset);
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float v) {
//        progressBar.setProgress((int) rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i(LOG_TAG, "onBufferReceived: " + bytes);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
//        progressBar.setIndeterminate(true);
        speech.stopListening();
        Log.i("Textafer", "current Context: " + context);
    }

    @Override
    public void onError(int i) {
        String errorMessage = getErrorText(i);
        Log.i(LOG_TAG, "FAILED " + errorMessage);
//        returnedError.setText(errorMessage);

        // rest voice recogniser
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");

        ArrayList<String> matches = results
                .getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
        String text = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION).get(0);
//        for (String result : matches)
//            text += result + "\n";
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();


        if(context.equals("command")){
            if(text.equals("computer") || text.equals("computers")){
                Toast.makeText(getApplicationContext(),"hello initialized",Toast.LENGTH_LONG).show();
                speak("Please name a graph");
                this.context = "graph";
                recognizerIntent.putExtra(EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,15000);
//                returnedText.setText(text);
            }
        }
        else if(context.equals("graph")){
            String[] words = text.split(" ");
            Soundex soundex = new Soundex();
            int x=0;
            try {
                for(String str:charts){
                    x = soundex.difference(words[0],str);
                    if(x>0){
                        graphingChart = str;
                        selectedChart = Arrays.asList(charts).indexOf(str);
//                        selectedChart = Arrays.binarySearch(charts,str);
                        Log.i("SelectedChart", "onResults: "+selectedChart);
                        break;
                    }
                }

            } catch (EncoderException e) {
                e.printStackTrace();
            }
            if(x>3){
//                chartBox.setText(graphingChart);
//                recognizerIntent.putExtra(EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,10000);
                if(graphingChart.equals("morning"))
                    speak("moulding"+" graph selected, please insert value");
                else
                    speak(graphingChart+" graph selected, please insert value");
                this.context = "number";
            }
        }
        else if(context.equals("number")){
//            numberBox.setText(text);
            if(!text.equals("i"))
                speak("you have inserted "+text);
            if(selectedChart == 0){
                speak("you have inserted "+text);
//                try {
//                    int yVal = Integer.parseInt(text);
//                    updateChart1(yVal);
//
//                } catch (EncoderException ei) {
//                    ei.printStackTrace();
//                }
                if(!text.equals("i")){
//                    int yVal = Integer.parseInt(text);
//                    updateChart1(yVal);
                    scrollView.smoothScrollTo(0, fetalGraph.getTop()-100);
                }
            }

            else if(selectedChart == 5){
                speak("you have inserted "+text);
//                int yVal = Integer.parseInt(text);
                updateChart5(text);
                scrollView.smoothScrollTo(0, fluidLayout.getTop()-100);
            }

            else if(selectedChart == 6){

                speak("you have inserted "+text);
//                int yVal = Integer.parseInt(text);
                updateChart6(text);
                scrollView.smoothScrollTo(0, fluidLayout.getTop()-100);
            }
            recognizerIntent.putExtra(EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,5000);
            if(!text.equals("i"))
                this.context = "command";
        }
        Toast.makeText(getApplicationContext(),"context "+context,Toast.LENGTH_LONG).show();
        Log.i("TextNow", "Current: " + text);
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onEvent");
    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case android.speech.SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case android.speech.SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case android.speech.SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case android.speech.SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case android.speech.SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                Log.i("TextAfter2", "getErrorTextConetext: " + context);
//                if(!context.equals("command"))
//                    context = "command";
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}