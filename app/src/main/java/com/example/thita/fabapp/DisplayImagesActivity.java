package com.example.thita.fabapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayImagesActivity extends AppCompatActivity {

    // Creating DatabaseReference.
    private DatabaseReference mDatabaseRef;

    public RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressBar mProgress;

    // Creating List of ImageUploadInfo class.
    private List<ImageUploadInfo> mListUpload ;

    public FloatingActionButton mFabbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // Assign id to RecyclerView.
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DisplayImagesActivity.this));

        mProgress = (ProgressBar)findViewById(R.id.progerss_list);
        mListUpload = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(DetailActivity.Database_Path);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mListUpload.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ImageUploadInfo upload = postSnapshot.getValue(ImageUploadInfo.class);
                    String name = upload.getImageName();
                    String url = upload.getImageURL();
                    mListUpload.add(upload);
                }

                mAdapter = new RecyclerViewAdapter(DisplayImagesActivity.this, mListUpload);
                mRecyclerView.setAdapter(mAdapter);

                mProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(DisplayImagesActivity.this, "addValueEventListener success", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DisplayImagesActivity.this, "addValueEventListener error", Toast.LENGTH_LONG).show();
                mProgress.setVisibility(View.INVISIBLE);
            }
        });

        mFabbtn = (FloatingActionButton) findViewById(R.id.fab);
        mFabbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayImagesActivity.this, DetailActivity.class);
                DisplayImagesActivity.this.startActivity(intent);
            }
        });

    }


}
