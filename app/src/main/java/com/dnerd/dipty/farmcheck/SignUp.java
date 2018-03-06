package com.dnerd.dipty.farmcheck;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    EditText mName,mEmail,mPassword;
    Button mSignUp;
    private FirebaseAuth mAuth;
    //ProgressDialog
    private ProgressDialog mRegProgress;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mStoreUserDefaultDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        mName = findViewById(R.id.signupEditTextName);
        mEmail = findViewById(R.id.signupEditTextEmail);
        mPassword = findViewById(R.id.signupEditTextPassword);
        mSignUp = findViewById(R.id.signupButton);

        mRegProgress = new ProgressDialog(this);

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });
    }

    private void signUpUser() {
        final String name = mName.getText().toString();
        String password = mPassword.getText().toString();
        String email = mEmail.getText().toString();

        //username = username.trim();
        password = password.trim();
        email = email.trim();

        /*If any of the fields are empty then alert dialog will pop up
          and after clicking ok cursor will focus on that field
         */
        if(name.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage(R.string.sign_up_error_message_name)
                    .setTitle(R.string.sign_up_error_title)
                    .setPositiveButton(android.R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            mName.requestFocus();
            return;
        }
        else if(email.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage(R.string.sign_up_error_message_password)
                    .setTitle(R.string.sign_up_error_title)
                    .setPositiveButton(android.R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            mPassword.requestFocus();
            return;
        }
        else if(name.isEmpty()&&password.isEmpty()&&email.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage(R.string.sign_up_error_message)
                    .setTitle(R.string.sign_up_error_title)
                    .setPositiveButton(android.R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage(R.string.validEmailError)
                    .setTitle(R.string.sign_up_error_title)
                    .setPositiveButton(android.R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            mEmail.requestFocus();
            return;

        }
        else if (password.length() < 6) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage(R.string.validPasswordError)
                    .setTitle(R.string.sign_up_error_title)
                    .setPositiveButton(android.R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            mPassword.requestFocus();
            return;

        }
        else {
            mRegProgress.setTitle("Registering User");
            mRegProgress.setMessage("Please wait while we create your account !");
            mRegProgress.setCanceledOnTouchOutside(false);
            mRegProgress.show();
            //crating new user
            final String finalEmail = email;
            final String finalEmail1 = email;
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mRegProgress.hide();
                    //if not successful
                    if (!task.isSuccessful()) {
                        /*if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                            builder.setMessage(R.string.firebase_signup_alreadyReagistered)
                                    .setTitle(R.string.sign_up_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }*/
                    }
                    else{
                        FirebaseUser cU =  FirebaseAuth.getInstance().getCurrentUser();

                        String current_user_id = cU.getUid();
                        mStoreUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

                        mStoreUserDefaultDataReference.child("user_name").setValue(name);
                        mStoreUserDefaultDataReference.child("user_email").setValue(finalEmail1);
                        mStoreUserDefaultDataReference.child("user_image").setValue("default_profile_image");
                        mStoreUserDefaultDataReference.child("user_thumb_image").setValue("default_profile_image").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful())
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                    builder.setMessage(R.string.firebase_signup_error)
                                            .setTitle(R.string.sign_up_error_title)
                                            .setPositiveButton(android.R.string.ok,null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                                else{
                                    Intent intent = new Intent(SignUp.this,Profile.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }
                        });

                    }
                }


            });
        }
    }


}
