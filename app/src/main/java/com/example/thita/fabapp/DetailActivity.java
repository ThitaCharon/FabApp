package com.example.thita.fabapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class DetailActivity extends AppCompatActivity {
    // Folder path for Firebase Storage.
    public static String Storage_Path = "Upload";
    // Root Database Name for Firebase Database.
    public static String Database_Path = "Uploads_db";
    // Creating button.
    public Button mChooseImageBtn, mUploadBtn, mShowListsImageBtn;

    // Creating ImageView.
    private ImageView mImageView;
    private EditText mImageText;

    // Image request code for onActivityResult() .
    public static final int PICK_IMAGE_REQUEST_CODE = 1;
    private ProgressBar mProgressBar ;
    // Creating URI filepath
    public Uri mImageUri, FilePathUri;
    // Creating StorageReference and DatabaseReference object.
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private StorageTask mUploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mChooseImageBtn = (Button)findViewById(R.id.pick_image_btn);
        mUploadBtn = (Button)findViewById(R.id.order_btn);
        mImageText = (EditText)findViewById(R.id.title_editText);
        mImageView = (ImageView)findViewById(R.id.image_view);
        mShowListsImageBtn = (Button)findViewById(R.id.showLists);
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar_pick_image);

//      // get Firebase instance
        storageRef = FirebaseStorage.getInstance().getReference(Storage_Path);
        databaseRef = FirebaseDatabase.getInstance().getReference(Database_Path);

        mProgressBar = new ProgressBar(DetailActivity.this);
        mChooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePicker();
            }
        });

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(DetailActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }else {
                    upLoadOrder();
                }
            }
        });

        mShowListsImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, DisplayImagesActivity.class);
                startActivity(intent);
            }
        });


    }

    //get file extension from image
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void upLoadOrder() {
        if (mImageUri != null){
            final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            mUploadTask = fileRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // delay for progress to display a moment
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(DetailActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                            ImageUploadInfo uploadItem = new ImageUploadInfo(mImageText.getText().toString().trim(),taskSnapshot.getStorage().getDownloadUrl().toString());
                            String uploadId = databaseRef.push().getKey();
                            databaseRef.child(uploadId).setValue(uploadItem);
                            // clear input
                            resetInput();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0* taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int)progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void imagePicker() {
        Intent intent_pickImage = new Intent();
        intent_pickImage.setType("image/*");
        intent_pickImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_pickImage, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(mImageView);
        }
    }

    private void resetInput() {
        mImageText.setText("");
        mImageView.setImageBitmap(null);
        mChooseImageBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mChooseImageBtn.setText(R.string.add_image);
    }





































    /** previous code**/
//
//    // Creating Method to get the selected image file Extension from File Path URI.
//    public String GetFileExtension(Uri uri) {
//        ContentResolver contentResolver = getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        // Returning the file Extension.
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
//    }
//
//    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
//    public void UploadImageFileToFirebaseStorage() {
//
//        // Checking whether FilePathUri Is empty or not.
//        if (FilePathUri != null) {
//
//            /**
//            // Setting progressDialog Title.
//            progressDialog.setTitle("Uploading...");
//             **/
//            // Showing progressDialog.
////            mProgressBar.show();
//
//
//            // Creating second StorageReference.
//            StorageReference storageReference2nd = storageRef.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
//            Log.d("File Extension : ----", GetFileExtension(FilePathUri));
//            Log.d("Storage Path : ----", Storage_Path + " Time : " + System.currentTimeMillis());
//
//            // Adding addOnSuccessListener to second StorageReference.
//            storageReference2nd.putFile(FilePathUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            // Getting image name from EditText and store into string variable.
//                            String TempImageName = mImageText.getText().toString().trim();
//                            // Hiding the progressDialog after done uploading.
////                            progressDialog.dismiss();
//                            // Showing toast message after done uploading.
//                            Toast.makeText(getApplicationContext(), "Item Uploaded ", Toast.LENGTH_LONG).show();
//                            @SuppressWarnings("VisibleForTests")
//                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(TempImageName, taskSnapshot.getStorage().getDownloadUrl().toString());
//
//                            // Getting image upload ID.
//                            String ImageUploadId = databaseRef.push().getKey();
//
//                            // Adding image upload id s child element into databaseReference.
//                            databaseRef.child(ImageUploadId).setValue(imageUploadInfo);
//
//                            //reset data for next order
//                            resetInput();
//                        }
//                    })
//                    // If something goes wrong .
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//
//                            // Hiding the progressDialog.
////                            progressDialog.dismiss();
//
//                            // Showing exception error message.
//                            Toast.makeText(DetailActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    })
//
//                    // On progress change upload time.
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            // Setting progressDialog Title.
////                            progressDialog.setTitle("Image is Uploading...");
//                        }
//                    });
//        }
//        else {
//            Toast.makeText(DetailActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
//        }
//    }



}
