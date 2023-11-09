    package com.example.she.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.she.models.UserModel;
import com.github.dhaval2404.imagepicker.ImagePicker;

import com.example.she.R;
import com.example.she.utils.AndroidUtils;
import com.example.she.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

    public class ProfileFragment extends Fragment {
    ImageView imageView;
    Button updateButton;
    EditText username;
    ProgressBar progressBar;
    UserModel userModel;
    Uri selectedImageUri;
    ActivityResultLauncher<Intent> imagePickLauncher;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    selectedImageUri = data.getData();
                    AndroidUtils.loadImage(selectedImageUri, imageView, getContext());
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        imageView = view.findViewById(R.id.imageProfileFrag);
        updateButton = view.findViewById(R.id.updateButtonProfileFrag);
        username = view.findViewById(R.id.usernameProfileFrag);
        progressBar = view.findViewById(R.id.progressbarProfileFrag);

        getUserdata();

        updateButton.setOnClickListener(view1 -> {

            if (selectedImageUri != null) {
                FirebaseUtils.getCurrentUserProfilePicStorageReference().putFile(selectedImageUri)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                onClickSetData();

                            }
                        });
                userModel.setProfile(String.valueOf(selectedImageUri));


            }
            onClickSetData();
        });


        imageView.setOnClickListener(view1 -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {

                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });

        });
        return view;

    }
        private void onClickSetData() {
            setInProgress(true);
            String usernameS = username.getText().toString();
            if (usernameS.isEmpty() || usernameS.length() < 3) {
                username.setError("Enter A Valid UserName");
                return;
            }
            userModel.setName(usernameS);
            setUserdata();
        }

        private void setUserdata() {

            FirebaseUtils.getCurrentUserDetailsFromDatabaseReference()
                    .setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Not Updated Successfully", Toast.LENGTH_SHORT).show();

                    }
                }
            });


        }

        private void getUserdata() {
            setInProgress(true);
            FirebaseUtils.getCurrentUserProfilePicStorageReference().getDownloadUrl()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            AndroidUtils.loadImage(task.getResult(),imageView,getContext());
                        }
                    });
            FirebaseUtils.getCurrentUserDetailsFromDatabaseReference()
                    .get().addOnCompleteListener( task -> {
                        setInProgress(false);
                        if (task.isSuccessful()) {
                            userModel = task.getResult().getValue(UserModel.class);
                            username.setText(userModel.getName());

                        }
                    });
        }

        void setInProgress(boolean inProgress) {
            if (inProgress) {
                progressBar.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                updateButton.setVisibility(View.VISIBLE);
            }
        }

    }