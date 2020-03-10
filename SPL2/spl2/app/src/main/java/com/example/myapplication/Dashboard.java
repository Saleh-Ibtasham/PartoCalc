package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseChange;
import com.couchbase.lite.DatabaseChangeListener;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Dashboard extends Fragment {

    private RecyclerView partograph_list_view;
    private List<Patient> graph_list;

//    private FirebaseFirestore firebaseFirestore;
//    private FirebaseAuth firebaseAuth;
    private DashRecyclerAdapter graphRecyclerAdapter;

    DatabaseConfiguration config;
    Database db;
    String new_document_id = "";
    FloatingActionButton fab;
    int intermediate_position = -1;

    String[] bed_numbers = new String[]{"","1","2","3","4","5"};
    List<String>bed_number_strings = Arrays.asList(bed_numbers);
    private String selected_bed_number = "0";

    onClickInterface onclickInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        config = new DatabaseConfiguration();

        try {
            db = new Database("partoCalc",config);
        } catch (CouchbaseLiteException e) {
            Log.e("database: ", "onCreateView: cannot be created");
        }

        graph_list = new ArrayList<>();
        partograph_list_view = view.findViewById(R.id.dashboard);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBedSelector();
            }
        });

        onclickInterface = new onClickInterface() {
            @Override
            public void setClick(String id, int position) {
                intermediate_position = position;
                Intent patientActivity = new Intent(getContext(),TestingPartograph.class);
                patientActivity.putExtra("mode","edit");
                patientActivity.putExtra("id",id);
                startActivityForResult(patientActivity,2);
            }
        };

        Query query = QueryBuilder.select(SelectResult.expression(Meta.id)).from(DataSource.database(db)).where(Expression.property("status").equalTo(Expression.string("active")));

        try {
            ResultSet rs = query.execute();
            for(Result item: rs){
                String id = item.getString("id");

                Log.i("resulsets", "onCreateView: "+ item.getString("id"));
//                Log.i("resulsets", "onCreateView: "+ document.getArray("timeInputs").toList().toString());
                graph_list.add(createPatient(id));
            }

            graphRecyclerAdapter = new DashRecyclerAdapter(graph_list,onclickInterface);
            partograph_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
            partograph_list_view.setAdapter(graphRecyclerAdapter);
            partograph_list_view.setHasFixedSize(true);



        } catch (CouchbaseLiteException e) {
            Log.i("dashboard", "onCreateView:" + " no results gained");
        }

        return  view;
    }

    private Patient createPatient(String id) {
        Document document = db.getDocument(id);
        Patient patient = new Patient();
        patient.setBedNumber(document.getString("bedNumber"));
        patient.setName(document.getString("name"));
        patient.setAdmissionDate(document.getString("admissionDate"));
        patient.setId(id);
        return patient;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                new_document_id = data.getStringExtra("newDocument");
                Log.i("resultingDocument", "onActivityResult: " + new_document_id);
            }
        }

        if(requestCode == 2){
            if(resultCode == RESULT_OK){
                String temp_document_id = data.getStringExtra("newDocument");
                Document doc = db.getDocument(temp_document_id);
                if(doc.getString("status").equals("inactive")){
                    graph_list.remove(intermediate_position);
                    graphRecyclerAdapter.notifyDataSetChanged();
                    intermediate_position = -1;
                }
            }
        }

        if(!new_document_id.equals("") && (requestCode == 1)){
            graph_list.add(createPatient(new_document_id));
            graphRecyclerAdapter.notifyDataSetChanged();
            new_document_id="";
        }
    }

    private void openBedSelector() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        alert.setTitle("Select Bed Number");
        final View view = LayoutInflater.from(this.getContext()).inflate(R.layout.bed_selector, null);
        final Spinner input = view.findViewById(R.id.bed_number);



        ArrayAdapter<String> bed_adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,bed_numbers);
        input.setAdapter(bed_adapter);
        alert.setView(view);

        input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_bed_number = Integer.toString(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(!bed_number_strings.contains(selected_bed_number)){
                    Toast.makeText(getContext(),"Please Select a valid bed",Toast.LENGTH_LONG).show();
                }
                Query query = QueryBuilder.select(SelectResult.expression(Meta.id)).from(DataSource.database(db)).where(Expression.property("status")
                        .equalTo(Expression.string("active")).and(Expression.property("bedNumber").equalTo(Expression.string(selected_bed_number))));
                ResultSet rs = null;
                try {
                    rs = query.execute();
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

                if (rs.allResults().size() != 0){
                    Toast.makeText(getContext(),"Bed Number: "+selected_bed_number+" is occupied.",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Toast.makeText(getContext(),"Please wait.......",Toast.LENGTH_LONG).show();
                    openPartographCreation();
                    return;
                }

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void openPartographCreation() {
        if(!selected_bed_number.equals("") && !selected_bed_number.equals("0")){
            Intent patientActivity = new Intent(this.getContext(),TestingPartograph.class);
            patientActivity.putExtra("bedNumber",selected_bed_number);
            patientActivity.putExtra("mode","create");
            startActivityForResult(patientActivity,1);
        }
        else{
            Toast.makeText(getContext(),"Please select a valid bed",Toast.LENGTH_LONG).show();
        }
    }



}
