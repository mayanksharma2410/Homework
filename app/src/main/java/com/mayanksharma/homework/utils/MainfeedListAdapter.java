package com.mayanksharma.homework.utils;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mayanksharma.homework.R;
import com.mayanksharma.homework.models.Photo;
import com.mayanksharma.homework.models.User;
import com.mayanksharma.homework.models.UserAccountSettings;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainfeedListAdapter extends ArrayAdapter<Photo> {
    private static final String TAG = "MainfeedListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currentUsername = "";
    private UserAccountSettings mUserAccountSettings;

    public MainfeedListAdapter(@NonNull Context context, int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;
        mReference = FirebaseDatabase.getInstance().getReference();
    }

    static class ViewHolder
    {
        CircleImageView mProfileImage;
        TextView username, timeDelta, description;
        SquareImageView image;
        Photo photo;
        User user = new User();

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null)
        {
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView)convertView.findViewById(R.id.username);
            holder.image = (SquareImageView)convertView.findViewById(R.id.postImage);
            holder.description = (TextView)convertView.findViewById(R.id.image_description);
            holder.timeDelta = (TextView)convertView.findViewById(R.id.image_time_posted);
            holder.mProfileImage = (CircleImageView)convertView.findViewById(R.id.profile_photo);
            holder.photo = getItem(position);

            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        //get the current users username
        getCurrentUsername();

        //set the image
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getImage_path(), holder.image);

        holder.description.setText(getItem(position).getDescription());
        holder.timeDelta.setText(getItem(position).getDate_created());

//        UniversalImageLoader.setImage(mUserAccountSettings.getProfile_photo(), holder.mProfileImage, null, "");
//        holder.username.setText(mUserAccountSettings.getUsername());

        //get the profile image and the username
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    
//                    currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();
                    Log.d(TAG, "onDataChange: found user: "
                            + singleSnapshot.getValue(UserAccountSettings.class).getUsername());

                    holder.username.setText(singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                    imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
                            holder.mProfileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return convertView;
    }

    private void getPhotoDetails()
    {
        Log.d(TAG, "getPhotoDetails: retrieving photo details...");

    }

    private void getCurrentUsername(){
        Log.d(TAG, "getCurrentUsername: retrieving user account settings");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private String getTimeStampDifference(Photo photo)
//    {
//        Log.d(TAG, "getTimeStampDifference: getting time stamp difference ");
//
//        String difference = "";
//        Calendar c = Calendar.getInstance();
//        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
//        Date today = c.getTime();
//        String.format(timeStamp, today);
//        Date timeStamp1;
//
//        final String photo_timeStamp = photo.getDate_created();
//        try
//        {
//            timeStamp1 = timeStamp
//        }catch (ParseException e)
//        {
//            Log.e(TAG, "getTimeStampDifference: ParseException " + e.getMessage());
//            difference = "0";
//        }
//
//        return difference;
//    }
}
