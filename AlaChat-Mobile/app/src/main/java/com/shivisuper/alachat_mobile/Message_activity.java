package com.shivisuper.alachat_mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shivisuper.alachat_mobile.models.ChatMessage;

import java.util.ArrayList;

import static com.shivisuper.alachat_mobile.Constants.userToSend;

public class Message_activity extends AppCompatActivity implements View.OnClickListener {


    private static final int GALLERY_INTENT = 2;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference refMsgFrom;
    private DatabaseReference refMsgTo;
    private Query myRef;
    private ArrayList<ChatMessage> messages;
    private ChatListAdapter adapter;
    private ListView messageLst;
    private EditText messageTxt;
    private ImageButton imageButton;
    private Button sendbtn;
    private String theKey;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);
        setUIComponents();
        initiliazeFirebase();
    }

    public void setUIComponents () {
        messages = new ArrayList<>();
        adapter = new ChatListAdapter(messages, this);
        messageLst = (ListView) findViewById(R.id.messageLst);

        messageLst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String val = (String) adapterView.getItemAtPosition(i);
                Intent toAc = new Intent(Message_activity.this, ViewImageActivity.class);
                userToSend = val;
                startActivity(toAc);
            }
        });


        messageTxt = (EditText) findViewById(R.id.messageTxt);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(this);
        sendbtn = (Button) findViewById(R.id.sendBtn);
        sendbtn.setOnClickListener(this);
        messageLst.setAdapter(adapter);
    }

    public void initiliazeFirebase () {
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        refMsgTo = database.getReference("userDetails/" + userToSend + "/message");
        refMsgFrom = database.getReference("userDetails/" + Constants.myself + "/message");
        theKey = getKey(userToSend, Constants.myself);
        myRef = refMsgFrom.orderByChild("theKey").equalTo(theKey);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage chat = dataSnapshot.getValue(ChatMessage.class);
                messages.add(chat);
                setTitle(userToSend);
                adapter.notifyDataSetChanged();
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

    public void showGallery () {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    public void sendMessage () {
        if (messageTxt.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
        } else {
            ChatMessage chat = new ChatMessage(userToSend, messageTxt.getText().toString(),
                    Constants.myself, theKey);
            refMsgFrom.push().setValue(chat);
            refMsgTo.push().setValue(chat);
            messageTxt.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refMsgTo = database.getReference("userDetails/" + userToSend + "/message");
        refMsgFrom = database.getReference("userDetails/" + Constants.myself + "/message");
        final String theKey = getKey(userToSend, Constants.myself);
        myRef = refMsgFrom.orderByChild("theKey").equalTo(theKey);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            //Log.v("uriData", uri.toString());
            StorageReference filePath = mStorage.child("Photo").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(Message_activity.this, "UploadDone", Toast.LENGTH_SHORT).show();
                    ChatMessage chat = new ChatMessage(userToSend,
                            "",
                            Constants.myself, theKey,
                            taskSnapshot.getDownloadUrl().toString());
                    refMsgTo.push().setValue(chat);
                    refMsgFrom.push().setValue(chat);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.imageButton) {
            showGallery();
        } else if (i == R.id.sendBtn) {
            sendMessage();
        }
    }
}