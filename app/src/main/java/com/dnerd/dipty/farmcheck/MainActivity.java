package com.dnerd.dipty.farmcheck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    LinearLayout mLinearLayout1,mLinearLayout2;
    Button mLogin,mSignUp;
    Animation mDownToUp,mUpToDown;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLinearLayout1 = findViewById(R.id.mainLinearLayout1);
        mLinearLayout2 = findViewById(R.id.mainLinearLayout2);
        mLogin = findViewById(R.id.mainLoginButton);
        mSignUp = findViewById(R.id.mainSignUpButton);

        mDownToUp = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        mUpToDown =AnimationUtils.loadAnimation(this,R.anim.uptodown);
        mLinearLayout1.setAnimation(mUpToDown);
        mLinearLayout2.setAnimation(mDownToUp);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user =mAuth.getCurrentUser();
        if(user!=null)
        {
            Intent intent = new Intent(MainActivity.this,Profile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SignUp.class);
                startActivity(intent);
            }
        });
    }
}
