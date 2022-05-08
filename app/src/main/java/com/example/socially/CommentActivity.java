package com.example.socially;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {
    ActionBar actionBar;
    RecyclerView commentRV;
    EditText commentContentET;
    ImageView userImageIV, commentBtnIV;

    //Progress Dialog
    ProgressDialog progressDialog;

    String postID, userProfilePicture, userName;

    FirebaseAuth mAuth;
    FirebaseUser user;
    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        setActionBar();
        initializeVariables();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        postID = getIntent().getStringExtra("postId");
        setupUserProfileImage();

        commentBtnIV.setOnClickListener(view -> postComment());
    }

    private void postComment() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding comment...");

        String comment = commentContentET.getText().toString().trim();

        if(TextUtils.isEmpty(comment)){
            Toast.makeText(getApplicationContext(), "Please type a comment...", Toast.LENGTH_SHORT).show();
            return;
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL)
                                    .getReference("Posts").child(postID).child("Comments");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cid", timeStamp);
        hashMap.put("comment", comment);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("uid", user.getUid());
        hashMap.put("uDp", userProfilePicture);
        hashMap.put("uName", userName);

        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Comment added successfully...", Toast.LENGTH_SHORT).show();
                    commentContentET.setText("");
                    updateCommentCount();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                });
    }

    boolean mProcessComment = false;
    private void updateCommentCount() {
        mProcessComment = true;
        DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts").child(postID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mProcessComment){
                    String comments = "" + snapshot.child("pComments").getValue();
                    try {
                        int newCommentVal = Integer.parseInt(comments) + 1;
                        ref.child("pComments").setValue(""+newCommentVal);
                        mProcessComment = false;
                    }catch (NumberFormatException e){
                        ref.child("pComments").setValue("1");
                        mProcessComment = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupUserProfileImage() {
        DatabaseReference db = FirebaseDatabase.getInstance(firebaseURL).getReference("Users");
        Query query = db.orderByChild("uid").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()) {
                    userProfilePicture = "" + ds.child("profileImage").getValue();
                    userName = "" + ds.child("firstName") + " " +ds.child("lastName");
                }
                try {
                    Picasso.get().load(userProfilePicture).into(userImageIV);
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setActionBar(){
        //Setting up a transparent actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#000000'>Comments</font>"));

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    private void initializeVariables(){
        commentRV = findViewById(R.id.comment_recyclerView);
        userImageIV = findViewById(R.id.comment_userImageIV);
        commentBtnIV = findViewById(R.id.comment_submitBtnIV);
        commentContentET = findViewById(R.id.comment_contentET);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}