package com.example.socially;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OtherUserProfileActivity extends AppCompatActivity {

    TextView output;
    ImageView chatIcon;
    String UID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        output = findViewById(R.id.output);
        chatIcon = findViewById(R.id.chat_icon);

        UID = getIntent().getStringExtra("UID");
        output.setText(UID);

        chatIcon.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Chat Icon", Toast.LENGTH_SHORT).show();
            //Chat Function
        });
    }
}