package com.example.socially;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    UsersAdapter usersAdapter;
    List<ModelUser> userList;

    private UsersAdapter.RecyclerViewClickListener listener;

    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";

    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //Setting up a transparent actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#000000'>Users</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Setting up a back arrow
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24);
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        userList = new ArrayList<>();
        getAllUsers();

        recyclerView = findViewById(R.id.recycler_view_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void getAllUsers() {
        //get current user
        FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();
        //reference to "Users" collection
        DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL).getReference("Users");

        //get all data from the "Users" collection
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //except current user
                    if(!modelUser.getUid().equals(cUser.getUid())){
                        userList.add(modelUser);
                    }
                    setOnClickListener();
                    usersAdapter = new UsersAdapter(getApplicationContext(), userList, listener);
                    recyclerView.setAdapter(usersAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOnClickListener() {
        listener = (v, position) -> {
            Intent intent = new Intent(getApplicationContext(), OtherUserProfileActivity.class);
            intent.putExtra("UID", userList.get(position).getUid());
            startActivity(intent);
        };
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}