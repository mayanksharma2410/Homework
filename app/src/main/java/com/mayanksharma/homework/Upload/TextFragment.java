package com.mayanksharma.homework.Upload;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TextFragment extends Fragment {
    public static final String TAG = "TextFragment";
    private Context mContext;

    //widgets
    private EditText t_Title, t_decription;
    private Spinner spinnerClass, spinnerSection;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        Log.d(TAG, "onCreateView: text fragment running...");

        mContext = getActivity();
        t_Title = (EditText)view.findViewById(R.id.title);
        t_decription = (EditText)view.findViewById(R.id.description);
        spinnerClass = (Spinner)view.findViewById(R.id.spinnerClass);
        spinnerSection = (Spinner)view.findViewById(R.id.spinnerSection);
        mFirebaseMethods = new FirebaseMethods(mContext);

        setupFirebaseAuth();

        TextView post = (TextView) view.findViewById(R.id.tvPost);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: posting the text homework...");

                //uploading now
                String tTitle = t_Title.getText().toString();
                String tDescription = t_decription.getText().toString();
                String classes = spinnerClass.getSelectedItem().toString();
                String section = spinnerSection.getSelectedItem().toString();
                Toast.makeText(mContext, "Uploading the Homework", Toast.LENGTH_SHORT).show();
                mFirebaseMethods.addTextToFirebase(tTitle, tDescription, classes, section);

            }
        });

        ImageView back = (ImageView)view.findViewById(R.id.ivBackArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to Home...");
                getActivity().finish();
            }
        });

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.classes));
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.sections));
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(adapter2);

        return view;
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


