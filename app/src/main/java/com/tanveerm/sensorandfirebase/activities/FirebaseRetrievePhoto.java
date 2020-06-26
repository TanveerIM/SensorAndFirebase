package com.tanveerm.sensorandfirebase.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tanveerm.sensorandfirebase.R;
import com.tanveerm.sensorandfirebase.model.Upload;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRetrievePhoto extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private List<Upload> mUploads;
    private ViewFlipper mViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_retrieve_photo);

        initialize();
    }

    private void initialize() {
        mUploads = new ArrayList<>();

        mViewFlipper = findViewById(R.id.imageSlider);

        // Create a database reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        // Read from the database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // add items in Model class
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }

                imageSliderLayout();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Toast.makeText(FirebaseRetrievePhoto.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        
    }

    private void imageSliderLayout() {
        for (Upload name : mUploads) {
            flipImages(name.getImageUrl());
        }

    }

    private void flipImages(String imageUrl) {
        ImageView imageView = new ImageView(this);
        Picasso.get().load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);

        mViewFlipper.addView(imageView);

        // set 5 seconds interval between each image
        mViewFlipper.setFlipInterval(5000);
        mViewFlipper.setAutoStart(true);

        mViewFlipper.startFlipping();
        // set sliding animation effect
        mViewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        mViewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);

    }
}
