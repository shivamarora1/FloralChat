package com.example.shivam.floralchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class anonymous_begin extends AppCompatActivity {

    Button cont_anony;
    EditText anony_name;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_begin);
        mAuth=FirebaseAuth.getInstance();
        anony_name=(EditText)findViewById(R.id.editText11);
        cont_anony=(Button) findViewById(R.id.button9);


        cont_anony.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ui_d=mAuth.getCurrentUser().getUid();
                DatabaseReference dr= FirebaseDatabase.getInstance().getReference().child("Users").child(ui_d).child("name");
                dr.setValue(anony_name.getText().toString());
                startActivity(new Intent(anonymous_begin.this,ChatRoomList.class));
            }
        });

    }
}
