package com.dnerd.dipty.farmcheck;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {
    private static final String TAG ="NewPostActivity" ;
    private Toolbar mNewPostToolbar;

    private ImageView mNewPostImage;
    private EditText mNewPostDesc;
    private Button mNewPostBtn;

    private Uri postImageUri = null;

    private ProgressBar mNewPostProgress;

    private StorageReference storageReference;
    private DatabaseReference mGetUsersDataReference,mNavGetUsersDataRefrence,mPostDatabaseReference;
    private FirebaseAuth mAuth;

    private String current_user_id;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mNewPostToolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(mNewPostToolbar);

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        final String onlineUserId = mAuth.getCurrentUser().getUid();
        mGetUsersDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserId);
        mGetUsersDataReference.keepSynced(true);

        mNewPostImage = findViewById(R.id.new_post_image);
        mNewPostDesc = findViewById(R.id.new_post_desc);
        mNewPostBtn = findViewById(R.id.post_btn);
        mNewPostProgress = findViewById(R.id.new_post_progress);

        mNewPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(NewPostActivity.this);

            }
        });

        mNewPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String desc = mNewPostDesc.getText().toString();
                mNewPostProgress.setVisibility(View.VISIBLE);

                final String randomName = UUID.randomUUID().toString();

                // PHOTO UPLOAD
                File newImageFile = new File(postImageUri.getPath());
                try {

                    compressedImageFile = new Compressor(NewPostActivity.this)
                            .setMaxHeight(720)
                            .setMaxWidth(720)
                            .setQuality(50)
                            .compressToBitmap(newImageFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                UploadTask filePath = storageReference.child("post_images").child(randomName + ".jpg").putBytes(imageData);
                filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                        final String downloadUri = task.getResult().getDownloadUrl().toString();

                        if (task.isSuccessful()) {

                            File newThumbFile = new File(postImageUri.getPath());
                            try {

                                compressedImageFile = new Compressor(NewPostActivity.this)
                                        .setMaxHeight(100)
                                        .setMaxWidth(100)
                                        .setQuality(1)
                                        .compressToBitmap(newThumbFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] thumbData = baos.toByteArray();

                            UploadTask uploadTask = storageReference.child("post_images/thumbs")
                                    .child(randomName + ".jpg").putBytes(thumbData);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();
                                    String time = getCurrentTime(v);
                                    mPostDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Post").child(onlineUserId);

                                    mPostDatabaseReference.child("image_url").setValue(downloadUri);
                                    mPostDatabaseReference.child("image_thumb").setValue(downloadthumbUri);
                                    mPostDatabaseReference.child("desc").setValue(desc);

                                    mPostDatabaseReference.child("timestamp").setValue(time).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Log.d(TAG, "onComplete: post table done");
                                                mNewPostProgress.setVisibility(View.INVISIBLE);
                                                Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                                Intent mainIntent = new Intent(NewPostActivity.this, DiscussionForum.class);
                                                startActivity(mainIntent);
                                                finish();
                                            } else{
                                                mNewPostProgress.setVisibility(View.INVISIBLE);
                                                Log.d(TAG, "onComplete: post table not done!");
                                            }
                                        }});

                                 /*   Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("user_id", current_user_id);
                                    postMap.put("image_url", downloadUri);
                                    postMap.put("image_thumb", downloadthumbUri);
                                    postMap.put("desc", desc);
                                    postMap.put("timestamp", FieldValue.serverTimestamp());*/

                                  /*  mPostDatabaseReference.child("Post")..
                                    add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {

                                            if (task.isSuccessful()) {

                                                Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                                Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();

                                            } else {


                                            }

                                            newPostProgress.setVisibility(View.INVISIBLE);

                                        }
                                    });*/

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    //Error handling

                                }
                            });


                        } else {

                            mNewPostProgress.setVisibility(View.INVISIBLE);

                        }

                }
            });


        }


});

}
    public String getCurrentTime(View view) {
        Calendar calendar = Calendar.getInstance();
      /*  SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String strTime =  mdformat.format(calendar.getTime());*/
        String strTime = DateFormat.getTimeInstance().format(calendar.getTime());
        return strTime;
        // display(strDate);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                mNewPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}

