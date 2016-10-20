package com.shivisuper.alachat_mobile;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.shivisuper.alachat_mobile.Constants.timerVal;

public class SendToActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference mStorage ;
    ArrayList<String> mMessages = new ArrayList<>();
    private static final int GALLERY_INTENT = 2;

    DatabaseReference friendReference;
    DatabaseReference memoryReference;
    DatabaseReference storyReference;
    DatabaseReference refMsgFrom;
    Uri uriForPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to);
        setTitle("Share With");
        friendReference = database.getReference("userDetails/"+Constants.myself+"/friends");
        memoryReference = database.getReference("userDetails/"+Constants.myself+"/memories");
        storyReference = database.getReference("stories/"+Constants.myself);
        refMsgFrom =  database.getReference("userDetails/" + Constants.myself + "/message");
        uriForPic = getIntent().getData();
        ArrayList<String> mFriends = new ArrayList<>();
        Button AddAsAStoryBtn = (Button) findViewById(R.id.AddAsAStoryBtn);
        Button AddAsAMemoryBtn = (Button) findViewById(R.id.AddAsAMemory);
        //Button takeImageBtn = (Button) findViewById(R.id.takeImageBtn);

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

    public void saveAsAMemory()
    {
        mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child("Photo").child(uriForPic.getLastPathSegment());
        filePath.putFile(uriForPic).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //String theKey = getKey(userToSend, Constants.myself);
                        try {
                            SentToActivityModel data = new
                                    SentToActivityModel(taskSnapshot.getDownloadUrl().toString(),
                                    Integer.toString(timerVal),getDate());
                            memoryReference.push().setValue(data);
                            //getContentResolver().delete(uriForPic, null, null);
                            File tmpvar = new File(uriForPic.getPath());
                            tmpvar.delete();
                            Intent storyIntent = new Intent(SendToActivity.this, StoryViewerActivity.class);
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

    public String getDate()
    {
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        Date date = new Date();
        return (dateFormat.format(date));
    }


    public void saveAsAStory()
    {
        refMsgFrom =  database.getReference("stories/" + Constants.myself);;
        mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child("Photo").child(uriForPic.getLastPathSegment());
        filePath.putFile(uriForPic).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //String theKey = getKey(userToSend, Constants.myself);
                        try {
                            SentToActivityModel data = new
                                    SentToActivityModel(taskSnapshot.getDownloadUrl().toString(),
                                    Integer.toString(timerVal),getDate());
                            storyReference.push().setValue(data);
                            //getContentResolver().delete(uriForPic, null, null);
                            File tmpvar = new File(uriForPic.getPath());
                            tmpvar.delete();
                            Intent storyIntent = new Intent(SendToActivity.this, StoryViewerActivity.class);
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
            filePath.putFile(uriForPic).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(SendToActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                            String theKey = getKey(userToSend, Constants.myself);
                            try {
                                ChatMessage chat = new ChatMessage(userToSend,
                                        "",
                                        Constants.myself, theKey,
                                        "snap",
                                        taskSnapshot.getDownloadUrl().toString());
                                refMsgTo.push().setValue(chat);
                                refMsgFrom.push().setValue(chat);
                                //getContentResolver().delete(uriForPic, null, null);
                                File tmpvar = new File(uriForPic.getPath());
                                tmpvar.delete();
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
