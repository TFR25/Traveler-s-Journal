package com.termproject.travelersjournal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import util.JournalApi;

public class SignIn extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText passwordEditText;
    private EditText emailEditText;
    private FirebaseAuth firebaseAuth;
    //Add Firestore connection
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Users == collection path
    private final CollectionReference collectionReference = db.collection("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        firebaseAuth = FirebaseAuth.getInstance();
        //connecting buttons
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        progressBar = findViewById(R.id.sign_in_progress);
        Button getStartedButton = findViewById(R.id.sign_in_button);
        Button getCreateAccountButton = findViewById(R.id.createAccount);
        getCreateAccountButton.setOnClickListener(v -> {
            //on click of create account button, move from SignIn.java to Register.java
            startActivity(new Intent(SignIn.this, Register.class));
        });
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                credentials(emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim());
            }
        });
    }
    private void credentials(String email, String pwd) {
        progressBar.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(pwd)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null){
                                user = firebaseAuth.getCurrentUser();
                                final String currentUserId = user.getUid();
                                collectionReference
                                        .whereEqualTo("userId", currentUserId)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                                @Nullable FirebaseFirestoreException e) {

                                                assert queryDocumentSnapshots != null;
                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                        JournalApi journalApi = JournalApi.getInstance();
                                                        journalApi.setUsername(snapshot.getString("username"));
                                                        journalApi.setUserId(snapshot.getString("userId"));

                                                        //Go to ListActivity
                                                        startActivity(new Intent(SignIn.this,
                                                                JournalCollection.class));
                                                        finish();
                                                    }
                                                }
                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(SignIn.this,
                            "Please enter email and password",
                            Toast.LENGTH_LONG)
                    .show();
        }
    }
}