package com.dnerd.dipty.farmcheck;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class Profile extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {
    private static final int gallery_pic = 101;
    CircleImageView mProfileImage;
    TextView mName,mEmail;
    Uri uriProfileImage;
    private ProgressDialog mProgressDialog;
    String mProfileImageUrl;
    FirebaseAuth mAuth;
    private DatabaseReference getUsersDataReference;
    StorageReference profileImageReference;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionbarDrawer;
    private MaterialSearchBar searchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchBar =  findViewById(R.id.searchBar);
        searchBar.setHint("Custom hint");
        searchBar.setSpeechMode(true);
        searchBar.setOnSearchActionListener((MaterialSearchBar.OnSearchActionListener) this);


        mProfileImage = findViewById(R.id.profileImage);
        mName = findViewById(R.id.profileTextViewUsername);
        mEmail = findViewById(R.id.profileTextViewEmail);
        drawer = findViewById(R.id.drawer_nav);
        actionbarDrawer = new ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close);
        actionbarDrawer.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(actionbarDrawer);
        actionbarDrawer.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navView = findViewById(R.id.nav_view);

        mAuth = FirebaseAuth.getInstance();
        String onlineUserId = mAuth.getCurrentUser().getUid();
        getUsersDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserId);
        profileImageReference = FirebaseStorage.getInstance().getReference();
        //offline capablities enabled
        getUsersDataReference.keepSynced(true);



        //loadUserInfo();
        getUsersDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String email = dataSnapshot.child("user_email").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();
                String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                mName.setText(name);
                mEmail.setText(email);
                //loadUserInfo();
                //Glide.with(Profile.this).load(image).into(mProfileImage);

                if(!image.equals("default_profile_image")) {
                    Picasso.with(Profile.this).load(image).placeholder(R.drawable.default_profile_image).into(mProfileImage);
                }
                //mProfileImage.setImageResource(Integer.parseInt(image));
                // Picasso.with(Profile.this).load(image).centerCrop().into(mProfileImage);
              /*  Picasso.with(Profile.this).load(image).centerCrop()
                        .into((CircleImageView) findViewById(R.id.settigns_profile_image));*/


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
                saveUserInfo();
                 loadUserInfo();
            }
        });
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.editProfile)
                {
                    Toast.makeText(Profile.this,"Edit Profile",Toast.LENGTH_LONG).show();
                   /* Intent phoneIntent = new Intent(Profile.this,EditPhoneNumber.class);
                    startActivity(phoneIntent);*/
                }
                else if(id==R.id.search)
                {
                    Toast.makeText(Profile.this,"Search",Toast.LENGTH_LONG).show();
                    /*Intent cardIntent = new Intent(Profile.this,CardNumber.class);
                    startActivity(cardIntent);*/
                }
                else if(id==R.id.discussion)
                {
                    Toast.makeText(Profile.this,"Discussion Forum",Toast.LENGTH_LONG).show();
                }
                else if(id==R.id.logout)
                {
                    Toast.makeText(Profile.this,"Log out",Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionbarDrawer.onOptionsItemSelected(item)||super.onOptionsItemSelected(item);

    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
            if (user.getPhotoUrl() != null) {
                //String photoUrl = user.getPhotoUrl().toString();
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(mProfileImage);
            }

            String displayName = user.getDisplayName();

        }
    }

    private void saveUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null&& mProfileImageUrl!=null)
        {
            UserProfileChangeRequest profileChange = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(mProfileImageUrl)).build();
            user.updateProfile(profileChange).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(Profile.this,R.string.profileUpdated,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == gallery_pic && resultCode == RESULT_OK && data !=null && data.getData() != null)
        {
            uriProfileImage = data.getData();
            /*CropImage.activity(uriProfileImage)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(Profile.this);*/

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                mProfileImage.setImageBitmap(bitmap);


                uploadImageToFirebaseStorgae();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorgae() {
        StorageReference profileInageRef = FirebaseStorage.getInstance().getReference("user_image/"+System.currentTimeMillis()+".jpg");

        if(uriProfileImage != null)
        {
            mProgressDialog = new ProgressDialog(Profile.this);
            mProgressDialog.setTitle("Uploading Image...");
            mProgressDialog.setMessage("Please wait while we upload and process the image.");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            profileInageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressDialog.dismiss();

                    mProfileImageUrl = taskSnapshot.getDownloadUrl().toString();
                    getUsersDataReference.child("user_image").setValue(mProfileImageUrl);

                    Log.d("idb", "onSuccess: " + mProfileImageUrl);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(Profile.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }




    private void showImageChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image"),gallery_pic);
    }

    @Override
    public void onSearchStateChanged(boolean b) {
        String state = b ? "enabled" : "disabled";
        Toast.makeText(Profile.this, "Search " + state, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onSearchConfirmed(CharSequence charSequence) {
        Toast.makeText(this,"Searching "+ charSequence.toString()+" ......",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonClicked(int i) {
        switch (i){
            case MaterialSearchBar.BUTTON_NAVIGATION:
                Toast.makeText(Profile.this, "Button Nav " , Toast.LENGTH_SHORT).show();
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                Toast.makeText(Profile.this, "Speech " , Toast.LENGTH_SHORT).show();
        }
    }


}
