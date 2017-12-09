package com.example.shivam.floralchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomList extends AppCompatActivity {


    EditText search_query;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthState;
    FloatingActionButton float_btn;
    ImageView search_button;
    RecyclerView mRecycle_chatRoom_list;
    DatabaseReference mdatabase;
    FirebaseUser mFirebaseUser;
    static DatabaseReference mdatabase_chats;
    ProgressDialog pg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);
        this.setTitle("CHAT ROOMS");
        float_btn=(FloatingActionButton) findViewById(R.id.float_btn);
        float_btn.setImageResource(R.drawable.ic_add_black_24dp);
        mAuth= FirebaseAuth.getInstance();
        mAuthState=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                else if(firebaseAuth.getCurrentUser().isAnonymous()){
                    float_btn.hide();
                }
            }
        };
        mdatabase_chats=FirebaseDatabase.getInstance().getReference().child("MessageClass");
        search_query=(EditText)findViewById(R.id.chatRoom_search);
        search_button=(ImageView)findViewById(R.id.button6);
        pg=new ProgressDialog(ChatRoomList.this);
        pg.setMessage("Loading Chat Rooms");
        pg.show();
        float_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Add_chat_room.class));
            }
        });
        search_query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchRooms(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mdatabase= FirebaseDatabase.getInstance().getReference().child("ChatRoom");
        mRecycle_chatRoom_list=(RecyclerView)findViewById(R.id.mRecycle_chatRoom_list);
        mRecycle_chatRoom_list.setHasFixedSize(true);
        LinearLayoutManager ll=new LinearLayoutManager(this);
        mRecycle_chatRoom_list.setLayoutManager(ll);
    }

    private void checkanonymoususer() {
        if(mFirebaseUser.isAnonymous())
        {
            float_btn.hide();
        }
    }


    public void onBackPressed()
    {
        finishAffinity();
        System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
//            case R.id.logout:
//            {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
//            }
//            case R.id.list_room:
//            {
//                startActivity(new Intent(getApplicationContext(),ChatRoomList.class));
//            }
            case R.id.menu_setting:
            {
                startActivity(new Intent(getApplicationContext(),settings.class));
            }
        }
        return true;
    }



    private void searchRooms(String s) {
        String query_to_search=s;
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query;
        if(s.equals(""))
        {
            query = mDatabase.child("ChatRoom").orderByChild("name");
        }
        else {
            query = mDatabase.child("ChatRoom").orderByChild("name").equalTo(query_to_search);
        }
            FirebaseRecyclerAdapter<ChatRoom, RoomList> mfbr = new FirebaseRecyclerAdapter<ChatRoom, RoomList>(ChatRoom.class, R.layout.room_list,
                    RoomList.class, query) {
                @Override
                protected void populateViewHolder(RoomList viewHolder, ChatRoom model, int position) {
                    viewHolder.setName(model.getName());
                    viewHolder.setImage(model.getImage());
                    viewHolder.setDescription(model.getDescription());
                    viewHolder.setContext(ChatRoomList.this);
                }

            };
            mRecycle_chatRoom_list.setAdapter(mfbr);
        pg.dismiss();
            }




    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthState);
        searchRooms("");
    }

    public static Bitmap decodeFromFirebase64(String image)
    {
        byte[] decodeByteArray=android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodeByteArray,0,decodeByteArray.length);
    }

    public static class RoomList extends RecyclerView.ViewHolder {

        View mView;
        Context context;
        TextView name_text;
        public RoomList(View itemView) {
            super(itemView);
            mView=itemView;
            name_text=(TextView)mView.findViewById(R.id.chatroom_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name=name_text.getText().toString();
                    Intent i=new Intent(context,MainActivity.class);
                    i.putExtra("room_name",name);
                    context.startActivity(i);
                }
            });
        }

        public void setContext(Context c){
            this.context=c;
        }
        public void setName(String name)
        {
            //TextView name_text=(TextView)mView.findViewById(R.id.chatroom_name);
            name_text.setText(name);
        }
        public void setDescription(String desc)
        {
            TextView desc_text=(TextView)mView.findViewById(R.id.chatroom_desc);
            desc_text.setText(desc);
        }
        public void setImage(String image)
        {
            Bitmap bm=decodeFromFirebase64(image);
            CircleImageView profile_pic=(CircleImageView)mView.findViewById(R.id.profile_image);
            profile_pic.setImageBitmap(bm);
        }


    }
}
