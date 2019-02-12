package com.example.nzaidi.chat_application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText edtUsername,edtPassword,edtEmail;
    Button btnSignUp,btnSignIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        edtUsername = (EditText)findViewById(R.id.edtUsername);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText)findViewById(R.id.edtPassword);

        mAuth = FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUp();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){

            transitionToSocialMediaActivity();

        }

    }

    private void signUp()
    {
        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this,"Signing up is successful" ,Toast.LENGTH_LONG).show();
                            transitionToSocialMediaActivity();
                            FirebaseDatabase.getInstance().getReference().child("my_users").child(task.getResult().getUser()
                            .getUid()).child("username").setValue(edtUsername.getText().toString());

                        }
                        else{

                            Toast.makeText(MainActivity.this,"Signing up is unsuccessful" ,Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    private void signIn()
    {
        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this,"Signing in  successfully" ,Toast.LENGTH_LONG).show();
                            transitionToSocialMediaActivity();

                        }else
                        {
                            Toast.makeText(MainActivity.this,"Signing in unsuccessful" ,Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }

    private void transitionToSocialMediaActivity()
    {
        Intent intent = new Intent(MainActivity.this,SocialMediaActivity.class);
        startActivity(intent);
    }
}
