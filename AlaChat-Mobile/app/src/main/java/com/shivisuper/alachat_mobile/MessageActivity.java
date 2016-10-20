package com.shivisuper.alachat_mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.shivisuper.alachat_mobile.adapters.ChatListAdapter;
import com.shivisuper.alachat_mobile.models.ChatMessage;

import java.util.ArrayList;
import java.util.Objects;

import static com.shivisuper.alachat_mobile.Constants.myself;
import static com.shivisuper.alachat_mobile.Constants.userToSend;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int GALLERY_INTENT = 2;
    private static final int VIEW_IMAGE_INTENT = 10;
    private static final int CAMERA_INTENT = 11;
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
    private Button btnCamera;
    private String theKey;
    private Uri uriFromCamera;

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
        setTitle(userToSend);
        setUIComponents();
        initiliazeFirebase();
        String caller = getIntent().getStringExtra("caller");
        if (caller != null && Objects.equals(caller, "Camera")) {
            uriFromCamera = getIntent().getData();
            updateMessage();
        }
    }

    public void setUIComponents () {
        messages = new ArrayList<>();
        adapter = new ChatListAdapter(messages, this);
        messageLst = (ListView) findViewById(R.id.messageLst);
        messageTxt = (EditText) findViewById(R.id.messageTxt);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(this);
        sendbtn = (Button) findViewById(R.id.sendBtn);
        sendbtn.setOnClickListener(this);
        btnCamera = (Button) findViewById(R.id.btn_shutter);
        btnCamera.setOnClickListener(this);
        messageLst.setAdapter(adapter);
        messageLst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatMessage msg = (ChatMessage) adapterView.getItemAtPosition(i);
                if (msg.getMessage().isEmpty()) {
                    try {
                        Intent toViewImgAc = new Intent(MessageActivity.this,
                                ViewImageActivity.class);
                        toViewImgAc.setData(Uri.parse(msg.getImageUri()));
                        if (Objects.equals(msg.getPicType(), "image"))
                            toViewImgAc.putExtra("gallery", true);
                        else
                            toViewImgAc.putExtra("gallery", false);
                        startActivityForResult(toViewImgAc, VIEW_IMAGE_INTENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void updateMessage() {
        Uri uri = uriFromCamera;
        StorageReference filePath = mStorage.child("Photo").child(uri.getLastPathSegment());
        final ProgressDialog progressDialog = new ProgressDialog(MessageActivity.this,
                R.style.Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving as Story...");
        progressDialog.show();
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ChatMessage chat1 = new ChatMessage(userToSend,
                        "Delivered",
                        myself,
                        theKey);
                ChatMessage chat2 = new ChatMessage(userToSend,
                        "",
                        myself,
                        theKey,
                        "snap",
                        taskSnapshot.getDownloadUrl().toString()
                );
                refMsgTo.push().setValue(chat2);
                refMsgFrom.push().setValue(chat1);
                progressDialog.dismiss();
            }
        });
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
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                messages.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }*/

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

    public void showCamera () {
        Intent camIntent = new Intent(MessageActivity.this, CameraActivity.class);
        camIntent.putExtra("caller", "MessageActivity");
        camIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        camIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(camIntent);
        //finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String theKey = getKey(userToSend, Constants.myself);
        myRef = refMsgFrom.orderByChild("theKey").equalTo(theKey);
        refMsgTo = database.getReference("userDetails/" + userToSend + "/message");
        refMsgFrom = database.getReference("userDetails/" + Constants.myself + "/message");
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            StorageReference filePath = mStorage.child("Photo").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ChatMessage chat = new ChatMessage(userToSend,
                            "",
                            Constants.myself, theKey,
                            "image",
                            taskSnapshot.getDownloadUrl().toString());
                    refMsgTo.push().setValue(chat);
                    refMsgFrom.push().setValue(chat);
                }
            });
        } else if (requestCode == VIEW_IMAGE_INTENT && resultCode == RESULT_OK) {
            final Intent retData = data;
            String picType = retData.getStringExtra("picType");
            if (Objects.equals(picType, "snap")) {
                StorageReference filepath = mStorage.child(retData.getData().getLastPathSegment());
                filepath.delete().
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                refMsgFrom.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        for (DataSnapshot record : dataSnapshot.getChildren()) {
                                            if (Objects.equals(record.getKey(), "imageUri")) {
                                                if (Objects.equals(record.getValue(),
                                                        retData.getData().toString())) {
                                                    record.getRef().getParent().removeValue();
                                                }
                                            }
                                        }
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
                        }).
                        addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MessageActivity.this,
                                        getResources().getString(R.string.unable_to_remove),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.imageButton) {
            showGallery();
        } else if (i == R.id.sendBtn) {
            sendMessage();
        } else if (i == R.id.btn_shutter) {
            showCamera();
        }
    }
}