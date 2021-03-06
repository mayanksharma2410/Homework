package com.mayanksharma.homework.Settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mayanksharma.homework.R;
import com.mayanksharma.homework.Upload.NextActivity;
import com.mayanksharma.homework.Upload.UploadActivity;
import com.mayanksharma.homework.models.UserAccountSettings;
import com.mayanksharma.homework.models.UserSettings;
import com.mayanksharma.homework.utils.FirebaseMethods;
import com.mayanksharma.homework.utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment{

    private static final String TAG = "EditProfileFragment";
    private CircleImageView mProfilePhoto;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //EditProfile Fragment widgets
    private EditText mUsername, mDisplayName, mStatus, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;

    //variables
    private UserSettings mUserSettings;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);

        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mStatus = (EditText)view.findViewById(R.id.status);
        mDescription = (EditText)view.findViewById(R.id.description);
        mEmail = (EditText)view.findViewById(R.id.email);
        mPhoneNumber = (EditText)view.findViewById(R.id.phone);
        mChangeProfilePhoto = (TextView)view.findViewById(R.id.change_profilePhoto);

        mFirebaseMethods = new FirebaseMethods(getActivity());

        setupFirebaseAuth();
        initImageLoader();

        //going back to settings
        ImageView backArrow = (ImageView)view.findViewById(R.id.backarrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: going back to settings activity");
                getActivity().finish();
            }
        });

        ImageView checkMark = (ImageView)view.findViewById(R.id.checkmark);
        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: saving changes now...");
                saveProfileSettings();
            }
        });

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo...");
                Intent intent = new Intent(getActivity(), UploadActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void initImageLoader()
    {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    /**
     * retrieve the data entered by the user and submits it to the database
     * before doing so it checks if the user name is unique
     */
    private void saveProfileSettings()
    {

        final String status = mStatus.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final long phone_number = Long.parseLong(mPhoneNumber.getText().toString());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //case1: when the user changes the email
                if (!mUserSettings.getUser().getEmail().equals(email))
                {
                    //step1) Reauthenticate (confirm email and password)
//                    ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
//                    dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
//                    dialog.setTargetFragment(EditProfileFragment.this, 1);

                    //step2) check if the email is already registered or not ('fetchProvidersForEmail(String email)')

                    //step3) change the email (submit the new email to the database and authentication)
                }

                //change rest of the settings that do not require uniqueness
                if (!mUserSettings.getSettings().getDisplay_name().equals(status))
                {
                    //update status
                    mFirebaseMethods.updateUserAccountSettings(status, null, 0);
                }

                if (!mUserSettings.getSettings().getStatus().equals(description))
                {
                    //update description
                    mFirebaseMethods.updateUserAccountSettings(null, description, 0);

                }

                if(!String.valueOf(mUserSettings.getUser().getPhone_number()).equals(phone_number))
                {
                    //update description
                    mFirebaseMethods.updateUserAccountSettings(null, null,0);

                }


                Toast.makeText(getActivity(), "Changes Saved...", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void setupProfileWidgets(UserSettings userSettings)
    {
        Log.d(TAG, "setupProfileWidgets: setting up widgets from data retrieved from firebase database..." + userSettings.toString());
        Log.d(TAG, "setupProfileWidgets: setting up widgets from data retrieved from firebase database..." + userSettings.getSettings().toString());

        mUserSettings = userSettings;
        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mStatus.setText(settings.getStatus());
        mDescription.setText(settings.getDescription());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));
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
        userID = mAuth.getCurrentUser().getUid();

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

                //retrieve user information from the database
                setupProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

                //retrieve images for the user
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
