package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class PartocalcActivity extends AppCompatActivity{

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SectionsPageAdapter mSectionsPageAdapter;
    private DroidSpeech droidSpeech;
    private OnDSListener onDSListener;

    private ViewPager mViewPager;
    private Toolbar toolbar;
    private String graphId;
    private Button cont;
    private String speechResult;

    private FirebaseFirestore firebaseFirestore;

    private TextToSpeech tts;
    private SpeechRecognizer speechRecog = SpeechRecognizer.createSpeechRecognizer(this);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partocalc_activity);

        graphId = getIntent().getStringExtra("graphId");
        firebaseFirestore = FirebaseFirestore.getInstance();

        toolbar = (Toolbar) findViewById(R.id.partoToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Graph");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        droidSpeech = new DroidSpeech(this,null);
        onDSListener = new OnDSListener() {
            @Override
            public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages) {

            }

            @Override
            public void onDroidSpeechRmsChanged(float rmsChangedValue) {

            }

            @Override
            public void onDroidSpeechLiveResult(String liveSpeechResult) {
                Toast.makeText(PartocalcActivity.this,liveSpeechResult,Toast.LENGTH_LONG).show();
                processResult(liveSpeechResult);
            }

            @Override
            public void onDroidSpeechFinalResult(String finalSpeechResult) {
                Toast.makeText(PartocalcActivity.this,finalSpeechResult,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDroidSpeechClosedByUser() {

            }

            @Override
            public void onDroidSpeechError(String errorMsg) {
                Toast.makeText(PartocalcActivity.this,errorMsg,Toast.LENGTH_LONG).show();
            }
        };

        droidSpeech.setOnDroidSpeechListener(onDSListener);
        droidSpeech.setContinuousSpeechRecognition(true);

        mViewPager.setCurrentItem(1);

        initializeTextToSpeech();

        droidSpeech.startDroidSpeechRecognition();
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


    private void processResult(String result_message) {
        result_message = result_message.toLowerCase();

        Toast.makeText(PartocalcActivity.this, result_message, Toast.LENGTH_LONG).show();

//        Handle at least four sample cases

//        First: What is your Name?
//        Second: What is the time?
//        Third: Is the earth flat or a sphere?
//        Fourth: Open a browser and open url
        if(result_message.indexOf("what") != -1){
            if(result_message.indexOf("your name") != -1){
                speak("My Name is Mr.Android. Nice to meet you!");
            }
            if (result_message.indexOf("time") != -1){
                speak("The time is now: " );
            }
        } else if (result_message.indexOf("earth") != -1){
            speak("Don't be silly, The earth is a sphere. As are all other planets and celestial bodies");
        } else if (result_message.indexOf("browser") != -1){
            speak("Opening a browser right away master.");
        }
    }

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

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new CervicalActivity(), "Cervical Dialatation");
        adapter.addFragment(new BarActivity(), "Contractions");
        adapter.addFragment(new FetalHeartRate(),"Fetal Heart Rate");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

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

    @Override
    protected void onPause() {
        super.onPause();
        tts.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Reinitialize the recognizer and tts engines upon resuming from background such as after openning the browser
        initializeSpeechRecognizer();
        initializeTextToSpeech();
    }



}
