package com.mayanksharma.homework.Upload;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mayanksharma.homework.R;
import com.mayanksharma.homework.utils.FirebaseMethods;
import com.mayanksharma.homework.utils.UniversalImageLoader;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";

    //widgets
    private EditText mDescription;
    private Spinner spinnerClass, spinnerSection;
    private ImageView mImage;

    //variables
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgURL;
    private Context mContext;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        Log.d(TAG, "onCreate: starting the post activity...");
        Log.d(TAG, "onCreate: got the chosen image " + getIntent().getStringExtra(getString(R.string.selected_image)));

        mDescription = (EditText)findViewById(R.id.description);
        spinnerClass = (Spinner)findViewById(R.id.spinnerClass);
        spinnerSection = (Spinner)findViewById(R.id.spinnerSection);
        mImage = (ImageView)findViewById(R.id.imageUpdate);
        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
//        mDescription.setBackgroundResource(R.drawable.border);
        mContext = NextActivity.this;

        setupFirebaseAuth();

        ImageView back = (ImageView)findViewById(R.id.ivBackArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to gallery...");
                finish();
            }
        });

        TextView post = (TextView)findViewById(R.id.tvPost);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: uploading...");

                //uploading now...
                String description = mDescription.getText().toString();
                String classes = spinnerClass.getSelectedItem().toString();
                String section = spinnerSection.getSelectedItem().toString();
                Toast.makeText(mContext, "Uploading the photo...", Toast.LENGTH_SHORT).show();
                mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), description, classes, section, imageCount, imgURL);
            }
        });

        setImage();

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.classes));
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.sections));
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(adapter2);
    }

    public void someMethod()
    {
        /*
        step 1)
        Create a data model for photos

        step 2)
        Add properties to the photo object(data, imgURL, photo_id, user_id, class, section)

        step 3)
        Count the no. of photos users already has

        step 4)
        a) upload the new photo to the firebase database and insert the new nodes to firebase database
        b) insert the "photos" nodes
        c) insert the "user_photos" nodes

         */
    }


    /**
     * gets the image url from the incoming intent and displays the
     */
    private void setImage()
    {
        Intent intent = getIntent();
        ImageView image = (ImageView)findViewById(R.id.imageUpdate);
        imgURL = intent.getStringExtra(getString(R.string.selected_image));
        UniversalImageLoader.setImage(imgURL, image, null, mAppend);
    }

    /*
     ******************************************* Firebase ***************************************************************
     */

    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: setting up authentication");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count " + imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged: signed in " + user.getUid());
                }else
                {
                    //User us not signeed in
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}