package com.example.she.otherui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.she.R;
import com.example.she.databinding.ActivityProfileBinding;
import com.example.she.fragments.ProfileFragment;
import com.example.she.models.UserModel;
import com.example.she.utils.AndroidUtils;
import com.example.she.utils.FirebaseUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import papaya.in.sendmail.SendMail;

public class ProfileActivity extends AppCompatActivity {
    ImageView imageView;
    Button updateButton;
    EditText username;
    ProgressBar progressBar;
    UserModel userModel;
    Uri selectedImageUri;
    ActivityResultLauncher<Intent> imagePickLauncher;
    ActivityProfileBinding binding;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.pink));
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    selectedImageUri = data.getData();
                    AndroidUtils.loadImage(selectedImageUri, imageView, getApplicationContext());
                }
            }
        });


        imageView = findViewById(R.id.imageProfile);
        updateButton = findViewById(R.id.updateButtonProfile);
        username = findViewById(R.id.usernameProfile);
        progressBar = findViewById(R.id.progressbarProfileFrag);

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
        binding.backButton.setOnClickListener(view -> onBackPressed());
        binding.shareButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Download our app from here : \n" +
                    "https://drive.google.com/file/d/1VH31ep1FUKqvQO8_lTIRZN-z75BpGDuZ/view?usp=sharing");
            startActivity(Intent.createChooser(intent, "Share Via "));
        });
        binding.logoutButton.setOnClickListener(view -> {
            Dialog dialog = new Dialog(ProfileActivity.this, R.style.dialogue);
            dialog.setContentView(R.layout.dialog_layout_logout);
            Button no, yes;
            yes = dialog.findViewById(R.id.yesbnt);
            no = dialog.findViewById(R.id.nobnt);
            yes.setOnClickListener(v1 -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            });
            no.setOnClickListener(v2 -> dialog.dismiss());
            dialog.show();
        });
        binding.feedbackButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Give Feedback")
            .setMessage("We are happy to hear you ❤️" +
                    "\nPlease write your feedback 🥰 ");
            final EditText editText = new EditText(ProfileActivity.this);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(editText);
            builder.setPositiveButton("submit", (dialogInterface, i) -> {

                SendMail mail = new SendMail("contact.mm.she@gmail.com", "cqplugjuiovcgdoy",
                        "contact.mm.she@gmail.com",
                        "Feedback from: " + userEmail,
                        editText.getText().toString());
                mail.execute();
                Toast.makeText(this, "Feedback Sent", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("cancel", (dialogInterface, i) -> {

                dialogInterface.cancel();

            });
            builder.show();
        });
        binding.checkUpdateButton.setOnClickListener(view -> {
            startLoadingDialog(2000);
        });
        binding.notificationButton.setOnClickListener(view->{
            startActivity(new Intent(ProfileActivity.this, NotificationActivity.class));
        });


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
                            Toast.makeText(ProfileActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Not Updated Successfully", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private void getUserdata() {
        setInProgress(true);
        FirebaseUtils.getCurrentUserProfilePicStorageReference().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AndroidUtils.loadImage(task.getResult(), imageView, getApplicationContext());
                    }
                });
        FirebaseUtils.getCurrentUserDetailsFromDatabaseReference()
                .get().addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        userModel = task.getResult().getValue(UserModel.class);
                        username.setText(userModel.getName());
                        userEmail = userModel.getMail();

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


    public void startLoadingDialog(int time) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(getLayoutInflater().inflate(R.layout.custom_dialog, null));
        builder.setCancelable(false);
        AlertDialog show =builder.create();

        show.show();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            show.dismiss();
            builder.setView(null);
            builder.setTitle("No Update found")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        }, time);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}