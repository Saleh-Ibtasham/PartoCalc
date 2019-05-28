package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

//public class DashRecyclerAdapter extends RecyclerView.Adapter<DashRecyclerAdapter.ViewHolder>{
//    public List<Patient> dash_list;
//    public Context context;
//
//    private FirebaseFirestore firebaseFirestore;
//    private FirebaseAuth firebaseAuth;
//
//
//
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
//
//    }
//
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//    }
//
//    @Override
//    public int getItemCount() {
//        return dash_list.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        private View mView;
//
//        private TextView descView;
//        private ImageView blogImageView;
//        private TextView blogDate;
//
//        private TextView blogUserName;
//        private CircleImageView blogUserImage;
//
//        private ImageView blogLikeBtn;
//        private TextView blogLikeCount;
//
//        private ImageView blogCommentBtn;
//
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            mView = itemView;
//
//            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
//            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);
//
//        }
//
//        public void setDescText(String descText){
//
//            descView = mView.findViewById(R.id.blog_desc);
//            descView.setText(descText);
//
//        }
//
//        public void setBlogImage(String downloadUri, String thumbUri){
//
//            blogImageView = mView.findViewById(R.id.blog_image);
//
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.drawable.image_placeholder);
//
//            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
//                    Glide.with(context).load(thumbUri)
//            ).into(blogImageView);
//
//        }
//
//        public void setTime(String date) {
//
//            blogDate = mView.findViewById(R.id.blog_date);
//            blogDate.setText(date);
//
//        }
//
//        public void setUserData(String name, String image){
//
//            blogUserImage = mView.findViewById(R.id.blog_user_image);
//            blogUserName = mView.findViewById(R.id.blog_user_name);
//
//            blogUserName.setText(name);
//
//            RequestOptions placeholderOption = new RequestOptions();
//            placeholderOption.placeholder(R.drawable.profile_placeholder);
//
//            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);
//
//        }
//
//        public void updateLikesCount(int count){
//
//            blogLikeCount = mView.findViewById(R.id.blog_like_count);
//            blogLikeCount.setText(count + " Likes");
//
//        }
//
//    }
//}
