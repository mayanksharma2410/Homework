package com.mayanksharma.homework;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mayanksharma.homework.Home.HomeFragment;
import com.mayanksharma.homework.Login.LoginActivity;
import com.mayanksharma.homework.Profile.ProfileFragment;
import com.mayanksharma.homework.Settings.AccountSettingsActivity;
import com.mayanksharma.homework.Upload.UploadActivity;
import com.mayanksharma.homework.Upload.UploadFragment;
import com.mayanksharma.homework.models.UserAccountSettings;
import com.mayanksharma.homework.models.UserSettings;
import com.mayanksharma.homework.utils.FirebaseMethods;
import com.mayanksharma.homework.utils.Permissions;
import com.mayanksharma.homework.utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";
    private Context mContext = HomeActivity.this;
    public static final int ACTIVITY_NUM = 0;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;


    private HomeFragment homeFragment;
    //private UploadFragment uploadFragment;
    private ProfileFragment profileFragment;
    private BottomNavigationView bottomNavigationView;
    String currentProfile = "";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //variables
    private int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting...");

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.BottomNavViewBar);
        FrameLayout mMainFrame = (FrameLayout)findViewById(R.id.relLayout2);

        //making objects of fragment classes
        homeFragment = new HomeFragment();
        //uploadFragment = new UploadFragment();
        profileFragment = new ProfileFragment();


        setupFirebaseAuth();
        initImageLoader();
        setFragment(homeFragment);
        setupToolbar();



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.ic_house:
                        setFragment(homeFragment);
                        return true;

                    case R.id.ic_upload:
                        //setFragment(uploadFragment);
                        Intent intent = new Intent(HomeActivity.this, UploadActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.ic_profile:
                        setFragment(profileFragment);
                        return true;
                }

                return false;
            }

        });

    }

    /**
     * check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionArray(String[] permissions)
    {
        Log.d(TAG, "checkPermissionArray: checking permission array...");

        for (int i = 0; i <= permissions.length; i++)
        {
            String check = permissions[i];
            if (!checkPermission(check))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * checking a single permission if it as been verified or not
     * @param permission
     * @return
     */
    public boolean checkPermission(String permission)
    {
        Log.d(TAG, "checkPermission: checking permission of " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(mContext, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "checkPermission: permission was not granted for " + permission);
            return false;
        }
        else
        {
            Log.d(TAG, "checkPermission: permission was granted for " + permission);
            return true;
        }
    }

    /**
     * verifying all permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions)
    {
        Log.d(TAG, "verifyPermissions: verifying permissions...");

        ActivityCompat.requestPermissions(HomeActivity.this, permissions, VERIFY_PERMISSIONS_REQUEST);
    }

    private void initImageLoader()
    {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setupToolbar()
    {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        ImageView profileMenu = (ImageView)findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });

    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.relLayout2, fragment);
        fragmentTransaction.commit();
    }


    /**
     * ****************************************** Firebase ***********************************************************
     */

    private void checkCurrentUser(FirebaseUser user)
    {
        if(user == null)
        {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase authentication");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(HomeActivity.this);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                checkCurrentUser(user);
                if(user != null)
                {
                    Log.d(TAG, "onAuthStateChanged: signed in");
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieving user profile information
                if(!checkProfile(mFirebaseMethods.getUserSettings(dataSnapshot)))
                {
                    bottomNavigationView.getMenu().removeItem(R.id.ic_upload);
                    Log.d(TAG, "onNavigationItemSelected: eliminating upload fragment...");

                }else{
                    Log.d(TAG, "onDataChange: parent/ student gonna be here");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean checkProfile(UserSettings userSettings)
    {
        Log.d(TAG, "checkProfile: checking the user profile...");
        UserAccountSettings settings = userSettings.getSettings();
        Log.d(TAG, "onDataChange: Current Profile is... " + currentProfile);
        Toast.makeText(mContext, "current profile is " + currentProfile, Toast.LENGTH_SHORT).show();

        if(settings.getProfile() !=  null && !settings.getProfile().isEmpty())
        {
            currentProfile = settings.getProfile();
        }

        if (currentProfile.equals(getString(R.string.Teacher)))
        {
            return true;
        }
        return false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        FirebaseUser user = mAuth.getCurrentUser();
        checkCurrentUser(user);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null)
        {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}