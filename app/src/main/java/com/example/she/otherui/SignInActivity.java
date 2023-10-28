package com.example.she.otherui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.she.R;
import com.example.she.databinding.ActivitySignInBinding;
import com.example.she.models.UserModel;
import com.example.she.utils.FirebaseUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    ActivitySignInBinding binding;
    ActivityResultLauncher<Intent> exampleActivityResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if(FirebaseUtils.isLoggedIn()){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }


        binding.createAccountTv.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        });
        binding.buttonLogin.setOnClickListener(view -> {
            String emaill = binding.editTextEmail.getText().toString();
            String passs = binding.editTextPassword.getText().toString();
            if (TextUtils.isEmpty(emaill) || TextUtils.isEmpty(passs)) {
                Toast.makeText(SignInActivity.this, "Enter Valid Details", Toast.LENGTH_SHORT).show();
            } else {
                auth.signInWithEmailAndPassword(emaill, passs).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        exampleActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task);
                    }
                });


        binding.googleButtonImg.setOnClickListener(view -> {
            googleSignIn();
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
                            Toast.makeText(this, "successful", Toast.LENGTH_SHORT).show();
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

    private void googleSignIn() {
        exampleActivityResult.launch(mGoogleSignInClient.getSignInIntent());

    }
}