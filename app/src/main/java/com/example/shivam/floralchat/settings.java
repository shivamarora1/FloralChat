package com.example.shivam.floralchat;

import android.app.ProgressDialog;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settings extends AppCompatActivity {


    EditText name;
    FirebaseUser mFirebaseUser;
    EditText email;
    EditText phone;
    EditText new_pass;
    EditText old_pass;
    Button log_out;
    Button continue_btn;
    DatabaseReference mDB;
    ProgressDialog pg;
    DatabaseReference reference_of_child_data;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.setTitle("Settings");

        pg=new ProgressDialog(this);
        mFirebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        mAuth=FirebaseAuth.getInstance();
        old_pass=(EditText)findViewById(R.id.editText10);
        name=(EditText)findViewById(R.id.editText6);
        email=(EditText)findViewById(R.id.editText7);
        phone=(EditText)findViewById(R.id.editText8);
        new_pass=(EditText)findViewById(R.id.editText9);
        log_out=(Button)findViewById(R.id.button8);
        continue_btn=(Button)findViewById(R.id.button7);
        user=FirebaseAuth.getInstance().getCurrentUser();
        checkanonymoususer();
        String email_id=user.getEmail();
        email.setText(email_id);

        String uid=user.getUid();
        mDB= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        reference_of_child_data= mDB.child("name");
        reference_of_child_data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            name.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        reference_of_child_data=mDB.child("phone");
        reference_of_child_data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phone.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pg.setMessage("Working on...");
                String up_name = name.getText().toString();
                String up_email = email.getText().toString();
                String uo_phone = phone.getText().toString();
                final String new_passw = new_pass.getText().toString();
                String old_password = old_pass.getText().toString();

                if (user.isAnonymous()) {
                    reference_of_child_data = mDB.child("name");
                    reference_of_child_data.setValue(up_name);
                    Toast.makeText(settings.this, "Name updated...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    reference_of_child_data = mDB.child("name");
                reference_of_child_data.setValue(up_name);

                user.updateEmail(up_email);
                reference_of_child_data = mDB.child("phone");
                reference_of_child_data.setValue(uo_phone);
                if (!new_passw.equals("") && !old_password.equals("")) {

                    AuthCredential cr = EmailAuthProvider.getCredential(up_email, old_password);
                    user.reauthenticate(cr).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(new_passw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            pg.dismiss();
                                            Toast.makeText(settings.this, "Password updated...", Toast.LENGTH_SHORT).show();
                                            old_pass.setText("");
                                            new_pass.setText("");
                                        } else {
                                            pg.dismiss();
                                            Toast.makeText(settings.this, "Password not updated...", Toast.LENGTH_SHORT).show();
                                            old_pass.setText("");
                                        }
                                    }
                                });
                            } else {
                                pg.dismiss();
                                Toast.makeText(settings.this, "Wrong Password..", Toast.LENGTH_SHORT).show();
                                old_pass.setText("");
                            }
                        }
                    });
                } else {
                    Toast.makeText(settings.this, "Saved...", Toast.LENGTH_SHORT).show();
                }

            }
                pg.dismiss();
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
    }

    private void checkanonymoususer() {
        mFirebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(mFirebaseUser.isAnonymous())
        {
            email.setEnabled(false);
            phone.setEnabled(false);
            new_pass.setEnabled(false);
            old_pass.setEnabled(false);
        }
    }
}
