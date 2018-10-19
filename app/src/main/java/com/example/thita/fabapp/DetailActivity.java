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


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
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
    private EditText mImageText, mQuantity, mUnit;

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
        mQuantity = (EditText)findViewById(R.id.quantity_text);
        mUnit = (EditText)findViewById(R.id.unit_text);
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
                    Toast.makeText(DetailActivity.this, R.string.UPLOADING, Toast.LENGTH_SHORT).show();
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
        if (mImageUri != null ) {
            final StorageReference photoRef = storageRef.child(mImageUri.getLastPathSegment());
            // Upload file to Firebase Storage
            photoRef.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        mImageUri = task.getResult();
                        Log.d("mImageUri", mImageUri.toString());
                        // delay for progress to display a moment
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                        ImageUploadInfo uploadInfo = new ImageUploadInfo(mImageText.getText().toString(), mImageUri.toString(),mQuantity.getText().toString(),mUnit.getText().toString());
                        databaseRef.push().setValue(uploadInfo);
                        resetInput();
                    } else {
                        Toast.makeText(DetailActivity.this, R.string.FAIL_UPLOAD + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (mImageText != null) {
            //TODO if no image select
            Toast.makeText(this, R.string.NO_IMAGE_SELECTED, Toast.LENGTH_SHORT).show();
            ImageUploadInfo uploadInfo = new ImageUploadInfo(mImageText.getText().toString(), null,mQuantity.getText().toString(),mUnit.getText().toString());
            databaseRef.push().setValue(uploadInfo);
            resetInput();
        } else {
            Toast.makeText(this, R.string.SELECT_TITTLE, Toast.LENGTH_SHORT).show();
        }

    }

    private void imagePicker() {
        Intent intent_pickImage = new Intent();
        intent_pickImage.setType("image/*");
        intent_pickImage.setAction(Intent.ACTION_GET_CONTENT);
        intent_pickImage.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent_pickImage, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(mImageView);
            mChooseImageBtn.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            mChooseImageBtn.setText(R.string.change_image);
        }
    }

    private void resetInput() {
        mImageText.setText("");
        mQuantity.setText("");
        mUnit.setText("");
        mImageView.setImageBitmap(null);
        mChooseImageBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mChooseImageBtn.setText(R.string.add_image);
    }

}
