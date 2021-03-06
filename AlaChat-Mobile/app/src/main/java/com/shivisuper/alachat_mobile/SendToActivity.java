package com.shivisuper.alachat_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;



import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shivisuper.alachat_mobile.models.ChatMessage;
import com.shivisuper.alachat_mobile.models.SentToActivityModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.shivisuper.alachat_mobile.Constants.myself;
import static com.shivisuper.alachat_mobile.Constants.timerVal;

public class SendToActivity extends AppCompatActivity {
    int increment = 4;
    MyLocation myLocation = new MyLocation();
    double Lat;
    double Lng;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference mStorage ;
    ArrayList<String> mMessages = new ArrayList<>();
    private static final int GALLERY_INTENT = 2;

    DatabaseReference friendReference;
    DatabaseReference memoryReference;
    DatabaseReference storyReference;
    DatabaseReference refMsgFrom;
    Uri uriForPic;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to);
        setTitle("Share With");


        myLocation.getLocation(getApplicationContext(), locationResult);

        boolean r = myLocation.getLocation(getApplicationContext(),
                locationResult);



        /*googleApiClient = new GoogleApiClient.Builder(this, this, this).
                addApi(LocationServices.API).build();*/
        friendReference = database.getReference("userDetails/" + myself+"/friends");
        memoryReference = database.getReference("userDetails/" + myself+"/memories");
        storyReference = database.getReference("stories/" + myself);
        refMsgFrom =  database.getReference("userDetails/" + myself + "/message");
        uriForPic = getIntent().getData();
        ArrayList<String> mFriends = new ArrayList<>();
        Button AddAsAStoryBtn = (Button) findViewById(R.id.AddAsAStoryBtn);
        Button AddAsAMemoryBtn = (Button) findViewById(R.id.AddAsAMemory);

        getFriendsList();

        // Add the Image to the user Story
        AddAsAMemoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAsAMemory();
            }
        });

        // Add the Image to user Memory.
        AddAsAStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAsAStory();
            }
        });


        //Show list of friends
        ListView listOfFriends = (ListView)findViewById(R.id.listOfFriends);
        listOfFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String userToSend = (String) adapterView.getItemAtPosition(i);
                //Toast.makeText(SendToActivity.this, userToSend , Toast.LENGTH_LONG).show();
                sendAsAMessage(userToSend);
            }
        });
    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }*/

    /*private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    };*/

    public void saveAsAMemory()
    {
        mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child("Photo").child(uriForPic.getLastPathSegment());
        /*if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }*/
        /*if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION ) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        final double longitude = location.getLongitude();
        final double latitude = location.getLatitude();*/
        final ProgressDialog progressDialog = new ProgressDialog(SendToActivity.this,
                R.style.Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving as Memory...");
        progressDialog.show();
        filePath.putFile(uriForPic).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        try {
                            SentToActivityModel data = new
                                    SentToActivityModel(taskSnapshot.getDownloadUrl().toString(),
                                    Integer.toString(timerVal),getDate(),
                                    Lng,
                                    Lat);
                            memoryReference.push().setValue(data);
                            //getContentResolver().delete(uriForPic, null, null);
                            File tmpvar = new File(uriForPic.getPath());
                            tmpvar.delete();
                            progressDialog.dismiss();
                            Toast.makeText(SendToActivity.this, "Added as a Memory", Toast.LENGTH_SHORT).show();
                            /*Intent storyIntent = new Intent(SendToActivity.this, StoryViewerActivity.class);
                            storyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(storyIntent);*/
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendToActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
    }



    public MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {

        @Override
        public void gotLocation(Location location) {
            // TODO Auto-generated method stub
            double Longitude = location.getLongitude();
            double Latitude = location.getLatitude();
            Lat = Latitude;
            Lng = Longitude;
            //Toast.makeText(getApplicationContext(), "Got Location",
                //    Toast.LENGTH_LONG).show();

            try {
                SharedPreferences locationpref = getApplication()
                        .getSharedPreferences("location", MODE_WORLD_READABLE);
                SharedPreferences.Editor prefsEditor = locationpref.edit();
                prefsEditor.putString("Longitude", Longitude + "");
                prefsEditor.putString("Latitude", Latitude + "");
                prefsEditor.commit();
                System.out.println("SHARE PREFERENCE ME PUT KAR DIYA.");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };


    public String getDate()
    {
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        Date date = new Date();
        return (dateFormat.format(date));
    }


    public void saveAsAStory()
    {
        refMsgFrom =  database.getReference("stories/" + Constants.myself);
        mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child("Photo").child(uriForPic.getLastPathSegment());
        final ProgressDialog progressDialog = new ProgressDialog(SendToActivity.this,
                R.style.Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving as Story...");
        progressDialog.show();
        filePath.putFile(uriForPic).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        try {
                            SentToActivityModel data = new
                                    SentToActivityModel(taskSnapshot.getDownloadUrl().toString(),
                                    Integer.toString(timerVal),getDate());
                            storyReference.push().setValue(data);
                            //getContentResolver().delete(uriForPic, null, null);
                            File tmpvar = new File(uriForPic.getPath());
                            tmpvar.delete();
                            progressDialog.dismiss();
                            Intent storyIntent = new Intent(SendToActivity.this, StoriesListActivity.class);
                            storyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(storyIntent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendToActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
    }


    public void sendAsAMessage(final String userToSend)
    {
        final DatabaseReference refMsgTo = database.getReference("userDetails/" + userToSend + "/message");
        mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child("Photo").child(uriForPic.getLastPathSegment());
        final ProgressDialog progressDialog = new ProgressDialog(SendToActivity.this,
                R.style.Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending to " + userToSend);
        progressDialog.show();
        filePath.putFile(uriForPic).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Toast.makeText(SendToActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        String theKey = getKey(userToSend, myself);
                        try {
                            ChatMessage chat1 = new ChatMessage(userToSend,
                                    "Delivered",
                                    myself,
                                    theKey);

                            ChatMessage chat2 = new ChatMessage(userToSend,
                                    "",
                                    Constants.myself, theKey,
                                    "snap",
                                    taskSnapshot.getDownloadUrl().toString());

                            refMsgTo.push().setValue(chat2);
                            refMsgFrom.push().setValue(chat1);
                            //getContentResolver().delete(uriForPic, null, null);
                            File tmpvar = new File(uriForPic.getPath());
                            tmpvar.delete();
                            progressDialog.dismiss();
                            Intent friendlist = new Intent(SendToActivity.this, FriendListActivity.class);
                            friendlist.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(friendlist);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendToActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                        Log.e("upload: ", e.getMessage());
                    }
                });
    }

    public static String getKey(String name1, String name2) {
        String comb;
        if (name1.compareTo(name2) > 0) {
            comb = name1 + "_" + name2;
            return comb;
        } else {
            comb = name2 + "_" + name1;
            return comb;
        }
    }


    public void  getFriendsList()
    {
        Log.v("Inside","Inside");
        ListView listOfFriends = (ListView)findViewById(R.id.listOfFriends);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mMessages);
        listOfFriends.setAdapter(adapter);

        friendReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String message = dataSnapshot.getValue(String.class);
                Log.v("E_CHILD_ADDED", message);
                mMessages.add(message);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                String message = dataSnapshot.getValue(String.class);
                Log.v("E_CHILD_CHANGED", message);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                String message = dataSnapshot.getValue(String.class);
                Log.v("E_CHILD_REMOVED", message);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
