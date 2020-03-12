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
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

public class ViewingPartograph extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private int SCREEN_HEIGHT;
    private int SCREEN_WIDTH;
    private Toolbar toolbar;
    private Button notifier;
    private ScrollView scrollView, scrollViewB;

    private LineChart fetalGraph, cervicalGraph, maternalGraph;
    private BarChart contractionGraph;
    private TableLayout fluid, time, oxytocin, medicine, temperature, urine;

    private LineDataSet fetalDataSet = new LineDataSet(null, null);
    private LineDataSet cervicalDataSet = new LineDataSet(null, "cervical");
    private LineDataSet descendDataSet = new LineDataSet(null, "head descend");
    private BarDataSet contractionDataSet;
    private LineDataSet maternalDataSet = new LineDataSet(null, null);

    private ArrayList<ILineDataSet> cervicalDataSets = new ArrayList<>();
    private ArrayList<ILineDataSet> maternalDataSets = new ArrayList<>();
    private ArrayList<ILineDataSet> pressureDataSets = new ArrayList<>();
    private ArrayList<PressureEntry> pressureEntries = new ArrayList<>();

    private ArrayList<String> zoneCharts = new ArrayList<>();

    private LineDataSet fetalDataSet1, fetalDataSet2, cervicalDataSet1, cervicalDataSet2;

    private LineData fetalData, cervicalData, maternalData, descendData;
    private BarData contractionData;

    private int fetalX = 0;
    private int cervicalX = 0;
    private int contractionX = 0;
    private int maternalX = 0;
    private int descendX = 0;
    private double pressureX = 0.5;
    private int fluidX = 0, mouldingX = 0, oxyAmX = 0, oxyDrX = 0, tempX = 0, proteanX = 0, acetoneX = 0, amountX = 0;

    private TextToSpeech tts;


    private MediaPlayer okSound, invalidSound, unsafeSound;

    MyHelper myHelper;
    SQLiteDatabase sqLiteDatabase;

    Database db;
    DatabaseConfiguration config;

    private boolean fetalPointsAdded = false, cervicalPointsAdded = false, contractionPointsAdded = false, maternalPointsAdded = false, descendPointAdded = false,
            pressurePointAdded = false, partoGraphInitialized = false;
    private int initializationFlag = 0;
    //    private String[] charts = {"fetal heart rate", "cervical dilatation", "contraction", "maternal pulse", "head descend five by","amniotic fluid","moulding","oxytocin amount", "oxytocin drops","temperature","protean","acetone","amount"};
    private String[] charts = {"fetal", "cervical", "contraction", "pulse", "descend", "fluid", "moulding", "temperature", "protein", "acetone", "urine", "pressure"};
    private List<ArrayList<String>> charts2 = new ArrayList<>();
    private HashMap<String, Chart> chartHashMap;
    private String[] xAxisCervical = new String[24];

    private String[] digitsPlus = {"absent", "1", "2", "3"};

    private List<Integer> barColors = new ArrayList<>();

    private BluetoothHeadset bluetoothHeadset;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothProfile.ServiceListener profileListener;

    private BluetoothDevice btDevice;

    private RelativeLayout fluidLayout, timeLayout, oxytocinLayout, tempLayout, urineLayout;
    private LinearLayout medicineLayout;

    private TableRow tableRow;


    private android.speech.SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private boolean isInputOk = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

    private PreviousEntry previousEntry = null;

    private EditText admission_date, admission_time, hours, hospital_number, para, gravida, name;
    private Spinner membrane_input;
    private String amPm, membrane;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog timePickerDialog;

    private String bed_number;
    private String mode;

    private String document_id = "";

    String[] membrane_items = new String[]{"", "no", "yes"};
    List<String> membrane_items_list = Arrays.asList(membrane_items);

    ArrayList<Integer> contraction_regions = new ArrayList<>();

    private boolean deleteGraph = false;
    private String delete_graph_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewing_partograph);

        config = new DatabaseConfiguration();
        try {
            db = new Database("partoCalc", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();

        document_id = intent.getStringExtra("id");

        toolbar = (Toolbar) findViewById(R.id.partoToolBar);
        notifier = (Button) findViewById(R.id.notifier);
        notifier.setBackgroundColor(getResources().getColor(R.color.notifier_green));
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, membrane_items);

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

        getScreenDimension();
        createHelper();
        createFetal();
        createCervical();
        createContraction();
        createMaternal();
        craeteTables();
        createGraphMaps();
        clearDatabase();

        admission_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ViewingPartograph.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
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


                timePickerDialog = new TimePickerDialog(ViewingPartograph.this, new TimePickerDialog.OnTimeSetListener() {
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
                if (parent.getItemAtPosition(position).toString().equals("yes")) {
                    hours.setEnabled(false);
                } else {
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

        populateView();
    }

    private void populateView() {
        Document doc = db.getDocument(document_id);

        Log.i("populateDocument", "populateView: " + doc.toString());

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

        loadFromDocument(listCervical, "cervical");
        loadFromDocument(listFetal, "fetal");
        loadFromDocumentContraction(listContraction, listContractionRegions, "contraction");
        loadFromDocument(listDescend, "descend");
        loadFromDocument(listPulse, "maternal");
        loadFromDocumentPressure(listPressure, "pressure");
        loadFromDocumentTables(listFluid, "fluid", 0, fluid);
        loadFromDocumentTables(listMoulding, "moulding", 1, fluid);
        loadFromDocumentTables(listTimeInputs, "timeInputs", 1, time);
        loadFromDocumentTables(listTemperature, "temperature", 0, temperature);
        loadFromDocumentTables(listProtein, "protein", 0, urine);
        loadFromDocumentTables(listAcetone, "acetone", 1, urine);
        loadFromDocumentTables(listAmount, "amount", 2, urine);
        loadFromDocumentDrugs(listDrugs, medicineLayout);

    }

    private void loadFromDocumentDrugs(List<Object> list, LinearLayout medicineLayout) {

        for (Object o : list) {
            HashMap<String, Object> normalMap = (HashMap<String, Object>) o;
            int x = Integer.parseInt(normalMap.get("xValue").toString());
            String y = normalMap.get("yValue").toString();
            View v = medicineLayout.getChildAt(x);
            ((EditText) v).setText(y);
            ((EditText) v).setEnabled(false);
        }
    }

    private void loadFromDocumentTables(List<Object> list, String tableEntity, int row, TableLayout tableLayout) {

        tableRow = (TableRow) tableLayout.getChildAt(row);

        for (Object o : list) {
            HashMap<String, Object> normalMap = (HashMap<String, Object>) o;
            int x = Integer.parseInt(normalMap.get("xValue").toString());
            String y = normalMap.get("yValue").toString();
            TableRow temp = (TableRow) tableRow.getChildAt(x);
            TextView tempText = (TextView) temp.getChildAt(0);
            tempText.setText(y);
            if (tableEntity.equals("fluid"))
                fluidX = x + 1;
            if (tableEntity.equals("moulding")) {
                mouldingX = x + 1;
                tempText.setTextSize(12);
            }
            if (tableEntity.equals("temperature"))
                tempX = x + 1;
            if (tableEntity.equals("protein"))
                proteanX = x + 1;
            if (tableEntity.equals("acetone"))
                acetoneX = x + 1;
            if (tableEntity.equals("amount"))
                amountX = x + 1;
        }
    }

    private void loadFromDocumentPressure(List<Object> list, String graph) {
        String graphTable = graph + "Graph";

        for (Object o : list) {
            HashMap<String, Object> normalMap = (HashMap<String, Object>) o;

            Double x = Double.parseDouble(normalMap.get("xInput").toString());
            int sysTol = Integer.parseInt(normalMap.get("sysTol").toString());
            int dysTol = Integer.parseInt(normalMap.get("dysTol").toString());

            myHelper.insertDataForPressure(x, sysTol, dysTol, graphTable);
            pressurePointAdded = true;
            pressureX = x + 8;
        }
        loadMaternal();
    }

    private void loadFromDocumentContraction(List<Object> listContraction, List<Object> listContractionRegions, String graph) {
        String graphTable = graph + "Graph";

        for (int i = 0; i < listContraction.size(); i++) {
            HashMap<String, Object> entry = (HashMap<String, Object>) listContraction.get(i);
            Integer region = Integer.parseInt(listContractionRegions.get(i).toString());

            int x = Integer.parseInt(entry.get("x").toString());
            int y = Integer.parseInt(entry.get("y").toString());
            myHelper.insertData(x, y, graphTable);

            if (region == 3)
                barColors.add(getResources().getColor(R.color.contraction3));
            else if (region == 2)
                barColors.add(getResources().getColor(R.color.contraction2));
            else if (region == 1)
                barColors.add(getResources().getColor(R.color.contraction1));

            contraction_regions.add(region);
            contractionPointsAdded = true;
            contractionX = x + 1;

        }
        loadContraction();
    }

    private void loadFromDocument(List<Object> list, String graph) {

        String graphTable = graph + "Graph";
        if (graph == "cervical") {
            initializationFlag = 1;
        }

        for (Object o : list) {
            HashMap<String, Object> normalMap = (HashMap<String, Object>) o;

            int x = Integer.parseInt(normalMap.get("x").toString());
            int y = Integer.parseInt(normalMap.get("y").toString());
            myHelper.insertData(x, y, graphTable);
            if (graph.equals("fetal")) {
                zoneMonitor(graph, x, y);
                fetalPointsAdded = true;
                fetalX = x + 1;
            }
            if (graph.equals("cervical")) {
                if ((initializationFlag == 1) && updateEveryX(y)) {
                    initializationFlag = 2;
                    partoGraphInitialized = true;
                }
                zoneMonitor(graph, x, y);
                cervicalPointsAdded = true;
                cervicalX = x + 8;
            }
            if (graph.equals("descend")) {
                descendPointAdded = true;
                descendX = x + 1;
            }
            if (graph.equals("pulse")) {
                maternalPointsAdded = true;
                maternalX = x + 1;
            }

        }

        if (graph.equals("fetal") || graph.equals("descend"))
            loadFetal();
        if (graph.equals("cervical"))
            loadCervical();
        if (graph.equals("pulse"))
            loadMaternal();


    }

    private void clearDatabase() {
        if (!fetalPointsAdded) {
            myHelper.deleteAll("fetalGraph");
        }
        if (!cervicalPointsAdded) {
            myHelper.deleteAll("cervicalGraph");
        }
        if (!descendPointAdded) {
            myHelper.deleteAll("descendGraph");
        }
        if (!contractionPointsAdded) {
            myHelper.deleteAll("contractionGraph");
        }
        if (!maternalPointsAdded) {
            myHelper.deleteAll("maternalGraph");
        }
        if (!pressurePointAdded) {
            myHelper.deleteAll("pressureGraph");
        }

    }

    private void createGraphArrays() {
        String[] fetal = new String[]{"fetal", "eagle", "peter"};//,"beetle","petal","pital","fiddle","feudal","sheetal"
        ArrayList<String> tempList = new ArrayList<>(Arrays.asList(fetal));
        charts2.add(tempList);
        String[] fluid = new String[]{"fluid", "sweet", "floyd"}; //,"free","sleep"
        tempList = new ArrayList<>(Arrays.asList(fluid));
        charts2.add(tempList);
        String[] moudling = new String[]{"moulding", "morning", "monday"};
        tempList = new ArrayList<>(Arrays.asList(moudling));
        charts2.add(tempList);
        String[] cervical = new String[]{"cervical", "sorry", "salvia", "send", "so"};
        tempList = new ArrayList<>(Arrays.asList(cervical));
        charts2.add(tempList);
        String[] descend = new String[]{"descend"};//,"desent","dissent","desend"
        tempList = new ArrayList<>(Arrays.asList(descend));
        charts2.add(tempList);
        String[] contraction = new String[]{"contraction", "construction", "congestion", "injection", "direction"};//,"sanderson","contractor","condition"
        tempList = new ArrayList<>(Arrays.asList(contraction));
        charts2.add(tempList);
        String[] pulse = new String[]{"pulse", "pass", "paisa", "spouse"};//,"parse","parts"
        tempList = new ArrayList<>(Arrays.asList(pulse));
        charts2.add(tempList);
        String[] pressure = new String[]{"pressure"};
        tempList = new ArrayList<>(Arrays.asList(pressure));
        charts2.add(tempList);
        String[] temperature = new String[]{"temperature"};
        tempList = new ArrayList<>(Arrays.asList(temperature));
        charts2.add(tempList);
        String[] protein = new String[]{"protein", "reading", "brody", "loading", "running"};//,"baby","body","bean","funny"
        tempList = new ArrayList<>(Arrays.asList(protein));
        charts2.add(tempList);
        String[] acetone = new String[]{"acetone", "epitome", "evidence", "sedone"};//,"austin","addedon","methadone"
        tempList = new ArrayList<>(Arrays.asList(acetone));
        charts2.add(tempList);
        String[] urine = new String[]{"urine", "united", "using"};//,"uranus","union","haloween","youmean"
        tempList = new ArrayList<>(Arrays.asList(urine));
        charts2.add(tempList);
    }

    private void createHelper() {
        myHelper = new MyHelper(getApplicationContext());
        sqLiteDatabase = myHelper.getWritableDatabase();
        myHelper.onCreate(sqLiteDatabase);
    }

    private void getScreenDimension() {

        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
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
        for (int i = 0; i < 12; i++) {
            TableRow temp = (TableRow) tableRow.getChildAt(i);
            TextView tempText = (TextView) temp.getChildAt(0);
            tempText.setText(Integer.toString(hours));
            hours++;
        }
    }

    private void resetTimeSeries() {
        tableRow = (TableRow) time.getChildAt(1);
        for (int i = 0; i < 12; i++) {
            TableRow temp = (TableRow) tableRow.getChildAt(i);
            TextView tempText = (TextView) temp.getChildAt(0);
            tempText.setText("");
        }
    }

    private void createFluid() {
        fluid = new TableLayout(getApplicationContext());
        fluid.setPadding(0, 0, 0, 0);
        fluid.setLayoutParams(new TableLayout.LayoutParams(fluidLayout.getWidth(), SCREEN_HEIGHT / 10));

        fluidLayout.addView(fluid);

        for (int i = 0; i < 2; i++) {
            initializeFluid(i);
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 24; j++) {
                addColumnsToFluid(i);
            }
        }
    }

    private synchronized void initializeFluid(int pos) {
        TableRow fluidRow = new TableRow(getApplicationContext());
        fluidRow.setPadding(0, 0, 0, 0);
        fluidRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, SCREEN_HEIGHT / 20));
        this.fluid.addView(fluidRow, pos);
    }

    private synchronized void addColumnsToFluid(int id) {
        TableRow tableAdd = (TableRow) fluid.getChildAt(id);
        tableRow = new TableRow(getApplicationContext());
        Log.i("Fluid", "Fluid Width: " + fluid.getWidth());
        TableRow.LayoutParams layoutParamsTableRow = new TableRow.LayoutParams(27, SCREEN_HEIGHT / 22);
        tableRow.setPadding(3, 3, 3, 3);
        tableRow.setBackground(getDrawable(R.drawable.cell_bacground));
        tableRow.setLayoutParams(layoutParamsTableRow);
        TextView label_date = new TextView(getApplicationContext());
        label_date.setText("");
        label_date.setTextSize(14);
        this.tableRow.addView(label_date);
        this.tableRow.setTag("yolo");
        tableAdd.addView(tableRow);
    }

    private void createTime() {
        time = new TableLayout(getApplicationContext());
        time.setPadding(0, 0, 0, 0);
        time.setLayoutParams(new TableLayout.LayoutParams(timeLayout.getWidth(), SCREEN_HEIGHT / 6));

        timeLayout.addView(time);

        for (int i = 0; i < 2; i++) {
            initializeTime(i);
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 12; j++) {
                addColumnsToTime(i);
            }
        }
    }

    private void addColumnsToTime(int id) {
        TableRow tableAdd = (TableRow) time.getChildAt(id);
        tableRow = new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow;
        if (id == 0) {
            layoutParamsTableRow = new TableRow.LayoutParams(54, SCREEN_HEIGHT / 18);
        } else {
            layoutParamsTableRow = new TableRow.LayoutParams(54, SCREEN_HEIGHT / 12);
        }

        tableRow.setPadding(3, 3, 3, 3);
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
        timeRow.setPadding(0, 0, 0, 0);
        timeRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, SCREEN_HEIGHT / 10));
        this.time.addView(timeRow, pos);
    }

    private void createOxytocin() {
        oxytocin = new TableLayout(getApplicationContext());
        oxytocin.setPadding(0, 0, 0, 0);
        oxytocin.setLayoutParams(new TableLayout.LayoutParams(oxytocinLayout.getWidth(), SCREEN_HEIGHT / 10));

        oxytocinLayout.addView(oxytocin);

        for (int i = 0; i < 2; i++) {
            initializeOxytocin(i);
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 24; j++) {
                addColumnsToOxytocin(i);
            }
        }
    }

    private void addColumnsToOxytocin(int id) {
        TableRow tableAdd = (TableRow) oxytocin.getChildAt(id);
        tableRow = new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow;
        layoutParamsTableRow = new TableRow.LayoutParams(27, SCREEN_HEIGHT / 20);
        tableRow.setPadding(3, 3, 3, 3);
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
        oxytocinRow.setPadding(0, 0, 0, 0);
        oxytocinRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, SCREEN_HEIGHT / 10));
        this.oxytocin.addView(oxytocinRow, pos);
    }


    private void createMedicine() {

        for (int j = 0; j < 12; j++) {
            addColumnsToMedicine(0);
        }

        medicineLayout.setRotation(-90);

    }

    private void addColumnsToMedicine(int id) {
        EditText label_date = new EditText(getApplicationContext());
        label_date.setTextSize(getResources().getDimension(R.dimen.cell_text_size));
        label_date.setWidth(SCREEN_HEIGHT / 3);
        label_date.setHeight(55);
        label_date.setText("");
        label_date.setPadding(10, 0, 0, 0);
        label_date.setBackground(getDrawable(R.drawable.cell_bacground));
        medicineLayout.addView(label_date);
    }

    private void createTemp() {
        temperature = new TableLayout(getApplicationContext());
        temperature.setPadding(0, 0, 0, 0);
        temperature.setLayoutParams(new TableLayout.LayoutParams(tempLayout.getWidth(), SCREEN_HEIGHT / 20));

        tempLayout.addView(temperature);

        initializeTemperature(0);

        for (int j = 0; j < 12; j++) {
            addColumnsToTemperature(0);
        }
    }

    private void addColumnsToTemperature(int id) {
        TableRow tableAdd = (TableRow) temperature.getChildAt(id);
        tableRow = new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow;
        layoutParamsTableRow = new TableRow.LayoutParams(53, SCREEN_HEIGHT / 20);
        tableRow.setPadding(3, 3, 3, 3);
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
        tempRow.setPadding(0, 0, 0, 0);
        tempRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, SCREEN_HEIGHT / 20));
        this.temperature.addView(tempRow, pos);
    }

    private void createUrine() {
        urine = new TableLayout(getApplicationContext());
        urine.setPadding(0, 0, 0, 0);
        urine.setLayoutParams(new TableLayout.LayoutParams(urineLayout.getWidth(), SCREEN_HEIGHT / 5));

        urineLayout.addView(urine);

        for (int i = 0; i < 3; i++) {
            initializeUrine(i);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 12; j++) {
                addColumnsToUrine(i);
            }
        }
    }

    private void addColumnsToUrine(int id) {
        TableRow tableAdd = (TableRow) urine.getChildAt(id);
        tableRow = new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow;
        layoutParamsTableRow = new TableRow.LayoutParams(53, SCREEN_HEIGHT / 20);
        tableRow.setPadding(3, 3, 3, 3);
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
        urineRow.setPadding(0, 0, 0, 0);
        urineRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, SCREEN_HEIGHT / 20));
        this.urine.addView(urineRow, pos);
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
        chartHashMap.put(charts[0], fetalGraph);
        chartHashMap.put(charts[1], cervicalGraph);
        chartHashMap.put(charts[2], contractionGraph);
        chartHashMap.put(charts[3], maternalGraph);
    }

    private void createFetal() {
        fetalGraph = findViewById(R.id.fetal_graph);

//        fetalHelper = new MyHelper(getApplicationContext());
//        fetalDB = fetalHelper.getWritableDatabase();

        fetalDataSet1 = new LineDataSet(null, null);
        fetalDataSet2 = new LineDataSet(null, null);

        fetalDataSet1.addEntry(new Entry(0, 120));
        fetalDataSet1.addEntry(new Entry(24, 120));
        fetalDataSet1.setColor(Color.GRAY);
        fetalDataSet1.setDrawValues(false);
        fetalDataSet1.setLineWidth(5);
        fetalDataSet1.setLabel("lower-limit");

        fetalDataSet2.addEntry(new Entry(0, 160));
        fetalDataSet2.addEntry(new Entry(24, 160));
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

        yAxis.setLabelCount(13, true);
        yAxitext.setLabelCount(13, true);
        xAxis.setLabelCount(25, true);
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

    private void createCervical() {
        cervicalGraph = findViewById(R.id.cervical_graph);

//        cervicalHelper = new MyHelper(getApplicationContext());
//        cervicalDB = cervicalHelper.getWritableDatabase();

//        descendHelper = new MyHelper(getApplicationContext());
//        descendDB = descendHelper.getWritableDatabase();

        cervicalDataSet1 = new LineDataSet(null, null);
        cervicalDataSet2 = new LineDataSet(null, null);

        initializeXAxisCervical();

        cervicalDataSet1.addEntry(new Entry(0, 4));
        cervicalDataSet1.addEntry(new Entry(12, 10));
        cervicalDataSet1.setColor(Color.GREEN);
        cervicalDataSet1.setLineWidth(5);
        cervicalDataSet1.setDrawValues(false);
        cervicalDataSet1.setLabel("Alert");

        cervicalDataSet2.addEntry(new Entry(8, 4));
        cervicalDataSet2.addEntry(new Entry(20, 10));
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

        yAxis.setLabelCount(13, true);
        yAxitext.setLabelCount(13, true);
        xAxis.setLabelCount(25, true);
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
        int x = 1;
        for (int i = 0; i < 24; i++) {
            xAxisCervical[i] = Integer.toString(x);
            x++;
        }
    }

    private void createContraction() {
        contractionGraph = findViewById(R.id.contraction_graph);

//        contractionHelper = new MyHelper(getApplicationContext());
//        contractionDB = contractionHelper.getWritableDatabase();

        contractionData = new BarData();

        XAxis xAxis = contractionGraph.getXAxis();
        YAxis yAxis = contractionGraph.getAxisLeft();
        YAxis yAxitext = contractionGraph.getAxisRight();

        yAxis.setLabelCount(6, true);
        yAxitext.setLabelCount(6, true);
        xAxis.setLabelCount(25, true);
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

    private void createMaternal() {
        maternalGraph = findViewById(R.id.maternal_graph);

//        maternalHelper = new MyHelper(getApplicationContext());
//        maternalDB = maternalHelper.getWritableDatabase();

        maternalData = new LineData();

        XAxis xAxis = maternalGraph.getXAxis();
        YAxis yAxis = maternalGraph.getAxisLeft();
        YAxis yAxitext = maternalGraph.getAxisRight();

        yAxis.setLabelCount(13, true);
        yAxitext.setLabelCount(13, true);
        xAxis.setLabelCount(25, true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.partocalc_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.delete_graph) {
            showConfirmationDialogue();
            return true;
        }
        if (id == R.id.exit_graphs) {
            onBackPressed();
            return true;
        }

        return true;
    }


    private void showConfirmationDialogue() {

        String CancelButtonText = "Cancel";

        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this).setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        builder.setTitle("Delete Patient Status");
        builder.setMessage("Do you want to delete the partograph of the patient permanently?");
        builder.addButton("Delete", Color.parseColor("#ffffff"), Color.parseColor("#33cc33"),
                CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGraph = true;
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
                if (deleteGraph) {
                    delete_graph_id = document_id;
                    deleteGraph = false;
                    exitPartoGraph();
                }

            }
        });
    }

    private void loadContraction() {
        List<BarEntry> tempbars = getBarData();

        if (contractionDataSet == null) {
            contractionDataSet = new BarDataSet(tempbars, "bardata");
            contractionDataSet.setDrawValues(false);
        } else {
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
        String[] columns = {"xValues", "yValues"};

        Cursor cursor = sqLiteDatabase.query("contractionGraph", columns, null, null, null, null, "xValues ASC");

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dp.add(new BarEntry(cursor.getInt(0), cursor.getInt(1)));
        }
        return dp;
    }

    private void zoneMonitor(String graph, double xValue, double yValue) {
        boolean alert = false;
        if (graph.equals("cervical")) {
            alert = checkCervicalForYellow(xValue, yValue);
            if (!zoneCharts.contains(graph) && alert)
                zoneCharts.add(graph);
            if (!alert)
                zoneCharts.remove("cervical");
        } else if (graph.equals("fetal")) {
            alert = checkFetalForYellow(yValue);
            if (!zoneCharts.contains(graph) && alert)
                zoneCharts.add(graph);
            if (!alert)
                zoneCharts.remove("fetal");
        }

        if (alert)
            notifier.setBackgroundColor(getResources().getColor(R.color.notifier_yellow));
        if (zoneCharts.isEmpty())
            notifier.setBackgroundColor(getResources().getColor(R.color.notifier_green));

    }

    private boolean checkFetalForYellow(double yValue) {
        if ((yValue > 160) || (yValue < 120))
            return true;
        else
            return false;
    }

    private boolean checkCervicalForYellow(double xValue, double yValue) {
        double x = xValue - 2 * yValue + 8;
        if (x > 0)
            return true;
        else
            return false;
    }

    //    https://brightinventions.pl/blog/charts-on-android-2/

    private void loadCervical() {

        ArrayList<Entry> tempCervical = getData2();
        ArrayList<Entry> tempDescend = getData5();

        cervicalDataSet = new LineDataSet(tempCervical, "Cervical Dialation");
        cervicalDataSet.setDrawCircles(true);
        cervicalDataSet.setDrawCircleHole(true);
        cervicalDataSet.setDrawValues(false);
        cervicalDataSet.setCircleColor(Color.CYAN);
        cervicalDataSet.setCircleRadius(7);
        cervicalDataSet.setCircleHoleRadius(2);


        descendDataSet = new LineDataSet(tempDescend, "Foetal Head Decent");
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

        Log.i("cervicalDatasets", "updateChart2: " + cervicalDataSets.size());
        Log.i("cervicalDataset", "updateChart2: " + cervicalDataSet.getEntryCount());
        Log.i("descendDataset", "updateChart2:" + descendDataSet.getEntryCount());

        cervicalGraph.clear();

        cervicalData = new LineData(cervicalDataSets);

        cervicalGraph.setData(cervicalData);
        cervicalGraph.invalidate();

        if (tempCervical.isEmpty()) {
            cervicalPointsAdded = false;
            initializationFlag = 0;
            partoGraphInitialized = false;
            resetTimeSeries();
            resetEveryX();
        }

        if (tempDescend.isEmpty())
            descendPointAdded = false;
    }

    private boolean updateEveryX(int yVal) {
        if ((yVal < 4) || (yVal > 10)) {
            return false;
        } else {
            if (yVal == 5) {
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
            } else if (yVal == 6) {
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

            } else if (yVal == 7) {
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

            } else if (yVal == 8) {
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

            } else if (yVal == 9) {
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
            } else if (yVal == 10) {
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

    private void resetEveryX() {
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

    private void loadMaternal() {
        ArrayList<Entry> tempPulse = getData4();
        maternalDataSet = new LineDataSet(tempPulse, "Pulse Readings");
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

        if (tempPulse.isEmpty())
            maternalPointsAdded = false;
        if (pressureDataSets.isEmpty())
            pressurePointAdded = false;
    }

    private ArrayList<ILineDataSet> createPressureLines() {
        ArrayList<ILineDataSet> dp = new ArrayList<>();
        boolean flag = true;
        for (PressureEntry p : pressureEntries) {
            LineDataSet temp = new LineDataSet(null, null);
            temp.addEntry(new Entry((float) p.xInput, p.sysTol));
            temp.addEntry(new Entry((float) p.xInput, p.dysTol));
            if (flag) {
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
        String[] columns = {"xValues", "yValues1", "yValues2"};

        Cursor cursor = sqLiteDatabase.query("pressureGraph", columns, null, null, null, null, "xValues ASC");

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dp.add(new PressureEntry(cursor.getDouble(0), cursor.getInt(1), cursor.getInt(2)));
        }
        return dp;
    }

    private ArrayList<Entry> getData1() {
        ArrayList<Entry> dp = new ArrayList<>();
        String[] columns = {"xValues", "yValues"};

        Cursor cursor = sqLiteDatabase.query("fetalGraph", columns, null, null, null, null, "xValues ASC");

        Log.i("FetalGetData", "getData1: " + cursor.getCount());
        if (cursor.getCount() == 0)
            return dp;

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dp.add(new Entry(cursor.getInt(0), cursor.getInt(1)));
        }
        return dp;
    }

    private ArrayList<Entry> getData2() {
        ArrayList<Entry> dp = new ArrayList<>();
        String[] columns = {"xValues", "yValues"};

        Cursor cursor = sqLiteDatabase.query("cervicalGraph", columns, null, null, null, null, "xValues ASC");

        Log.i("CervicalGetData", "getData2: " + cursor.getCount());
        if (cursor.getCount() == 0)
            return dp;
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dp.add(new Entry(cursor.getInt(0), cursor.getInt(1)));
        }
        return dp;
    }

    private ArrayList<Entry> getData4() {
        ArrayList<Entry> dp = new ArrayList<>();
        String[] columns = {"xValues", "yValues"};

        Cursor cursor = sqLiteDatabase.query("maternalGraph", columns, null, null, null, null, "xValues ASC");

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dp.add(new Entry(cursor.getInt(0), cursor.getInt(1)));
        }
        return dp;
    }

    private ArrayList<Entry> getData5() {
        ArrayList<Entry> dp = new ArrayList<>();
        String[] columns = {"xValues", "yValues"};

        Cursor cursor = sqLiteDatabase.query("descendGraph", columns, null, null, null, null, "xValues ASC");

        Log.i("DescendGetData", "getData5: " + cursor.getCount());
        if (cursor.getCount() == 0)
            return dp;
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dp.add(new Entry(cursor.getInt(0), cursor.getInt(1)));
        }
        return dp;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "pause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

        if (tempList.isEmpty())
            fetalPointsAdded = false;

    }

    @Override
    public void onBackPressed() {
        exitPartoGraph();
    }

    private void exitPartoGraph() {
        Intent intent = new Intent();
        intent.putExtra("documentToDelete", delete_graph_id);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
