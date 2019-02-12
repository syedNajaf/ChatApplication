package com.example.nzaidi.chat_application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class PostsSentToMeActivity extends AppCompatActivity {

    private ListView postsSentToMeListView;
    private ArrayList<String> users;
    private ArrayAdapter adapter;
    private FirebaseAuth mAuth;
    private ArrayList<DataSnapshot> posts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_sent_to_me);

        mAuth = FirebaseAuth.getInstance();

        postsSentToMeListView = findViewById(R.id.postsSentToMeListView);
        users = new ArrayList<>();
        posts = new ArrayList<>();

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, users);


        postsSentToMeListView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("app_users").child(mAuth.getCurrentUser().getUid()).child("received_posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                users.add((String) dataSnapshot.child("fromWhom").getValue());
                posts.add(dataSnapshot);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                int i = 0;

                for (DataSnapshot snapshot : posts) {

                    if (snapshot.getKey().equals(dataSnapshot.getKey())) {

                        posts.remove(i);
                        users.remove(i);

                    }

                    i++;

                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postsSentToMeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                DataSnapshot dataSnapshot = posts.get(i);
                Intent intent = new Intent(PostsSentToMeActivity.this, ViewPostActivity.class);

                intent.putExtra("imageIdentifier", (String) dataSnapshot.child("imageIdentifier").getValue());
                intent.putExtra("uid", dataSnapshot.getKey());
                intent.putExtra("imageLink", (String) dataSnapshot.child("imageLink").getValue());
                intent.putExtra("des", (String) dataSnapshot.child("des").getValue());


                startActivity(intent);

            }
        });

    }


}
