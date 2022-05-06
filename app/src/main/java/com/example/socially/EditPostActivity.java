package com.example.socially;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class EditPostActivity extends AppCompatActivity {

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        //Setting up a transparent actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#000000'>Edit Post</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //Setting up a close button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        final Drawable close = getResources().getDrawable(R.drawable.ic_baseline_close_24);
        close.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(close);
    }
}