package com.mayanksharma.homework.Home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mayanksharma.homework.R;
import com.mayanksharma.homework.models.Photo;
import com.mayanksharma.homework.models.Text;
import com.mayanksharma.homework.models.UserAccountSettings;
import com.mayanksharma.homework.utils.MainfeedListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    //variables
    private ArrayList<Photo> mPhotos;
    private ArrayList<Text> mText;
    private ListView mListView;
    private MainfeedListAdapter mAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(getContext(), "Home", Toast.LENGTH_SHORT).show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mListView = (ListView)view.findViewById(R.id.listView);
        mPhotos = new ArrayList<>();
        mText = new ArrayList<>();

        getIt();

        return view;
    }

    private void getIt()
    {
        Log.d(TAG, "getIt: getting things ");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child(getString(R.string.dbname_photos))
//                .orderByChild(getString(R.string.field_user_id));

        Query query = reference
                .child(getString(R.string.dbname_photos))
                .orderByChild(getString(R.string.field_user_id));

        Log.d(TAG, "getIt: query value is..." + query);


        Query query1 = reference
                .child(getString(R.string.dbname_user_text))
                .orderByChild(getString(R.string.field_user_id));

        Log.d(TAG, "getIt: query value is..." + query);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren())
                {
////                    currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();
                    Log.d(TAG, "onDataChange: okay, found something...:" );

                    Photo photo = new Photo();
                    UserAccountSettings settings = new UserAccountSettings();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    photo.setDescription(objectMap.get(getString(R.string.field_description)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                    mPhotos.add(photo);

                }
                displayPhoto();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren())
                {
                    Log.d(TAG, "onDataChange: okay text retrieving is running...");

                    Text text = new Text();
                    UserAccountSettings settings = new UserAccountSettings();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    text.setDescription(objectMap.get(getString(R.string.field_description)).toString());
                    text.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                    text.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                    mText.add(text);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayPhoto()
    {
        if (mPhotos != null)
        {
            Collections.sort(mPhotos, new Comparator<Photo>() {
                @Override
                public int compare(Photo o1, Photo o2) {
                    return o2.getDate_created().compareTo(o1.getDate_created());
                }
            });
            mAdapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPhotos);
            mListView.setAdapter(mAdapter);
        }
    }

}
