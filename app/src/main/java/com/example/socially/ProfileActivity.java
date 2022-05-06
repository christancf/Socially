package com.example.socially;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.fonts.Font;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    ImageView profileImageIV, moreOptionsIV, coverImageIV, backArrowIV;
    TextView profileIdTV;

    //User Name
    String userName;

    FirebaseAuth mAuth;
    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Creating firebase instance
        mAuth = FirebaseAuth.getInstance();

        profileImageIV = findViewById(R.id.profileImageIV);
        moreOptionsIV = findViewById(R.id.moreOptionsIV);
        coverImageIV = findViewById(R.id.coverImageIV);
        profileIdTV = findViewById(R.id.profileIdTV);
        backArrowIV = findViewById(R.id.backArrow);

        //OnClick
        moreOptionsIV.setOnClickListener(view -> showMoreOptions());
        backArrowIV.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), HomeActivity.class)));
    }

    private void checkUserStatus(){
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, "checkUserStatus: function called. User = " + user);
        if(user != null){
            FirebaseDatabase db = FirebaseDatabase.getInstance(firebaseURL);
            DatabaseReference ref = db.getReference("Users");
            ref.child(user.getUid()).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Map<String, Object> UserData = (Map<String, Object>) task.getResult().getValue();

                    System.out.println(UserData.get("firstName"));
                    userName = (String) UserData.get("firstName")+ " " +UserData.get("lastName");
                    profileIdTV.setText(userName);
                }
            });
        }else{
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    //show dialog
    private void showMoreOptions() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout editProfile = dialog.findViewById(R.id.editProfileLL);
        LinearLayout blockedUsers = dialog.findViewById(R.id.blockedUsersLL);
        LinearLayout logOut = dialog.findViewById(R.id.logOutLL);

        editProfile.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "Edit Profile", Toast.LENGTH_SHORT).show());
        blockedUsers.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "Blocked Users", Toast.LENGTH_SHORT).show());
        logOut.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "Log Out", Toast.LENGTH_SHORT).show());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.bottomDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}