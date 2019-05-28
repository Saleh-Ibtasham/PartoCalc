package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountInfo extends Fragment {


    private CircleImageView setupImage;
    private Uri mainImageURI = null;

    private String user_id;

    private boolean isChanged = false;

    private EditText setupName,setupEmpId,setupNumber,setupType;
    private Button setupBtn;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Bitmap compressedImageFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_info, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        setupImage = view.findViewById(R.id.setup_image);
        setupEmpId = view.findViewById(R.id.para);
        setupName = view.findViewById(R.id.patient_name);
        setupType = view.findViewById(R.id.type);
        setupBtn = view.findViewById(R.id.patient_create);
        setupNumber = view.findViewById(R.id.hosnum);


        setupName.setKeyListener(null);
        setupType.setKeyListener(null);
        setupEmpId.setKeyListener(null);
        setupNumber.setKeyListener(null);

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

                        setupName.setText(name);
                        setupEmpId.setText(empId);
                        setupType.setText(type);
                        setupNumber.setText(phone);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.mipmap.default_image);

                        Glide.with(AccountInfo.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);


                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(AccountInfo.this.getContext(), "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

            }
        });

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent;
                settingIntent = new Intent(getActivity(),AccountSettings.class);
                startActivity(settingIntent);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

}
