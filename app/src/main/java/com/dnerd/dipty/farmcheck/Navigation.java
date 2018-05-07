package com.dnerd.dipty.farmcheck;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Dipty on 5/2/2018.
 */

public class Navigation extends AppCompatActivity {
    private CircleImageView mNavPic;
    private TextView mEmail,mName;

    private FirebaseAuth mAuth;
    private DatabaseReference mGetUsersDataReference,mNavGetUsersDataRefrence,mMapDatabaseReference;
    private StorageReference profileImageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        mNavPic = findViewById(R.id.navProfileImage);
        mName = findViewById(R.id.navTextViewName);
        mEmail = findViewById(R.id.navTextViewEmail);

        mAuth = FirebaseAuth.getInstance();
        String onlineUserId = mAuth.getCurrentUser().getUid();
        mGetUsersDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserId);
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
                //if(!phone.equals("+880***********")){
                //mPhone.setText(phone);
                //}
                if(!image.equals("default_profile_image"))
                {
                    //Picasso.with(Profile.this).load(image).placeholder(R.drawable.default_profile_image).into(mProfileImage);
                    Picasso.with(Navigation.this).load(image).placeholder(R.drawable.default_profile_image).networkPolicy(NetworkPolicy.OFFLINE).into(mNavPic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(Navigation.this).load(image).placeholder(R.drawable.default_profile_image).into(mNavPic);
                        }
                    });
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
