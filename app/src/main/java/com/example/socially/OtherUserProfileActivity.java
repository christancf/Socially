package com.example.socially;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socially.activities.ChatActivity;
import com.example.socially.adapters.AdapterPost;
import com.example.socially.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OtherUserProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;
    DatabaseReference db;
    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";

    String userName, userEmail, userProfilePicture, userCoverPicture;

    ImageView profileImageIV, coverImageIV, backArrowIV;
    TextView profileIdTV;

    TextView output;
    ImageView chatIcon;
    String UID;

    RecyclerView userPostRecyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        //get uid of clicked user to retrieve his posts
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        //output = findViewById(R.id.output);
        chatIcon = findViewById(R.id.chat_icon);
        profileImageIV = findViewById(R.id.profileImageIV);
        coverImageIV = findViewById(R.id.coverImageIV);
        profileIdTV = findViewById(R.id.profileIdTV);
        backArrowIV = findViewById(R.id.backArrow);

        backArrowIV.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), HomeActivity.class)));

        //recycler view
        userPostRecyclerView = findViewById(R.id.recycler_view_user_post);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);

        //show latest posts first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        //set layout to recycler view
        userPostRecyclerView.setLayoutManager(layoutManager);

        //init post list
        postList = new ArrayList<>();

        checkUserStatus();
        loadHisPosts();

        //chat
        UID = getIntent().getStringExtra("UID");
//        output.setText(UID);


        chatIcon.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Opening Chat...", Toast.LENGTH_SHORT).show();
            //Chat Function
            Intent intent2 = new Intent(this, ChatActivity.class);
            intent2.putExtra("hisUid", UID);
            this.startActivity(intent2);

        });
    }

    private void loadHisPosts() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        userPostRecyclerView.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);   //query to load user specific posts
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    postList.add(myPosts);

                    adapterPost = new AdapterPost(getApplicationContext(), postList);
                    userPostRecyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OtherUserProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus(){
       user = firebaseAuth.getCurrentUser();
        //Log.d(TAG, "checkUserStatus: function called. User = " + user);
        if(user != null){
            userEmail = user.getEmail();
            setupUserProfile();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void setupUserProfile() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()) {
                    System.out.println(ds);
                    userName = "" + ds.child("firstName").getValue() + " " + ds.child("lastName").getValue();
                    userProfilePicture = "" + ds.child("profileImage").getValue();
                    userCoverPicture = "" + ds.child("coverImage").getValue();
                }
                profileIdTV.setText(userName);
                setProfileImage(userProfilePicture, userCoverPicture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setProfileImage(String profile_img, String cover_img) {
        System.out.println(profile_img);
        System.out.println(cover_img);
        try {
            Picasso.get().load(profile_img).into(profileImageIV);
        }catch (Exception e){

        }
        try {
            Picasso.get().load(cover_img).into(coverImageIV);
        }catch (Exception e){
            System.out.println(e);
        }
    }
}