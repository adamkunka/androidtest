package com.example.test1;


import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[] {
                                getString(R.string.title_section1),
                                getString(R.string.title_section2),
                                getString(R.string.title_section3),
                        }),
                this);

   
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        Fragment fragment = new DummySectionFragment();
        Bundle args = new Bundle();
        args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        return true;
    }

 	
   /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
        private Camera mCamera;
        private int mCameraId;
        private CameraPreview mCameraPreview;

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }

		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			super.onPause();

			releaseCamera();
		}

		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
				
			mCamera = getCameraInstance();//Camera.open();
			if (mCamera==null)
			{
		    	Toast.makeText(getActivity().getBaseContext(), "Cannot access camera!", Toast.LENGTH_LONG).show();
		    	return;
			}
			mCameraPreview = new CameraPreview(getActivity(), mCamera);
			setCameraDisplayOrientation(getActivity());
			FrameLayout preview = (FrameLayout) getActivity().findViewById(R.id.camera_preview);
	        preview.addView(mCameraPreview);
	        }

		/** A safe way to get an instance of the Camera object. */
		public Camera getCameraInstance(){
		    Camera c = null;
		    try {
		    	mCameraId = Camera.getNumberOfCameras()-1;
		        c = Camera.open(mCameraId); // attempt to get a Camera instance
		    }
		    catch (Exception e){
		        // Camera is not available (in use or does not exist)
		    }
		    return c; // returns null if camera is unavailable
		}
		
		private void releaseCamera(){
	        if (mCamera != null){
	            mCamera.release();        // release the camera for other applications
	            mCamera = null;
	        }
		}
		
	     public void setCameraDisplayOrientation(Activity activity) {
		     android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		     android.hardware.Camera.getCameraInfo(mCameraId, info);
		     
		     int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		     int degrees = 0;
		     
		     switch (rotation) {
		         case Surface.ROTATION_0: degrees = 0; break;
		         case Surface.ROTATION_90: degrees = 90; break;
		         case Surface.ROTATION_180: degrees = 180; break;
		         case Surface.ROTATION_270: degrees = 270; break;
		     }

		     int result;
		     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
		         result = (info.orientation + degrees) % 360;
		         result = (360 - result) % 360;  // compensate the mirror
		     } else {  // back-facing
		         result = (info.orientation - degrees + 360) % 360;
		     }
		     mCamera.setDisplayOrientation(result);
		 }		
	    
		

    }

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}

}
