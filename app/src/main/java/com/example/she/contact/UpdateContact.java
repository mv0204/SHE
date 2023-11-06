package com.example.she.contact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.she.R;
import com.example.she.db.MyDbHandler;

public class UpdateContact extends AppCompatActivity {
    EditText name,no;
    Button update,delete;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.pink));

        name=findViewById(R.id.nameEt);
        no=findViewById(R.id.phoneNoEt);
        update=findViewById(R.id.update);
        delete=findViewById(R.id.delete);
        builder=new AlertDialog.Builder(this);
        Intent intent=getIntent();
        MyDbHandler db = new MyDbHandler(this);
        name.setText(intent.getStringExtra("nameI"));
        no.setText(intent.getStringExtra("noI"));
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(no.getText().toString().isEmpty() || name.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Name And Number to be updated",Toast.LENGTH_SHORT).show();

                }
                else if(no.getText().toString().length()<10||no.getText().toString().length()>10){
                    Toast.makeText(getApplicationContext(), "Enter a valid phone no be updated", Toast.LENGTH_SHORT).show();

                }
                else {
                    boolean b=db.updateData( name.getText().toString(), no.getText().toString());
                    if(b){
                        Toast.makeText(getApplicationContext(),"Successfully updated ",Toast.LENGTH_SHORT).show();
                    }

                    else {
                        Toast.makeText(getApplicationContext(),"Data not Updated ",Toast.LENGTH_SHORT).show();

                    }}
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter name to be deleted", Toast.LENGTH_SHORT).show();
                }

                else {

                    builder.setTitle("Delete Contact").setMessage("Do you want to delete this contact ?").setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.deleteData(name.getText().toString());
                                    Toast.makeText(getApplicationContext(), "Successfully deleted the contact ", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();


                }



            }
        });

    }
}