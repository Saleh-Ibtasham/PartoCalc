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

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountInfo extends Fragment {


    private CircleImageView setupImage;
    private Uri mainImageURI = null;

    private String user_id;

    private boolean isChanged = false;

    private EditText setupName,setupEmpId,setupNumber,setupType;
    private Button setupBtn;

    private Bitmap compressedImageFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_info, container, false);



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
