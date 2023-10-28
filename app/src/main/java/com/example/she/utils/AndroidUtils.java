package com.example.she.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.she.models.UserModel;


public class AndroidUtils {
    Context context;
    public static void passUserModelAsIntent(Intent intent, UserModel userModel){
        intent.putExtra("userName",userModel.getName());
        intent.putExtra("userId",userModel.getUserId());
        intent.putExtra("userEmail",userModel.getMail());
    }
    public static UserModel getUserModelAsIntent(Intent intent){
    UserModel userModel=new UserModel();
    userModel.setName(intent.getStringExtra("userName"));
    userModel.setUserId(intent.getStringExtra("userId"));
    userModel.setMail(intent.getStringExtra("userPhone"));
    return userModel;
    }
    public static void loadImage(Uri uri, ImageView imageView, Context context){
        Glide.with(context).load(uri).apply(RequestOptions.circleCropTransform()).into(imageView);

    }

}
