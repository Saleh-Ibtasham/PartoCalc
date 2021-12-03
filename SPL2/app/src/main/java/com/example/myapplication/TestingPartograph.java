package com.example.myapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MicrophoneInfo;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.speech.RecognitionListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;


import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Soundex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private Button cont,notifier;
    private ScrollView scrollView,scrollViewB;


    private static final String KWS_SEARCH = "wakeup";
    private static final String MENU_SEARCH = "menu";
    private static final String KEYPHRASE = "hey mobile";

    private LineChart fetalGraph, cervicalGraph, maternalGraph;
    private BarChart contractionGraph;
    private TableLayout fluid,time,oxytocin,medicine,temperature,urine;

//    private List<RowHeader> liquorRowHeaderList;
//    private List<>;

    private int tableColumnCount = 0;

    private LineDataSet fetalDataSet = new LineDataSet(null,null);
    private LineDataSet cervicalDataSet = new LineDataSet(null,"cervical");
    private LineDataSet descendDataSet = new LineDataSet(null,"head descend");
    private BarDataSet contractionDataSet;
    private LineDataSet maternalDataSet = new LineDataSet(null,null);

    private ArrayList<ILineDataSet> cervicalDataSets = new ArrayList<>();
    private ArrayList<ILineDataSet> maternalDataSets = new ArrayList<>();
    private ArrayList<ILineDataSet> pressureDataSets = new ArrayList<>();
    private ArrayList<PressureEntry> pressureEntries = new ArrayList<>();
    private ArrayList<Entry> cervicalList = new ArrayList<>();
    private ArrayList<Entry> descendList = new ArrayList<>();

    private ArrayList<String> zoneCharts = new ArrayList<>();

    private LineDataSet fetalDataSet1,fetalDataSet2,cervicalDataSet1,cervicalDataSet2;

    private LineData fetalData, cervicalData, maternalData, descendData;
    private BarData contractionData;

    private int fetalX = 0;
    private int fetalY;
    private int cervicalX = 0;
    private int cervicalY;
    private int contractionX = 0;
    private int contractionY;
    private int maternalX = 0;
    private int maternalY;
    private int descendX = 0;
    private double pressureX = 0.5;
    private int fluidX=0, mouldingX=0, oxyAmX = 0, oxyDrX = 0, tempX=0, proteanX =0, acetoneX=0, amountX = 0;


    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer;

    private TextToSpeech tts;

    private SpeechRecognizer speechRecognizer;

    private MediaPlayer okSound, invalidSound, unsafeSound;

    MyHelper myHelper;
    SQLiteDatabase sqLiteDatabase;

    Database db;
    DatabaseConfiguration config;

    private boolean fetalPointsAdded = false , cervicalPointsAdded = false, contractionPointsAdded = false, maternalPointsAdded = false, descendPointAdded = false,
            pressurePointAdded = false, partoGraphInitialized = false;
    private int initializationFlag = 0;
