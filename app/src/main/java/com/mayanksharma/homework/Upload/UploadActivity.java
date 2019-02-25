package com.mayanksharma.homework.Upload;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mayanksharma.homework.R;
import com.mayanksharma.homework.utils.Permissions;
import com.mayanksharma.homework.utils.SectionsPagerAdapter;

public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "UploadActivity";
    public Context mContext = UploadActivity.this;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private PhotoFragment photoFragment;
    private TextFragment textFragment;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_upload);
        Log.d(TAG, "onCreate: upload activity starting...");

        mViewPager = (ViewPager)findViewById(R.id.container);
        tabLayout = (TabLayout)findViewById(R.id.tabsBottom);

        if(checkPermissionsArray(Permissions.PERMISSIONS)){
            //setFragment(photoFragment);
            setupViewPager();

            Log.d(TAG, "onCreateView: things working fine till now...");
        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }

    public int getTask()
    {
        Log.d(TAG, "getTask: TASK " + getIntent().getFlags());
        return getIntent().getFlags();
    }
    private void setupViewPager(){
        SectionsPagerAdapter adapter =  new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PhotoFragment());
        adapter.addFragment(new TextFragment());

        mViewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.photo));
        tabLayout.getTabAt(1).setText(getString(R.string.text));


    }


    /**
     * verifiy all the permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                UploadActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }


    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(mContext, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }
}
