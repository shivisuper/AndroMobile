package com.shivisuper.alachat_mobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddFriendsActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<String> userLit = new ArrayList<>();
    ListView lv;
    Button button;
    TextView userNameText;
    private ArrayList<String> listOfUsersCanBeAddedAsFriends = new ArrayList<>();
    private ArrayList<String> listOfUserFriends = new ArrayList<>();
    private String searchName ;
//    Button searchBtn = (Button) findViewById(R.id.searchFirnedButton);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_activity);

//        generateListContent();

        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);*/

        lv = (ListView) findViewById(R.id.listViewAddFriends);
        lv.setItemsCanFocus(false);
        button = (Button) findViewById(R.id.searchFirnedButton);
        TextView toSearchText;

        userNameText = (TextView) findViewById(R.id.friendNameText);


    }

    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {   }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count == 0) {
                //Put your code here.
                //Runs when delete/backspace pressed on soft key (tested on htc m8)
                //You can use EditText.getText().length() to make if statements here
                lv.setAdapter(null);
                listOfUsersCanBeAddedAsFriends.clear();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onStart() {
        super.onStart();

        setTitle("Add Friends");

//        getDetailsFromdb();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                lv.setAdapter(null);
                userNameText = (TextView) findViewById(R.id.searchFriendText);
                String username = userNameText.getText().toString();
                if (username.matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter a username" + username, Toast.LENGTH_SHORT).show();
                } else {
                    searchUserName(username);
                    searchName = username;
                }

                userNameText.addTextChangedListener(textWatcher);

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String val = (String) adapterView.getItemAtPosition(i);

                Log.v("alaa", val);

            }
        });
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
                    Log.v("NotPresent", "NotPresent");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void checkUserAlreadyFriend(final String toSearch) {

        final DatabaseReference getFriends = database.getReference("userDetails/"+ Constants.myself+"/friends");

        final ArrayAdapter<String> adapter = new MyListAdapter(this, R.layout.friend_to_add_list_view, listOfUsersCanBeAddedAsFriends);
        lv.setAdapter(adapter);

        getFriends.child(toSearch).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Already friend of this user
                    Toast.makeText(getApplicationContext(), toSearch + " is already a friend", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    Log.v("UserIsAlreadyFriend", toSearch);
                } else {
                    //Not in friends list. So add it in database
                    listOfUsersCanBeAddedAsFriends.add(toSearch);
                    adapter.notifyDataSetChanged();
//                    getFriends.child(toSearch).setValue(toSearch);
                    Log.v("NotUserIsAlreadyFriend", toSearch);



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }


    private class MyListAdapter extends ArrayAdapter<String> {

        private int layout;

        public MyListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mainViewHolder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                final ViewHolder viewHolder = new ViewHolder();

                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);
                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_btn);
                viewHolder.title.setText(searchName);
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String toAddFriend = viewHolder.title.getText().toString();
                        DatabaseReference addAsFriendRef = database.getReference("userDetails/"+ Constants.myself+"/");
                        addAsFriendRef.child("friends").child(toAddFriend).setValue(toAddFriend);
                        Intent intent = new Intent(AddFriendsActivity.this, FriendListActivity.class);
                        startActivity(intent);
                        finish();

                        Toast.makeText(getContext() , "Friend Added : " + viewHolder.title.getText(), Toast.LENGTH_LONG).show();
                    }
                });

                convertView.setTag(viewHolder);
            } else {
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.title.setText(getItem(position));
            }
            return convertView;
        }

    }


    public class ViewHolder {
        ImageView thumbnail;
        TextView title;
        Button button;
    }


}