//    private String[] charts = {"fetal heart rate", "cervical dilatation", "contraction", "maternal pulse", "head descend five by","amniotic fluid","moulding","oxytocin amount", "oxytocin drops","temperature","protean","acetone","amount"};
    private String[] charts = {"fetal", "cervical", "contraction", "pulse", "descend","fluid","moulding","temperature","protein","acetone","urine","pressure"};
    private List<ArrayList<String>> charts2 = new ArrayList<>();
    private String[] commands = {"input", "delete"};
    private HashMap<String, Chart> chartHashMap;
    private int[] counters = {0,0,0,0};
    private String[] xAxisCervical = new String[24];

    private String[] tens = {"twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};
    private String[] hundreds = {"hundred"};
    private String[] digits = {"zero", "one", "two", "three", "four", "five", "six",
            "seven", "eight", "nine", "ten"};
    private String[] digitsPlus = {"absent", "1", "2", "3"};
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
    private boolean isInputOk = false;

    private String graphingChart=null;
    private int selectedChart=-1;

    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

    private PreviousEntry previousEntry = null;

    private  EditText admission_date, admission_time, hours, hospital_number, para, gravida,name;
    private Spinner membrane_input;
    private String amPm, membrane;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog timePickerDialog;

    private String bed_number;
    private String mode;

    private String document_id = "";

    String [] membrane_items = new String[]{"","no","yes"};
    List<String> membrane_items_list = Arrays.asList(membrane_items);

    ArrayList<Integer> contraction_regions = new ArrayList<>();

    private boolean graphSaved = false;
    private boolean exitingGraph = false;

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

        config = new DatabaseConfiguration();
        try {
            db = new Database("partoCalc",config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");

        if(mode.equals("create")){
            bed_number = intent.getStringExtra("bedNumber");
        }else {
            document_id = intent.getStringExtra("id");
            graphSaved = true;
        }

        toolbar = (Toolbar) findViewById(R.id.partoToolBar);
        notifier = (Button) findViewById(R.id.notifier);
        notifier.setBackgroundColor(getResources().getColor(R.color.notifier_green));
//        toolbar.setTitleTextColor(getResources().getColor(R.color.titlecolor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Graph");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        name = (EditText) findViewById(R.id.patient_name_input);
        hospital_number = (EditText) findViewById(R.id.hospital_num_input);
        para = (EditText) findViewById(R.id.para);
        gravida = (EditText) findViewById(R.id.gravida);

        admission_date = (EditText) findViewById(R.id.admission_date_input);
        admission_time = (EditText) findViewById(R.id.admission_time_input);

        hours = (EditText) findViewById(R.id.hours_input);


        hours.setEnabled(false);
        hours.setText("");



        membrane_input = (Spinner) findViewById(R.id.membrane_input);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,membrane_items);

        membrane_input.setAdapter(adapter);

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

        createGraphArrays();
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
        clearDatabase();

        okSound = MediaPlayer.create(getApplicationContext(), R.raw.right);
        invalidSound = MediaPlayer.create(getApplicationContext(), R.raw.case_closed);
        unsafeSound = MediaPlayer.create(getApplicationContext(), R.raw.yellow);

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        am.setStreamMute(AudioManager.STREAM_MUSIC,true);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        am.startBluetoothSco();
        am.setBluetoothScoOn(true);



        admission_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        TestingPartograph.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date = month + "/" + day + "/" + year;
                admission_date.setText(date);
            }
        };

        admission_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);


                timePickerDialog = new TimePickerDialog(TestingPartograph.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        admission_time.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();
            }
        });

        membrane_input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                membrane = parent.getItemAtPosition(position).toString();
                if(parent.getItemAtPosition(position).toString().equals("yes")){
                    if(mode.equals("create"))
                        hours.setEnabled(true);
                }
                else{
                    hours.setEnabled(false);
                    hours.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                hours.setEnabled(false);
                hours.setText("");
            }
        });

        if(mode.equals("edit")){
            populateView();
        }

        resetSpeechRecognizer();
        initializeTextToSpeech();
        setRecogniserIntent();
        speech.startListening(recognizerIntent);
        
    }

    private void populateView() {
        Document doc = db.getDocument(document_id);

        Log.i("populateDocument", "populateView: "+ doc.toString());

        name.setEnabled(false);
        gravida.setEnabled(false);
        para.setEnabled(false);
        admission_date.setEnabled(false);
        admission_time.setEnabled(false);
        hospital_number.setEnabled(false);
        membrane_input.setEnabled(false);
        hours.setEnabled(false);

        name.setText(doc.getString("name"));
        gravida.setText(doc.getString("gravida"));
        para.setText(doc.getString("para"));
        admission_date.setText(doc.getString("admissionDate"));
        admission_time.setText(doc.getString("admissionTime"));
        hospital_number.setText(doc.getString("hospitalNumber"));
        hours.setText(doc.getString("hours"));
        membrane_input.setSelection(membrane_items_list.indexOf(doc.getString("membrane")));

        bed_number = doc.getString("bedNumber");


        List<Object> listTimeInputs = doc.getArray("timeInputs").toList();
        List<Object> listDescend = doc.getArray("descend").toList();
        List<Object> listPressure = doc.getArray("pressure").toList();
        List<Object> listProtein = doc.getArray("protein").toList();
        List<Object> listAcetone = doc.getArray("acetone").toList();
        List<Object> listAmount = doc.getArray("amount").toList();
        List<Object> listFluid = doc.getArray("fluid").toList();

        List<Object> listTemperature = doc.getArray("temperature").toList();
        List<Object> listFetal = doc.getArray("fetal").toList();
        List<Object> listCervical = doc.getArray("cervical").toList();
        List<Object> listMoulding = doc.getArray("moulding").toList();
        List<Object> listDrugs = doc.getArray("drugs").toList();
        List<Object> listPulse = doc.getArray("pulse").toList();
        List<Object> listContraction = doc.getArray("contraction").toList();
        List<Object> listContractionRegions = doc.getArray("contractionRegions").toList();

        loadFromDocument(listCervical,"cervical");
        loadFromDocument(listFetal,"fetal");
        loadFromDocumentContraction(listContraction,listContractionRegions,"contraction");
        loadFromDocument(listDescend,"descend");
        loadFromDocument(listPulse,"maternal");
        loadFromDocumentPressure(listPressure,"pressure");
        loadFromDocumentTables(listFluid,"fluid",0, fluid);
        loadFromDocumentTables(listMoulding,"moulding",1, fluid);
        loadFromDocumentTables(listTimeInputs,"timeInputs",1, time);
        loadFromDocumentTables(listTemperature,"temperature",0, temperature);
        loadFromDocumentTables(listProtein,"protein",0, urine);
        loadFromDocumentTables(listAcetone,"acetone",1, urine);
        loadFromDocumentTables(listAmount,"amount",2, urine);
        loadFromDocumentDrugs(listDrugs, medicineLayout);

//        fluid,time,temperature,urine;
    }

    private void loadFromDocumentDrugs(List<Object> list, LinearLayout medicineLayout) {

        for(Object o: list){
            HashMap<String,Object> normalMap = (HashMap<String, Object>) o;
            int x = Integer.parseInt(normalMap.get("xValue").toString());
            String y = normalMap.get("yValue").toString();
            View v = medicineLayout.getChildAt(x);
            ((EditText)v).setText(y);
        }
    }

    private void loadFromDocumentTables(List<Object> list, String tableEntity, int row, TableLayout tableLayout) {

        tableRow = (TableRow) tableLayout.getChildAt(row);

        for(Object o: list){
            HashMap<String,Object> normalMap = (HashMap<String, Object>) o;
            int x = Integer.parseInt(normalMap.get("xValue").toString());
            String y = normalMap.get("yValue").toString();
            TableRow temp = (TableRow) tableRow.getChildAt(x);
            TextView tempText = (TextView) temp.getChildAt(0);
            tempText.setText(y);
            if(tableEntity.equals("fluid"))
                fluidX = x + 1;
            if(tableEntity.equals("moulding"))
            {
                mouldingX = x + 1;
                tempText.setTextSize(12);
            }
            if(tableEntity.equals("temperature"))
                tempX = x + 1;
            if(tableEntity.equals("protein"))
                proteanX = x + 1;
            if(tableEntity.equals("acetone"))
                acetoneX = x + 1;
            if(tableEntity.equals("amount"))
                amountX = x + 1;
        }
    }

    private void loadFromDocumentPressure(List<Object> list, String graph) {
        String graphTable = graph + "Graph";

        for(Object o: list){
            HashMap<String,Object> normalMap = (HashMap<String, Object>) o;

            Double x = Double.parseDouble(normalMap.get("xInput").toString());
            int sysTol = Integer.parseInt(normalMap.get("sysTol").toString());
            int dysTol = Integer.parseInt(normalMap.get("dysTol").toString());

            myHelper.insertDataForPressure(x,sysTol,dysTol,graphTable);
            pressurePointAdded = true;
            pressureX = x+8;
        }
        loadMaternal();
    }

    private void loadFromDocumentContraction(List<Object> listContraction, List<Object> listContractionRegions, String graph) {
        String graphTable = graph + "Graph";

        for (int i=0; i<listContraction.size();i++){
            HashMap<String,Object> entry = (HashMap<String, Object>) listContraction.get(i);
            Integer region = Integer.parseInt(listContractionRegions.get(i).toString());

            int x = Integer.parseInt(entry.get("x").toString());
            int y = Integer.parseInt(entry.get("y").toString());
            myHelper.insertData(x, y, graphTable);

            if(region == 3)
                barColors.add(getResources().getColor(R.color.contraction3));
            else if(region == 2)
                barColors.add(getResources().getColor(R.color.contraction2));
            else if(region == 1)
                barColors.add(getResources().getColor(R.color.contraction1));

            contraction_regions.add(region);
            contractionPointsAdded = true;
            contractionX = x+1;

        }
        loadContraction();
    }

    private void loadFromDocument(List<Object> list, String graph) {

        String graphTable = graph + "Graph";
        if(graph == "cervical"){
            initializationFlag = 1;
        }

        for(Object o: list){
            HashMap<String,Object> normalMap = (HashMap<String, Object>) o;

            int x = Integer.parseInt(normalMap.get("x").toString());
            int y = Integer.parseInt(normalMap.get("y").toString());
            myHelper.insertData(x, y,graphTable);
            if(graph.equals("fetal")){
                zoneMonitor(graph,x,y);
                fetalPointsAdded = true;
                fetalX = x + 1;
            }
            if(graph.equals("cervical")){
                if((initializationFlag == 1) && updateEveryX(y)){
                    initializationFlag = 2;
                    partoGraphInitialized = true;
                }
                zoneMonitor(graph,x,y);
                cervicalPointsAdded = true;
                cervicalX = x+8;
            }
            if(graph.equals("descend")){
                descendPointAdded = true;
                descendX = x+1;
            }
            if(graph.equals("pulse")){
                maternalPointsAdded = true;
                maternalX = x+1;
            }

        }

        if(graph.equals("fetal") || graph.equals("descend"))
            loadFetal();
        if(graph.equals("cervical"))
            loadCervical();
        if(graph.equals("pulse"))
            loadMaternal();


    }

    private void clearDatabase() {
        if(!fetalPointsAdded){
            myHelper.deleteAll("fetalGraph");
        }
        if(!cervicalPointsAdded){
            myHelper.deleteAll("cervicalGraph");
        }
        if(!descendPointAdded){
            myHelper.deleteAll("descendGraph");
        }
        if(!contractionPointsAdded){
            myHelper.deleteAll("contractionGraph");
        }
        if(!maternalPointsAdded){
            myHelper.deleteAll("maternalGraph");
        }
        if(!pressurePointAdded){
            myHelper.deleteAll("pressureGraph");
        }

    }

    private void createGraphArrays() {
        String[] fetal = new String[]{"fetal","eagle","peter"};//,"beetle","petal","pital","fiddle","feudal","sheetal"
        ArrayList<String> tempList = new ArrayList<>(Arrays.asList(fetal));
        charts2.add(tempList);
        String[] fluid = new String[]{"fluid","sweet","floyd"}; //,"free","sleep"
        tempList = new ArrayList<>(Arrays.asList(fluid));
        charts2.add(tempList);
        String[] moudling = new String[]{"moulding","morning","monday"};
        tempList = new ArrayList<>(Arrays.asList(moudling));
        charts2.add(tempList);
        String[] cervical = new String[]{"cervical","sorry","salvia","send","so"};
        tempList = new ArrayList<>(Arrays.asList(cervical));
        charts2.add(tempList);
        String[] descend = new String[]{"descend"};//,"desent","dissent","desend"
        tempList = new ArrayList<>(Arrays.asList(descend));
        charts2.add(tempList);
        String[] contraction = new String[]{"contraction","construction","congestion","injection","direction"};//,"sanderson","contractor","condition"
        tempList = new ArrayList<>(Arrays.asList(contraction));
        charts2.add(tempList);
        String[] pulse = new String[]{"pulse","pass","paisa","spouse"};//,"parse","parts"
        tempList = new ArrayList<>(Arrays.asList(pulse));
        charts2.add(tempList);
        String[] pressure = new String[]{"pressure"};
        tempList = new ArrayList<>(Arrays.asList(pressure));
        charts2.add(tempList);
        String[] temperature = new String[]{"temperature"};
        tempList = new ArrayList<>(Arrays.asList(temperature));
        charts2.add(tempList);
        String[] protein = new String[]{"protein","reading","brody","loading","running"};//,"baby","body","bean","funny"
        tempList = new ArrayList<>(Arrays.asList(protein));
        charts2.add(tempList);
        String[] acetone = new String[]{"acetone","epitome","evidence","sedone"};//,"austin","addedon","methadone"
        tempList = new ArrayList<>(Arrays.asList(acetone));
        charts2.add(tempList);
        String[] urine = new String[]{"urine","united","using"};//,"uranus","union","haloween","youmean"
        tempList = new ArrayList<>(Arrays.asList(urine));
        charts2.add(tempList);
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
        createTimeSeries();
        createOxytocin();
        createMedicine();
        createTemp();
        createUrine();
    }

    private void createTimeSeries() {
        tableRow = (TableRow) time.getChildAt(0);
        int hours = 1;
        for(int i=0;i<12;i++) {
            TableRow temp = (TableRow) tableRow.getChildAt(i);
            TextView tempText = (TextView) temp.getChildAt(0);
            tempText.setText(Integer.toString(hours));
            hours++;
        }
    }

    private void setTimeSeries(){
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        tableRow = (TableRow) time.getChildAt(1);
        int i = cervicalX/2;
        for(;i<12;i++){
            TableRow temp = (TableRow) tableRow.getChildAt(i);
            TextView tempText = (TextView) temp.getChildAt(0);
            tempText.setText(sdf.format(date));
            cal.add(Calendar.HOUR,1);
            date = cal.getTime();
        }
    }

    private void resetTimeSeries(){
        tableRow = (TableRow) time.getChildAt(1);
        for(int i=0; i<12; i++){
            TableRow temp = (TableRow) tableRow.getChildAt(i);
            TextView tempText = (TextView) temp.getChildAt(0);
            tempText.setText("");
        }
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
        label_date.setTextSize(14);
        this.tableRow.addView(label_date);
        this.tableRow.setTag("yolo");
        tableAdd.addView(tableRow);
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
        label_date.setText("");
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
        label_date.setTextSize(14);
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
        fetalDataSet1.setDrawValues(false);
        fetalDataSet1.setLineWidth(5);
        fetalDataSet1.setLabel("lower-limit");

        fetalDataSet2.addEntry(new Entry(0,160));
        fetalDataSet2.addEntry(new Entry(24,160));
        fetalDataSet2.setColor(Color.GRAY);
        fetalDataSet2.setDrawValues(false);
        fetalDataSet2.setLineWidth(5);
        fetalDataSet2.setLabel("upper-limit");

        fetalData = new LineData();
        fetalData.addDataSet(fetalDataSet1);
        fetalData.addDataSet(fetalDataSet2);

        XAxis xAxis = fetalGraph.getXAxis();
        YAxis yAxis = fetalGraph.getAxisLeft();
        YAxis yAxitext = fetalGraph.getAxisRight();

        yAxis.setLabelCount(13,true);
        yAxitext.setLabelCount(13,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(200);
        yAxitext.setAxisMaximum(200);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(80);
        yAxitext.setAxisMinimum(80);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxitext.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        yAxitext.setGranularity(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        yAxitext.setDrawLabels(false);

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
        cervicalDataSet1.setDrawValues(false);
        cervicalDataSet1.setLabel("Alert");

        cervicalDataSet2.addEntry(new Entry(8,4));
        cervicalDataSet2.addEntry(new Entry(20,10));
        cervicalDataSet2.setColor(Color.RED);
        cervicalDataSet2.setLineWidth(5);
        cervicalDataSet2.setDrawValues(false);
        cervicalDataSet2.setLabel("Action");

        cervicalDataSets.add(cervicalDataSet1);
        cervicalDataSets.add(cervicalDataSet2);

        cervicalData = new LineData(cervicalDataSets);
        descendData = new LineData();
//        cervicalData.addDataSet(cervicalDataSet1);
//        cervicalData.addDataSet(cervicalDataSet2);

        XAxis xAxis = cervicalGraph.getXAxis();
        YAxis yAxis = cervicalGraph.getAxisLeft();
        YAxis yAxitext = cervicalGraph.getAxisRight();

        yAxis.setLabelCount(13,true);
        yAxitext.setLabelCount(13,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(12);
        yAxitext.setAxisMaximum(12);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(0);
        yAxitext.setAxisMinimum(0);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxitext.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        yAxitext.setGranularity(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        yAxitext.setDrawLabels(false);

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
        YAxis yAxitext = contractionGraph.getAxisRight();

        yAxis.setLabelCount(6,true);
        yAxitext.setLabelCount(6,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(5);
        yAxitext.setAxisMaximum(5);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(0);
        yAxitext.setAxisMinimum(0);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxitext.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        yAxitext.setGranularity(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        yAxitext.setDrawLabels(false);

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
        YAxis yAxitext = maternalGraph.getAxisRight();

        yAxis.setLabelCount(13,true);
        yAxitext.setLabelCount(13,true);
        xAxis.setLabelCount(25,true);
        xAxis.setAxisMaximum(24);
        yAxis.setAxisMaximum(180);
        yAxitext.setAxisMaximum(180);
        xAxis.setAxisMinimum(0);
        yAxis.setAxisMinimum(60);
        yAxitext.setAxisMinimum(60);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxitext.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1f);
        yAxitext.setGranularity(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        yAxitext.setDrawLabels(false);

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
                    speak("Hello there, I am ready.");;
                }
            }
        });
    }

    private void speak(String message) {

        HashMap<String, String> myHashRender = new HashMap<String, String>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_VOICE_CALL));
        if(Build.VERSION.SDK_INT >= 21){
            tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH,myHashRender);
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
            return true;
        }

        if (id == R.id.save_graphs) {
            showConfirmationDialogue();
            return true;
        }

        if(id == R.id.exit_graphs){
            onBackPressed();
            return true;
        }
