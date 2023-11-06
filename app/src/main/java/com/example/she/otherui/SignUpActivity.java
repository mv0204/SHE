package com.example.she.otherui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.she.R;
import com.example.she.databinding.ActivitySignUpBinding;
import com.example.she.models.UserModel;
import com.example.she.utils.FirebaseUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;

    String emailPattern = "[a-zA-Z0-9.-_]+@[a-z]+\\.+[a-z]+";
    String imageUri;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    ActivityResultLauncher<Intent> exampleActivityResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.pink));

        auth = FirebaseAuth.getInstance();
        exampleActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task);
                    }
                });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        binding.googleButtonImg.setOnClickListener(view -> {
            signIn();
        });

        binding.loginTv.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, SignInActivity.class)));
        binding.buttonCreateAccount.setOnClickListener(view -> {
            String namee = binding.editTextName.getText().toString();
            String passs = binding.editTextPassword.getText().toString();
            String emaill = binding.editTextEmail.getText().toString();
            if (TextUtils.isEmpty(namee) || TextUtils.isEmpty(emaill) || TextUtils.isEmpty(passs)) {
                Toast.makeText(SignUpActivity.this, "Enter Valid Information", Toast.LENGTH_SHORT).show();
            } else if (!emaill.matches(emailPattern)) {
                binding.editTextEmail.setError("Enter a Valid Email");
            } else if (passs.length() < 6) {
                binding.editTextPassword.setError("Enter Long Pass");
            } else {
                auth.createUserWithEmailAndPassword(emaill, passs)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = FirebaseUtils.getUserDetailsFromDatabaseReference(id);
                                StorageReference storageReference=FirebaseUtils.getCurrentUserProfilePicStorageReference();


                                imageUri = "https://firebasestorage.googleapis.com/v0/b/she-final.appspot.com/o/user_1.png?alt=media&token=26726be2-d46e-4c36-9a54-ad97487e77e5";

                                UserModel users = new UserModel(id, namee, imageUri, emaill, passs);
                                reference.setValue(users).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(this, "successful creating account", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Error Creating User", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String idToken = account.getIdToken();
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(this, "successful creating account", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = auth.getCurrentUser();
                            UserModel userModel = new UserModel();
                            userModel.setName(user.getDisplayName());
                            userModel.setUserId(user.getUid());
                            userModel.setMail(user.getEmail());
                            userModel.setProfile(user.getPhotoUrl().toString());

                            FirebaseUtils.getUserDetailsFromDatabaseReference(user.getUid()).setValue(userModel).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                        }
                    });
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void signIn() {
        exampleActivityResult.launch(mGoogleSignInClient.getSignInIntent());

    }
}