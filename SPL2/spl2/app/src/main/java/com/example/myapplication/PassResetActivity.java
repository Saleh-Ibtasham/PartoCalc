package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class PassResetActivity extends AppCompatActivity {

    EditText email;
    Button resetBtn, backLgnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_reset);

        email = findViewById(R.id.email_txt);
        resetBtn = findViewById(R.id.reset_btn);
        backLgnBtn = findViewById(R.id.login_btn);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = email.getText().toString();
                    if(!TextUtils.isEmpty(s)){
                }
                else {
                    Toast.makeText(PassResetActivity.this,"Please type an Email",Toast.LENGTH_LONG).show();
                }
            }
        });


        backLgnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

    }
}
