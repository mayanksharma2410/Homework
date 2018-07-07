package com.mayanksharma.homework.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mayanksharma.homework.HomeActivity;
import com.mayanksharma.homework.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait;
    private Context mContext;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = LoginActivity.this;
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        mEmail = (EditText)findViewById(R.id.input_email);
        mPassword = (EditText)findViewById(R.id.input_password);
        mPleaseWait = (TextView)findViewById(R.id.textPleaseWait);

        Log.d(TAG, "onCreate: login activity starting...");
        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);


        setupFirebaseAuth();
        init();
    }

    private boolean isStringNull(String string)
    {
        Log.d(TAG, "isStringNull: checking if string is null...");
        if (string.equals(""))
        {
            return true;
        }
        else
            return false;
    }

    /**
     * ****************************************** Firebase ***********************************************************
     */

    private void init()
    {
        //initializing button for logging in
        Button mButton = (Button)findViewById(R.id.btn_login);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: logging in...");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (isStringNull(email) && isStringNull(password))
                {
                    Toast.makeText(mContext, "Please fill in all the details", Toast.LENGTH_SHORT).show();
                }else{

                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "onComplete: signing in with email " + task.isSuccessful());

                            if(!task.isSuccessful())
                            {
                                //sign in failed
                                Log.d(TAG, "signInWithEmail:failed login", task.getException());
                                Toast.makeText(LoginActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                                mPleaseWait.setVisibility(View.GONE);
                            }
                            else
                            {
                                try {
                                    //sign in success
                                    Log.d(TAG, "signInWithEmail:successful login", task.getException());
                                    Toast.makeText(LoginActivity.this, R.string.auth_success, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(mContext, HomeActivity.class);
                                    startActivity(intent);
                                    mProgressBar.setVisibility(View.GONE);
                                    mPleaseWait.setVisibility(View.GONE);
                                }catch (NullPointerException e)
                                {
                                    Log.d(TAG, "onComplete: NullPointerException: " + e.getMessage());
                                }
                            }
                        }
                    });
                }
            }
        });


        TextView linkSignUp = (TextView)findViewById(R.id.link_signup);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to register activity...");
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
            }
        });


        /**
         * navigating user to the home Activity
         */
        if(mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase authentication");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    Log.d(TAG, "onAuthStateChanged: signed in");
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
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
