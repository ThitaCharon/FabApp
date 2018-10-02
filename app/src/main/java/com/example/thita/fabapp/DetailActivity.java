package com.example.thita.fabapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {
    // Folder path for Firebase Storage.
    public static String Storage_Path = "all_order_uploadss";
    // Root Database Name for Firebase Database.
    public static String Database_Path = "all_order_uploads_database";
    // Creating button.
    public Button chooseImageBtn, uploadImageBtn;
    // Creating EditText.
    private EditText imageName;
    // Creating ImageView.
    private ImageView selectImage;
    // Image request code for onActivityResult() .
    public static final int Image_Request_Code = 7;
    private ProgressDialog progressDialog ;
    // Creating URI filepath
    public Uri FilePathUri;
    // Creating StorageReference and DatabaseReference object.
    public StorageReference storageReference;
    public DatabaseReference databaseReference;

    public Button DisplayImageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//      // get Firebase instance
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        // assign id to button textView and editText
        chooseImageBtn = (Button)findViewById(R.id.pick_image_btn);
        uploadImageBtn = (Button)findViewById(R.id.order_btn);
        imageName = (EditText)findViewById(R.id.title_editText);
        selectImage = (ImageView)findViewById(R.id.image_view);
        DisplayImageButton = (Button)findViewById(R.id.DisplayImagesButton);
        progressDialog = new ProgressDialog(DetailActivity.this);

        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open storage to get image
                Intent intent_pickImage = new Intent();
                intent_pickImage.setType("image/*");
                intent_pickImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent_pickImage, "Please Select Image"), Image_Request_Code);
            }
        });

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImageFileToFirebaseStorage();
            }
        });

        DisplayImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DetailActivity.this, DisplayImagesActivity.class);
                startActivity(intent);
            }
        });


    }

    private void resetInput() {
        imageName.setText("");
        selectImage.setImageBitmap(null);
        chooseImageBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        chooseImageBtn.setText(R.string.add_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            Log.d("FilePathUri" , FilePathUri.toString());
            try {
                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                selectImage.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
                chooseImageBtn.setText(R.string.change_image);
                chooseImageBtn.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            /**
            // Setting progressDialog Title.
            progressDialog.setTitle("Uploading...");
             **/
            // Showing progressDialog.
            progressDialog.show();


            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            Log.d("File Extension : ----", GetFileExtension(FilePathUri));
            Log.d("Storage Path : ----", Storage_Path + " Time : " + System.currentTimeMillis());

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.
                            String TempImageName = imageName.getText().toString().trim();

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Item Uploaded ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(TempImageName, taskSnapshot.getStorage().getDownloadUrl().toString());

                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();

                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);

                            //reset data for next order
                            resetInput();
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception error message.
                            Toast.makeText(DetailActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");
                        }
                    });
        }
        else {
            Toast.makeText(DetailActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }
    }



}
