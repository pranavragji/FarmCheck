package com.dnerd.dipty.farmcheck;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login extends AppCompatActivity {
    TextView mcreateId,mForgetPassword;
    Button mLogin;
    EditText mEmail,mPassword;
    FirebaseAuth mAuth;
    //ProgressDialog
    private ProgressDialog mRegProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        mcreateId = findViewById(R.id.loginTextViewCreateAccount);
        mEmail = findViewById(R.id.loginEditTextEmail);
        mPassword = findViewById(R.id.loginEditTextPassword);
        mForgetPassword = findViewById(R.id.loginTextViewForgetPassword);
        mLogin = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();
        mRegProgress = new ProgressDialog(this);

        FirebaseUser user =mAuth.getCurrentUser();
        /*if(user!=null)
        {
            Intent intent = new Intent(Login.this,Profile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }*/

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        //If not registered and clicks create account
        mcreateId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void userLogin() {
        String password = mPassword.getText().toString();
        String email = mEmail.getText().toString();

        //username = username.trim();
        password = password.trim();
        email = email.trim();

        /*If any of the fields are empty then alert dialog will pop up
          and after clicking ok cursor will focus on that field
         */
        if(email.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setMessage(R.string.sign_up_error_message_email)
                    .setTitle(R.string.sign_up_error_title)
                    .setPositiveButton(android.R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            mEmail.requestFocus();
            return;
        }
        else if(password.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setMessage(R.string.login_error_message_password)
                    .setTitle(R.string.sign_up_error_title)
                    .setPositiveButton(android.R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            mPassword.requestFocus();
            return;
        }
        else if(password.isEmpty()&&email.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setMessage(R.string.login_error_message)
                    .setTitle(R.string.sign_up_error_title)
                    .setPositiveButton(android.R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setMessage(R.string.validEmailError)
                    .setTitle(R.string.login_error_title)
                    .setPositiveButton(android.R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            mEmail.requestFocus();
            return;

        }
        else{
            mRegProgress.setTitle("Login User");
            mRegProgress.setMessage("Please wait while logging in your account!");
            mRegProgress.setCanceledOnTouchOutside(false);
            mRegProgress.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mRegProgress.hide();
                    if(task.isSuccessful())
                    {

                        Intent intent = new Intent(Login.this,Profile.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                        builder.setMessage(R.string.firebase_login_error)
                                .setTitle(R.string.login_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                }
            });
        }
    }
}
