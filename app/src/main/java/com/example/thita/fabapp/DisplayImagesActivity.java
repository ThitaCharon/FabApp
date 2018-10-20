package com.example.thita.fabapp;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
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
    private static final String JOB_TAG = "MyJobService";
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private ValueEventListener mDbListener;
    private FirebaseJobDispatcher mDispatcher;
    private FirebaseAnalytics mAnalytics;

    private String mUsername;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private ProgressBar mProgress;

    public static final String PREF_COUNT = "PREF ITEM COUNT" ;
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

        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        mProgress = (ProgressBar)findViewById(R.id.progress_list);
        mListUpload = new ArrayList<>();
        mAdapter = new RecyclerViewAdapter(DisplayImagesActivity.this, mListUpload);
        mRecyclerView.setAdapter(mAdapter);

        mStorage = FirebaseStorage.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(DetailActivity.Database_Path);

        mAnalytics = FirebaseAnalytics.getInstance(this);
//        mDbListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                mListUpload.clear();
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    ImageUploadInfo upload = postSnapshot.getValue(ImageUploadInfo.class);
//                    String name = upload.getImageName();
//                    upload.setKey(postSnapshot.getKey());
//                    mListUpload.add(upload);
//                }
//                mAdapter.notifyDataSetChanged();
//                mProgress.setVisibility(View.INVISIBLE);
//                Toast.makeText(DisplayImagesActivity.this, "addValueEventListener success", Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(DisplayImagesActivity.this, "addValueEventListener error", Toast.LENGTH_LONG).show();
//                mProgress.setVisibility(View.INVISIBLE);
//            }
//        });
//        enableSwipeToDeleteAndUndo();

        mFabbtn = (FloatingActionButton) findViewById(R.id.fab);

        mFabbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnalytics.setUserProperty(String.valueOf(R.string.ADD_ORDER), mAdapter.getItemCount() + "");
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
                    mAnalytics.setUserProperty(String.valueOf(R.string.USERNAME), user.getDisplayName());
                    performDisplay();
                    Toast.makeText(DisplayImagesActivity.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();

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

    private void performDisplay() {
        mDbListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mListUpload.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ImageUploadInfo upload = postSnapshot.getValue(ImageUploadInfo.class);
                    String name = upload.getImageName();
                    upload.setKey(postSnapshot.getKey());
                    mListUpload.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                SharedPreferences.Editor editor = getSharedPreferences(PREF_COUNT, MODE_PRIVATE).edit();
                editor.putInt(String.valueOf(R.string.TotalItem), mAdapter.getItemCount());
                editor.apply();
                mProgress.setVisibility(View.INVISIBLE);
//                Toast.makeText(DisplayImagesActivity.this, R.string.TOAST_LISTENER_ONSUCCESS, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DisplayImagesActivity.this, R.string.TOAST_LISTENER_ERROR, Toast.LENGTH_LONG).show();
                mProgress.setVisibility(View.INVISIBLE);
            }
        });
        enableSwipeToDeleteAndUndo();

    }


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
                Toast.makeText(DisplayImagesActivity.this, R.string.SIGN_OUT_ACCOUNT, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeAllData() {
        mDatabaseRef.setValue(null);
        Toast.makeText(DisplayImagesActivity.this, R.string.CLEAR_LIST, Toast.LENGTH_SHORT).show();
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

                SharedPreferences.Editor editor = getSharedPreferences(PREF_COUNT, MODE_PRIVATE).edit();
                editor.putInt(String.valueOf(R.string.TotalItem), mAdapter.getItemCount());
                editor.apply();

                Toast.makeText(DisplayImagesActivity.this, R.string.DELETE_ITEM, Toast.LENGTH_LONG).show();
            }
        });
    }



    // TODO Handle Authentication
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

    private void scheduleJob() {
        Job myJob = mDispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag(JOB_TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(5, 30))
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        mDispatcher.mustSchedule(myJob);
        Toast.makeText(this, R.string.job_scheduled, Toast.LENGTH_LONG).show();
    }

    private void cancelJob(String jobTag) {
        if ("".equals(jobTag)) {
            mDispatcher.cancelAll();
        } else {
            mDispatcher.cancel(jobTag);
        }
        Toast.makeText(this, R.string.job_cancelled, Toast.LENGTH_LONG).show();
    }
}
