package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
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
import java.util.List;

public class BedManagement extends Fragment {

    private RecyclerView partograph_list_view;
    private List<Patient> graph_list;

    private onClickInterface onclickInterface;

    private BedManagementRecyclerAdapter bedRecyclerAdapter;

    DatabaseConfiguration config;
    Database db;
    String delete_document_id = "";
    FloatingActionButton fab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bed_management, container, false);

        config = new DatabaseConfiguration();

        try {
            db = new Database("partoCalc",config);
        } catch (CouchbaseLiteException e) {
            Log.e("database: ", "onCreateView: cannot be created");
        }

        graph_list = new ArrayList<>();
        partograph_list_view = view.findViewById(R.id.bed_management);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllDocuments();
            }
        });

        onclickInterface = new onClickInterface() {
            @Override
            public void setClick(String id, int position) {
                graph_list.remove(position);
                deletePatient(id);
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

            bedRecyclerAdapter = new BedManagementRecyclerAdapter(graph_list,onclickInterface);
            partograph_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
            partograph_list_view.setAdapter(bedRecyclerAdapter);
            partograph_list_view.setHasFixedSize(true);



        } catch (CouchbaseLiteException e) {
            Log.i("dashboard", "onCreateView:" + " no results gained");
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void deletePatient(String id) {
        Document document = db.getDocument(id);

        try {
            db.delete(document);
            Toast.makeText(getContext(),"Partograph of Patient successfully deleted",Toast.LENGTH_LONG).show();
            bedRecyclerAdapter.notifyDataSetChanged();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
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

    private void deleteAllDocuments() {
        Toast.makeText(getContext(),"This feature is still on hold.",Toast.LENGTH_LONG).show();
    }

}
