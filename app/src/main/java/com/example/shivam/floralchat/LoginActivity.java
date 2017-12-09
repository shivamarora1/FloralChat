package com.example.shivam.floralchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText textView;
    TextView forgot_pass;//textview3
    TextView signup;//textview4
    Button anonymous;//button3
    EditText textView2;
    Button button2;
    DatabaseReference mDBR;
    FirebaseAuth mFirebaseAuth;
    ProgressDialog pg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getSupportActionBar().hide();
        pg=new ProgressDialog(this);
        pg.setMessage("Please Wait...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textView=(EditText)findViewById(R.id.textView);

        forgot_pass=(TextView)findViewById(R.id.textView3);
        signup=(TextView)findViewById(R.id.textView4);
        anonymous=(Button)findViewById(R.id.button3);
        textView2=(EditText)findViewById(R.id.textView2);
        mDBR= FirebaseDatabase.getInstance().getReference().child("Users");
        mFirebaseAuth=FirebaseAuth.getInstance();

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///Forgot pass logic
                Toast.makeText(LoginActivity.this, "Forgot Password Clicked....", Toast.LENGTH_SHORT).show();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        anonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///Anonymopus Button
                mFirebaseAuth.signInAnonymously();
                startActivity(new Intent(LoginActivity.this,anonymous_begin.class));
            }
        });
    }

    public void onBackPressed()
    {
        finishAffinity();
        System.exit(0);
    }


    public void logRealIn(View v){
        String email=textView.getText().toString();
        String password=textView2.getText().toString();
        if(email.equals("")&&password.equals(""))
        {
            Toast.makeText(this, "Please fill your credentials...", Toast.LENGTH_SHORT).show();
        }
        else{
            pg.show();
            mFirebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        checkUserExists();
                    }
                    else {
                        textView.setText("");
                        textView2.setText("");
                        pg.dismiss();
                        Toast.makeText(LoginActivity.this, "Wrong Credentials...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
    public void checkUserExists(){
        final String uid=mFirebaseAuth.getCurrentUser().getUid();
        mDBR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uid)){
                    pg.dismiss();
                    Toast.makeText(LoginActivity.this, "Welcome...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,ChatRoomList.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
