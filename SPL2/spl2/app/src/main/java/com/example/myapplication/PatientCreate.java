package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PatientCreate extends AppCompatActivity {

    EditText name,gravida,para,hosNum,membrane,hours,admissionDate,admissionTime;
    Toolbar toolbar;
    Button createPatient;
    ProgressBar progressBar;


    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog timePickerDialog;
    private String amPm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_form);

        name = findViewById(R.id.patient_name);
        gravida = findViewById(R.id.gravida);
        para = findViewById(R.id.para);
        hosNum = findViewById(R.id.hosnum);
        membrane = findViewById(R.id.membrane);
        hours = findViewById(R.id.hours);
        admissionDate = findViewById(R.id.admission_date);
        admissionTime = findViewById(R.id.admission_time);
        createPatient = findViewById(R.id.patient_create);
        progressBar = findViewById(R.id.progressBar);


        toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create PatientFile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        createPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String patient_name = name.getText().toString();
                final String patient_gravida = gravida.getText().toString();
                final String patient_para = para.getText().toString();
                final String patient_hosnum = hosNum.getText().toString();
                final String patient_membrane = membrane.getText().toString();
                final String patient_hour = hours.getText().toString();
                final String patient_adTime = admissionTime.getText().toString();
                final String patient_adDate = admissionDate.getText().toString();

                Toast.makeText(PatientCreate.this, "Patient Created",Toast.LENGTH_LONG).show();

                Intent calcActivity = new Intent(PatientCreate.this,TestingPartograph.class);
                startActivity(calcActivity);
                finish();

                if(!TextUtils.isEmpty(patient_name) && !TextUtils.isEmpty(patient_gravida) &&!TextUtils.isEmpty(patient_hosnum)
                        &&!TextUtils.isEmpty(patient_membrane) &&!TextUtils.isEmpty(patient_para) &&!TextUtils.isEmpty(patient_hour)){
                    progressBar.setVisibility(View.VISIBLE);
//                    storeFirestore(patient_name,patient_gravida,patient_para,patient_hour,patient_membrane,
//                            patient_para,patient_hosnum,patient_adTime,patient_adDate);
                }
            }
        });

        admissionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        PatientCreate.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date = month + "/" + day + "/" + year;
                admissionDate.setText(date);
            }
        };

        admissionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);


                timePickerDialog = new TimePickerDialog(PatientCreate.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        admissionTime.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();
            }
        });
    }
    public void storeFirestore(String patient_name, String patient_gravida, String patient_para, String patient_hour,
                               String patient_membrane, String patientPara, String patient_hosnum, String patient_adTime, String patient_adDate){

    }

    private void createGraph(final String graphId, String userId) {

        final Map<String , String > newmap = new HashMap<>();
        newmap.put("userId",userId);


//        firebaseFirestore.collection("graphs").document(graphId).set(newmap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful())
//                {
//                    Toast.makeText(PatientCreate.this,"Added graph with ID: " + graphId, Toast.LENGTH_LONG).show();
//                    Intent calcActivity = new Intent(PatientCreate.this,PartocalcActivity.class);
//                    calcActivity.putExtra("graphId",graphId);
//                    startActivity(calcActivity);
//                    finish();
//                }
//                else{
//                    String e = task.getException().getMessage();
//                    Toast.makeText(PatientCreate.this,"(FireStore Error) : " + e,Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

}
