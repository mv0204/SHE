package com.example.she.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.she.R;
import com.example.she.databinding.ActivityChatsHomeBinding;
import com.example.she.models.UserModel;
import com.example.she.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatsHome extends AppCompatActivity {
    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    ChatUserAdapter adapter;
    ArrayList<UserModel> usersArrayList;

    ActivityChatsHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatsHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.pink));

        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);

        auth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseUtils.getUserDatabaseReference();
        usersArrayList = new ArrayList<>();
        setRecyclerAdapter();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel users = dataSnapshot.getValue(UserModel.class);
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(users.getUserId())) {
                            usersArrayList.add(users);
                            if(binding.noMSG.getVisibility()==View.VISIBLE){
                            binding.noMSG.setVisibility(View.GONE);
                        }}

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void setRecyclerAdapter() {
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatUserAdapter(ChatsHome.this, usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);
    }
}