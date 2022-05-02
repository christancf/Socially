package com.example.socially;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;

public class RegisterDetailsActivity extends AppCompatActivity {

    private static final String TAG = "RegisterDetailsActivity";
    public static final String GENDER = "com.example.socially.extra.GENDER";
    public static final String FIRST_NAME = "com.example.socially.extra.FIRST_NAME";
    public static final String LAST_NAME = "com.example.socially.extra.LAST_NAME";
    public static final String DATE_OF_BIRTH = "com.example.socially.extra.DATE_OF_BIRTH";

    TextView genderTV;
    EditText firstNameET, lastNameET;
    Button btn_next; //register details button
    TextInputLayout firstNameTIL, lastNameTIL;
    String genderString, firstNameString, lastNameString;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_details);

        //need to setup date picker, get data, set data.
        genderTV = findViewById(R.id.genderTV);
        firstNameET = findViewById(R.id.firstNameEt);
        lastNameET = findViewById(R.id.lastNameEt);
        btn_next = findViewById(R.id.register_details_btn);
        firstNameTIL = findViewById(R.id.firstNameTIL);
        lastNameTIL = findViewById(R.id.lastNameTIL);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Details");

        //Back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        firstNameET.requestFocus();

        btn_next.setOnClickListener(view -> {
            int id = ((RadioGroup) findViewById(R.id.genderRG)).getCheckedRadioButtonId();
            firstNameString = firstNameET.getText().toString();
            lastNameString = lastNameET.getText().toString();
            genderString = getGender(id);
            String dateOfBirth = String.valueOf(new Date().getTime());
            if(checkFieldValidation()) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra(FIRST_NAME, firstNameString);
                intent.putExtra(LAST_NAME, lastNameString);
                intent.putExtra(GENDER, genderString);
                intent.putExtra(DATE_OF_BIRTH, dateOfBirth);
                startActivity(intent);
            }
        });
    }

    private boolean checkFieldValidation() {
        if(firstNameString.length() == 0){
            firstNameET.requestFocus();
            firstNameTIL.setError("Field is empty");
            return false;
        }else firstNameET.setError(null);
        if(lastNameString.length() == 0){
            lastNameET.requestFocus();
            lastNameTIL.setError("Field is empty");
            return false;
        }else firstNameET.setError(null);
        if(genderString.length() == 0){
            Toast.makeText(getApplicationContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getGender(int id) {
        String genderTemp = "";
        switch (id){
            case R.id.femaleRb: genderTemp = "Female"; break;
            case R.id.maleRb: genderTemp = "Male"; break;
            case R.id.preferNotToSayRb: genderTemp = "Prefer not to say";
        }
        return genderTemp;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}