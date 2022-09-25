package com.example.androidlab12;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AddGundam extends AppCompatActivity {
    String Storage_Path = "Gundams/";
    String Database_Path = "Gundams";
    android.widget.Button bChoose, bAdd;
    android.widget.EditText edName, edPrice ;
    android.widget.ImageView img;
    android.net.Uri FilePathUri;

    //uri to store file
    private Uri filePath;

    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;
    android.app.ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gundam);

        // initital UI
        bChoose = (Button) findViewById(R.id.bChoose);
        bAdd = (Button) findViewById(R.id.bAdd);
        edName = (EditText) findViewById(R.id.edName);
        edPrice = (EditText) findViewById(R.id.edPrice);
        img = (ImageView) findViewById(R.id.img);

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        progressDialog = new android.app.ProgressDialog(AddGundam.this);

        // ปุ่มเลือกภาพ
        bChoose.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                android.content.Intent intent = new android.content.Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
                startActivityForResult(android.content.Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });

        // ปุ่มอัพโหลดภาพ
        bAdd.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                // Calling method to upload selected image on Firebase storage.
                uploadFile();
            }
        });

    }

    /*
    ------------------------
       onActivityResult
    ------------------------
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // Setting up bitmap selected image into ImageView.
                img.setImageBitmap(bitmap);
                // After selecting image change choose button above text.
                bChoose.setText("selected");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /*
    ------------------------
        uploadFile()
    ------------------------
    */
    public void uploadFile() {

        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            final StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(filePath));

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //----
                            // Download file From Firebase Storage ==>> store real path at Firebase Storage
                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadPhotoUrl) {
                                    Gundam gundam = new Gundam(edName.getText().toString().trim(),
                                            Integer.parseInt(edPrice.getText().toString().trim()),
                                            downloadPhotoUrl.toString());

                                    //adding an upload to firebase database
                                    String uploadId = mDatabase.push().getKey();
                                    mDatabase.child(uploadId).setValue(gundam);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            android.widget.Toast.makeText(AddGundam.this, "Please Select Image or Add Image Name", android.widget.Toast.LENGTH_LONG).show();
        }
    }
}