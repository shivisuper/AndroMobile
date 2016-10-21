package com.shivisuper.alachat_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.PointF;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScannerActivity extends AppCompatActivity implements
        QRCodeReaderView.OnQRCodeReadListener {

    private static final String TAG = "ScannerActivity";
    String scannedUser;
    private QRCodeReaderView mydecoderview;
    ListView lv;
    //private ArrayList<String> listOfUsersCanBeAddedAsFriends = new ArrayList<>();
    //DatabaseReference detailsRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        //listOfUsersCanBeAddedAsFriends.clear();
        //Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        if (text.length() >= 10) {
            scannedUser = text.substring(0, 9);
        } else {
            scannedUser = text;
        }
        searchUserName(scannedUser);
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

    public void searchUserName(final String toSearch) {
        DatabaseReference myRef = database.getReference("userDetails/");

        myRef.child(toSearch).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    checkUserAlreadyFriend(toSearch);
                } else {
                    //user does not exist.
                    Toast.makeText(getApplicationContext(), toSearch + " could not be found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void checkUserAlreadyFriend(final String toSearch) {

        final DatabaseReference getFriends = database.getReference("userDetails/"+ Constants.myself+"/friends");

        getFriends.child(toSearch).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Already friend of this user
                    Toast.makeText(getApplicationContext(), toSearch + " is already a friend", Toast.LENGTH_SHORT).show();
                } else {
                    //Not in friends list. So add it in database
                    //listOfUsersCanBeAddedAsFriends.add(toSearch);
                    DatabaseReference addAsFriendRef = database.getReference("userDetails/"+ Constants.myself+"/");
                    addAsFriendRef.child("friends").child(toSearch).setValue(toSearch);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}
