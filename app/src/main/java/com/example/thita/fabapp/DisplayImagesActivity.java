package com.example.thita.fabapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayImagesActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener{

    // Creating DatabaseReference.
    public static final int RC_SIGN_IN = 1;
    public static final String ANONYMOUS = "anonymous";
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private ValueEventListener mDbListener;

    private String mUsername;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener mChildEventListener;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
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
        mAdapter = new RecyclerViewAdapter(DisplayImagesActivity.this, mListUpload);
        mRecyclerView.setAdapter(mAdapter);

        mStorage = FirebaseStorage.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(DetailActivity.Database_Path);




        mDbListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mListUpload.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ImageUploadInfo upload = postSnapshot.getValue(ImageUploadInfo.class);
                    String name = upload.getImageName();
                    String url = upload.getImageURL();
                    upload.setKey(postSnapshot.getKey());
                    mListUpload.add(upload);
                }
                mAdapter.notifyDataSetChanged();
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

        //AuthStateListen
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is login

                    onSignedInInitialize(user.getDisplayName());
                    Toast.makeText(DisplayImagesActivity.this, "Sign in with " + user.getDisplayName(), Toast.LENGTH_SHORT).show();

                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };

    }//end onCreate()



    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                if (mAdapter.getItemCount() == 1){
                    removeAllData();
                }
                mAdapter.setOnItemClickListener(DisplayImagesActivity.this);
                onDelete(position);


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
                AuthUI.getInstance().signOut(this);
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
    public void onDelete(int position) {
        ImageUploadInfo selectedItem = mListUpload.get(position);
        final String selectedKey = selectedItem.getKey();
        StorageReference itermRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        itermRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(DisplayImagesActivity.this, "Item Deleted ", Toast.LENGTH_LONG).show();
            }
        });
    }



    // TODO Handle Authentication
    // unset the user name clear the message list and detach the listener
    // unset the user name clear the message list and detach the listener
    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
    }

    // sign in and display to login ui flow
    private void onSignedInInitialize(String displayName) {
        mUsername = displayName;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

    }

}
