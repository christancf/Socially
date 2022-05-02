package com.example.socially;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SlashScreenTwoActivity extends AppCompatActivity {
    Button btn_next_slash_two;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash_screen_two);

        btn_next_slash_two = findViewById(R.id.btn_next_slash_two);

        btn_next_slash_two.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SlashScreenThreeActivity.class)));
    }
}