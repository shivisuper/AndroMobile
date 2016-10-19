package com.shivisuper.alachat_mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageUploadActivity extends AppCompatActivity {

    private Button mSelectImage;

    private StorageReference mStorage;

    private static final int GALLERY_INTENT = 2;

    private ProgressDialog mProgressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload_activity);

        mSelectImage = (Button) findViewById(R.id.selectImage);
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");

                startActivityForResult(intent , GALLERY_INTENT);

            }
        });
    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK )
        {
            final Uri uri = data.getData();
            Log.v("uriData",uri.toString());

            StorageReference filePath = mStorage.child("Photo").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ImageUploadActivity.this, "UploadDone", Toast.LENGTH_LONG).show();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.v("aa",downloadUrl.toString());
                }
            });
        }
    }
}
