package com.example.socially;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.socially.activities.ChatActivity;

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

        //Setting up a transparent actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#000000'>Posts</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
            Toast.makeText(getApplicationContext(), "Add Post", Toast.LENGTH_SHORT).show();
        });
        ChatIV.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Chat", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ChatActivity.class));
        });
        ProfileIV.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        });
    }
}