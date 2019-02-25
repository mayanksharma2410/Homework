package com.mayanksharma.homework.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mayanksharma.homework.HomeActivity;
import com.mayanksharma.homework.R;
import com.mayanksharma.homework.Settings.AccountSettingsActivity;
import com.mayanksharma.homework.models.Photo;
import com.mayanksharma.homework.models.Text;
import com.mayanksharma.homework.models.User;
import com.mayanksharma.homework.models.UserAccountSettings;
import com.mayanksharma.homework.models.UserSettings;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;

    //variables
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }


    public void uploadNewPhoto(String photoType, final String description, final String classes, final String section, int count, String imgURL)
    {
        Log.d(TAG, "uploadNewPhoto: attempting to upload a new photo...");
        FilePaths filePaths = new FilePaths();

        //case 1) NEW PHOTO
        if (photoType.equals(mContext.getString(R.string.new_photo)))
        {
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            //convert image url to bitmap
            Bitmap bm = ImageManager.getBitmap(imgURL);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "photo upload successfully...", Toast.LENGTH_SHORT).show();

                    //add the new photo the "photos" noe and "user_photos" node
                    addPhotoToFirebase(description, classes, section, firebaseUrl.toString());
                    Log.d(TAG, "onSuccess: uploaded successfully...");

                    //navigate to the main feed so that the user can see what he uploaded
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed...");
                    Toast.makeText(mContext, "photo upload failed", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress)
                    {
                        Toast.makeText(mContext, "photo upload progress " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress " + progress + "% done");
                }
            });


        } //case 2) NEW PROFILE PHOTO
        else if (photoType.equals(mContext.getString(R.string.profile_photo)))
        {
            Log.d(TAG, "uploadNewPhoto: uploading a NEW PROFILE PHOTO...");

            ((AccountSettingsActivity)mContext).setViewPager(
                    ((AccountSettingsActivity)mContext).pagerAdapter
                            .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment)));

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap
            Bitmap bm = ImageManager.getBitmap(imgURL);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "photo upload successfully...", Toast.LENGTH_SHORT).show();

                    //insert into "user_account_settings" node
                    setProfilePhoto(firebaseUrl.toString());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed...");
                    Toast.makeText(mContext, "photo upload failed", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress)
                    {
                        Toast.makeText(mContext, "photo upload progress " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress " + progress + "% done");
                }
            });
        }
    }

    private void setProfilePhoto(String url)
    {
        Log.d(TAG, "setProfilePhoto: setting new profile photo..." + url);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo)).setValue(url);
    }


    public String getTimestamp()
    {
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        return timeStamp;
    }

    /**
     * adding text homework to firebase
     * @param title
     * @param description
     * @param classes
     * @param section
     */
    public void addTextToFirebase(String title, String description, String classes, String section)
    {
        Log.d(TAG, "uploadNewText: attempting to upload homework...");

        String newTextKey = myRef.child(mContext.getString(R.string.dbname_text)).push().getKey();
        Text text = new Text();
        text.setTitle(title);
        text.setDescription(description);
        text.setClasses(classes);
        text.setSection(section);
        text.setDate_created(getTimestamp());
        text.setUser_id(userID);

        //inserting into database now
        myRef.child(mContext.getString(R.string.dbname_user_text)).child(newTextKey).setValue(text);

        Log.d(TAG, "addTextToFirebase: text homework uploaded...");
        Toast.makeText(mContext, "Homework uploaded", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(mContext, HomeActivity.class);
        mContext.startActivity(intent);


    }

    /**
     * adding photo and its details to firebase database
     * @param description
     * @param classes
     * @param sections
     * @param url
     */
    private void addPhotoToFirebase(String description, String classes, String sections, String url)
    {
        Log.d(TAG, "addPhotoToFirebase: uploading photo to firebase...");

        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setDescription(description);
        photo.setClasses(classes);
        photo.setSection(sections);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setUser_id(userID);
        photo.setPhoto_id(newPhotoKey);

        //inserting into database now
        myRef.child(mContext.getString(R.string.dbname_user_photos)).child(userID).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);

    }

    public int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds: dataSnapshot.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren())
        {
            count++;
        }
        return count;
    }


    public boolean checkIfUsernameExists(String username, DataSnapshot datasnapshot){
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");

        User user = new User();

        for (DataSnapshot ds: datasnapshot.child(userID).getChildren()){
            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);

            user.setUsername(ds.getValue(User.class).getUsername());
            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());

            if(StringManipulations.expandUsername(user.getUsername()).equals(username)){
                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
                return true;
            }
        }
        return false;
    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     * @param username
     * @param profile
     * @param classes
     * @param section
     */
    public void registerNewEmail(final String email, String password, final String username, final String profile,
                                 final String classes, final String section){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                        }
                        else if(task.isSuccessful()){


                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }

                    }
                });
    }


    public void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {

                    }else
                    {
                        Toast.makeText(mContext, "couldn't sent the verification email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * add information to user nodes
     * add information to user_account_settings node
     * @param email
     * @param username
     * @param profile
     * @param description
     * @param status
     * @param profile_photo
     */
    public void addNewUser(String email, String username, String profile, String description, String status, String profile_photo,
                           String classes, String section){

        User user = new User( StringManipulations.condenseUsername(username),  1,  userID, email, profile, classes, section);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);


        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                profile_photo,
                status,
                StringManipulations.condenseUsername(username),
                profile,
                userID,
                classes,
                section
        );

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);

    }

    /**
     * update user_account_settings node for the current user
     * @param status
     * @param description
     * @param phoneNumber
     */
    public void updateUserAccountSettings(String status, String description, long phoneNumber)
    {
        Log.d(TAG, "updateUserAccountSettings: updating user account settings...");

        if (status != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_status))
                    .setValue(status);
        }

        if (description != null)
        {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }

        if (phoneNumber != 0)
        {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);
        }
    }

    /**
     * retrieve user information from currently logged in user
     * Database: user_account_settings node
     * @param dataSnapshot
     * @return
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot)
    {
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from firebase...");

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds: dataSnapshot.getChildren())
        {

            //user_account_settings node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings)))
            {
                Log.d(TAG, "getUserAccountSettings: dataSnapshot " + ds); //try ds.child(userID)

                try
                {
                    settings.setDisplay_name(ds.child(userID).getValue(UserAccountSettings.class).getDisplay_name());
                    settings.setUsername(ds.child(userID).getValue(UserAccountSettings.class).getUsername());
                    settings.setStatus(ds.child(userID).getValue(UserAccountSettings.class).getStatus());
                    settings.setDescription(ds.child(userID).getValue(UserAccountSettings.class).getDescription());
                    settings.setProfile(ds.child(userID).getValue(UserAccountSettings.class).getProfile());
                    settings.setProfile_photo(ds.child(userID).getValue(UserAccountSettings.class).getProfile_photo());


                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information " + settings.toString());

                }catch (NullPointerException e)
                {
                    Log.d(TAG, "getUserAccountSettings: Null Pointer exception " + e.getMessage());
                }

            }

            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: dataSnapshot " + ds); //try ds.child(userID)

                try
                {
                    user.setUsername(ds.child(userID).getValue(User.class).getUsername());
                    user.setEmail(ds.child(userID).getValue(User.class).getEmail());
                    user.setPhone_number(ds.child(userID).getValue(User.class).getPhone_number());
                    user.setUser_id(ds.child(userID).getValue(User.class).getUser_id());
                    user.setProfile(ds.child(userID).getValue(User.class).getProfile());

                    Log.d(TAG, "getUserAccountSettings: retrieved user information " + user.toString());

                }catch (NullPointerException e)
                {
                    Log.d(TAG, "getUserAccountSettings: Null Pointer Exception " + e.getMessage());
                }
            }
        }

        return new UserSettings(user, settings);
    }

}