//        else if (id == R.id.preview_graphs){
////            refreshGraphs();
//            return true;
//        }

        return true;
    }

    private void showConfirmationDialogue() {

        String CancelButtonText = "Cancel";
        if(exitingGraph)
            CancelButtonText = "Exit";

        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this).setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        builder.setTitle("Save Patient Status");
        builder.setMessage("Do you want to save changes made to the PartoGraph?");
        builder.addButton("Save", Color.parseColor("#ffffff"), Color.parseColor("#33cc33"),
                CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mode.equals("create"))
                            saveGraphs();
                        if(mode.equals("edit"))
                            editGraphs();
                        dialog.cancel();
                    }
                });
        builder.addButton(CancelButtonText, Color.parseColor("#ffffff"), Color.parseColor("#ffffff"),
                CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        CFAlertDialog alertDialog = builder.show();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(exitingGraph)
                    exitPartoGraph();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                exitingGraph = false;
            }
        });
    }

    private void editGraphs() {

        name.setEnabled(false);
        gravida.setEnabled(false);
        para.setEnabled(false);
        admission_date.setEnabled(false);
        admission_time.setEnabled(false);
        hospital_number.setEnabled(false);
        membrane_input.setEnabled(false);
        hours.setEnabled(false);

        ArrayList<Entry> fetal_inputs = new ArrayList<>();
        fetal_inputs = getData1();
        ArrayList<Entry> cervical_inputs = new ArrayList<>();
        cervical_inputs = getData2();
        ArrayList<Entry> descend_inputs = new ArrayList<>();
        descend_inputs = getData5();
        ArrayList<PressureEntry> pressure_inputs = new ArrayList<>();
        pressure_inputs = getDataForPressure();
        ArrayList<Entry> pulse_inputs = new ArrayList<>();
        pulse_inputs = getData4();
        List <BarEntry> contraction_inputs = new ArrayList<>();
        contraction_inputs = getBarData();
        ArrayList <TableEntry> fluid_inputs = new ArrayList<>();
        fluid_inputs = getTableEntries(fluid,0);
        ArrayList <TableEntry> moulding_inputs = new ArrayList<>();
        moulding_inputs = getTableEntries(fluid,1);
        ArrayList <TableEntry> time_inputs = new ArrayList<>();
        time_inputs = getTableEntries(time,1);
        ArrayList<TableEntry> drugs = new ArrayList<>();
        drugs = getDrugs();
        ArrayList <TableEntry> temperature_inputs = new ArrayList<>();
        temperature_inputs = getTableEntries(temperature,0);
        ArrayList<TableEntry> protein_inputs = new ArrayList();
        protein_inputs = getTableEntries(urine,0);
        ArrayList<TableEntry> acetone_inputs = new ArrayList<>();
        acetone_inputs = getTableEntries(urine,1);
        ArrayList<TableEntry> amount_inputs = new ArrayList<>();
        amount_inputs = getTableEntries(urine,2);

        String status = "";

        if(cervical_inputs.size() > 0){
            if(cervical_inputs.get(cervical_inputs.size()-1).getY() >= 10.00){
                status = "inactive";
            }else {
                status = "active";
            }
        }
        else {
            status = "active";
        }

        Patient patient = new Patient();

        patient.setFetal(fetal_inputs);
        patient.setCervical(cervical_inputs);
        patient.setDescend(descend_inputs);
        patient.setPressure(pressure_inputs);
        patient.setPulse(pulse_inputs);
        patient.setContraction(contraction_inputs);
        patient.setContractionRegions(contraction_regions);
        patient.setFluid(fluid_inputs);
        patient.setMoulding(moulding_inputs);
        patient.setTimeInputs(time_inputs);
        patient.setDrugs(drugs);
        patient.setTemperature(temperature_inputs);
        patient.setProtein(protein_inputs);
        patient.setAcetone(acetone_inputs);
        patient.setAmount(amount_inputs);
        patient.setStatus(status);

        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = mapper.convertValue(patient,Map.class);

        Log.i("EditedPatient", map.toString());
        Log.i("EditedPatient", map.get("descend").toString());

        Document doc = db.getDocument(document_id);
        MutableDocument mut = doc.toMutable();

        String[] keys = new String[]{"status","fetal","cervical","descend","pressure","pulse","contraction","contractionRegions","fluid","moulding","timeInputs",
                "drugs","temperature","protein","acetone","amount"};
        for(String key:keys){
            mut.setValue(key,map.get(key));
        }

        try {
            db.save(mut);
            graphSaved = true;
            Toast.makeText(getApplicationContext(),"Patient Status Updated",Toast.LENGTH_LONG).show();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }

    private void saveAsMutableArrays(Map<String, Object> map, String key, MutableDocument mut) {
        List<Object> list = Arrays.asList(map.get(key));
        MutableArray array = new MutableArray(list);

        mut.setArray(key,array);
        Log.i("EditedPatient", mut.getArray(key).toList().toString());
    }

    private void saveGraphs() {
        String patient_name = "";
        patient_name = name.getText().toString();
        String patient_para = "";
        patient_para = para.getText().toString();
        String patient_gravida = "";
        patient_gravida = gravida.getText().toString();
        String patient_hospital_number = "";
        patient_hospital_number = hospital_number.getText().toString();
        String patient_admission_date = "";
        patient_admission_date = admission_date.getText().toString();
        String patient_admission_time = "";
        patient_admission_time = admission_time.getText().toString();
        String patient_hours = "";
        patient_hours = hours.getText().toString();
        String patient_membrane = "";
        patient_membrane = membrane;

        if(patient_name.equals("") || patient_para.equals("") || patient_gravida.equals("") || patient_hospital_number.equals("") || patient_admission_date.equals("") ||
                patient_admission_time.equals("") || patient_membrane.equals("")){
            Toast.makeText(getApplicationContext(),"Please Fill up all the patient information in the first section.",Toast.LENGTH_LONG).show();
            return;
        }

        if(patient_membrane.equals("yes") && patient_hours.equals("")){
            Toast.makeText(getApplicationContext(),"Please Fill up all the patient information in the first section.",Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<Entry> fetal_inputs = new ArrayList<>();
        fetal_inputs = getData1();
        ArrayList<Entry> cervical_inputs = new ArrayList<>();
        cervical_inputs = getData2();
        ArrayList<Entry> descend_inputs = new ArrayList<>();
        descend_inputs = getData5();
        ArrayList<PressureEntry> pressure_inputs = new ArrayList<>();
        pressure_inputs = getDataForPressure();
        ArrayList<Entry> pulse_inputs = new ArrayList<>();
        pulse_inputs = getData4();
        List <BarEntry> contraction_inputs = new ArrayList<>();
        contraction_inputs = getBarData();
        ArrayList <TableEntry> fluid_inputs = new ArrayList<>();
        fluid_inputs = getTableEntries(fluid,0);
        ArrayList <TableEntry> moulding_inputs = new ArrayList<>();
        moulding_inputs = getTableEntries(fluid,1);
        ArrayList <TableEntry> time_inputs = new ArrayList<>();
        time_inputs = getTableEntries(time,1);
        ArrayList<TableEntry> drugs = new ArrayList<>();
        drugs = getDrugs();
        ArrayList <TableEntry> temperature_inputs = new ArrayList<>();
        temperature_inputs = getTableEntries(temperature,0);
        ArrayList<TableEntry> protein_inputs = new ArrayList();
        protein_inputs = getTableEntries(urine,0);
        ArrayList<TableEntry> acetone_inputs = new ArrayList<>();
        acetone_inputs = getTableEntries(urine,1);
        ArrayList<TableEntry> amount_inputs = new ArrayList<>();
        amount_inputs = getTableEntries(urine,2);

        String status = "";

        if(cervical_inputs.size() > 0){
            if(cervical_inputs.get(cervical_inputs.size()-1).getY() >= 10.00){
                status = "inactive";
            }else {
                status = "active";
            }
        }
        else {
            status = "active";
        }

        Log.i("cervical_inputs", cervical_inputs.toString());
        Log.i("cervical_inputs_status", "Status: " + status);



        Patient patient = new Patient(patient_name,patient_gravida,patient_para,patient_hospital_number,patient_hours,patient_membrane,
                patient_admission_date,patient_admission_time,bed_number,status);

        patient.setFetal(fetal_inputs);
        patient.setCervical(cervical_inputs);
        patient.setDescend(descend_inputs);
        patient.setPressure(pressure_inputs);
        patient.setPulse(pulse_inputs);
        patient.setContraction(contraction_inputs);
        patient.setContractionRegions(contraction_regions);
        patient.setFluid(fluid_inputs);
        patient.setMoulding(moulding_inputs);
        patient.setTimeInputs(time_inputs);
        patient.setDrugs(drugs);
        patient.setTemperature(temperature_inputs);
        patient.setProtein(protein_inputs);
        patient.setAcetone(acetone_inputs);
        patient.setAmount(amount_inputs);

//        newtask.setValue("patient",json);

        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = mapper.convertValue(patient,Map.class);

        MutableDocument newtask = new MutableDocument(map);

        Log.i("new_document", newtask.toString());
        String id = newtask.getId();

        document_id = id;

        try {
            db.save(newtask);
            graphSaved = true;
            Toast.makeText(getApplicationContext(),"Patient Status Saved. Patient informations of the 1st sections are uneditable now.",Toast.LENGTH_LONG).show();
            mode = "edit";
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


//
//        Document document = db.getDocument(id);
//        Log.i("new_document", document.toString());

    }

    private ArrayList<TableEntry> getTableEntries(TableLayout tableLayout, int row) {
        ArrayList<TableEntry> tableEntries = new ArrayList<>();

        tableRow = (TableRow) tableLayout.getChildAt(row);

        int childs = tableRow.getChildCount();

        for(int i=0; i<childs; i++){
            TableRow temp = (TableRow) tableRow.getChildAt(i);
            TextView tempText = (TextView) temp.getChildAt(0);
            String ans = tempText.getText().toString();
            if(!ans.equals("")){
                tableEntries.add(new TableEntry(i,ans));
            }
        }

        return tableEntries;
    }

    private ArrayList<TableEntry> getDrugs() {
        ArrayList<TableEntry> drugs = new ArrayList<>();
        int childCount = medicineLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = medicineLayout.getChildAt(i);
            String drug = ((EditText) v).getText().toString();
            drugs.add(new TableEntry(i,drug));
        }

        return drugs;
    }

    private void deleteGraphs() {

    }


    private void deletePatient() {
    }


    private void updateChart12(int yVal) {
        tableRow = (TableRow) urine.getChildAt(2);

        TableRow temp = (TableRow) tableRow.getChildAt(amountX);
        TextView tempText = (TextView) temp.getChildAt(0);
        tempText.setText(Integer.toString(yVal));

        Date currentTime = new Date();
        previousEntry = new PreviousEntry("amount",amountX, currentTime);

        amountX+=2;
        okSound.start();
        isInputOk = true;
        graphSaved = false;
    }

    private void updateChart11(String yVal) {
        tableRow = (TableRow) urine.getChildAt(1);

        TableRow temp = (TableRow) tableRow.getChildAt(acetoneX);
        TextView tempText = (TextView) temp.getChildAt(0);
        String ans = null;
        if(yVal.contains(digitsPlus[0])){
            ans = "---";
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
            speak("invalid input.");
            return;
        }

        tempText.setText(ans);

        Date currentTime = new Date();
        previousEntry = new PreviousEntry("acetone",acetoneX, currentTime);

        acetoneX++;
        okSound.start();
        isInputOk = true;
        graphSaved = false;
    }

    private void updateChart10(String yVal) {
        tableRow = (TableRow) urine.getChildAt(0);

        TableRow temp = (TableRow) tableRow.getChildAt(proteanX);
        TextView tempText = (TextView) temp.getChildAt(0);
        String ans = null;
        if(yVal.contains(digitsPlus[0])){
            ans = "---";
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
            speak("invalid input");
            return;
        }

        tempText.setText(ans);

        Date currentTime = new Date();
        previousEntry = new PreviousEntry("protean",proteanX, currentTime);

        proteanX++;
        okSound.start();
        isInputOk = true;
        graphSaved = false;
    }

    private void updateChart9(double yVal, int local) {
        tableRow = (TableRow) temperature.getChildAt(0);

        TableRow temp = (TableRow) tableRow.getChildAt(tempX);
        TextView tempText = (TextView) temp.getChildAt(0);
//        if(local == 1){
//            tempText.setText(Double.toString(yVal)+" C");
//        }
//        else{
//            tempText.setText(Double.toString(yVal)+" F");
//        }
        if((yVal < 32.00) || ((yVal>40.00) && (yVal < 54.00))){
            unsafeSound.start();
        }
        if((yVal > 32.00) && (yVal<40.00)){
            tempText.setText(Double.toString(yVal)+" C");
        }
        else if((yVal > 90.00) && (yVal<103.00))
            tempText.setText(Double.toString(yVal)+" F");
        else{
            Toast.makeText(getApplicationContext(),"Temperature not normal",Toast.LENGTH_LONG).show();
        }

        Date currentTime = new Date();
        previousEntry = new PreviousEntry("temperature",tempX, currentTime);

        tempX++;
        okSound.start();
        isInputOk = true;
        graphSaved = false;
    }

    private void updateChart8(int yVal) {
        tableRow = (TableRow) oxytocin.getChildAt(1);

        TableRow temp = (TableRow) tableRow.getChildAt(oxyDrX);
        TextView tempText = (TextView) temp.getChildAt(0);
        tempText.setText(Integer.toString(yVal));

        oxyDrX++;
        okSound.start();
        isInputOk = true;
    }

    private void updateChart7(int yVal) {
        tableRow = (TableRow) oxytocin.getChildAt(0);

        TableRow temp = (TableRow) tableRow.getChildAt(oxyAmX);
        TextView tempText = (TextView) temp.getChildAt(0);
        tempText.setText(Integer.toString(yVal));

        oxyAmX++;
        okSound.start();
        isInputOk = true;
    }

    private void updateChart6(String yVal) {
        tableRow = (TableRow) fluid.getChildAt(1);

        TableRow temp = (TableRow) tableRow.getChildAt(mouldingX);
        TextView tempText = (TextView) temp.getChildAt(0);
        String ans = null;
        Log.i("fluid input", "updateChart6: " + yVal);
        if(yVal.equalsIgnoreCase(digitsPlus[0])){
            ans = "0";
        }
        else if(yVal.equalsIgnoreCase(digitsPlus[1]))
        {
            ans = "+";
        }
        else if(yVal.equalsIgnoreCase(digitsPlus[2]))
        {
            ans = "++";
        }
        else if(yVal.equalsIgnoreCase(digitsPlus[3]))
        {
            ans = "+++";
        }
        else{
            invalidSound.start();
            speak("invalid input");
            return;
        }

        tempText.setTextSize(12);
        tempText.setText(ans);

        Date currentTime = new Date();
        previousEntry = new PreviousEntry("moulding",mouldingX, currentTime);

        mouldingX++;
        okSound.start();
        isInputOk = true;
        graphSaved = false;
    }

    private void updateChart5(String yVal) throws EncoderException {
        tableRow = (TableRow) fluid.getChildAt(0);
        String yValInserted = fuildInputCheck(yVal);
//        if(yVal.equalsIgnoreCase("i") || yVal.equalsIgnoreCase("c") || yVal.equalsIgnoreCase("m") || yVal.equalsIgnoreCase("b")){
        if(!yValInserted.equals("")){
            TableRow temp = (TableRow) tableRow.getChildAt(fluidX);
            TextView tempText = (TextView) temp.getChildAt(0);
            tempText.setText(yValInserted.substring(0,1));

            Date currentTime = new Date();
            previousEntry = new PreviousEntry("fluid",fluidX, currentTime);

            fluidX++;
            okSound.start();
            isInputOk = true;
            graphSaved = false;
        }
        else{
            speak("invalid input");
        }
    }

    private String fuildInputCheck(String yVal) throws EncoderException {
        String []inputs = new String[]{"Intact","Color","Maconium"};
        int x = 0, count = 0;
        Soundex soun = new Soundex();
        for(String  s: inputs){
            x = soun.difference(yVal,s);
            if(x>2){
                return s;
            }
        }
        return "";
    }

    private boolean checkNumberValidity(String text) {
        String []words = text.split(" ");

        Log.i("text", "checkNumberValidity: "+text);

//        Toast.makeText(getApplicationContext(), "input here", Toast.LENGTH_LONG).show();

        if(text.equals("")){
            return false;
        }
        if(words.length > 4)
        {
            return false;
        }
        String prunedNumber = pruneNumber(text);

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

    private void updateChart3(String text) {
        contractionData.removeDataSet(contractionDataSet);

        if(contractionPointsAdded == false)
        {
            myHelper.deleteAll("contractionGraph");
        }
        int last = text.indexOf("second");

        if(last < 0){
            speak("Invalid input for contraction");
            invalidSound.start();
            return;
        }
        int region = 0;

//        String number = "";
//        number = text.substring(0,last);
//        String yValString = number.trim();

//        if(!checkNumberValidity(yValString)){
//            Log.i("yvalString", "updateChart3: "+yValString);
//            Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_LONG).show();
//            invalidSound.start();
//            return;
//        }
//        int seconds = convertWordsToNum(yValString);
//        int seconds = 0;
//        try {
//            seconds = Integer.parseInt(yValString);
//        } catch (Exception e) {
//            e.printStackTrace();
//            speak("Invalid input for contraction");
//            invalidSound.start();
//            return;
//        }
//        Log.i("value", "updateChart3: " + seconds);
        if(text.contains("under 20 seconds") || text.contains("under 20 second")){
            region = 1;
        }
        else if(text.contains("more than 20 seconds") || text.contains("more than 20 second")){
            region = 2;
        }
        else if(text.contains("more than 40 seconds") || text.contains("more than 40 second")){
            region = 3;
        }
        text = text.replace("under 20 seconds","");
        text = text.replace("under 20 second","");
        text = text.replace("more than 20 seconds","");
        text = text.replace("more than 20 second","");
        text = text.replace("more than 40 seconds","");
        text = text.replace("more than 40 second","");
        text = text.trim();

        int lastIn = text.length();
        String secondsPhrase = text.substring(0,lastIn);
        String secondsNumber = secondsPhrase.replace("seconds", "");
        secondsNumber = secondsNumber.replace("second", "");
        secondsNumber = secondsNumber.replace("times","");
        secondsNumber = secondsNumber.replace("time","");
        secondsNumber = secondsNumber.replace("x","");
        secondsNumber = secondsNumber.replace("X","");
        String valueSeconds = secondsNumber.trim();
        if(valueSeconds.equalsIgnoreCase("to") || valueSeconds.equalsIgnoreCase("two") || valueSeconds.equalsIgnoreCase("tu"))
            valueSeconds = "2";
        if(valueSeconds.equalsIgnoreCase("free") || valueSeconds.equalsIgnoreCase("three"))
            valueSeconds = "3";
        if(valueSeconds.equalsIgnoreCase("four") || valueSeconds.equalsIgnoreCase("for"))
            valueSeconds = "4";
        if(valueSeconds.equalsIgnoreCase("five"))
            valueSeconds = "5";
        if(valueSeconds.equalsIgnoreCase("one"))
            valueSeconds = "1";
//        if(!checkNumberValidity(valueSeconds)){
//            Log.i("valueSeconds", "updateChart3: "+valueSeconds+"hello");
//            Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_LONG).show();
//            invalidSound.start();
//            return;
//        }
//        int yVal = convertWordsToNum(valueSeconds);
        int yVal = 0;
        try {
            yVal = Integer.parseInt(valueSeconds);
        } catch (Exception e) {
            e.printStackTrace();
            speak("Invalid input for contraction");
        }
        Log.i("seconds", "updateChart3: " + yVal);

        if((yVal < 1) || (yVal > 5)){
            Toast.makeText(getApplicationContext(),"Input out of Contraction range", Toast.LENGTH_LONG).show();
            invalidSound.start();
            invalidSound.start();
            return;
        }

        myHelper.insertData(contractionX, yVal, "contractionGraph");

        if(region == 3)
            barColors.add(getResources().getColor(R.color.contraction3));
        else if(region == 2)
            barColors.add(getResources().getColor(R.color.contraction2));
        else if(region == 1)
            barColors.add(getResources().getColor(R.color.contraction1));

        contraction_regions.add(region);

        contractionPointsAdded = true;

        loadContraction();

        Date currentDate = new Date();
        previousEntry = new PreviousEntry("contractionGraph", contractionX, currentDate);

        contractionX += 1;
        okSound.start();
        isInputOk = true;
        graphSaved = false;

    }

    private void loadContraction() {
        List<BarEntry> tempbars = getBarData();

        if(contractionDataSet == null){
            contractionDataSet = new BarDataSet(tempbars,"bardata");
            contractionDataSet.setDrawValues(false);
        }


        else{
            contractionDataSet.clear();
            contractionDataSet.setValues(tempbars);
        }

        contractionData.setBarWidth(1f);
        contractionGraph.getLegend().setEnabled(false);
        contractionGraph.setFitBars(true);
        contractionDataSet.setColors(barColors);
        contractionData.addDataSet(contractionDataSet);
        contractionGraph.clear();
        contractionGraph.setData(contractionData);
        contractionGraph.invalidate();

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

    private void zoneMonitor(String graph, double xValue, double yValue) {
        boolean alert = false;
        if(graph.equals("cervical")){
            alert = checkCervicalForYellow(xValue,yValue);
            if(!zoneCharts.contains(graph) && alert)
                zoneCharts.add(graph);
            if(!alert)
                zoneCharts.remove("cervical");
        }
        else if(graph.equals("fetal")){
            alert = checkFetalForYellow(yValue);
            if(!zoneCharts.contains(graph) && alert)
                zoneCharts.add(graph);
            if(!alert)
                zoneCharts.remove("fetal");
        }

        if(alert)
            notifier.setBackgroundColor(getResources().getColor(R.color.notifier_yellow));
        if(zoneCharts.isEmpty())
            notifier.setBackgroundColor(getResources().getColor(R.color.notifier_green));

    }

    private boolean checkFetalForYellow(double yValue) {
        if((yValue > 160) || (yValue < 120))
            return true;
        else
            return false;
    }

    private boolean checkCervicalForYellow(double xValue, double yValue) {
        double x = xValue - 2*yValue + 8;
        if(x>0)
            return true;
        else
            return false;
    }

    private void updateChart1(int yVal) {
        fetalData.removeDataSet(fetalDataSet);

        if(fetalPointsAdded == false)
        {
            myHelper.deleteAll("fetalGraph");
        }

        if((yVal < 80) || (yVal > 200)){
            Toast.makeText(getApplicationContext(),"Input out of Fetal heart rate range", Toast.LENGTH_LONG).show();
            speak("Input out of Fetal heart rate range");
            invalidSound.start();
            return;
        }

        if((yVal < 110) || (yVal > 160)){
            Toast.makeText(getApplicationContext(),"Input out of Fetal heart rate safezone", Toast.LENGTH_LONG).show();
            unsafeSound.start();
        }

        zoneMonitor("fetal",fetalX,yVal);
        myHelper.insertData(fetalX, yVal,"fetalGraph");

        fetalPointsAdded = true;

        loadFetal();

        Date currentDate = new Date();
        previousEntry = new PreviousEntry("fetalGraph", fetalX, currentDate);

        fetalX += 1;
        okSound.start();
        isInputOk = true;
        graphSaved = false;
    }
    //    https://brightinventions.pl/blog/charts-on-android-2/
    private void updateChart2(int yVal, int x){
        if(x == 1){

            if(!cervicalPointsAdded)
            {
                if((initializationFlag == 1) && updateEveryX(yVal)){
                    initializationFlag = 2;
                    partoGraphInitialized = true;
                    setTimeSeries();
                }
                myHelper.deleteAll("cervicalGraph");
                if(!descendPointAdded)
                    myHelper.deleteAll("descendGraph");
            }

            if((yVal < 4) || (yVal > 10)){
                Toast.makeText(getApplicationContext(),"Input out of cervical dialation range", Toast.LENGTH_LONG).show();
                invalidSound.start();
                return;
            }
            zoneMonitor("cervical", cervicalX, yVal);
            myHelper.insertData(cervicalX, yVal, "cervicalGraph");

//            Entry newEntry = new Entry(cervicalX,yVal);
//            newEntry.setIcon();
//            cervicalDataSet.setDrawIcons(true);
//            cervicalList.add(newEntry);
//
//            printListCervical();
            cervicalPointsAdded = true;

            Date currentDate = new Date();
            previousEntry = new PreviousEntry("cervicalGraph", cervicalX, currentDate);

            cervicalX += 8;
        }
        else{
            if(!descendPointAdded)
            {
                myHelper.deleteAll("descendGraph");
                if(!cervicalPointsAdded)
                    myHelper.deleteAll("cervicalGraph");
            }

            if((yVal < 1) || (yVal > 5)){
                Toast.makeText(getApplicationContext(),"Input out of feotal head descend range", Toast.LENGTH_LONG).show();
                invalidSound.start();
                return;
            }
            myHelper.insertData(descendX, yVal, "descendGraph");

            descendPointAdded = true;

            Date currentDate = new Date();
            previousEntry = new PreviousEntry("descendGraph", descendX, currentDate);

            descendX += 4;
        }

        loadCervical();


        okSound.start();
        isInputOk = true;
        graphSaved = false;
    }

    private void loadCervical() {

        ArrayList<Entry> tempCervical = getData2();
        ArrayList<Entry> tempDescend = getData5();

        cervicalDataSet = new LineDataSet(tempCervical,"Cervical Dialation");
        cervicalDataSet.setDrawCircles(true);
        cervicalDataSet.setDrawCircleHole(true);
        cervicalDataSet.setDrawValues(false);
        cervicalDataSet.setCircleColor(Color.CYAN);
        cervicalDataSet.setCircleRadius(7);
        cervicalDataSet.setCircleHoleRadius(2);



        descendDataSet = new LineDataSet(tempDescend,"Foetal Head Decent");
        descendDataSet.setLabel("Foetal Head Readings");
        descendDataSet.setDrawCircles(true);
        descendDataSet.setDrawCircleHole(true);
        descendDataSet.setDrawValues(false);
        descendDataSet.setCircleColor(R.color.descend);
        descendDataSet.setColor(R.color.descend);
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

        cervicalData = new LineData(cervicalDataSets);

        cervicalGraph.setData(cervicalData);
        cervicalGraph.invalidate();

        if(tempCervical.isEmpty()){
            cervicalPointsAdded = false;
            initializationFlag = 0;
            partoGraphInitialized = false;
            resetTimeSeries();
            resetEveryX();
        }

        if(tempDescend.isEmpty())
            descendPointAdded = false;
    }

    private boolean updateEveryX(int yVal) {
        if((yVal < 4) || (yVal > 10)){
            return false;
        }
        else{
            if(yVal == 5){
                cervicalX = 2;
                fetalX = 2;
                descendX = 2;
                contractionX = 2;
                maternalX = 2;
                pressureX = 1.5;
                fluidX = 2;
                mouldingX = 2;
                tempX = 1;
                proteanX = 1;
                acetoneX = 1;
                amountX = 1;
            }
            else if(yVal == 6){
                cervicalX = 4;
                fetalX = 4;
                descendX = 4;
                contractionX = 4;
                maternalX = 4;
                pressureX = 3.5;
                fluidX = 4;
                mouldingX = 4;
                tempX = 2;
                proteanX = 2;
                acetoneX = 2;
                amountX = 2;

            }
            else if(yVal == 7){
                cervicalX = 6;
                fetalX = 6;
                descendX = 6;
                contractionX = 6;
                maternalX = 6;
                pressureX = 5.5;
                fluidX = 6;
                mouldingX = 6;
                tempX = 3;
                proteanX = 3;
                acetoneX = 3;
                amountX = 3;

            }
            else if(yVal == 8){
                cervicalX = 8;
                fetalX = 8;
                descendX = 8;
                contractionX = 8;
                maternalX = 8;
                pressureX = 7.5;
                fluidX = 8;
                mouldingX = 8;
                tempX = 4;
                proteanX = 4;
                acetoneX = 4;
                amountX = 4;

            }
            else if(yVal == 9){
                cervicalX = 10;
                fetalX = 10;
                descendX = 10;
                contractionX = 10;
                maternalX = 10;
                pressureX = 9.5;
                fluidX = 10;
                mouldingX = 10;
                tempX = 5;
                proteanX = 5;
                acetoneX = 5;
                amountX = 5;
            }
            else if(yVal == 10){
                cervicalX = 12;
                fetalX = 12;
                descendX = 12;
                contractionX = 12;
                maternalX = 12;
                pressureX = 11.5;
                fluidX = 12;
                mouldingX = 12;
                tempX = 6;
                proteanX = 6;
                acetoneX = 6;
                amountX = 6;
            }
            return true;
        }
    }

    private void resetEveryX(){
        cervicalX = 0;
        fetalX = 0;
        descendX = 0;
        contractionX = 0;
        maternalX = 0;
        pressureX = 0.5;
        fluidX = 0;
        mouldingX = 0;
        tempX = 0;
        proteanX = 0;
        acetoneX = 0;
        amountX = 0;
    }

    private void printListCervical() {
        for(Entry e: cervicalList){
            Log.i("CervicalList", "printListCervical: "+e.toString());
        }
    }

    private void updateChart4(int yVal, int i){
        maternalData.removeDataSet(maternalDataSet);
        if(i == 1){
            if(!maternalPointsAdded)
            {
                myHelper.deleteAll("maternalGraph");
                if(!pressurePointAdded)
                    myHelper.deleteAll("pressureGraph");
            }

            if((yVal < 50) || (yVal > 110)){
                Toast.makeText(getApplicationContext(),"Input out of Patient heart rate range", Toast.LENGTH_LONG).show();
                invalidSound.start();
                return;
            }


            if((yVal < 60) || (yVal > 100)){
                Toast.makeText(getApplicationContext(),"Input out of Patient heart rate safezone", Toast.LENGTH_LONG).show();
                unsafeSound.start();
            }
            myHelper.insertData(maternalX, yVal, "maternalGraph");

            maternalPointsAdded = true;

            Date currentDate = new Date();
            previousEntry = new PreviousEntry("maternalGraph", maternalX, currentDate);

            maternalX += 1;
        }
        else if(i == 2){
            if(!pressurePointAdded){
                myHelper.deleteAll("pressureGraph");
                if(!maternalPointsAdded)
                    myHelper.deleteAll("maternalGraph");
            }
            int sysTol = yVal/1000;
            int dysTol = yVal%1000;

            if(sysTol < 100 || sysTol > 180){
                Toast.makeText(getApplicationContext(),"Input out of Patient pressure safezone", Toast.LENGTH_LONG).show();
                unsafeSound.start();
            }

            if(dysTol < 60 || dysTol > 100){
                Toast.makeText(getApplicationContext(),"Input out of Patient pressure safezone", Toast.LENGTH_LONG).show();
                unsafeSound.start();
            }


            myHelper.insertDataForPressure(pressureX,sysTol,dysTol,"pressureGraph");

            Date currentDate = new Date();
            previousEntry = new PreviousEntry("pressureGraph", pressureX, currentDate);

            pressurePointAdded = true;
            pressureX += 8.00;
        }


        loadMaternal();

        okSound.start();
        isInputOk = true;
        graphSaved = false;

    }

    private void loadMaternal() {
        ArrayList<Entry> tempPulse = getData4();
        maternalDataSet = new LineDataSet(tempPulse,"Pulse Readings");
        maternalDataSet.setDrawCircles(true);
        maternalDataSet.setDrawCircleHole(true);
        maternalDataSet.setDrawValues(false);
        maternalDataSet.setCircleColor(Color.CYAN);
        maternalDataSet.setCircleRadius(7);
        maternalDataSet.setCircleHoleRadius(2);

        pressureEntries = getDataForPressure();
        pressureDataSets = createPressureLines();

        maternalDataSets.clear();

        maternalDataSets.add(maternalDataSet);
        maternalDataSets.addAll(pressureDataSets);

        maternalGraph.clear();

        maternalData = new LineData(maternalDataSets);

        maternalGraph.setData(maternalData);
        maternalGraph.invalidate();

        if(tempPulse.isEmpty())
            maternalPointsAdded = false;
        if(pressureDataSets.isEmpty())
            pressurePointAdded = false;
    }

    private ArrayList<ILineDataSet> createPressureLines() {
        ArrayList<ILineDataSet> dp = new ArrayList<>();
        boolean flag = true;
        for(PressureEntry p : pressureEntries){
            LineDataSet temp = new LineDataSet(null, null);
            temp.addEntry(new Entry((float) p.xInput,p.sysTol));
            temp.addEntry(new Entry((float) p.xInput,p.dysTol));
            if(flag){
                temp.setLabel("Pressure");
                flag = false;
            }
            temp.setCircleColor(Color.MAGENTA);
            temp.setDrawValues(false);
            temp.setColor(Color.MAGENTA);
            dp.add(temp);
        }
        return dp;
    }

    private ArrayList<PressureEntry> getDataForPressure() {
        ArrayList<PressureEntry> dp = new ArrayList<>();
        String [] columns = {"xValues","yValues1","yValues2"};

        Cursor cursor = sqLiteDatabase.query("pressureGraph", columns, null, null, null, null, "xValues ASC");

        for(int i=0; i<cursor.getCount(); i++)
        {
            cursor.moveToNext();
            dp.add(new PressureEntry(cursor.getDouble(0),cursor.getInt(1),cursor.getInt(2)));
        }
        return dp;
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
        setupBluetooth();
        resetSpeechRecognizer();
        initializeTextToSpeech();
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        am.startBluetoothSco();
        am.setBluetoothScoOn(true);
        speech.startListening(recognizerIntent);
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "pause");
        super.onPause();
        speech.stopListening();
        tts.shutdown();
        am.setMode(AudioManager.MODE_NORMAL);
        am.stopBluetoothSco();
        am.setBluetoothScoOn(false);
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
        am.setMode(AudioManager.MODE_NORMAL);
        am.stopBluetoothSco();
        am.setBluetoothScoOn(false);
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
//            am.setStreamMute(AudioManager.STREAM_MUSIC,false);
            if(text.contains("computer")){
                Toast.makeText(getApplicationContext(),"hello initialized",Toast.LENGTH_LONG).show();
                speak("Please name a graph");
                this.context = "graph";
                recognizerIntent.putExtra(EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,15000);
                speak("Please name a graph");
//                returnedText.setText(text);
            }
//            am.setStreamMute(AudioManager.STREAM_MUSIC,true);
        }
        else if(context.equals("graph")){
            String[] words = text.split(" ");
            Toast.makeText(getApplicationContext(),"Insert Graph", Toast.LENGTH_LONG).show();
            if(words[0].equals("nothing")){
                this.context = "command";
            }
            if(words[0].equals("remove")){
                Date currentTime = new Date();

                if(previousEntry == null){
                    Toast.makeText(getApplicationContext(),"No previous inputs have been recorded", Toast.LENGTH_LONG).show();
                    speak("No inputs to delete now");
                    this.context = "command";
                }
                else{
                    long interval = (currentTime.getTime() - previousEntry.getDate().getTime())/1000;
                    if(interval > 60){
                        speak("Sorry you cannot remove your input now");
                        Toast.makeText(getApplicationContext(),"Sorry you cannot remove your input now", Toast.LENGTH_LONG).show();
                        this.context = "command";
                    }
                    else{
                        speak("DO you want to delete the previous entry");
                        Toast.makeText(getApplicationContext(),"Do you want to delete the previous entry?", Toast.LENGTH_LONG).show();
                        this.context = "remove";
                    }
                }

            }
            Soundex soundex = new Soundex();
            int x=0;
            try {
                int i = 0;
                boolean graphFlag = false;
                for(ArrayList<String> str:charts2){
                    for(String s: str){
                        x = soundex.difference(words[0],s);
                        if(x>2){
                            graphingChart = charts2.get(i).get(0);
                            selectedChart = Arrays.asList(charts).indexOf(graphingChart);
//                        selectedChart = Arrays.binarySearch(charts,str);
                            Log.i("SelectedChart", "onResults: "+graphingChart);
                            graphFlag = true;
                            break;
                        }
                    }
                    if(graphFlag == true)
                        break;
                    i++;
                }

            } catch (EncoderException e) {
                e.printStackTrace();
            }
            if((x>2) && (!words[0].equals("nothing"))){
//                chartBox.setText(graphingChart);
//                recognizerIntent.putExtra(EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,10000);
//                if(graphingChart.equals("morning"))
//                    speak("moulding"+" graph selected, please insert value");
//                else

                if(selectedChart == 0)
                    scrollView.smoothScrollTo(0, fetalGraph.getTop()-100);
                else if(selectedChart == 1)
                    scrollView.smoothScrollTo(0, cervicalGraph.getTop()-100);
                else if(selectedChart == 2)
                    scrollView.smoothScrollTo(0, contractionGraph.getTop()-100);
                else if(selectedChart == 3)
                    scrollView.smoothScrollTo(0, maternalGraph.getTop()-100);
                else if(selectedChart == 4)
                    scrollView.smoothScrollTo(0, cervicalGraph.getTop()-100);
                else if(selectedChart == 5)
                    scrollView.smoothScrollTo(0, fluidLayout.getTop()-100);
                else if(selectedChart == 6)
                    scrollView.smoothScrollTo(0, fluidLayout.getTop()-100);
                else if(selectedChart == 7)
                    scrollView.smoothScrollTo(0, tempLayout.getTop()-100);
                else if(selectedChart == 8)
                    scrollView.smoothScrollTo(0, urineLayout.getTop()-100);
                else if(selectedChart == 9)
                    scrollView.smoothScrollTo(0, urineLayout.getTop()-100);
                else if(selectedChart == 10)
                    scrollView.smoothScrollTo(0, urineLayout.getTop()-100);
                else if(selectedChart == 11)
                    scrollView.smoothScrollTo(0, maternalGraph.getTop()-100);

                if(!partoGraphInitialized){
                    if(selectedChart != 1){
                        Toast.makeText(getApplicationContext(),"You must enter cervical dialation 1st",Toast.LENGTH_LONG).show();
                        this.context = "graph";
                        speak("Enter cervical graph first");
                    }
                    else{

                        initializationFlag = 1;
                        speak(graphingChart+" graph selected, please insert value");
                        this.context = "number";
                    }
                }
                else{
                    speak(graphingChart+" graph selected, please insert value");
                    this.context = "number";
                }

            }
        }
        else if(context.equals("remove")){
            if(text.contains("yes")){
                String graph = previousEntry.getGraphName();
                int newIntX = -1;
                double newDoubleX = -1;
                try {
                    newIntX = (int)previousEntry.getxValue();
                }
                catch (Exception e){
                    newDoubleX = (double)previousEntry.getxValue();
                }

                if(graph.equals("fetalGraph") || graph.equals("cervicalGraph") || graph.equals("descendGraph") || graph.equals("contractionGraph") || graph.equals("maternalGraph") || graph.equals("pressureGraph")){
                    myHelper.deleteEntry(graph);
                    if(graph.equals("fetalGraph") && (newIntX != -1)){

                        fetalX = newIntX;

                        loadFetal();
                        previousEntry = null;
                        ArrayList<Entry> tempList = new ArrayList<>();
                        tempList = getData1();
                        Entry tempEntry;
                        if(!tempList.isEmpty()){
                            tempEntry = tempList.get(tempList.size()-1);

                            double xValue, yValue;
                            xValue = tempEntry.getX();
                            yValue = tempEntry.getY();
                            zoneMonitor("fetal", xValue,yValue);

                        }

//                        try {
//                            Cursor c = myHelper.getHighestValues(graph);
//                            values = new int[] {c.getInt(0),c.getInt(1)};
//                            zoneMonitor("fetal", values[0],values[1]);
//                        }catch (Exception e){
//                            speak("there is a problem");
//                        }
//                        Cursor c = myHelper.getHighestValues(graph);
//                        int index = c.getColumnIndexOrThrow("xValues");
//                        int xValue = c.getInt(index);
//                        index = c.getColumnIndexOrThrow("yValues");
//                        int yValue = c.getInt(index);


                    }
                    else if(graph.equals("cervicalGraph") && (newIntX != -1)){
                        cervicalX = newIntX;

                        loadCervical();
                        previousEntry = null;
                        ArrayList<Entry> tempList;

                        tempList = getData1();

                        if(!tempList.isEmpty()){

                            Entry tempEntry = tempList.get(tempList.size()-1);

                            double xValue, yValue;
                            xValue = tempEntry.getX();
                            yValue = tempEntry.getY();

                            zoneMonitor("cervical", xValue,yValue);
                        }
                    }
                    else if(graph.equals("descendGraph") && (newIntX != -1)){
                        descendX = newIntX;
                        loadCervical();
                        previousEntry = null;
                    }
                    else if(graph.equals("contractionGraph") && (newIntX != -1)){
                        contractionX = newIntX;
                        if(barColors.size() > 0)
                            barColors.remove(barColors.size()-1);
                        loadContraction();
                        previousEntry = null;
                    }
                    else if(graph.equals("maternalGraph") && (newIntX != -1)){
                        maternalX = newIntX;
                        loadMaternal();
                        previousEntry = null;
                    }
                    else if(graph.equals("pressureGraph") && (newDoubleX != -1)){
                        pressureX = newDoubleX;
                        loadMaternal();
                        previousEntry = null;
                    }
                }
                else{
                    if(graph.equals("moulding")){
                        TextView tempText = (TextView) ((TableRow)((TableRow) fluid.getChildAt(1)).getChildAt(newIntX)).getChildAt(0);
                        tempText.setText("");
                        mouldingX = newIntX;
                        previousEntry = null;
                    }
                    if(graph.equals("fluid")){
                        TextView tempText = (TextView) ((TableRow)((TableRow) fluid.getChildAt(0)).getChildAt(newIntX)).getChildAt(0);
                        tempText.setText("");
                        fluidX = newIntX;
                        previousEntry = null;
                    }
                    if(graph.equals("temperature")){
                        TextView tempText = (TextView) ((TableRow)((TableRow) temperature.getChildAt(0)).getChildAt(newIntX)).getChildAt(0);
                        tempText.setText("");
                        tempX = newIntX;
                        previousEntry = null;
                    }
                    if(graph.equals("protean")){
                        TextView tempText = (TextView) ((TableRow)((TableRow) urine.getChildAt(0)).getChildAt(newIntX)).getChildAt(0);
                        tempText.setText("");
                        proteanX = newIntX;
                        previousEntry = null;
                    }
                    if(graph.equals("acetone")){
                        TextView tempText = (TextView) ((TableRow)((TableRow) urine.getChildAt(1)).getChildAt(newIntX)).getChildAt(0);
                        tempText.setText("");
                        acetoneX = newIntX;
                        previousEntry = null;
                    }
                    if(graph.equals("amount")){
                        TextView tempText = (TextView) ((TableRow)((TableRow) urine.getChildAt(2)).getChildAt(newIntX)).getChildAt(0);
                        tempText.setText("");
                        amountX = newIntX;
                        previousEntry = null;
                    }
                }
            }
            else{
                speak("waiting for your command");
            }

            this.context = "command";
        }
        else if(context.equals("number")){
//            numberBox.setText(text);
//            am.setStreamMute(AudioManager.STREAM_MUSIC,false);
            if(text.equals("nothing"))
                this.context = "command";
            else{
                Toast.makeText(getApplicationContext(),"Graph: "+ charts[selectedChart],Toast.LENGTH_LONG).show();
                if(initializationFlag >= 1){
                    if(selectedChart == 1){

                        speak("you have inserted "+text);

                        text = text.replace("centimetre","");
                        text = text.replace("cm","");
                        text = text.replace("Centimetre", "");
                        text = text.replace("CM","");

                        text = text.trim();
//                if(!checkNumberValidity(text)){
//                    Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_LONG).show();
//                    invalidSound.start();
//                    return;
//                }
                        if(text.equalsIgnoreCase("to") || text.equalsIgnoreCase("two") || text.equalsIgnoreCase("tu") || text.equalsIgnoreCase("too"))
                            text = "2";
                        if(text.equalsIgnoreCase("free") || text.equalsIgnoreCase("three"))
                            text = "3";
                        if(text.equalsIgnoreCase("four") || text.equalsIgnoreCase("for"))
                            text = "4";
                        if(text.equalsIgnoreCase("five") || text.equalsIgnoreCase("size"))
                            text = "5";
                        if(text.equalsIgnoreCase("one"))
                            text = "1";
                        if(text.equalsIgnoreCase("sex") || text.equalsIgnoreCase("six"))
                            text = "6";
                        if(text.equalsIgnoreCase("seven"))
                            text = "7";
                        if(text.equalsIgnoreCase("eight") || text.equalsIgnoreCase("it"))
                            text = "8";
                        if(text.equalsIgnoreCase("nine"))
                            text = "9";
                        if(text.equalsIgnoreCase("ten") || text.equalsIgnoreCase("full dialation"))
                            text = "10";
                        int yVal = 0;

                        try {
                            yVal = Integer.parseInt(text);
                            updateChart2(yVal,1);

                        } catch (Exception e) {
                            e.printStackTrace();
                            speak("You must insert a number");
                        }
                        scrollView.smoothScrollTo(0, cervicalGraph.getTop()-100);

                    }
                }
                if(partoGraphInitialized){
                    if(selectedChart == 0){

                        speak("you have inserted "+text);
                        Log.i("fetalGraph", "onResults: "+ "this is talking");

                        int yVal = 0;
                        try {
                            yVal = Integer.parseInt(text);
                            updateChart1(yVal);
                        } catch (Exception e) {
                            e.printStackTrace();
                            speak("You must insert a number");
                        }
                        scrollView.smoothScrollTo(0, fetalGraph.getTop()-100);
                    }

                    else if(selectedChart == 2){

                        speak("you have inserted "+text);
//                int yVal = Integer.parseInt(text);
                        updateChart3(text);
                        scrollView.smoothScrollTo(0, contractionGraph.getTop()-100);

                    }
                    else if(selectedChart == 3){
                        speak("you have inserted "+text);
//                int yVal = Integer.parseInt(text);
                        int yVal = 0;
                        try {
                            yVal = Integer.parseInt(text);
                            updateChart4(yVal,1);

                        } catch (Exception e) {
                            e.printStackTrace();
                            speak("You must insert a number");
                        }

                        scrollView.smoothScrollTo(0, maternalGraph.getTop()-100);
                    }
                    else if(selectedChart == 4){
                        text = text.replace("5 by","");
                        text = text.replace("five by","");
                        text = text.trim();
                        speak("you have inserted "+text);
                        if(text.equalsIgnoreCase("to") || text.equalsIgnoreCase("two") || text.equalsIgnoreCase("tu") || text.equalsIgnoreCase("too"))
                            text = "2";
                        if(text.equalsIgnoreCase("free") || text.equalsIgnoreCase("three"))
                            text = "3";
                        if(text.equalsIgnoreCase("four") || text.equalsIgnoreCase("for"))
                            text = "4";
                        if(text.equalsIgnoreCase("five"))
                            text = "5";
                        if(text.equalsIgnoreCase("one"))
                            text = "1";
//                if(!checkNumberValidity(text)){
//                    Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_LONG).show();
//                    invalidSound.start();
//                    return;
//                }
                        int yVal = 0;
                        try {
                            yVal = Integer.parseInt(text);
                            updateChart2(yVal,2);

                        } catch (Exception e) {
                            e.printStackTrace();
                            speak("You must insert a number");
                        }
                        scrollView.smoothScrollTo(0, cervicalGraph.getTop()-100);
                    }
                    else if(selectedChart == 5){
                        speak("you have inserted "+text);
//                int yVal = Integer.parseInt(text);
                        try {
                            updateChart5(text);
                        } catch (EncoderException e) {
                            e.printStackTrace();
                        }
                        scrollView.smoothScrollTo(0, fluidLayout.getTop()-100);
                    }

                    else if(selectedChart == 6){

                        text = text.replace("plus","");
                        text = text.replace("Plus","");
                        text = text.replace("class","");
                        text = text.replace("Class","");
                        text = text.replace("+","");
                        text = text.trim();

                        if(text.equalsIgnoreCase("one"))
                            text = "1";
                        if(text.equalsIgnoreCase("free"))
                            text = "3";
                        if(text.equalsIgnoreCase("tu") || text.equalsIgnoreCase("to"))
                            text = "2";

                        Log.i("updatechart6", "onResults: " + text);
                        speak("you have inserted "+text);
//                int yVal = Integer.parseInt(text);
                        updateChart6(text);
                        scrollView.smoothScrollTo(0, fluidLayout.getTop()-100);
                    }
                    else if(selectedChart == 7){

                        scrollView.smoothScrollTo(0,tempLayout.getTop()-100);
                        speak("you have inserted "+text);
                        text = text.replace("degree","");
                        text = text.replace("degrees","");
                        int local = 0;
                        if(text.contains("centigrade"))
                            local = 1;
                        else
                            local = 2;
                        text = text.replace("centigrade","");
                        text = text.replace("fahrenheit","");
                        text = text.replace("celsius","");
                        text = text.replace("Celsius","");

                        text = text.trim();

                        double yVal = 0.0;
                        try {
                            yVal = Double.parseDouble(text);
                            updateChart9(yVal,local);

                        } catch (Exception e) {
                            e.printStackTrace();
                            speak("You must insert a number");
                        }


                    }
                    else if(selectedChart == 8){
                        text = text.replace("plus","");
                        text = text.replace("Plus","");
                        text = text.replace("class","");
                        text = text.replace("Class","");
                        text = text.replace("+","");
                        text = text.trim();

                        if(text.equalsIgnoreCase("one"))
                            text = "1";
                        if(text.equalsIgnoreCase("free"))
                            text = "3";
                        if(text.equalsIgnoreCase("tu") || text.equalsIgnoreCase("to"))
                            text = "2";

                        Log.i("updatechart6", "onResults: " + text);
                        speak("you have inserted "+text);
//                int yVal = Integer.parseInt(text);
                        updateChart10(text);
                        scrollView.smoothScrollTo(0, urineLayout.getTop()-100);
                    }
                    else if(selectedChart == 9){
                        text = text.replace("plus","");
                        text = text.replace("Plus","");
                        text = text.replace("class","");
                        text = text.replace("Class","");
                        text = text.replace("+","");
                        text = text.trim();

                        if(text.equalsIgnoreCase("one"))
                            text = "1";
                        if(text.equalsIgnoreCase("free"))
                            text = "3";
                        if(text.equalsIgnoreCase("tu") || text.equalsIgnoreCase("to"))
                            text = "2";

                        Log.i("updatechart6", "onResults: " + text);
                        speak("you have inserted "+text);
//                int yVal = Integer.parseInt(text);
                        updateChart11(text);
                        scrollView.smoothScrollTo(0, urineLayout.getTop()-100);
                    }
                    else if(selectedChart == 10){

                        speak("you have inserted "+text);
//                int yVal = Integer.parseInt(text);
                        int yVal = 0;

                        try {
                            yVal = Integer.parseInt(text);
                            updateChart12(yVal);

                        } catch (Exception e) {
                            e.printStackTrace();
                            speak("You must insert a number");
                        }
                        scrollView.smoothScrollTo(0, urineLayout.getTop()-100);
                    }
                    else if(selectedChart == 11){
                        speak("you have inserted "+text);
//                int yVal = Integer.parseInt(text);
                        String newInput = text.replace("by", "");
                        newInput = newInput.trim().replaceAll("\\s+", " ");
                        newInput = newInput.replace("/"," ");
                        String []pressures = newInput.split(" ");

                        int yVal = 0;

                        try{
                            yVal = Integer.parseInt(pressures[0])*1000+Integer.parseInt(pressures[1]);
                            updateChart4(yVal,2);
                        }catch (Exception e){
                            e.printStackTrace();
                            speak("Invalid input");
                        }

                        scrollView.smoothScrollTo(0, maternalGraph.getTop()-100);
                    }
                    recognizerIntent.putExtra(EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,5000);
                }

            }

            if(isInputOk){
                this.context = "command";
                isInputOk = false;
            }
//            am.setStreamMute(AudioManager.STREAM_MUSIC,true);
        }
//        Toast.makeText(getApplicationContext(),"context "+context,Toast.LENGTH_LONG).show();
        Log.i("TextNow", "Current: " + text);

        speech.startListening(recognizerIntent);
    }

    private void loadFetal() {

        fetalDataSet.clear();

        ArrayList<Entry> tempList = getData1();
        fetalDataSet.setValues(tempList);
        fetalDataSet.setLabel("Readings");
        fetalDataSet.setDrawCircles(true);
        fetalDataSet.setDrawCircleHole(true);
        fetalDataSet.setDrawValues(false);
        fetalDataSet.setCircleColor(Color.CYAN);
        fetalDataSet.setCircleRadius(6);
        fetalDataSet.setCircleHoleRadius(3);

        fetalData.addDataSet(fetalDataSet);
        fetalGraph.clear();
        fetalGraph.setData(fetalData);
        fetalGraph.invalidate();

        if(tempList.isEmpty())
            fetalPointsAdded = false;

    }

    @Override
    public void onBackPressed(){
        exitingGraph = true;
        if(!graphSaved){
            showConfirmationDialogue();
        }
        else{
            exitPartoGraph();
        }
    }

    private void exitPartoGraph() {
        Intent intent = new Intent();
        intent.putExtra("newDocument", document_id);
        Log.i("giving_document_id", "onBackPressed: "+document_id);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onEvent");
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
//
//        ProgressBar progressBar;
//        progressBar = new ProgressBar(TestingPartograph.this);
//        progressBar.set
//        super.onCreate(savedInstanceState, persistentState);
//    }

    //    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        Toast.makeText(this.getApplicationContext(),"ON key up evenfired",Toast.LENGTH_LONG).show();
//        super.onBackPressed();
//        return super.onKeyUp(keyCode, event);
//    }

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