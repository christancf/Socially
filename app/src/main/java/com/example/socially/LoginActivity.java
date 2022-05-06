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
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    TextInputLayout loginEmailTIL;
    EditText loginEmailET, loginPasswordET;
    Button loginBtn;
    TextView signUpRedirectTV;
    ActionBar actionBar;

    ProgressDialog progressDialog;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        //Initialize firebaseAuth
        mAuth = FirebaseAuth.getInstance();

        loginEmailET = findViewById(R.id.loginEmailET);
        loginPasswordET = findViewById(R.id.loginPasswordET);
        loginBtn = findViewById(R.id.login_btn);
        signUpRedirectTV = findViewById(R.id.redirectSignUpTV);

        loginBtn.setOnClickListener(view -> {
            String email = loginEmailET.getText().toString();
            String pass = loginPasswordET.getText().toString();

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                loginEmailTIL.setError("Invalid Email");
                loginEmailET.requestFocus();
            }else{
                loginUser(email, pass);
            }
        });
        loginEmailET.setOnClickListener(view -> {
            loginEmailTIL.setError("");
        });

        signUpRedirectTV.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RegisterDetailsActivity.class)));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
    }

    private void loginUser(String email, String pass) {
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        // Login failed
                        Toast.makeText(getApplicationContext(), "Authentication failed!! Please try again later", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }

    private void checkUserStatus(){
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, "checkUserStatus: function called. User = " + user);
        if(user != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}