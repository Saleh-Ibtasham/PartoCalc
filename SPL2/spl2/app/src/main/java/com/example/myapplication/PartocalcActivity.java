package com.example.myapplication;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PartocalcActivity extends AppCompatActivity{

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    private Toolbar toolbar;
    private  String graphId;

    private FirebaseFirestore firebaseFirestore;

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

}
