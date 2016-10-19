package com.shivisuper.alachat_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.shivisuper.alachat_mobile.Constants.userToSend;

public class FriendList_Activity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<String> mFriends = new ArrayList<>();
    ListView mListView;
    DatabaseReference myRef;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list_);
        setTitle(getResources().getString(R.string.title_friend_list));
        setUIComponents();
        initializeFirebase();
    }

    public void initializeFirebase () {
        myRef = database.getReference("userDetails/"+ Constants.myself+"/friends");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String friend = dataSnapshot.getValue(String.class);
                Log.v("E_CHILD_ADDED", friend);
                mFriends.add(friend);
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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setUIComponents () {
        mListView = (ListView) findViewById(R.id.friendListView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String val = (String) adapterView.getItemAtPosition(i);
                Intent toAc = new Intent(FriendList_Activity.this, Message_activity.class);
                userToSend = val;
                startActivity(toAc);
            }
        });
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mFriends);
        mListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_signout).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Intent intent = new Intent(FriendList_Activity.this, AddFriends_activity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}