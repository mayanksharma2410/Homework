package com.mayanksharma.homework.Profile;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mayanksharma.homework.Login.LoginActivity;
import com.mayanksharma.homework.R;
import com.mayanksharma.homework.models.User;
import com.mayanksharma.homework.models.UserAccountSettings;
import com.mayanksharma.homework.models.UserSettings;
import com.mayanksharma.homework.utils.FirebaseMethods;
import com.mayanksharma.homework.utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private final String TAG = "ProfileFragment";
    private ProgressBar mProgressBar;
    private TextView mDisplayName, mUsername, mStatus, mDescription, mProfile;
    private CircleImageView mProfilePhoto;
    private Context mContext;
    private ImageView icStatus, icDescription;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(getContext(), "Profile", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreateView: starting profile fragment");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProfilePhoto = (CircleImageView)view.findViewById(R.id.profile_image);

        mDisplayName = (TextView)view.findViewById(R.id.display_name);
        mProfile = (TextView)view.findViewById(R.id.tvProfile);
        mUsername = (TextView)view.findViewById(R.id.tvUsername);
        mStatus = (TextView)view.findViewById(R.id.tvStatus);
        mDescription = (TextView)view.findViewById(R.id.tvDescription);
        mProgressBar = (ProgressBar)view.findViewById(R.id.profileProgressBar);
        icStatus = (ImageView)view.findViewById(R.id.ic_status);
        icDescription= (ImageView)view.findViewById(R.id.ic_description);

        icStatus.setVisibility(View.INVISIBLE);
        icDescription.setVisibility(View.INVISIBLE);

        mFirebaseMethods = new FirebaseMethods(getActivity());

        setupFirebaseAuth();
        initImageLoader();
        //setProfileImage();
        return view;
    }

    private void setupProfileWidgets(UserSettings userSettings)
    {
        Log.d(TAG, "setupProfileWidgets: setting up widgets from firebase database..." + userSettings.toString());
        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mStatus.setText(settings.getStatus());
        mDescription.setText(settings.getDescription());
        mProfile.setText(settings.getProfile());
        mProgressBar.setVisibility(View.GONE);
        icStatus.setVisibility(View.VISIBLE);
        icDescription.setVisibility(View.VISIBLE);

    }

    private void setProfileImage()
    {
        Log.d(TAG, "setProfileImage: setting up profile image");
        String imgURL = "images.idgesg.net/images/article/2017/08/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg";
        UniversalImageLoader.setImage(imgURL, mProfilePhoto, mProgressBar, "https://");
    }


    private void initImageLoader()
    {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /**     ******************************************* Firebase ***********************************************************

     */


    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase authentication");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if(user != null)
                {
                    Log.d(TAG, "onAuthStateChanged: signed in " + user.getUid());
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve user information from the firebase
                setupProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
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
        if(mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
