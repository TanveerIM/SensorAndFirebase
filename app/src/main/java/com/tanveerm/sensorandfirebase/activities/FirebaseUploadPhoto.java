package com.tanveerm.sensorandfirebase.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tanveerm.sensorandfirebase.R;
import com.tanveerm.sensorandfirebase.model.Upload;

public class FirebaseUploadPhoto extends AppCompatActivity implements View.OnClickListener {

    public static final int PICK_IMAGE_REQUEST = 100;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    // URL to store file
    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_upload_photo);

        initialize();

    }

    private void initialize() {

        mButtonChooseImage = findViewById(R.id.buttonChooseImage);
        mButtonUpload = findViewById(R.id.buttonUpload);
        mTextViewShowUploads = findViewById(R.id.textViewShowUploads);
        mImageView = findViewById(R.id.imageView);
        mProgressBar = findViewById(R.id.progress_bar);

        mButtonChooseImage.setOnClickListener(this);
        mButtonUpload.setOnClickListener(this);
        mTextViewShowUploads.setOnClickListener(this);

        // Create a storage reference
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        // Create a database reference
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.buttonChooseImage:
                openFileChooser();
                break;
            case R.id.buttonUpload:
                /* Here we check that
                 * file uploading is completed or not*/
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(this, "Upload in progress...", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }

                break;
            case R.id.textViewShowUploads:

                mImageView.setImageDrawable(null);
                Intent intent = new Intent(this, FirebaseRetrievePhoto.class);
                startActivity(intent);

                break;
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mImageView);
        }
    }

    // It will return as the extension of the selected file
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile() {
        if (mImageUri != null) {
            //getting the storage reference
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(mImageUri));

            //adding the file to reference
            /* which is use to manage and
             * monitor the status of the upload*/
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadURL = uri;
                                    Log.e("TAG", "Url:  "+downloadURL);
                                    Upload upload = new Upload(downloadURL.toString());
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                    Toast.makeText(FirebaseUploadPhoto.this, "Upload successful", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FirebaseUploadPhoto.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                            mProgressBar.setProgress((int) progress);
                            int num = mProgressBar.getProgress();
                            Log.e("TAG", "ProgressBar: " + num);

                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();;
        }
    }
}
