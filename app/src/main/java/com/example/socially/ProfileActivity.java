package com.example.socially;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socially.notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socially.adapters.AdapterPost;
import com.example.socially.models.ModelPost;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    ImageView profileImageIV, moreOptionsIV, coverImageIV, backArrowIV;
    TextView profileIdTV;
    //User Name
//    String userName;
    String mUID;
    String userName, userEmail, userProfilePicture, userCoverPicture;

    //recycler view
    RecyclerView userPostRecyclerView;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;

    List<ModelPost> postList;
    AdapterPost adapterPost;
    String uid;

    String storagePath = "Users_Profile_Cover_Images/";

    String checkProfileOrCover;

    ProgressDialog progressDialog;

    Uri image_uri;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference db;
    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";

    ActivityResultLauncher activityResultLauncherCamera, activityResultLauncherGallery;

    FirebaseUser user;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Creating firebase instance
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance(firebaseURL);
        db = firebaseDatabase.getReference("Users");

        activityResultLauncherGallery = activityLauncherGallery();
        activityResultLauncherCamera = activityLauncherCamera();

        //init permission arrays
        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);

        //initializing variables;
        profileImageIV = findViewById(R.id.profileImageIV);
        moreOptionsIV = findViewById(R.id.moreOptionsIV);
        coverImageIV = findViewById(R.id.coverImageIV);
        profileIdTV = findViewById(R.id.profileIdTV);
        backArrowIV = findViewById(R.id.backArrow);


        checkUserStatus();

        //update token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            try {
                String tokenRefresh = instanceIdResult.getToken();
                updateToken(tokenRefresh);
            } catch (Exception e) {
                System.out.println(e);
            }
        });

        //OnClick
        moreOptionsIV.setOnClickListener(view -> showMoreOptions());
        backArrowIV.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), HomeActivity.class)));
        profileImageIV.setOnClickListener(view -> showMoreProfileOptions());

        userPostRecyclerView = findViewById(R.id.recycler_view_user_post);
        //linear layout for recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //show latest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recycler view
        userPostRecyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        checkUserStatus();
        loadMyPosts();
    }

    private void loadMyPosts() {

        //init post list
        DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts");
        //query to load posts
        Query query = ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    //add to list
                    postList.add(myPosts);

                    //adapter
                    adapterPost = new AdapterPost(getApplicationContext(), postList);
                    //set this adapter to recycler view
                    userPostRecyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL).getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    private void checkUserStatus(){
        user = mAuth.getCurrentUser();
        Log.d(TAG, "checkUserStatus: function called. User = " + user);
        if(user != null){
            uid = user.getUid();
            userEmail = user.getEmail();
            setupUserProfile();
            //for notifications function
            mUID = user.getUid();

            //save uid of currently signed in user in shared preferences
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();

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

    private void setupUserProfile() {

        Query query = db.orderByChild("email").equalTo(userEmail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()) {
                    userName = "" + ds.child("firstName").getValue() + " " + ds.child("lastName").getValue();
                    userProfilePicture = "" + ds.child("profileImage").getValue();
                    userCoverPicture = "" + ds.child("coverImage").getValue();
                }
                profileIdTV.setText(userName);
                setProfileImage(userProfilePicture, userCoverPicture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setProfileImage(String profile_img, String cover_img) {
            try {
                Picasso.get().load(profile_img).into(profileImageIV);
            }catch (Exception e){

            }
            try {
                Picasso.get().load(cover_img).into(coverImageIV);
            }catch (Exception e){

            }
    }

    //show dialog options
    private void showMoreOptions() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout editProfile = dialog.findViewById(R.id.editProfileLL);
        LinearLayout blockedUsers = dialog.findViewById(R.id.blockedUsersLL);
        LinearLayout logOut = dialog.findViewById(R.id.logOutLL);

        editProfile.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "Edit Profile", Toast.LENGTH_SHORT).show());
        blockedUsers.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "Blocked Users", Toast.LENGTH_SHORT).show());
        logOut.setOnClickListener(view -> {
            logOutOption();
            dialog.cancel();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.bottomDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showMoreProfileOptions() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.update_profile_image_layout);

        LinearLayout uploadProfile = dialog.findViewById(R.id.uploadProfileLL);
        LinearLayout uploadCover = dialog.findViewById(R.id.uploadCoverLL);
        LinearLayout removeProfile = dialog.findViewById(R.id.removeCoverLL);
        LinearLayout removeCover = dialog.findViewById(R.id.removeProfileLL);

        uploadProfile.setOnClickListener(view -> {
            uploadProfilePicture();
            dialog.cancel();
        });
        uploadCover.setOnClickListener(view -> uploadCoverPicture());
        removeProfile.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "remove profile", Toast.LENGTH_SHORT).show());
        removeCover.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "Remove Cover", Toast.LENGTH_SHORT).show());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.bottomDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void uploadCoverPicture() {
        progressDialog.setMessage("Uploading...");
        checkProfileOrCover="coverImage";
        getImageDialog();
    }

    private void uploadProfilePicture() {
        progressDialog.setMessage("Uploading...");
        checkProfileOrCover="profileImage";
        getImageDialog();
    }

    private void getImageDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.get_image_dialog_layout);

        LinearLayout cameraLL = dialog.findViewById(R.id.cameraOptionLL);
        LinearLayout galleryLL = dialog.findViewById(R.id.galleryOptionLL);

        cameraLL.setOnClickListener(view -> {
            if(!checkCameraPermission()) {
                requestCameraPermission();
            } else {
                cameraOption();
                dialog.cancel();
            }
        });
        galleryLL.setOnClickListener(view -> {
            if(!checkStoragePermission()) {
                requestStoragePermission();
            } else {
                galleryOption();
                dialog.cancel();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.alertDialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void galleryOption() {
        activityResultLauncherGallery.launch("image/*");
    }

    private void cameraOption() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Profile Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Description");
        //put image uri
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        activityResultLauncherCamera.launch(intent);
    }

    private ActivityResultLauncher activityLauncherGallery() {
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
//                    profileImageIV.setImageURI(uri);
                    image_uri = uri;
                    uploadImage(image_uri);
                });
        return mGetContent;
    }

    private ActivityResultLauncher activityLauncherCamera() {
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
//                        Bundle bundle = result.getData().getExtras();
//                        System.out.println(bundle);
//                        Bitmap bitmap = (Bitmap) bundle.get("data");
//                        profileImageIV.setImageBitmap(bitmap);
                        uploadImage(image_uri);
                    }
                });
        return activityResultLauncher;
    }

    private void uploadImage(Uri image_uri) {
        progressDialog.show();
        String filePathAndName = storagePath + "" + checkProfileOrCover + "_" + user.getUid();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);

        ref.putFile(image_uri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadUri = uriTask.getResult();

                    //check whether image is uploaded and uri received
                    if(uriTask.isSuccessful()){
                        HashMap<String, Object> results = new HashMap<>();
                        results.put(checkProfileOrCover, downloadUri.toString());
                        Log.d(TAG, "uploadImage: "+ downloadUri.toString());
                        db.child(user.getUid()).updateChildren(results)
                                .addOnSuccessListener(unused -> {
                                    progressDialog.dismiss();
                                    Log.d(TAG, "uploadImage: success");
                                    setupUserProfile();
                                    Toast.makeText(getApplicationContext(), "Image Uploaded...", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Log.d(TAG, "uploadImage: failure");
                                    Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
                                });
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }

    private void logOutOption(){
        final Dialog logOutDialog = new Dialog(this);
        logOutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logOutDialog.setContentView(R.layout.alert_dialog_layout);

        TextView cancelTV = logOutDialog.findViewById(R.id.cancelTV);
        TextView logOutTV = logOutDialog.findViewById(R.id.logOutTV);

        cancelTV.setOnClickListener(view -> logOutDialog.cancel());
        logOutTV.setOnClickListener(view -> {
            logOutDialog.cancel();
            mAuth.signOut();
            checkUserStatus();
        });

        logOutDialog.show();
        logOutDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        logOutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        logOutDialog.getWindow().getAttributes().windowAnimations = R.style.alertDialogAnimation;
        logOutDialog.getWindow().setGravity(Gravity.CENTER);
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        //check if camera permission is enabled or not
        boolean cameraResult = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        return cameraResult && checkStoragePermission();
    }

    private void requestCameraPermission() {
        //request runtime camera permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if(grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && writeStorageAccepted) {
                        //both permissions are granted
                        cameraOption();

                    } else {
                        //camera or gallery or both permissions are denied
                        Toast.makeText(this, "Please enable Camera & Storage permissions", Toast.LENGTH_SHORT).show();
                    }

                } else {

                }
            }
            break;
            case STORAGE_REQUEST_CODE: {

                if(grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(writeStorageAccepted) {
                        //storage permission is granted
                        galleryOption();

                    } else {
                        //storage permission is denied
                        Toast.makeText(this, "Please enable Storage permission", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }

            }
            break;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.post_menu, menu);
//
//        MenuItem menuItem = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView) menuItem.getActionView();
//        searchView.setQueryHint("Search post...");
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if(!TextUtils.isEmpty(query)) {
//                    searchMyPosts(query);
//                } else {
//                    loadMyPosts();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if(!TextUtils.isEmpty(newText)) {
//                    searchMyPosts(newText);
//                } else {
//                    loadMyPosts();
//                }
//                return false;
//            }
//        });
//
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
 }