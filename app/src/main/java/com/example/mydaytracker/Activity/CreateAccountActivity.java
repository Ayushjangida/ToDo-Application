package com.example.mydaytracker.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mydaytracker.R;
import com.example.mydaytracker.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText getFirstName, getLastName, getEmail, getPassword;
    private Button createAccountButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("User");

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        getFirstName = findViewById(R.id.create_account_first_name_edittext);
        getLastName = findViewById(R.id.create_account_last_name_edittext);
        getEmail = findViewById(R.id.create_account_email_edittext);
        getPassword = findViewById(R.id.create_account_password_edittext);
        createAccountButton = findViewById(R.id.create_account_create_account_button);
        progressBar = findViewById(R.id.create_account_progressbar);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = getFirstName.getText().toString().trim();
                String lastName = getLastName.getText().toString().trim();
                String email = getEmail.getText().toString().trim();
                String password = getPassword.getText().toString().trim();

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        && password.length() > 6
                        && !TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(lastName) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    createUserAccount(firstName, lastName, email, password);
                }

                if(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))  {
                    Toast.makeText(CreateAccountActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                if(password.length() < 6)   {
                    getPassword.setError("Password must be more than 6 charecters");
                    getPassword.requestFocus();
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())    {
                    getEmail.setError("Please enter a valid email");
                    getEmail.requestFocus();
                }


            }
        });
    }

    private void createUserAccount(final String firstName, final String lastName, String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())    {
                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())    {
                                                progressBar.setVisibility(View.VISIBLE);
                                                getFirstName.setText("");
                                                getLastName.setText("");
                                                getEmail.setText("");
                                                getPassword.setText("");
                                                user = firebaseAuth.getCurrentUser();
                                                final String currentUserID = user.getUid();

                                                Map<String, String> userObj = new HashMap<>();
                                                userObj.put("userId", currentUserID);
                                                userObj.put("first name", firstName);
                                                userObj.put("last name", lastName);

                                                db.collection("User").document(currentUserID)
                                                        .set(userObj)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                progressBar.setVisibility(View.INVISIBLE);
//                                                                UserApi.getInstance().setFirsName(firstName);
//                                                                UserApi.getInstance().setFirsName(lastName);
//                                                                UserApi.getInstance().setUserId(currentUserID);
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                                                                Toast.makeText(CreateAccountActivity.this, "Registered successfully. Please check your mail to verify account", Toast.LENGTH_LONG).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                            }
                                        }
                                    });
//                            progressBar.setVisibility(View.VISIBLE);
//                            user = firebaseAuth.getCurrentUser();
//                            final String currentUserID = user.getUid();
//
//                            Map<String, String> userObj = new HashMap<>();
//                            userObj.put("userId", currentUserID);
//                            userObj.put("first name", firstName);
//                            userObj.put("last name", lastName);
//
//                           db.collection("User").document(currentUserID)
//                                   .set(userObj)
//                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                       @Override
//                                       public void onSuccess(Void aVoid) {
//                                           progressBar.setVisibility(View.INVISIBLE);
//                                           UserApi.getInstance().setFirsName(firstName);
//                                           UserApi.getInstance().setFirsName(lastName);
//                                           UserApi.getInstance().setUserId(currentUserID);
//                                           startActivity(new Intent(CreateAccountActivity.this, UserActivity.class));
//                                       }
//                                   })
//                                   .addOnFailureListener(new OnFailureListener() {
//                                       @Override
//                                       public void onFailure(@NonNull Exception e) {
//
//                                       }
//                                   });
                        }
                        else   {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(CreateAccountActivity.this,
                                        "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
