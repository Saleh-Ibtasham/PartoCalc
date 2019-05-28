package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class AccountSettings extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageURI = null;

    private String user_id;

    private boolean isChanged = false;

    private EditText setupName,setupEmpId,setupNumber;
    private String setupType;
    private Button setupBtn;
    private ProgressBar setupProgress;
    private Spinner spinner;
    private boolean typeSelected = false;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Toolbar setupToolbar;

    private Bitmap compressedImageFile;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        setupToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        setupImage = findViewById(R.id.setup_image);
        setupEmpId = findViewById(R.id.para);
        setupName = findViewById(R.id.patient_name);
        setupBtn = findViewById(R.id.patient_create);
        setupNumber = findViewById(R.id.hosnum);
        spinner = findViewById(R.id.spinner);
        setupProgress = findViewById(R.id.progressBar);

        final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(AccountSettings.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.types));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String empId = task.getResult().getString("employeeID");
                        String phone = task.getResult().getString("phone");
                        String type = task.getResult().getString("type");
                        String image = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);
                        int spinnerPosition = myAdapter.getPosition(type);
                        spinner.setSelection(spinnerPosition);
                        setupName.setText(name);
                        setupEmpId.setText(empId);

                        setupNumber.setText(phone);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.mipmap.default_image);

                        Glide.with(AccountSettings.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);


                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(AccountSettings.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    setupType = (String) spinner.getItemAtPosition(spinner.getSelectedItemPosition());
                    typeSelected = true;
                } else if (i == 2) {
                    setupType = (String) spinner.getItemAtPosition(spinner.getSelectedItemPosition());
                    typeSelected = true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                typeSelected = false;
            }
        });
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString();
                final String empId = setupEmpId.getText().toString();
                final String type = setupType;
                final String phone = setupNumber.getText().toString();


                if (!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(empId)&& !TextUtils.isEmpty(type)&& typeSelected == true && !TextUtils.isEmpty(phone)&& mainImageURI != null) {

                    setupProgress.setVisibility(View.VISIBLE);

                    if (isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();

                        File newImageFile = new File(mainImageURI.getPath());
                        try {

                            compressedImageFile = new Compressor(AccountSettings.this)
                                    .setMaxHeight(125)
                                    .setMaxWidth(125)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();

                        UploadTask image_path = storageReference.child("profile_images").child(user_id + ".jpg").putBytes(thumbData);

                        image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if(!task.isSuccessful())
                                    throw task.getException();
                                return storageReference.child("profile_images").child(user_id + ".jpg").getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()) {
                                    storeFirestore(task, user_name,empId,type,phone);

                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(AccountSettings.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                    setupProgress.setVisibility(View.INVISIBLE);

                                }
                            }
                        });

                    } else {

                        storeFirestore(null, user_name, empId, type, phone);

                    }

                }

            }

        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(AccountSettings.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(AccountSettings.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(AccountSettings.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }

        });

    }
    private void storeFirestore(Task<Uri> task, String userName, String empId, String type, String phone) {

        Uri download_uri;

        if(task != null) {

            download_uri = task.getResult();

        } else {

            download_uri = mainImageURI;

        }

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", userName);
        userMap.put("employeeID",empId);
        userMap.put("type",type);
        userMap.put("phone",phone);
        userMap.put("image", download_uri.toString());


        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(AccountSettings.this, "The user Settings are updated.", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(AccountSettings.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(AccountSettings.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);

            }
        });


    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(AccountSettings.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
