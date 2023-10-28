package com.example.she.gpt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.she.R;

import java.util.ArrayList;

public class MessageAdapterGPT extends RecyclerView.Adapter<MessageAdapterGPT.Viewholder> {

    ArrayList<MessageModelGPT> messageList;
    Context context;

    public MessageAdapterGPT(ArrayList<MessageModelGPT> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageAdapterGPT.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout,null);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapterGPT.Viewholder holder, int position) {
    MessageModelGPT message=messageList.get(position);
    if (message.getSentBy().equals(MessageModelGPT.SENT_BY_ME)){
        holder.leftChatView.setVisibility(View.GONE);
        holder.rightChatView.setVisibility(View.VISIBLE);
        holder.rightChatTextView.setText(message.getMessage());
    }else {
        holder.rightChatView.setVisibility(View.GONE);
        holder.leftChatView.setVisibility(View.VISIBLE);
        holder.leftChatTextView.setText(message.getMessage());
    }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{


        LinearLayout leftChatView,rightChatView;
        TextView leftChatTextView,rightChatTextView;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            leftChatView=itemView.findViewById(R.id.leftChatView);
            leftChatTextView=itemView.findViewById(R.id.leftChatTextView);
            rightChatTextView=itemView.findViewById(R.id.rightChatTextView);
            rightChatView=itemView.findViewById(R.id.rightChatView);
        }
    }
}
