package com.example.socially;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SlashScreenThreeActivity extends AppCompatActivity {
    private static final String TAG = "SlashScreenThreeActivity";

    Button btn_get_started;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash_screen_three);

        btn_get_started = findViewById(R.id.btn_get_started);

        btn_get_started.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), GetStartedActivity.class)));
    }
}