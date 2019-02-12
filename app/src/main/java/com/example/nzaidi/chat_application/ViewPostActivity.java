package com.example.nzaidi.chat_application;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class ViewPostActivity extends AppCompatActivity {

    private ImageView postImageView;
    private TextView txtPostDescription;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        mAuth = FirebaseAuth.getInstance();


        postImageView = findViewById(R.id.postImage);
        txtPostDescription = findViewById(R.id.txtPostDescription);


        txtPostDescription.setText(getIntent().getStringExtra("des"));
        Picasso.get().load(getIntent().getStringExtra("imageLink")).into(postImageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FirebaseDatabase.getInstance().getReference()
                .child("app_users").child(mAuth.getCurrentUser()
                .getUid()).child("received_posts")
                .child(getIntent().getStringExtra("uid")).removeValue();
        FirebaseStorage.getInstance().getReference().child("myImages").child(getIntent().getStringExtra("imageIdentifier")).delete();


    }
}

