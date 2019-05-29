package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends Fragment {

    private RecyclerView blog_list_view;
    private List<Patient> blog_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DashRecyclerAdapter blogRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        blog_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null)
        {
            final String currentUserId = firebaseUser.getUid();

            blogRecyclerAdapter = new DashRecyclerAdapter(blog_list);
            blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
            blog_list_view.setAdapter(blogRecyclerAdapter);
            blog_list_view.setHasFixedSize(true);

//        CollectionReference patients = firebaseFirestore.collection("patients");
//        Query query = patients.whereEqualTo("userId",currentUserId);
//        Task<QuerySnapshot> querySnapshot = query.get();
//        for (DocumentSnapshot document : querySnapshot.getResult().getDocuments()) {
//            if(document.getType() == )
//        }

            Query firstQuery = firebaseFirestore.collection("patients").whereEqualTo("userId",currentUserId);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String patientId = doc.getDocument().getId();
                                Patient patient = doc.getDocument().toObject(Patient.class).withId(patientId);


                                blog_list.add(patient);

                                blogRecyclerAdapter.notifyDataSetChanged();

                            }
                        }

                    }

                }

            });
        }

        return  view;
    }


}
