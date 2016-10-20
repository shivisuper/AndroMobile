package com.shivisuper.alachat_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shivisuper.alachat_mobile.adapters.MemoryAdapter;
import com.shivisuper.alachat_mobile.models.Photo;
import com.shivisuper.alachat_mobile.models.Story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.shivisuper.alachat_mobile.Constants.myself;

public class Memories_Activity extends AppCompatActivity {

    MemoryAdapter memoryAdapter;
    List<Photo> mySnaps = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories);
        setTitle("My Memories");

        GridView gridView;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myMemoryReff =  database.getReference("userDetails/"+ myself +"/memories/");
        myMemoryReff.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Photo currPhoto = new Photo();
                currPhoto.setPhotoPath((String)(((HashMap)(dataSnapshot.getValue())).get("PhotoPath")));
                mySnaps.add(currPhoto);
                memoryAdapter.notifyDataSetChanged();
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

        gridView = (GridView) findViewById(R.id.myMemories);
         memoryAdapter =  new MemoryAdapter(Memories_Activity.this, R.layout.single_memory_layout,mySnaps);
        gridView.setAdapter(memoryAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Story tempStory = new Story();
                List<Photo> storyPhoto = new ArrayList<Photo>();
                mySnaps.get(position).setTimeout(1001);
                storyPhoto.add(mySnaps.get(position));
                tempStory.setStoryPhotos(storyPhoto);
                tempStory.setCreatedBy("My Memories");
                Intent myIntent = new Intent(Memories_Activity.this, StoryViewerActivity.class);
                myIntent.putExtra("Story", tempStory);
                startActivity(myIntent);
            }
        });

        Button mapButton = (Button) findViewById(R.id.btn_mapMemories);
        mapButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent2 = new Intent(Memories_Activity.this, MapsActivity.class);
                startActivity(myIntent2);
            }
        });

    }
}
