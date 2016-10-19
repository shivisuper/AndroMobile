package com.shivisuper.alachat_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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

public class StoriesListActivity extends AppCompatActivity {




    private List<Story> stories = new ArrayList<>();
   private List<Photo> currPhotos = new ArrayList<Photo>();
    private StoryAdapter storyAdapter;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_list);
       // getActionBar().setTitle(getTheme().toString());
       // getActionBar().setTitle("Stories");
        // getSupportActionBar().setTitle("All crazy stuff");



        GridView gridView;

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
                    if(photoSnapshot.getKey().contains("Photo")) {
                        a.put(photoSnapshot.getKey(), photoSnapshot.getValue());
                        Photo currPhoto = new Photo();
                        if ((((HashMap) a.get(photoSnapshot.getKey())).get("Timeout")) != null)
                            currPhoto.setTimeout(((Long) (((HashMap) a.get(photoSnapshot.getKey())).get("Timeout"))).intValue());
                        currPhoto.setPhotoPath((String) (((HashMap) a.get(photoSnapshot.getKey())).get("PhotoPath")));


                        currPhotos.add(currPhoto);
                    }
                    else
                    {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");

                        try {
                            currStory.setCreatedOn(dateFormat.parse((String) photoSnapshot.getValue()));
                        }
                        catch(ParseException e)
                        {

                            currStory.setCreatedOn(new Date(45545455));
                        }

                    }
                }
                currStory.setStoryPhotos(currPhotos);
                stories.add(currStory);
                storyAdapter.notifyDataSetChanged();
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

        storyAdapter = new StoryAdapter(StoriesListActivity.this, R.layout.single_story_layout,stories);
        gridView.setAdapter(storyAdapter);

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




    }




}
