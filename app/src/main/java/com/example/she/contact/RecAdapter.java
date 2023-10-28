package com.example.she.contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.she.R;

import java.util.ArrayList;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.RecViewholder> {
    ArrayList<RecModel> userlist;
    Context context;
    Activity activity;

    public RecAdapter(Activity activity,ArrayList<RecModel> userlist,Context context) {
        this.activity=activity;
        this.userlist = userlist;
        this.context=context;
    }


    @NonNull
    @Override
    public RecViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_design, parent, false);
        return new RecViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecViewholder holder, int position) {
        int resource = userlist.get(position).getImg();
        String msg = userlist.get(position).getTxt();
        String msg2 = userlist.get(position).getTxt2();
        holder.imageView.setImageResource(resource);
        holder.textViewname.setText(msg);
        holder.textViewphone.setText(msg2);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"clicked",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context, UpdateContact.class);
                intent.putExtra("nameI",String.valueOf(userlist.get(holder.getAdapterPosition()).getTxt()));
                intent.putExtra("noI",String.valueOf(userlist.get(holder.getAdapterPosition()).getTxt2()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class RecViewholder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewname, textViewphone;
        View mView;

        public RecViewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            textViewname = itemView.findViewById(R.id.textViewName);
            textViewphone = itemView.findViewById(R.id.textViewPhone);
            mView=itemView;


        }
    }
}
