package com.shivisuper.alachat_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shivisuper.alachat_mobile.adapters.StoryAdapter;
import com.shivisuper.alachat_mobile.models.Photo;
import com.shivisuper.alachat_mobile.models.Story;

import static com.shivisuper.alachat_mobile.Constants.myself;

public class StoriesListActivity extends AppCompatActivity {




    private List<Story> stories = new ArrayList<>();
    private List<Story> myStoryList = new ArrayList<>();
   private List<Photo> currPhotos = new ArrayList<Photo>();
    private StoryAdapter storyAdapter;
    private StoryAdapter myStoryAdapter;

private Story dissStory;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_list);
       // getActionBar().setTitle(getTheme().toString());
       // getActionBar().setTitle("Stories");
        // getSupportActionBar().setTitle("All crazy stuff");



        GridView gridView;
        GridView gridViewMyStory;
        Date cdate = new Date(System.currentTimeMillis());

//

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
       final DatabaseReference myStoryRef =  database.getReference("stories/");
        myStoryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Story currStory = new Story();
                currStory.setCreatedBy(dataSnapshot.getKey());
                Map<String,Object> a = new HashMap<String, Object>();
                List<Photo> currPhotos = new ArrayList<Photo>();

                for (DataSnapshot photoSnapshot: dataSnapshot.getChildren()) {
                    //if(photoSnapshot.getKey().contains("Photo")) {
                        a.put(photoSnapshot.getKey(), photoSnapshot.getValue());
                        Photo currPhoto = new Photo();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
                    try {
                        currPhoto.setPhotoDate(dateFormat.parse((String) (((HashMap) a.get(photoSnapshot.getKey())).get("Date"))));
                    }
                    catch(ParseException e)
                    {
                        currStory.setCreatedOn(new Date(45545455));
                    }


                        if ((((HashMap) a.get(photoSnapshot.getKey())).get("TimeOut")) != null) {
                            Long timeout = Long.parseLong((String)(((HashMap) a.get(photoSnapshot.getKey())).get("TimeOut")));
                            currPhoto.setTimeout(timeout.intValue());
                        }
                        currPhoto.setPhotoPath((String) (((HashMap) a.get(photoSnapshot.getKey())).get("PhotoPath")));


                        currPhotos.add(currPhoto);
                }
                currStory.setStoryPhotos(currPhotos);
                if(currStory.getCreatedBy().contains(myself))
                    myStoryList.add(currStory);
                else
                    stories.add(currStory);

                storyAdapter.notifyDataSetChanged();
                myStoryAdapter.notifyDataSetChanged();
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


        Story newStory = new Story();
        List<Photo> storyPhotos = new ArrayList<>();


        gridView = (GridView) findViewById(R.id.publicStories_layout);
        gridViewMyStory = (GridView) findViewById(R.id.myStories_layout);
        storyAdapter = new StoryAdapter(StoriesListActivity.this, R.layout.single_story_layout,stories);
        myStoryAdapter = new StoryAdapter(StoriesListActivity.this, R.layout.single_story_layout,myStoryList);
        gridView.setAdapter(storyAdapter);
        gridViewMyStory.setAdapter(myStoryAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // remove the clicked item from arraylist
               if(stories.get(position).getStoryPhotos()!=null) {
                   Intent myIntent = new Intent(StoriesListActivity.this, StoryViewerActivity.class);
                   myIntent.putExtra("Story", stories.get(position));
                   startActivity(myIntent);
               }
                else
               {

                   Toast.makeText(StoriesListActivity.this, "No photos in this story!",
                           Toast.LENGTH_SHORT).show();

               }
            }
        });
        gridViewMyStory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // remove the clicked item from arraylist
                if(myStoryList.get(position).getStoryPhotos()!=null) {
                    Intent myIntent = new Intent(StoriesListActivity.this, StoryViewerActivity.class);
                    myIntent.putExtra("Story", myStoryList.get(position));
                    startActivity(myIntent);
                }
                else
                {

                    Toast.makeText(StoriesListActivity.this, "No photos in this story!",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });


        final DatabaseReference mydissStoryRef =  database.getReference("DiscoveryPhotos/");
        dissStory = new Story();
        final List<Photo> dissPhotos = new ArrayList<Photo>();
        dissStory.setStoryPhotos(dissPhotos);
        dissStory.setCreatedBy("Discovered: Superheros");

        mydissStoryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Photo currPhoto = new Photo();
                currPhoto.setTimeout(5);
                currPhoto.setPhotoPath((String)dataSnapshot.getValue());
                dissPhotos.add(currPhoto);
                dissStory.setStoryPhotos(dissPhotos);
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



        Button mapButton = (Button) findViewById(R.id.btn_discoveredStory);
        mapButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {


                Intent myIntent = new Intent(StoriesListActivity.this, StoryViewerActivity.class);
                myIntent.putExtra("Story", dissStory);
                startActivity(myIntent);
            }
        });




    }




}
