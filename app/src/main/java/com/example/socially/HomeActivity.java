package com.example.socially;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    ImageView HomeIV, UsersIV, AddPostIV, ChatIV, ProfileIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        HomeIV = findViewById(R.id.home);
        UsersIV = findViewById(R.id.users);
        AddPostIV = findViewById(R.id.addPost);
        ChatIV = findViewById(R.id.chat);
        ProfileIV = findViewById(R.id.profile);

        //create an intent, start activity and work on your function
        HomeIV.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
        });
        UsersIV.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Users", Toast.LENGTH_SHORT).show();
            /*
            * startActivity(new Intent(getApplicationContext(), UserActivity.class));
            * */
        });
        AddPostIV.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Add Post", Toast.LENGTH_SHORT).show();
        });
        ChatIV.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Chat", Toast.LENGTH_SHORT).show();
        });
        ProfileIV.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
        });
    }
}