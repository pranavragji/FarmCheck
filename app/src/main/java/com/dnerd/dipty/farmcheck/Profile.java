package com.dnerd.dipty.farmcheck;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private static final String TAG = "Profile";
    private static final int gallery_pic = 101;
    private CircleImageView mProfileImage,mNavProfileImage,mNavPic;
    private ImageView mAddProfilePic;
    private TextView mName,mEmail,mPhone,mNavEmail;
    private Uri uriProfileImage;
    private ProgressDialog mProgressDialog;
    private String mProfileImageUrl;
    private FirebaseAuth mAuth;
    private DatabaseReference mGetUsersDataReference,mNavGetUsersDataRefrence,mMapDatabaseReference;
    private StorageReference profileImageReference;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionbarDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Toolbar toolbar =  findViewById(R.id.toolbar);
        //toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mProfileImage = findViewById(R.id.profileImage);
        mName = findViewById(R.id.profileTextViewUsername);
        mEmail = findViewById(R.id.profileTextViewEmail);
      //  mPhone = findViewById(R.id.profileTextViewPhone);
/*        mNavProfileImage = findViewById(R.id.navProfileImage);
        mNavEmail = findViewById(R.id.navTextViewEmail);*/
        mAddProfilePic =findViewById(R.id.addProfilePic);

        drawer = findViewById(R.id.drawer_nav);
        actionbarDrawer = new ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close);
        actionbarDrawer.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(actionbarDrawer);
        actionbarDrawer.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navView = findViewById(R.id.nav_view);

        View header = navView.getHeaderView(0);
        final TextView mNavEmail = header.findViewById(R.id.navTextViewEmail);
        final TextView mNavName = header.findViewById(R.id.navTextViewName);
        mNavPic = header.findViewById(R.id.navProfileImage);

        mAuth = FirebaseAuth.getInstance();
        String onlineUserId = mAuth.getCurrentUser().getUid();
        mGetUsersDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserId);
        mGetUsersDataReference.keepSynced(true);

       /* mMapDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Saved_location").child(onlineUserId);


        mMapDatabaseReference.child("location_counter").setValue(String.valueOf(0));
        mMapDatabaseReference.child(String.valueOf(0)).child("location_name").setValue("locationName");
        mMapDatabaseReference.child(String.valueOf(0)).child("location_latitude").setValue("latitude");
        mMapDatabaseReference.child(String.valueOf(0)).child("location_longitude").setValue("longitude").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful())
                {
                    Log.d(TAG, "onComplete: Saved_location table not created!");
                }
                else{
                    Log.d(TAG, "onComplete: Saved_location table created!");
                }
            }
        });*/

        profileImageReference = FirebaseStorage.getInstance().getReference();
        //offline capablities enabled
        mGetUsersDataReference.keepSynced(true);

        mGetUsersDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String email = dataSnapshot.child("user_email").getValue().toString();
                //String phone = dataSnapshot.child("user_phone").getValue().toString();
                final String image = dataSnapshot.child("user_image").getValue().toString();
               /* String thumb_image = dataSnapshot.child("user_thumb_image").toString();*/

                mName.setText(name);
                mEmail.setText(email);
                mNavEmail.setText(email);
                mNavName.setText(name);

               // toolbar.setTitle(name);
                //if(!phone.equals("+880***********")){
                //mPhone.setText(phone);
                //}
                if(!image.equals("default_profile_image"))
                {
                    //Picasso.with(Profile.this).load(image).placeholder(R.drawable.default_profile_image).into(mProfileImage);
                    Picasso.with(Profile.this).load(image).placeholder(R.drawable.default_profile_image).networkPolicy(NetworkPolicy.OFFLINE).into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(Profile.this).load(image).placeholder(R.drawable.default_profile_image).into(mProfileImage);
                        }
                    });

                    Picasso.with(Profile.this).load(image).placeholder(R.drawable.default_profile_image).networkPolicy(NetworkPolicy.OFFLINE).into(mNavPic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(Profile.this).load(image).placeholder(R.drawable.default_profile_image).into(mNavPic);
                        }
                    });
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mAddProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
                saveUserInfo();
                // loadUserInfo();
            }
        });
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.search) {
                    Toast.makeText(Profile.this, "search", Toast.LENGTH_LONG).show();
                    Intent phoneIntent = new Intent(Profile.this, ClassifierActivity.class);
                    startActivity(phoneIntent);
                }
                else if(id==R.id.discussion)
                {
                    Intent discussionIntent = new Intent(Profile.this, DiscussionForum.class);
                    startActivity(discussionIntent);
                    Toast.makeText(Profile.this, R.string.discussion,Toast.LENGTH_LONG).show();
                }
                else if(id==R.id.logout)
                {
                    mAuth.signOut();
                    Intent intent = new Intent(Profile.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
                    mGetUsersDataReference.child("user_image").setValue(mProfileImageUrl);


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

}