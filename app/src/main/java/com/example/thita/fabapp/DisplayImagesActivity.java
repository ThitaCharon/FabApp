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
    private DatabaseReference databaseReference;

    // Creating RecyclerView.
    public RecyclerView mRecyclerView;

    // Creating RecyclerView.Adapter.
    private RecyclerView.Adapter mAdapter;

    // Creating Progress dialog
    private ProgressDialog mProgressDialog;

    // Creating List of ImageUploadInfo class.
    private List<ImageUploadInfo> mList = new ArrayList<>();

    public FloatingActionButton mFabbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        // Assign id to RecyclerView.
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        mRecyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DisplayImagesActivity.this));

        // Assign activity this to progress dialog.
        mProgressDialog = new ProgressDialog(DisplayImagesActivity.this);

        // Setting up message in Progress dialog.
        mProgressDialog.setMessage("Loading..Images From Firebase.");

        // Showing progress dialog.
        mProgressDialog.show();

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference(DetailActivity.Database_Path);

        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);
                    mList.add(imageUploadInfo);
                    Log.e("Image info ", imageUploadInfo.toString());
                }

                mAdapter = new RecyclerViewAdapter(getApplicationContext(), mList);
                mRecyclerView.setAdapter(mAdapter);

                // Hiding the progress dialog.
                mProgressDialog.dismiss();
                Toast.makeText(DisplayImagesActivity.this, "addValueEventListener success", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                mProgressDialog.dismiss();
                Toast.makeText(DisplayImagesActivity.this, "addValueEventListener error", Toast.LENGTH_LONG).show();


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
