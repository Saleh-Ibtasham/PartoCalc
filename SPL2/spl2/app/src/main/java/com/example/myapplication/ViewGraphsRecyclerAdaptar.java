package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ViewGraphsRecyclerAdaptar extends RecyclerView.Adapter<ViewGraphsRecyclerAdaptar.ViewHolder> {
    public List<Patient> bed_list;
    public Context context;
    onClickInterface onclickInterface;

    public ViewGraphsRecyclerAdaptar(List<Patient> bed_list, onClickInterface onclickInterface) {
        this.bed_list = bed_list;
        this.onclickInterface = onclickInterface;
    }

    @Override
    public ViewGraphsRecyclerAdaptar.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bed_file_view, parent, false);
        context = parent.getContext();
        return new ViewGraphsRecyclerAdaptar.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewGraphsRecyclerAdaptar.ViewHolder holder, int position) {
        Patient patient = bed_list.get(position);
        holder.name.setText(patient.getName());
        holder.admissionDate.setText(patient.getAdmissionDate());
        holder.bedNumber.setText(patient.getBedNumber());
        holder.hospitalNumber.setText(patient.getHospitalNumber());
        holder.id = patient.getId();
        holder.position = position;

    }

    @Override
    public int getItemCount() {
        return bed_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView name, admissionDate, bedNumber, hospitalNumber;

        private Button viewBtn;
        private String id;
        private int position;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            name = (TextView) itemView.findViewById(R.id.patient_name_input);
            admissionDate = (TextView) itemView.findViewById(R.id.patient_create_input);
            bedNumber = (TextView) itemView.findViewById(R.id.bed_name_input);
            hospitalNumber = (TextView) itemView.findViewById(R.id.patient_hospital_number_input);
            viewBtn = mView.findViewById(R.id.view_button);

            viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("View Patent Status");
                    builder.setMessage("Do you want to view the partograph?");
//                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onclickInterface.setClick(id, position);
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
