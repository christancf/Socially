package com.example.socially;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

//import com.example.socially.activities.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    ImageView HomeIV, UsersIV, AddPostIV, ChatIV, ProfileIV;
    FirebaseAuth mAuth;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setTitle("Posts");

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
//        ColorDrawable colorDrawable
//                = new ColorDrawable(Color.parseColor("#FFFFFF"));

        // Set BackgroundDrawable
//        actionBar.setBackgroundDrawable(colorDrawable);
//        actionBar.


        HomeIV = findViewById(R.id.home);
        UsersIV = findViewById(R.id.users);
        AddPostIV = findViewById(R.id.addPost);
        ChatIV = findViewById(R.id.chat);
        ProfileIV = findViewById(R.id.profile);

        //create an intent, start activity and work on your function
        HomeIV.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
        });
        UsersIV.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), UsersActivity.class)));

        AddPostIV.setOnClickListener(view -> {
            //Toast.makeText(getApplicationContext(), "Add Post", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), CreatePostActivity.class));
        });

        ChatIV.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Chat", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(this, ChatActivity.class));
        });
        ProfileIV.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        });
    }
}