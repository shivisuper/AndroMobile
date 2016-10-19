package com.shivisuper.alachat_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.PointF;
import android.util.Log;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.*;

public class ScannerActivity extends AppCompatActivity implements
        QRCodeReaderView.OnQRCodeReadListener {

    private static final String TAG = "ScannerActivity";
    String scannedUser;
    private QRCodeReaderView mydecoderview;
    final String mDbChild = "userDetails";
    DatabaseReference detailsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        /*try {
            JSONObject obj = (JSONObject) new JSONTokener(text).nextValue();
            String username = obj.getString("username");
            String uid = obj.getString("uid");
            Log.d(TAG, username + " " + uid);
        } catch (JSONException j) {
            Log.d(TAG, j.getMessage());
        }*/
        scannedUser = text;
        queryFirebase();
    }

    @Override
    public void cameraNotFound() {

    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mydecoderview.getCameraManager().stopPreview();
    }

    public void queryFirebase() {
        detailsRef = FirebaseDatabase.getInstance().getReference(mDbChild);
        DatabaseReference friendsref = detailsRef.child("/friends");
    }
}
