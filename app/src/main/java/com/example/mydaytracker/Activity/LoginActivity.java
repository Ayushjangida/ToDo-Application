package com.example.mydaytracker.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mydaytracker.R;
import com.example.mydaytracker.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText getEmail, getPassword;
    private Button loginButton, createAccountButton, resendEmailButton;
    private ProgressBar progressBar;

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        //connecting the instances
        getEmail = findViewById(R.id.login_activity_email_edittext);
        getPassword = findViewById(R.id.login_activity_password_edittext);
        loginButton = findViewById(R.id.login_activity_loginbutton);
        createAccountButton = findViewById(R.id.login_activity_create_accountbutton);
        progressBar = findViewById(R.id.login_activity_progressbar);
        resendEmailButton = findViewById(R.id.login_activity_resend_email_accountbutton);

        resendEmailButton.setOnClickListener(this);

        //Setting up Onclick buttons
        loginButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
    }

    //Setting up OnClick Function
    @Override
    public void onClick(View view) {
        switch(view.getId())    {
            case R.id.login_activity_loginbutton :
                loginUser();
                break;

            case R.id.login_activity_create_accountbutton :
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
                break;

            case R.id.login_activity_resend_email_accountbutton :
                firebaseAuth.getCurrentUser().sendEmailVerification().
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())    {
                                    Toast.makeText(LoginActivity.this, "Email verification link sent again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                break;
        }
    }

    private void loginUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email = getEmail.getText().toString().trim();
        String password = getPassword.getText().toString().trim();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))   {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())    {
                        if (firebaseAuth.getCurrentUser().isEmailVerified())    {
                            user = firebaseAuth.getCurrentUser();
                            UserApi.getInstance().setUserId(user.getUid());
                            startActivity(new Intent(LoginActivity.this, UserActivity.class));
                        }
                       else {
                           Toast.makeText(LoginActivity.this, "Please verify your email to login", Toast.LENGTH_LONG).show();
                           resendEmailButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                    else    {
                        Toast.makeText(LoginActivity.this, "Authentication failed. Please check if email/password is correct",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }
        else    {
            Toast.makeText(LoginActivity.this, "Please enter Email and Password", Toast.LENGTH_SHORT).show();
        }
    }

}
