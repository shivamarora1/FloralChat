package com.example.shivam.floralchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URI;

public class Add_chat_room extends AppCompatActivity {


    private static final int SELECT_PICTURE = 100;
    EditText add_chat_name;
    EditText add_chat_desc;
    Button add_chat_create;
    Button add_chat_reset;
    private StorageReference mStorageRef;
    ImageView add_chat_image;
    Uri uri;
    DatabaseReference db;
    StorageReference mStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat_room);


        checkPermissions();

        mStorageRef = FirebaseStorage.getInstance().getReference();
       mStorage= FirebaseStorage.getInstance().getReference();
        db= FirebaseDatabase.getInstance().getReference().child("ChatRoom");
        add_chat_name=(EditText)findViewById(R.id.editText4);
        add_chat_desc=(EditText)findViewById(R.id.editText5);
        add_chat_create=(Button)findViewById(R.id.button4);
        add_chat_reset=(Button)findViewById(R.id.button5);
        add_chat_image=(ImageView)findViewById(R.id.add_new_image);

        add_chat_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_chat_name.setText("");
                add_chat_desc.setText("");
                add_chat_image.setImageResource(R.drawable.ic_local_florist_black_24dp);
            }
        });

        add_chat_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=add_chat_name.getText().toString();
                String desc=add_chat_desc.getText().toString();
                if(uri==null)
                {
                    Toast.makeText(Add_chat_room.this, "Select an Image...", Toast.LENGTH_SHORT).show();
                }
                else{
                final DatabaseReference post=db.push();
                post.child("name").setValue(name);
                post.child("description").setValue(desc);
                    Bitmap bm=((BitmapDrawable)add_chat_image.getDrawable()).getBitmap();
                    encodeBitmapAndSaveToFirebase(bm,post);
                }
            }
        });

        add_chat_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();

            }
        });

    }


    private void openImageChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }

    private void checkPermissions() {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                int permission=Add_chat_room.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");

                if(permission!=0)
                {
                    this.requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
            }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode==SELECT_PICTURE)
        {
                uri=data.getData();
                if(uri!=null)
                {
                    add_chat_image.setImageURI(uri);
                }
                else{
                    Toast.makeText(this, "No Image Selected...", Toast.LENGTH_SHORT).show();
                }
                 }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap,DatabaseReference post) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
            String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            post.child("image").setValue(imageEncoded).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(Add_chat_room.this, "Chat Room Created Successfully.....", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Add_chat_room.this,ChatRoomList.class));
                    }
                    else{
                        Toast.makeText(Add_chat_room.this, "Please try again later...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



}
