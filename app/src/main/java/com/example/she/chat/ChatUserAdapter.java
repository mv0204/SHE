package com.example.she.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.she.R;
import com.example.she.models.UserModel;
import com.example.she.utils.AndroidUtils;

import java.util.ArrayList;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.viewholder> {
    Context context;
    ArrayList<UserModel> usersArrayList;

    public ChatUserAdapter(ChatsHome context, ArrayList<UserModel> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;
    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_chat_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        UserModel user = usersArrayList.get(position);
        holder.username.setText(user.getName());
        AndroidUtils.loadImage(Uri.parse(user.getProfile()), holder.userImg, context);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatWindow.class);
            intent.putExtra("name", user.getName());
            intent.putExtra("receiverImg", user.getProfile());
            intent.putExtra("uId", user.getUserId());
            context.startActivity(intent);

        });

    }


    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        ImageView userImg;
        TextView username;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.imageViewUserProfile);
            username = itemView.findViewById(R.id.username);

        }
    }
}
