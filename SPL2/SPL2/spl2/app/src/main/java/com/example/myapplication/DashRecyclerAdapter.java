package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;

public class DashRecyclerAdapter extends RecyclerView.Adapter<DashRecyclerAdapter.ViewHolder>{
    public List<Patient> dash_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Task<QuerySnapshot> querySnapshot;
    private List<DocumentSnapshot> list;

    public DashRecyclerAdapter(List<Patient> dash_list){
        this.dash_list = dash_list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);


        final String blogPostId = dash_list.get(position).PatientId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();


        String user_id = dash_list.get(position).getId();

        CollectionReference patients = firebaseFirestore.collection("patients");
        Query query = patients.whereEqualTo("userId",currentUserId);
        querySnapshot = query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        createHolder(holder,document);
                    }

                }
            }
        });


    }

    private void createHolder(ViewHolder holder,QueryDocumentSnapshot document) {

        ArrayList<String> desctext = new ArrayList<>();
        desctext.add(document.get("name").toString());
        desctext.add(document.get("gravida").toString());
        desctext.add(document.get("para").toString());
        desctext.add(document.get("hosNum").toString());
        desctext.add(document.get("hours").toString());
        desctext.add(document.get("membranes").toString());
        desctext.add(document.get("admissionDate").toString());
        desctext.add(document.get("admissionTime").toString());
        holder.setDashItem(desctext);
    }

    @Override
    public int getItemCount() {
        return dash_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private EditText name, gradiva, para, admissionDate, admissionTime, hosnum, hours , membranes, status;

        private Button viewBtn;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            viewBtn = mView.findViewById(R.id.dash_btn);
        }

        public void setDashItem(ArrayList<String> descText){

            name = mView.findViewById(R.id.patient_name_input);
            gradiva = mView.findViewById(R.id.gravida);
            para = mView.findViewById(R.id.para);
            admissionDate = mView.findViewById(R.id.admission_date_input);
            admissionTime = mView.findViewById(R.id.admission_time_input);
            hosnum = mView.findViewById(R.id.hospital_num_input);
            hours = mView.findViewById(R.id.hours_input);
            membranes = mView.findViewById(R.id.membrane_input);
            status = mView.findViewById(R.id.status_input);

            name.setText(descText.get(0));
            gradiva.setText(descText.get(1));
            para.setText(descText.get(2));
            hosnum.setText(descText.get(3));
            hours.setText(descText.get(4));
            membranes.setText(descText.get(5));
            admissionDate.setText(descText.get(6));
            admissionTime.setText(descText.get(7));


        }


    }
}
