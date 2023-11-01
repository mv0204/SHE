package com.example.she.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.she.R;
import com.example.she.databinding.ActivityChatWindowBinding;
import com.example.she.models.ChatMessageModel;
import com.example.she.utils.AndroidUtils;
import com.example.she.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatWindow extends AppCompatActivity {
    public static String senderImg;
    public static String receiverIImg;
    String receiverImg, receiverUid, receiverName, senderUID;
    ActivityChatWindowBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    String senderRoom, receiverRoom;
    RecyclerView recyclerView;
    ArrayList<ChatMessageModel> messagesArrayList;
    ChatMessageAdapter chatMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatWindowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = findViewById(R.id.recyclerViewCW);


        firebaseAuth = FirebaseAuth.getInstance();

        receiverName = getIntent().getStringExtra("name");
        receiverImg = getIntent().getStringExtra("receiverImg");
        receiverUid = getIntent().getStringExtra("uId");
        messagesArrayList = new ArrayList<>();

        setRecyclerAdapter();
        AndroidUtils.loadImage(Uri.parse(receiverImg), binding.profileImg, this);
        binding.recivername.setText("" + receiverName);

        senderUID = firebaseAuth.getUid();

        senderRoom = senderUID + receiverUid;
        receiverRoom = receiverUid + senderUID;

        DatabaseReference chatReference = FirebaseUtils.getUserChatReference().child(senderRoom).child("messages");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessageModel messages = dataSnapshot.getValue(ChatMessageModel.class);
                    messagesArrayList.add(messages);
                }
                chatMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }); // updating messages and showing in ui

        DatabaseReference reference = FirebaseUtils.getUserDetailsFromDatabaseReference(senderUID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profile").getValue().toString();
                receiverIImg = receiverImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });  // for initialising pics


        binding.sendBtn.setOnClickListener(view -> {
            String message = binding.chatMsg.getText().toString();
            if (message.isEmpty()) {
                Toast.makeText(ChatWindow.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.chatMsg.setText("");
            Date date = new Date();
            ChatMessageModel messages = new ChatMessageModel(message, senderUID, date.getTime());

            database = FirebaseDatabase.getInstance();
            chatReference.push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    FirebaseUtils.getUserChatReference().child(receiverRoom).child("messages")
                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            });
        });

    }

    private void setRecyclerAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatMessageAdapter = new ChatMessageAdapter(ChatWindow.this, messagesArrayList);
        recyclerView.setAdapter(chatMessageAdapter);
    }
}