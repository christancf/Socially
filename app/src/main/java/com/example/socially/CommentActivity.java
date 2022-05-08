package com.example.socially;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;

public class CommentActivity extends AppCompatActivity {
    ActionBar actionBar;
    RecyclerView commentRV;
    EditText commentContentET;
    ImageView userImageIV, commentBtnIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        setActionBar();
        initializeVariables();


    }


    private void setActionBar(){
        //Setting up a transparent actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#000000'>Comments</font>"));
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    private void initializeVariables(){
        commentRV = findViewById(R.id.commentRV);
        userImageIV = findViewById(R.id.userImageIV);
        commentBtnIV = findViewById(R.id.commentBtnIV);
        commentContentET = findViewById(R.id.commentContentET);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}