package com.example.myapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private String current_user_id;
    private String current_user_email;
    private static int DELAY_TIME = 1000;
    private Database db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        mAuth = FirebaseAuth.getInstance();
//        firebaseFirestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.partoToolBar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.main_content);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        View heaeder = navigationView.getHeaderView(0);
////        ImageView header_image = (ImageView) heaeder.findViewById(R.id.nav_image);
////        TextView header_username = (TextView) heaeder.findViewById(R.id.nav_username);
////        TextView header_email = (TextView) heaeder.findViewById(R.id.nav_email);
//
////        setNavBar(header_image,header_username,header_email);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        
        openDatabase();
//        deleteCouchbaesDatabase();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Dashboard()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }



    private void deleteCouchbaesDatabase() {
        try {
            db.delete();
            db.close();
            db = null;
            Log.i("database_deleted", "deleteCouchbaesDatabase: Deleted Database");
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private void openDatabase() {
        CouchbaseLite.init(this.getApplicationContext());
        DatabaseConfiguration config = new DatabaseConfiguration();
        try {
            db = new Database("partoCalc", config);
        } catch (CouchbaseLiteException e) {
            Log.i("Database Error: ", "openDatabase: failed to open");
        }

    }

//    private void setNavBar(final ImageView header_image, final TextView header_username, final TextView header_email) {
//
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        if(firebaseUser != null)
//        {
//            String user_id = firebaseUser.getUid();
//            final String email = firebaseUser.getEmail();
//            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                    if(task.isSuccessful()){
//
//                        if(task.getResult().exists()){
//
//                            String name = task.getResult().getString("name");
//                            String image = task.getResult().getString("image");
//
//                            header_username.setText(name);
//                            header_email.setText(email);
//
//                            RequestOptions placeholderRequest = new RequestOptions();
//                            placeholderRequest.placeholder(R.mipmap.ic_launcher_round);
//
//                            Glide.with(MainActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(header_image);
//
//
//                        }
//
//                    } else {
//
//                        String error = task.getException().getMessage();
//                        Toast.makeText(MainActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();
//
//                    }
//
//                }
//            });
//        }
//
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Dashboard()).commit();
                break;
            case R.id.nav_bed_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BedManagement()).commit();
                break;
            case R.id.nav_view_graphs:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ViewGraphs()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        closeDatabase();
        super.onDestroy();
    }

    private void closeDatabase() {
        if(db != null){
            try {
                db.close();
            } catch (CouchbaseLiteException e) {
                Log.i("dbClose:", "closeDatabase: database cannot be closed.");
            }
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if(currentUser == null){
//            sendToLogin();
//            finish();
//        }
//        else {
//
//            current_user_id = mAuth.getCurrentUser().getUid();
//            current_user_email = mAuth.getCurrentUser().getEmail();
//
//            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                    if(task.isSuccessful()){
//
//                        if(!task.getResult().exists()){
//
//                            Intent setupIntent = new Intent(MainActivity.this, AccountSettings.class);
//                            startActivity(setupIntent);
//                            finish();
//
//                        }
//
//                    } else {
//
//                        String errorMessage = task.getException().getMessage();
//                        Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
//
//
//                    }
//
//                }
//            });
//
//        }
//
//    }

//    private void logOut() {
//
//        this.mAuth.signOut();
//        sendToLogin();
//    }
//    private void sendToLogin() {
//
//        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(loginIntent);
//        finish();
//
//    }

}