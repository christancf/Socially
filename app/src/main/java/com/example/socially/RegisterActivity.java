package com.example.socially;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    String first_name, last_name, gender, dateOfBirth;
    EditText emailET, passwordET, confirmPasswordET;
    ActionBar actionBar;
    Button registerBtn;
    String email, password, profileImg;
    ProgressDialog progressDialog;
    TextInputLayout emailTIL, passwordTIL, confirmPasswordTIL;


    String default_img = "https://firebasestorage.googleapis.com/v0/b/socially-14fd2.appspot.com/o/Users_Profile_Cover_Images%2FprofileImage?alt=media&token=a71e66e7-e9d1-4b16-82b4-f09911831f9c";
    //Firebase
    private FirebaseAuth mAuth;
    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseDatabase = FirebaseDatabase.getInstance(firebaseURL);
        db = firebaseDatabase.getReference("Users");

        //Setting up a transparent actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#000000'>Create Account</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Setting up a back arrow
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24);
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        registerBtn = findViewById(R.id.register_btn);
        emailTIL = findViewById(R.id.emailTIL);
        passwordTIL = findViewById(R.id.passwordTIL);
        confirmPasswordTIL = findViewById(R.id.confirmPasswordTIL);

        first_name = getIntent().getStringExtra(RegisterDetailsActivity.FIRST_NAME);
        last_name = getIntent().getStringExtra(RegisterDetailsActivity.LAST_NAME);
        dateOfBirth = getIntent().getStringExtra(RegisterDetailsActivity.DATE_OF_BIRTH);
        gender = getIntent().getStringExtra(RegisterDetailsActivity.GENDER);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        registerBtn.setOnClickListener(view -> {
            email = emailET.getText().toString();
            password = passwordET.getText().toString();
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailET.requestFocus();
                emailTIL.setError("Please enter a valid email");
            }else if(password.length() < 8){
                passwordET.requestFocus();
                passwordTIL.setError("Password must be atleast 8 characters.");
            }else{
                registerUser(email, password);
            }
        });
    }

    private void registerUser(String email, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();

                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("uid", uid);
                        hashMap.put("firstName", first_name);
                        hashMap.put("lastName", last_name);
                        hashMap.put("dateOfBirth", dateOfBirth);
                        hashMap.put("gender", gender);
                        hashMap.put("email", email);
                        hashMap.put("currentCity", "");
                        hashMap.put("homeTown", "");
                        hashMap.put("relationshipStatus", "");
                        hashMap.put("profileImage", default_img);
                        hashMap.put("coverImage", "");
                        hashMap.put("onlineStatus", "online");
                        hashMap.put("typingTo", "noOne");


                        //insert data
                        db.child(uid).setValue(hashMap)
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                                        //signout
                                        mAuth.signOut();
                                        //intent to login activity
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT);
                                        mAuth.signOut();
                                        progressDialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(), GetStartedActivity.class));
                                        //remove authentication

                                    }
                                }).addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                    else {
                        // Registration failed
                        Toast.makeText(getApplicationContext(), "Registration failed!! Please try again later", Toast.LENGTH_SHORT).show();
                        emailET.setText("");
                        passwordET.setText("");
                        confirmPasswordET.setText("");
                    }
                    progressDialog.dismiss();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}

