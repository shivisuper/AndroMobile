package com.shivisuper.alachat_mobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shivisuper.alachat_mobile.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.shivisuper.alachat_mobile.Constants.myself;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnClickListener {

    private static final String TAG = "CameraActivity";
    private FirebaseAuth mAuth;
    private String loginInfo;
    final String mDbChild = "userDetails";
    private DatabaseReference usersRef;
    private String userName;
    static final int MEDIA_TYPE_IMAGE = 1;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 550;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private Bitmap photo;
    private static int currentCamID;
    int mPictureWidth = 1920;//1280;
    int mPictureHeight = 1080;//720;
    public static final String ARG_FROM_CAMERA = "username";

    @Bind(R.id.btnCapture) Button btnCapture;
    @Bind(R.id.btnCameraSwitch) Button btnCameraSwitch;
    @Bind(R.id.btn_flash_on) Button btnFlashOn;
    @Bind(R.id.btn_flash_off) Button btnFlashOff;
    @Bind(R.id.view_flash_off) View viewFlashOff;
    @Bind(R.id.view_flash_on) View viewFlashOn;
    @Bind(R.id.frame_flash_unavailable) View viewFlashUnavailable;
    @Bind(R.id.view_image) View photoPreview;
    @Bind(R.id.camera_preview) View cameraPreview;
    @Bind(R.id.cameraSurface) SurfaceView surfaceView;
    @Bind(R.id.img_view) ImageView imgView;
    @Bind(R.id.btnSave) Button btnSave;
    @Bind(R.id.btnCancel) Button btnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        loginInfo = mAuth.getCurrentUser().getEmail();
        usersRef = FirebaseDatabase.getInstance().getReference(mDbChild);
        getCurrentUsername();
        initializeListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_favorite).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                mAuth.signOut();
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getCurrentUsername() {
        usersRef.orderByChild("email").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getEmail() != null && Objects.equals(user.getEmail(), loginInfo)) {
                    userName = dataSnapshot.getKey();
                    myself = userName;
                    Toast.makeText(CameraActivity.this, "Welcome back " + userName + "!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initializeListeners() {
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
        btnCapture.setOnClickListener(this);
        currentCamID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        btnCameraSwitch.setOnClickListener(this);
        btnFlashOff.setOnClickListener(this);
        btnFlashOn.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        surfaceView.setOnTouchListener(gestureListener);
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    Log.d(TAG, "swipe was too long: " + Float.toString((e1.getY() - e2.getY())));
                    return false;
                }
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) >
                        SWIPE_THRESHOLD_VELOCITY) {
                    Toast.makeText(CameraActivity.this, "Left Swipe", Toast.LENGTH_SHORT).show();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) >
                        SWIPE_THRESHOLD_VELOCITY) {
                    //Toast.makeText(CameraActivity.this, "Right Swipe", Toast.LENGTH_SHORT).show();
                    Intent friendList = new Intent(getApplicationContext(), FriendList_Activity.class);
                    startActivity(friendList);
                } else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) >
                        SWIPE_THRESHOLD_VELOCITY) {
                    Toast.makeText(CameraActivity.this, "Up Swipe", Toast.LENGTH_SHORT).show();
                } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) >
                        SWIPE_THRESHOLD_VELOCITY) {
                    Intent qrintent = new Intent(getApplicationContext(), AlacodeActivity.class);
                    String qrcode_data = "{" +
                            "\"uid\":" + "\"" + mAuth.getCurrentUser().getUid() + "\"" +
                            ", \"username\":" + "\"" + userName + "\"" +
                            "}";
                    qrintent.putExtra(ARG_FROM_CAMERA, qrcode_data);
                    startActivity(qrintent);
                    //Toast.makeText(CameraActivity.this, "Down Swipe", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception SimpleOnGesture: " + e.getMessage());
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "AlaChat");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    private void scanMedia(String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        sendBroadcast(scanFileIntent);
    }

    public void toggleFlash(boolean on) {
        if(parameters != null) {
            Log.d("mCamera params: ", parameters.getFlashMode());
        }

        if(on) {
            viewFlashOn.setVisibility((View.VISIBLE));
            viewFlashOff.setVisibility(View.INVISIBLE);
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        }
        else {
            viewFlashOn.setVisibility(View.INVISIBLE);
            viewFlashOff.setVisibility(View.VISIBLE);
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        mCamera.setParameters(parameters);
    }

    public void takePhoto() {
        mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            } //onShutter is important for the shutter sound effect
        }, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                BitmapFactory.Options scalingOptions = new BitmapFactory.Options();
                /*scalingOptions.inSampleSize = camera.getParameters().getPictureSize().width /
                                imgView.getMeasuredWidth();*/
                photo = BitmapFactory.decodeByteArray(data, 0,
                        data.length, scalingOptions);
                if(currentCamID == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    photo = rotateImage(photo, 90);
                }
                else {
                    photo = rotateImage(photo, 270);
                }
                camera.stopPreview();
                cameraPreview.setVisibility(View.GONE);
                photoPreview.setVisibility(View.VISIBLE);
                imgView.setImageBitmap(photo);
            }
        });
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotatedImg = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
        source.recycle();
        return rotatedImg;
    }

    //in case of scanning multiple files together
    /*private ArrayList<String> mFilesToScan;

    *//**
     * Adds to the list of paths to scan when a media scan is started.
     *
     * @see {@link #scanMediaFiles()}
     * @param path
     *//*
    private void addToScanList(String path) {
        if (mFilesToScan == null)
            mFilesToScan = new ArrayList<String>();
        mFilesToScan.add(path);
    }

    *//**
     * Initiates a media scan of each of the files added to the scan list.
     *
     * @see {@see #addToScanList(String)}
     *//*
    private void scanMediaFiles() {
        if ((mFilesToScan != null) && (!mFilesToScan.isEmpty())) {
            for (String path : mFilesToScan) {
                scanMedia(path);
            }
            mFilesToScan.clear();
        } else {
            Log.e(TAG, "Media scan requested when nothing to scan");
        }
    }*/

    public void hideFlash(boolean hide) {
        if(hide) {
            viewFlashUnavailable.setVisibility(View.VISIBLE);
            viewFlashOn.setVisibility(View.GONE);
            viewFlashOff.setVisibility(View.GONE);
        }
        else {
            viewFlashOff.setVisibility(View.VISIBLE);
            viewFlashOn.setVisibility(View.INVISIBLE);
            viewFlashUnavailable.setVisibility(View.GONE);
        }
    }

    public void saveImage() {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null){
            Log.d(TAG, "Error creating media file, check storage permissions");
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pictureFile);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(CameraActivity.this, "Imaged saved successfully!",
                    Toast.LENGTH_SHORT).show();
            scanMedia(pictureFile.getPath());
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            cancelPhotoView();
        }
    }

    public void cancelPhotoView() {
        photoPreview.setVisibility(View.GONE);
        cameraPreview.setVisibility(View.VISIBLE);
        mCamera.startPreview();
    }

    public void switchCamera() {
        mCamera.stopPreview();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        if(currentCamID == Camera.CameraInfo.CAMERA_FACING_BACK){
            currentCamID = Camera.CameraInfo.CAMERA_FACING_FRONT;
            hideFlash(true);
        }
        else {
            currentCamID = Camera.CameraInfo.CAMERA_FACING_BACK;
            hideFlash(false);
        }
        try {
            mCamera = Camera.open(currentCamID);
            setCameraDisplayOrientation(CameraActivity.this, currentCamID, mCamera);
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId,
                                                   android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
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
        camera.setDisplayOrientation(result);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCamera == null) {
            try {
                mCamera = getCameraInstance();
                mHolder = surfaceView.getHolder();
                mHolder.addCallback(this);
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(currentCamID); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.d(TAG, "getCameraInstance: " + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        releaseCamera();
        try{
            mCamera = Camera.open(currentCamID);
            parameters = mCamera.getParameters();
            parameters.setPictureSize(mPictureWidth, mPictureHeight);
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            for (Camera.Size size : sizes) {
                Log.i(TAG, "Available resolution: "+size.width+" "+size.height);
            }
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceHolder.removeCallback(this);
        releaseCamera();
    }

    public void releaseCamera() {

        if(mCamera != null) {
            mCamera.stopPreview();
            mHolder.removeCallback(this);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnCapture) {
            takePhoto();
        } else if (i == R.id.btnCameraSwitch) {
            switchCamera();
        } else if (i == R.id.btn_flash_on) {
            toggleFlash(false);
        } else if (i == R.id.btn_flash_off) {
            toggleFlash(true);
        } else if (i == R.id.btnSave) {
            saveImage();
        } else if (i == R.id.btnCancel) {
            cancelPhotoView();
        }
    }
}