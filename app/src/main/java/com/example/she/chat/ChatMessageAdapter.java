package com.example.she.chat;

import static com.example.she.chat.ChatWindow.receiverIImg;
import static com.example.she.chat.ChatWindow.senderImg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.she.R;
import com.example.she.models.ChatMessageModel;
import com.example.she.utils.AndroidUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatMessageAdapter extends RecyclerView.Adapter{
    Context context;
    ArrayList<ChatMessageModel> messagesAdpterArrayList;
    int ITEM_SEND=1;
    int ITEM_RECEIVE=2;

    public ChatMessageAdapter(Context context, ArrayList<ChatMessageModel> messagesAdpterArrayList) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new receiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){
        ChatMessageModel message = messagesAdpterArrayList.get(position);
//        holder.itemView.setOnLongClickListener(view -> {
//            new AlertDialog.Builder(context).setTitle("Delete")
//                    .setMessage("Are you sure you want to delete this message?")
//                    .setPositiveButton("Yes", (dialogInterface, i) -> {
//
//                    }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).show();
//
//            return false;
//        });
        if (holder.getClass()==senderViewHolder.class){
            senderViewHolder viewHolder = (senderViewHolder) holder;
            viewHolder.msgTxt.setText(message.getMessage());
            AndroidUtils.loadImage(Uri.parse(senderImg),viewHolder.imageView,context);
        }else { receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.msgTxt.setText(message.getMessage());
            AndroidUtils.loadImage(Uri.parse(receiverIImg),viewHolder.imageView,context);



        }
    }
    @Override
    public int getItemViewType(int position) {
        ChatMessageModel message = messagesAdpterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesAdpterArrayList.size();
    }
    class  senderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView msgTxt;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView= itemView.findViewById(R.id.imageViewSenderProfile);
            msgTxt = itemView.findViewById(R.id.senderTxt);

        }
    }
    class receiverViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView msgTxt;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewReceiverProfile);
            msgTxt = itemView.findViewById(R.id.receiverTxt);
        }
    }
}
