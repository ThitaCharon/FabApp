package com.example.thita.fabapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayImagesActivity extends AppCompatActivity implements RecyclerViewAdapter.OnModify{

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


        mProgress = (ProgressBar)findViewById(R.id.progress_list);
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
                Toast.makeText(DisplayImagesActivity.this, "addValueEventListener success", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DisplayImagesActivity.this, "addValueEventListener error", Toast.LENGTH_LONG).show();
                mProgress.setVisibility(View.INVISIBLE);
            }
        });

        enableSwipeToDeleteAndUndo();

        mFabbtn = (FloatingActionButton) findViewById(R.id.fab);
        mFabbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayImagesActivity.this, DetailActivity.class);
                DisplayImagesActivity.this.startActivity(intent);
            }
        });

    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
//                final String item = mAdapter.getData().get(position);

//                mAdapter.removeItem(position);

                Toast.makeText(DisplayImagesActivity.this, "Swipe success", Toast.LENGTH_LONG).show();


//                Snackbar snackbar = Snackbar
//                        .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
//                snackbar.setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        mAdapter.restoreItem(item, position);
//                        recyclerView.scrollToPosition(position);
//                    }
//                });

//                snackbar.setActionTextColor(Color.YELLOW);
//                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu item) {
        getMenuInflater().inflate(R.menu.menu_main,item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_clear:
                removeAllData();
                return true;
            case R.id.action_sign_out:
                Toast.makeText(DisplayImagesActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeAllData() {
        mDatabaseRef.setValue(null);
        Toast.makeText(DisplayImagesActivity.this, "Clear", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void removeItem(int position) {
        mAdapter.getItemId(position);
    }
}
