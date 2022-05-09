package com.example.socially;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreatePostActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;

    //views
    CircleImageView profilePictureCIV;
    TextView userNameTV, actionBarTitle, savePost;
    EditText postContentET;
    ImageView postContentImageIV, cameraIV, galleryIV, closeIV;
    Button publishPostBtn;

    //info of the post to be edited
    String editContent, editPostImage;

    //user info
    String userFirstName, userLastName, userEmail, userId, userProfilePicture;

    Uri image_rui = null;

    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";

    //progress bar
    ProgressDialog pd;

    ActivityResultLauncher activityResultLauncher;
    ActivityResultLauncher activityResultLauncher2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        activityResultLauncher = activityLauncher();
        activityResultLauncher2 = activityLauncherGallery();

        //init views
        closeIV = findViewById(R.id.btn_close);
        profilePictureCIV = findViewById(R.id.civ_profile_picture);
        userNameTV = findViewById(R.id.tv_user_name);
        cameraIV = findViewById(R.id.iv_camera);
        galleryIV = findViewById(R.id.iv_gallery);
        publishPostBtn = findViewById(R.id.btn_publish_post);
        actionBarTitle = findViewById(R.id.tv_actionbar_title);
        postContentET = findViewById(R.id.et_post_content);
        postContentImageIV = findViewById(R.id.iv_post_content_image);
        savePost = findViewById(R.id.tv_save_post);

        //init permission arrays
        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        //get info of the editPost
        Intent intent = getIntent();
        String isUpdateKey = "" + intent.getStringExtra("key");
        String editPostID = "" + intent.getStringExtra("editPostID");

        if(isUpdateKey.equals("editPost")) {
            actionBarTitle.setText("Edit Post");
            //publishPostBtn.setVisibility(View.GONE);
            publishPostBtn.setText("Save");
            loadPostData(editPostID);
        } else {
            //savePost.setVisibility(View.GONE);
        }

        //get info of current user
        userDbRef = FirebaseDatabase.getInstance(firebaseURL).getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(userEmail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()) {
                    userFirstName = "" + ds.child("firstName").getValue();
                    userLastName = "" + ds.child("lastName").getValue();
                    userEmail = "" + ds.child("email").getValue();
                    userProfilePicture = "" + ds.child("profileImage").getValue();
                    userNameTV.setText("" + ds.child("firstName").getValue() + " " + ds.child("lastName").getValue());
                    profilePictureCIV.setImageURI(Uri.parse("" + ds.child("profileImage").getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        closeIV.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), HomeActivity.class)));


        //upload button click listener
        publishPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get post content from EditText
                String postContent = postContentET.getText().toString().trim();

                if(TextUtils.isEmpty(postContent)) {
                    Toast.makeText(CreatePostActivity.this, "Write something to post...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isUpdateKey.equals("editPost")) {
                    beginUpload(postContent, editPostID);
                } else {
                    uploadData(postContent);
                }
            }
        });

        cameraIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            }
        });

        galleryIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGalley();
                }
            }
        });
    }

    private void beginUpload(String postContent, String editPostID) {
        pd.setMessage("Updating Post...");
        pd.show();

        if(!editPostImage.equals("noImage")) {
            //with image
            updateWasWithImage(postContent, editPostID);
        } else if(postContentImageIV.getDrawable() != null) {
            //with image
            updateWithNowImage(postContent, editPostID);
        } else {
            updateWithoutImage(postContent, editPostID);
        }
    }

    private void updateWithoutImage(String content, String editPostID) {

        HashMap<String, Object> hashMap = new HashMap<>();
        //put post info
        hashMap.put("uid", userId);
        hashMap.put("firstName", userFirstName);
        hashMap.put("lastName", userLastName);
        hashMap.put("email", userEmail);
        hashMap.put("profileImage", userProfilePicture);
        //hashMap.put("postID", timeStamp);
        hashMap.put("postContent", content);
        hashMap.put("postImage", "noImage");
        //hashMap.put("publishTime", timeStamp);

        DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts");
        ref.child(editPostID)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(CreatePostActivity.this, "Post Updated...", Toast.LENGTH_SHORT).show();

                        postContentET.setText("");
                        postContentImageIV.setImageURI(null);
                        image_rui = null;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(CreatePostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateWithNowImage(String content, String editPostID) {

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_" + timeStamp;

        //get image from ImageView
        Bitmap bitmap = ((BitmapDrawable)postContentImageIV.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //image compress
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image uploaded. getting url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        String downloadUri = uriTask.getResult().toString();
                        if(uriTask.isSuccessful()) {
                            //uploading the received url to database

                            HashMap<String, Object> hashMap = new HashMap<>();
                            //put post info
                            hashMap.put("uid", userId);
                            hashMap.put("firstName", userFirstName);
                            hashMap.put("lastName", userLastName);
                            hashMap.put("email", userEmail);
                            hashMap.put("profileImage", userProfilePicture);
                            //hashMap.put("postID", timeStamp);
                            hashMap.put("postContent", content);
                            hashMap.put("postImage", downloadUri);
                            //hashMap.put("publishTime", timeStamp);

                            DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts");
                            ref.child(editPostID)
                                    .updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            pd.dismiss();
                                            Toast.makeText(CreatePostActivity.this, "Post Updated...", Toast.LENGTH_SHORT).show();

                                            postContentET.setText("");
                                            postContentImageIV.setImageURI(null);
                                            image_rui = null;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(CreatePostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(CreatePostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateWasWithImage(String content, String editPostID) {
        //post is with image, so have to delete previous image first
        StorageReference mPictureRef = FirebaseStorage.getInstance().getReferenceFromUrl(editPostImage);
        mPictureRef.delete()
                .addOnSuccessListener(unused -> {
                    //image delete, upload new image
                    //postImage, postID, name, publishTime
                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    String filePathAndName = "Posts/" + "post_" + timeStamp;

                    //get image from ImageView
                    Bitmap bitmap = ((BitmapDrawable)postContentImageIV.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    //image compress
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                    ref.putBytes(data)
                            .addOnSuccessListener(taskSnapshot -> {
                                //image uploaded. getting url
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());

                                String downloadUri = uriTask.getResult().toString();
                                if(uriTask.isSuccessful()) {
                                    //uploading the received url to database

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    //put post info
                                    hashMap.put("uid", userId);
                                    hashMap.put("firstName", userFirstName);
                                    hashMap.put("lastName", userLastName);
                                    hashMap.put("email", userEmail);
                                    hashMap.put("profileImage", userProfilePicture);
                                    //hashMap.put("postID", timeStamp);
                                    hashMap.put("postContent", content);
                                    hashMap.put("postImage", downloadUri);
                                    //hashMap.put("publishTime", timeStamp);

                                    DatabaseReference ref1 = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts");
                                    ref1.child(editPostID)
                                            .updateChildren(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused1) {
                                                    pd.dismiss();
                                                    Toast.makeText(CreatePostActivity.this, "Post Updated...", Toast.LENGTH_SHORT).show();

                                                    postContentET.setText("");
                                                    postContentImageIV.setImageURI(null);
                                                    image_rui = null;
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    pd.dismiss();
                                                    Toast.makeText(CreatePostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(CreatePostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(CreatePostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadPostData(String editPostID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts");
        Query fquery = databaseReference.orderByChild("postID").equalTo(editPostID);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    editContent = "" + ds.child("postContent").getValue();
                    editPostImage = "" + ds.child("postImage").getValue();

                    //set content
                    postContentET.setText(editContent);

                    //set image
                    if(!editPostImage.equals("noImage")) {
                        try{
                            Picasso.get().load(editPostImage).into(postContentImageIV);
                        } catch (Exception e) {}
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void uploadData(String postContent) {
        pd.setMessage("Publishing post");
        pd.show();

        //for post image-name, post-id, post-publish-time
        String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/" + "post_" + timeStamp;

        if(postContentImageIV.getDrawable() != null) {

            //get image from ImageView
            Bitmap bitmap = ((BitmapDrawable)postContentImageIV.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //image compress
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            //post with image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image is uploaded to firebase, get the url
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            String downloadUri = uriTask.getResult().toString();

                            if(uriTask.isSuccessful()) {
                                //url is received, uploading post to firebase database
                                HashMap<Object, String> hashMap = new HashMap<>();

                                //put post info
                                hashMap.put("uid", userId);
                                hashMap.put("firstName", userFirstName);
                                hashMap.put("lastName", userLastName);
                                hashMap.put("email", userEmail);
                                hashMap.put("profileImage", userProfilePicture);
                                hashMap.put("postID", timeStamp);
                                hashMap.put("postContent", postContent);
                                hashMap.put("postImage", downloadUri);
                                hashMap.put("publishTime", timeStamp);

                                //path to store post data
                                DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts");

                                //put data in this ref
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //added post in database
                                                pd.dismiss();
                                                Toast.makeText(CreatePostActivity.this, "Post Published", Toast.LENGTH_SHORT).show();

                                                //reset views
                                                postContentET.setText("");
                                                postContentImageIV.setImageURI(null);
                                                image_rui = null;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed adding post in database
                                                pd.dismiss();
                                                Toast.makeText(CreatePostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed uploading image
                            pd.dismiss();
                            Toast.makeText(CreatePostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            //post without image
            HashMap<Object, String> hashMap = new HashMap<>();

            //put post info
            hashMap.put("uid", userId);
            hashMap.put("firstName", userFirstName);
            hashMap.put("lastName", userLastName);
            hashMap.put("email", userEmail);
            hashMap.put("profileImage", userProfilePicture);
            hashMap.put("postID", timeStamp);
            hashMap.put("postContent", postContent);
            hashMap.put("postImage", "noImage");
            hashMap.put("publishTime", timeStamp);

            //path to store post data
            DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts");

            //put data in this ref
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //added post in database
                            pd.dismiss();
                            Toast.makeText(CreatePostActivity.this, "Post Published", Toast.LENGTH_SHORT).show();


                            //reset views
                            postContentET.setText("");
                            postContentImageIV.setImageURI(null);
                            image_rui = null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed adding post in database
                            pd.dismiss();
                            Toast.makeText(CreatePostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void pickFromCamera() {
        //intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        //startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
        activityResultLauncher.launch(intent);


    }

    private ActivityResultLauncher activityLauncher() {
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        Bundle bundle = result.getData().getExtras();
                        System.out.println(bundle);
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        postContentImageIV.setImageBitmap(bitmap);

                    }
                });
        return activityResultLauncher;
    }

    private ActivityResultLauncher activityLauncherGallery() {
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    postContentImageIV.setImageURI(uri);
                    image_rui = uri;
                    System.out.println(uri);
                });
        return mGetContent;

    }

    private void pickFromGalley() {
        //intent to pick image from gallery
        //Intent intent = new Intent(Intent.ACTION_PICK);
       // intent.setType("image/*");
        activityResultLauncher2.launch("image/*");
        //startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
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
        boolean storageResult = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return cameraResult && storageResult;
    }

    private void requestCameraPermission() {
        //request runtime camera permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null) {
            userEmail = user.getEmail();
            userId = user.getUid();

        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //handle permission results
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
                        pickFromCamera();

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
                        pickFromGalley();

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

    //after picking image from camera or gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {

            if(requestCode == IMAGE_PICK_GALLERY_CODE) {
                //image is picked from gallery, get uri of image
                image_rui = data.getData();

                //set image to ImageView
                postContentImageIV.setImageURI(image_rui);

            }else if(requestCode == IMAGE_PICK_CAMERA_CODE) {
                //image is picked from camera, get uri of image

                postContentImageIV.setImageURI(image_rui);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}