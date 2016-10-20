package com.shivisuper.alachat_mobile;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import static com.shivisuper.alachat_mobile.Constants.timerVal;

public class ViewImageActivity extends Activity {

    Intent resultIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image);
		//setupActionBar();
		
		ImageView imageView = (ImageView)findViewById(R.id.imageView);
		
		final Uri imageUri = getIntent().getData();
        final boolean isGallery = getIntent().getBooleanExtra("gallery", false);
		
		Picasso.with(this).load(imageUri.toString()).into(imageView);
        resultIntent = new Intent();
        resultIntent.setData(imageUri);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
                if (!isGallery) {
                    setResult(RESULT_OK, resultIntent);
                    resultIntent.putExtra("picType", "snap");
                    finish();
                }
			}
		}, timerVal*1000);
	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK, resultIntent);
        resultIntent.putExtra("picType", "image");
        finish();
    }
}
