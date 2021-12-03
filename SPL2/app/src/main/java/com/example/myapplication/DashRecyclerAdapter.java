package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

public class DashRecyclerAdapter extends RecyclerView.Adapter<DashRecyclerAdapter.ViewHolder>{
    public List<Patient> dash_list;
    public Context context;
    onClickInterface onclickInterface;


    public DashRecyclerAdapter(List<Patient> dash_list, onClickInterface onclickInterface){
        this.dash_list = dash_list;
        this.onclickInterface = onclickInterface;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bed_file_dashboard, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        Patient patient = dash_list.get(position);
        holder.name.setText(patient.getName());
        holder.admissionDate.setText(patient.getAdmissionDate());
        holder.bedNumber.setText(patient.getBedNumber());
        holder.hospitalNumber.setText(patient.getHospitalNumber());
        holder.id = patient.getId();
        holder.postion = position;
    }

    @Override
    public int getItemCount() {
        return dash_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView name, admissionDate, bedNumber,hospitalNumber;

        private Button viewBtn;
        private String id;
        private int postion;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            name = (TextView) itemView.findViewById(R.id.patient_name_input);
            admissionDate = (TextView) itemView.findViewById(R.id.patient_create_input);
            bedNumber = (TextView) itemView.findViewById(R.id.bed_name_input);
            hospitalNumber = (TextView) itemView.findViewById(R.id.patient_hospital_number_input);
            viewBtn = mView.findViewById(R.id.update_button);

            viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Edit Patient Status");
                    builder.setMessage("Do you want to edit the partograph?");
//                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(v.getContext(),"Please wait while the partograph is initializing....",Toast.LENGTH_SHORT).show();
                            onclickInterface.setClick(id,postion);
                            return;
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.show();
                }
            });
        }

    }
}
