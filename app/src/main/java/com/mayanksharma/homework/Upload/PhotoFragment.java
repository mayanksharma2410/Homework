package com.mayanksharma.homework.Upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mayanksharma.homework.R;
import com.mayanksharma.homework.Settings.AccountSettingsActivity;
import com.mayanksharma.homework.utils.FilePaths;
import com.mayanksharma.homework.utils.FileSearch;
import com.mayanksharma.homework.utils.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class PhotoFragment extends Fragment {
    public static final String TAG = "PhotoFragment";

    //widgets
    private GridView mGridView;
    private ImageView image;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;
    private String mSelectedImage;

    //variables
    public ArrayList<String> directories;
    private static final int NUM_OF_COLUMNS = 3;
    private String mAppend = "file:/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        Log.d(TAG, "onCreateView: photo fragment running...");

        directories = new ArrayList<>();
        mGridView = (GridView)view.findViewById(R.id.gridView);
        image = (ImageView)view.findViewById(R.id.galleryImageView);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        directorySpinner = (Spinner)view.findViewById(R.id.spinnerDirectory);
        mProgressBar.setVisibility(View.GONE);


        ImageView close = (ImageView)view.findViewById(R.id.ivCloseUpdate);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the update activity...");
                getActivity().finish();
            }
        });


        TextView next = (TextView) view.findViewById(R.id.tvNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRootTask()) {
                    Log.d(TAG, "onClick: navigating to the post activity...");
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    startActivity(intent);
                }else
                {
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();

                }
            }
        });

        init();
        return view;
    }

    private boolean isRootTask()
    {
        if (((UploadActivity)getActivity()).getTask() == 0)
        {
            return true;
        }else
            return false;
    }

    public void init()
    {
        FilePaths filePaths = new FilePaths();

        //check for other fields inside /*storage/emulated/0/pictures*
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null)
        {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }

        directories.add(filePaths.CAMERA);

        ArrayList<String> directoryNames = new ArrayList<>();
        for(int i = 0; i < directories.size(); i++)
        {
            int index = directories.get(i).lastIndexOf("/");
            String string  = directories.get(i).substring(index).replace("/", " ");
            directoryNames.add(string);
        }


        //setting up the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, directoryNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: selected " + directories.get(position));

                //setup our image grid for the image chosen
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setupGridView(String selectedDirectory)
    {
        Log.d(TAG, "setupGridView: Directory chosen " +selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        //setting the grid column width
        int gridwidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridwidth/NUM_OF_COLUMNS;
        mGridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapt the images to the gridView
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imgURLs);
        mGridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view in inflated
        setImage(imgURLs.get(0), image, mAppend);
        mSelectedImage = imgURLs.get(0);


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image " + imgURLs.get(position));
                setImage(imgURLs.get(position), image, mAppend);
                mSelectedImage = imgURLs.get(position);
            }
        });
    }

    private void setImage(String imgUrl, ImageView image, String append)
    {
        Log.d(TAG, "setImage: setting up the image in the imageView...");
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgUrl, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
