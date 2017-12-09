package com.example.shivam.floralchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    EditText name,email,password;
    Button login,signup;
    DatabaseReference mDb;
    FirebaseAuth mAuth;
    ProgressDialog pg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.getSupportActionBar().hide();
        pg=new ProgressDialog(this);
        pg.setMessage("Please Wait...");
        name=(EditText)findViewById(R.id.editText3);
        email=(EditText)findViewById(R.id.editText);
        password=(EditText)findViewById(R.id.editText2);
        signup=(Button)findViewById(R.id.signup_button);

        mDb= FirebaseDatabase.getInstance().getReference().child("Users");
    }

 public void signUp(View v){
     String emails=email.getText().toString();
     String user=name.getText().toString();
     String pass=password.getText().toString();
     if(emails.equals("")||pass.equals("")||user.equals("")){
         Toast.makeText(this, "Please fill all fields...", Toast.LENGTH_SHORT).show();
     }
     else
     {
         pg.show();
         mAuth=FirebaseAuth.getInstance();
         mAuth.createUserWithEmailAndPassword(emails,pass).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful()){
                     Toast.makeText(SignUpActivity.this, "Successs..", Toast.LENGTH_SHORT).show();
                     String s=mAuth.getCurrentUser().getUid();
                     DatabaseReference dR= mDb.child(s);
                     dR.child("name").setValue(name.getText().toString());
                     pg.dismiss();
                     name.setText("");
                     email.setText("");
                     password.setText("");

                     Toast.makeText(SignUpActivity.this, "Signup Successful...", Toast.LENGTH_LONG).show();
                     startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                 }
                 else{
                     pg.dismiss();
                     Toast.makeText(SignUpActivity.this, "Error Occured...", Toast.LENGTH_SHORT).show();

                 }

             }
         });


     }

      }

}
