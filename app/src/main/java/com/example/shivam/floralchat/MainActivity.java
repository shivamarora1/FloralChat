package com.example.shivam.floralchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 100;

    static String user_name;
    public EditText message_box;
    ImageView emoji_btn;
    RecyclerView mrecyclerView;
    DatabaseReference mDatabaseRef;
    DatabaseReference mDatabase_user;
    FirebaseUser user_id;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String room_name=getIntent().getExtras().getString("room_name");
        //this.getSupportActionBar().hide();
        this.setTitle(room_name);
        emoji_btn=(ImageView)findViewById(R.id.imageView3);
        mAuth=FirebaseAuth.getInstance();
        message_box=(EditText)findViewById(R.id.message_box);
        mDatabaseRef= FirebaseDatabase.getInstance().getReference().child("MessageClass").child(room_name);
        mrecyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        mrecyclerView.setHasFixedSize(true);
        LinearLayoutManager lm=new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        mrecyclerView.setLayoutManager(lm);


        emoji_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<MessageClass,MessageViewHolder> fr=new FirebaseRecyclerAdapter<MessageClass, MessageViewHolder>(MessageClass.class,R.layout.message_layout,MessageViewHolder.class,mDatabaseRef)
        {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, MessageClass model, int position) {
viewHolder.setContent(model.getContent(),model.getname(),model.getTime(),MainActivity.this);
            }
        };
mrecyclerView.setAdapter(fr);
    }


    public void clickSendBtn(View v)
    {
        user_id=mAuth.getCurrentUser();
        mDatabase_user=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id.getUid());
        final String text=message_box.getText().toString();
final DatabaseReference newPost=mDatabaseRef.push();

        mDatabase_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_name=dataSnapshot.child("name").getValue().toString();
                SimpleDateFormat sf=new SimpleDateFormat("HH:mm dd/MM/yyyy");
                Date d=new Date();
                String dates=sf.format(d).toString();
                newPost.child("time").setValue(dates);
                newPost.child("content").setValue(text);
                newPost.child("name").setValue(dataSnapshot.child("name").getValue());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        message_box.setText("");
        mrecyclerView.scrollToPosition(mrecyclerView.getAdapter().getItemCount());
    }



    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        View mView;
        LinearLayout message_layout;
        public MessageViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

        }
        public void setContent(String content,String name,String time,Context context){
            message_layout=(LinearLayout)mView.findViewById(R.id.message_layout);
            //ImageView message_image=(ImageView)mView.findViewById(R.id.message_image);
            TextView message_uname=(TextView)mView.findViewById(R.id.message_uname);
            TextView message_time=(TextView)mView.findViewById(R.id.message_time);
            TextView msg=(TextView)mView.findViewById(R.id.message_content);
            msg.setText(content);
            message_uname.setText(name);
            message_time.setText(time);
            if(name==user_name){
                message_layout.setHorizontalGravity(Gravity.RIGHT);
                message_layout.setBackgroundResource(R.drawable.my_msg);
            }
        }
    }

//**************  Routines to Encode and Decode Images to the Firebase ***********//
    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permission=MainActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            if(permission!=0)
            {
                this.requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }

    }

////To show Image Choose Intent
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }


    public void encodeBitmapAndSaveToFirebase(Bitmap bitmapi,DatabaseReference post) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapi.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        post.child("image").setValue(imageEncoded).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Picture Successfully.....", Toast.LENGTH_SHORT).show();
                    //bitmap=Bitmap.createBitmap(10,10,Bitmap.Config.ARGB_8888);
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static Bitmap decodeFromFirebase64(String image)
    {
        byte[] decodeByteArray=android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodeByteArray,0,decodeByteArray.length);
    }

//**************  End Routines to Encode and Decode Images to the Firebase ***********//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode==SELECT_PICTURE)
        {
//            uri=data.getData();
//            if(uri!=null)
//            {
//                InputStream image_stream = null;
//                try {
//                    image_stream = getContentResolver().openInputStream(uri);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                bitmap= BitmapFactory.decodeStream(image_stream );
//                View b=null;
//                clickSendBtn(b);
//            }
//            else{
//                Toast.makeText(this, "No Image Selected...", Toast.LENGTH_SHORT).show();
//            }
        }
    }
}
