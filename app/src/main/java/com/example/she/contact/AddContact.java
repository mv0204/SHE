package com.example.she.contact;

import android.os.Bundle;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.she.R;
import com.example.she.databinding.ActivityAddContactBinding;
import com.example.she.db.DmModel;
import com.example.she.db.MyDbHandler;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class AddContact extends AppCompatActivity {
    TextInputEditText name, phoneNo;
    ActivityAddContactBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = findViewById(R.id.editTextName);
        phoneNo = findViewById(R.id.editTextPhoneNo);

        MyDbHandler db = new MyDbHandler(this);
        binding.insertbtn.setOnClickListener(v -> {
            String sName = name.getText().toString();
            if (sName.isEmpty()) {
                name.setError("Enter a name !");
            } else if (phoneNo.getText().toString().isEmpty()) {
                phoneNo.setError("Enter a Phone number");
            } else if (phoneNo.getText().toString().length() < 10 || phoneNo.getText().toString().length() > 10) {
                phoneNo.setError("Enter a valid no");
            } else {
                ArrayList<DmModel> list = db.viewData();
                ArrayList<String> sList = new ArrayList<>();
                for (DmModel d : list) {
                    sList.add(d.getKEY_NAME());
                }
                if (sList.contains(name.getText().toString())) {
                    name.setError("Enter a name that is not previously used!");
                    name.setText("");

                } else {
                    boolean res = db.insertData(name.getText().toString(), phoneNo.getText().toString());
                    if (res) {
                        Toast.makeText(AddContact.this, "Successful", Toast.LENGTH_SHORT).show();
                        name.setText("");
                        phoneNo.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }


            }
            finish();

        });

    }
}