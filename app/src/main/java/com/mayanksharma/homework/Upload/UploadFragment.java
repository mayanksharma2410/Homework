package com.mayanksharma.homework.Upload;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mayanksharma.homework.R;
import com.mayanksharma.homework.utils.Permissions;
import com.mayanksharma.homework.utils.SectionsPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment {
    public static final String TAG = "UploadFragment";

    public Context mContext = getActivity();
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private PhotoFragment photoFragment;
    private TextFragment textFragment;
    private ViewPager mViewPager;
    private TabLayout tabLayout;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        Log.d(TAG, "onCreateView: upload fragment started...");

        mViewPager = (ViewPager)view.findViewById(R.id.container);
        tabLayout = (TabLayout)view.findViewById(R.id.tabsBottom);

        if(checkPermissionsArray(Permissions.PERMISSIONS)){
            //setFragment(photoFragment);
            setupViewPager();

            Log.d(TAG, "onCreateView: things working fine till now...");
        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }

        return view;
    }

    private void setupViewPager(){
        SectionsPagerAdapter adapter =  new SectionsPagerAdapter(getFragmentManager());
//        adapter.addFragment(new PhotoFragment());
//        adapter.addFragment(new TextFragment());
//
        mViewPager.setAdapter(adapter);
//
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.photo));
        tabLayout.getTabAt(1).setText(getString(R.string.text));

        if (tabLayout.getTabCount() == 0)
        {
            setFragment(photoFragment);
        }else
            setFragment(textFragment);



    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.relLayout2, fragment);
        fragmentTransaction.commit();
    }

    /**
     * verifiy all the permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                getActivity(),
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

        int permissionRequest = ActivityCompat.checkSelfPermission(getActivity(), permission);

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
