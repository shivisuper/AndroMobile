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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image);
		//setupActionBar();
		
		ImageView imageView = (ImageView)findViewById(R.id.imageView);
		
		final Uri imageUri = getIntent().getData();
		
		Picasso.with(this).load(imageUri.toString()).into(imageView);
        final Intent resultIntent = new Intent();
        resultIntent.setData(imageUri);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
                setResult(RESULT_OK, resultIntent);
				finish();
			}
		}, timerVal*1000);
	}

	/*private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}*/
}
