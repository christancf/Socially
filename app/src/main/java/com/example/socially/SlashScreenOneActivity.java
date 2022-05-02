package com.example.socially;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SlashScreenOneActivity extends AppCompatActivity {
    private static final String TAG = "SlashScreenOneActivity";

    Button btn_next_slash_one;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash_screen_one);

        btn_next_slash_one = findViewById(R.id.btn_next_slash_one);

        btn_next_slash_one.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SlashScreenTwoActivity.class)));
    }
}