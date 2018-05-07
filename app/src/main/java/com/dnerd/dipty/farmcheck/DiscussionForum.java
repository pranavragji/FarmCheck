package com.dnerd.dipty.farmcheck;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;


public class DiscussionForum extends AppCompatActivity {
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    private FloatingActionButton mAddPostBtn;

    private BottomNavigationView mMainbottomNav;

   /* private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_forum);

        mToolbar =  findViewById(R.id.discuss_toolbar);
        //toolbar.setTitle("");
        setSupportActionBar(mToolbar);

        mAuth = FirebaseAuth.getInstance();
        //firebaseFirestore = FirebaseFirestore.getInstance();

        mAddPostBtn = findViewById(R.id.add_post_btn);
        mAddPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostIntent = new Intent(DiscussionForum.this, NewPostActivity.class);
                startActivity(newPostIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.discussion_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.discussion_logout:
                logOut();
                return true;

            case R.id.discussion_settings:

               /* Intent settingsIntent = new Intent(DiscussionForum.this, SetupActivity.class);
                startActivity(settingsIntent);*/

                return true;


            default:
                return false;


        }

    }
    private void logOut() {


        mAuth.signOut();
        Intent intent = new Intent (DiscussionForum.this,MainActivity.class);
        startActivity(intent);
        //sendToLogin();
    }
}
