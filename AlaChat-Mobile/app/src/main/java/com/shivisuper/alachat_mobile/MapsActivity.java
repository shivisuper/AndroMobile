package com.shivisuper.alachat_mobile;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.shivisuper.alachat_mobile.Constants.myself;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private List<LatLng> myCoordinates;
    private GoogleMap mMap;
private TextView myText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        myText = (TextView)findViewById(R.id.distanceText);
        myText.setBackgroundColor(Color.GRAY);

        mMap = googleMap;
         myCoordinates = new ArrayList<LatLng>();
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //myCoordinates.add(sydney);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myMemoryReff =  database.getReference("userDetails/"+ myself +"/memories/");
        myMemoryReff.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    LatLng curr = new LatLng((Double)(((HashMap)(dataSnapshot.getValue())).get("Lat")),(Double)(((HashMap)(dataSnapshot.getValue())).get("Lng")));
                    myCoordinates.add(curr);
                    mMap.addMarker(new MarkerOptions().position(curr).title((String)(((HashMap)(dataSnapshot.getValue())).get("Date"))));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
                    mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
                    updateDistanceText();

            }





            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







    }
    private void updateDistanceText(){

       double maxdistance = 0;
       for(int i =0;i<myCoordinates.size();i++)
       {
           for (int j =0 ; j < myCoordinates.size();j++)
           {
                if (i!=j)
                {
                    Location locI = new Location("");
                    locI.setLatitude(myCoordinates.get(i).latitude);
                    locI.setLongitude(myCoordinates.get(i).longitude);
                    Location locJ = new Location("");
                    locJ.setLatitude(myCoordinates.get(j).latitude);
                    locJ.setLongitude(myCoordinates.get(j).longitude);

                    double distance = locJ.distanceTo(locI);
                    //double distance  = Math.sqrt(Math.pow(myCoordinates.get(i).latitude - myCoordinates.get(j).latitude, 2) + Math.pow(myCoordinates.get(i).longitude - myCoordinates.get(j).longitude, 2));
                    if(distance>maxdistance)
                    {
                        maxdistance = distance;
                    }


                }

           }

       }
        if(maxdistance<1000)
            myText.setText("Total Snaps: "+myCoordinates.size()+"\nMost distant snaps are "+String.format("%.0f",maxdistance)+" meters apart!");
        else
            myText.setText("Total Snaps: "+myCoordinates.size()+"\nMost distant snaps are: "+String.format("%.0f",maxdistance/1000)+" km apart!");






    }
}
