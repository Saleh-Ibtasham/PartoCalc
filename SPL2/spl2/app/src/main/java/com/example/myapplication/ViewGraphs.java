package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class ViewGraphs extends Fragment {
    private RecyclerView partograph_list_view;
    private List<Patient> graph_list;

    private onClickInterface onclickInterface;

    private ViewGraphsRecyclerAdaptar viewGraphsRecyclerAdaptar;

    DatabaseConfiguration config;
    Database db;

    private int intermediate_position = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_graphs, container, false);

        config = new DatabaseConfiguration();

        try {
            db = new Database("partoCalc",config);
        } catch (CouchbaseLiteException e) {
            Log.e("database: ", "onCreateView: cannot be created");
        }

        graph_list = new ArrayList<>();
        partograph_list_view = view.findViewById(R.id.view_graphs);

        onclickInterface = new onClickInterface() {
            @Override
            public void setClick(String id, int position) {
                intermediate_position = position;
                viewPatient(id);
            }
        };

        Query query = QueryBuilder.select(SelectResult.expression(Meta.id)).from(DataSource.database(db)).orderBy(Ordering.property("admissionDate").descending());

        try {
            ResultSet rs = query.execute();
            for(Result item: rs){
                String id = item.getString("id");

                Log.i("resulsets", "onCreateView: "+ item.getString("id"));
//                Log.i("resulsets", "onCreateView: "+ document.getArray("timeInputs").toList().toString());
                graph_list.add(createPatient(id));
            }

            viewGraphsRecyclerAdaptar = new ViewGraphsRecyclerAdaptar(graph_list,onclickInterface);
            partograph_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
            partograph_list_view.setAdapter(viewGraphsRecyclerAdaptar);
            partograph_list_view.setHasFixedSize(true);



        } catch (CouchbaseLiteException e) {
            Log.i("dashboard", "onCreateView:" + " no results gained");
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void viewPatient(String id) {
        Intent patientActivity = new Intent(getContext(),ViewingPartograph.class);
        patientActivity.putExtra("id",id);
        startActivityForResult(patientActivity,1);
    }

    private Patient createPatient(String id) {
        Document document = db.getDocument(id);
        Patient patient = new Patient();
        patient.setBedNumber(document.getString("bedNumber"));
        patient.setName(document.getString("name"));
        patient.setAdmissionDate(document.getString("admissionDate"));
        patient.setHospitalNumber(document.getString("hospitalNumber"));
        patient.setId(id);
        return patient;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                String temp_document_id = data.getStringExtra("documentToDelete");
                if(!temp_document_id.equals("")){
                    Document doc = db.getDocument(temp_document_id);
                    try {
                        db.delete(doc);
                        graph_list.remove(intermediate_position);
                        viewGraphsRecyclerAdaptar.notifyDataSetChanged();
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                }
                intermediate_position = -1;
            }
        }
    }


}
