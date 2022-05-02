package com.example.socially;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class GetStartedActivity extends AppCompatActivity {
    private static final String TAG = "GetStartedActivity";

    Button btn_login, btn_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);

        btn_register.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RegisterDetailsActivity.class)));
        btn_login.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }
}