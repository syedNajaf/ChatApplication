package com.example.nzaidi.chat_application;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SocialMediaActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ImageView imgSelectImage;
    private Button btnCreatePost;
    private EditText edtDescription;
    private  Bitmap bitmap;
    private ListView listView;
    private ArrayList<String> appUsers;
    private ArrayAdapter adapter;
    private ArrayList<String> UIDs;
    private String uploadedImageLink;
    private String imageIdentifier;
    private String imageDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        firebaseAuth = FirebaseAuth.getInstance();

        imgSelectImage = findViewById(R.id.imgSelectImage);
        btnCreatePost = findViewById(R.id.btnCreatePost);

        edtDescription = findViewById(R.id.edtDescription);

        listView = findViewById(R.id.listView);
        appUsers = new ArrayList<>();
        UIDs = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, appUsers);
        listView.setAdapter(adapter);


        FirebaseDatabase.getInstance().getReference().child("app_users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String userEmail = (String) dataSnapshot.child("email").getValue();
                appUsers.add(userEmail);
                UIDs.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        imgSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadTheSelectedImageTotheServer();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put("fromWhom", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                dataMap.put("imageIdentifier", imageIdentifier);
                dataMap.put("imageLink", uploadedImageLink);
                dataMap.put("des", edtDescription.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("app_users").child(UIDs.get(i)).child("received_posts").push().setValue(dataMap);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logoutItem:
                firebaseAuth.signOut();

                finish();

                break;

            case R.id.sentToMeItem:

                Intent intent = new Intent(SocialMediaActivity.this, PostsSentToMeActivity.class);
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        firebaseAuth.signOut();

        finish();
    }

    private void selectImage() {
        if (Build.VERSION.SDK_INT < 23) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1000);
        } if (Build.VERSION.SDK_INT >= 23)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=  PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);

            } else {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1000);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            selectImage();

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            Uri chosenImageData = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageData);
                imgSelectImage.setImageBitmap(bitmap);


            } catch (Exception e) {

                e.printStackTrace();
            }

        }

    }

    private void uploadTheSelectedImageTotheServer() {


        // Get the data from an ImageView as bytes
        if (bitmap != null) {

            imgSelectImage.setDrawingCacheEnabled(true);
            imgSelectImage.buildDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
            imageIdentifier = UUID.randomUUID().toString() + ".png";


            final UploadTask uploadTask = FirebaseStorage.getInstance().getReference().
                    child("myImages").
                    child(imageIdentifier).putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(SocialMediaActivity.this, "Uploading Process Failed - Please try again", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    Toast.makeText(SocialMediaActivity.this, "Uploading was Successful", Toast.LENGTH_LONG).show();
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            uploadedImageLink = task.getResult().toString();

                        }
                    });


                }
            });
        }
    }
}
