package com.example.myapplication;

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


public class PartocalcActivity extends AppCompatActivity{

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partocalc_activity);

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

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.partocalc_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return true;
    }
}
