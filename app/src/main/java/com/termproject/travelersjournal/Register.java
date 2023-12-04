package com.termproject.travelersjournal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalApi;


public class Register extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Added Firestore connection
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Users is collection path
    private final CollectionReference collectionReference = db.collection("Users");


    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        Button create_account_button = findViewById(R.id.createAccount);
        progressBar = findViewById(R.id.progress_reg);
        emailEditText = findViewById(R.id.email_register);
        passwordEditText = findViewById(R.id.password_register);
        userNameEditText = findViewById(R.id.userName_register);
        //listening for changes with firebase auth
        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                //user already signed in

            } else {
                //no user yet
                progressBar.setVisibility(View.INVISIBLE);
            }
        };

        create_account_button.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(emailEditText.getText().toString())
                    && !TextUtils.isEmpty(passwordEditText.getText().toString())
                    && !TextUtils.isEmpty(userNameEditText.getText().toString())) {

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String username = userNameEditText.getText().toString().trim();

                createUserEmailAccount(email, password, username);
                startActivity(new Intent(Register.this, JournalEntry.class));
            } else {
                Toast.makeText(Register.this, "Empty Fields Not Allowed",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createUserEmailAccount(String email, String password, String username) {
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(username)) {

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    //task is object with user created
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //move to journal entry activity
                            currentUser = firebaseAuth.getCurrentUser();
                            assert currentUser != null;
                            String currentUserId = currentUser.getUid();

                            //user map to create user in user firestore collection

                            Map<String, String> userObj = new HashMap<>();
                            userObj.put("userId", currentUserId);
                            userObj.put("username", username);

                            //save to firestore database
                            collectionReference.add(userObj)
                                    .addOnSuccessListener(documentReference -> documentReference.get()
                                            .addOnCompleteListener(task1 -> {
                                                if (Objects.requireNonNull(task1.getResult()).exists()) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    String name = task1.getResult()
                                                            .getString("username");

                                                    JournalApi journalApi = JournalApi.getInstance();
                                                    journalApi.setUserId(currentUserId);
                                                    journalApi.setUsername(name);

                                                    Intent intent = new Intent(Register.this,
                                                            JournalEntry.class);
                                                    intent.putExtra("username", name);
                                                    intent.putExtra("userId", currentUserId);
                                                    startActivity(intent);

                                                }else {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            }))
                                    .addOnFailureListener(e -> progressBar.setVisibility(View.INVISIBLE));
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

        }).addOnFailureListener(e -> progressBar.setVisibility(View.INVISIBLE));

    }else {
            progressBar.setVisibility(View.INVISIBLE);

        }
}
    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}