package com.shivisuper.alachat_mobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.shivisuper.alachat_mobile.models.Photo;
import com.shivisuper.alachat_mobile.models.Story;

public class StoryViewerActivity extends AppCompatActivity {
    Boolean isLoaded = false;
    List<Bitmap> storyImages;
    private Handler progressBarHandler;
    private ProgressBar progressBar;
    private ProgressBar loadBar;


    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public ImageDownloader(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("MyApp", e.getMessage());
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }

    }


    class progressBarTask implements Runnable {
        private int timeToShow;
        public progressBarTask(int secs)
        {
            super();
            progressBar = (ProgressBar) findViewById(R.id.timeLeft);
            timeToShow = secs;
            progressBar.setMax(timeToShow*1000);

            //progressBar.setProgress(0);

        }
        @Override
        public void run() {

            for (int i = 0; i <= (timeToShow*1000)-100;i++) {
                final int value = i + ((i+1)*1000);
                try {
                    Thread.sleep(999);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBarHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(value);
                    }
                });
            }
            //progressBar.setProgress(0);

        }
    }

    class downloader implements Runnable {
        @Override
        public void run() {
            for (Photo curr:story.getStoryPhotos()) {
                String url = curr.getPhotoPath();
                Bitmap bitmap = null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    Log.e("Downloader", "GotImage");
                } catch (Exception e) {
                    Log.e("MyApp", e.getMessage());
                }
                storyImages.add(bitmap);
            }
            downloadHandler.post(new Runnable() {
                @Override
                public void run() {
                    startPhotoShow();
                }
            });

            Log.e("Downloader", "Done");
        }
    }

    private int mInterval = 2000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    private Handler downloadHandler;
    int photoNum = 0;
    Story story;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_story_viewer);

        Bundle bundle = getIntent().getExtras();
        story = (Story) getIntent().getSerializableExtra("Story");
        if(story.getCreatedBy()=="My Memories")
        {
            TextView loadText = (TextView)findViewById(R.id.loadText);
            loadText.setText("Downloading Photo");

        }
        //setTitle(story.getStoryName());
        setTitle(story.getCreatedBy());
        progressBar = (ProgressBar) findViewById(R.id.timeLeft);
        ImageView imageView= (ImageView) findViewById(R.id.photoView);
        imageView.setVisibility(View.INVISIBLE);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == findViewById(R.id.photoView)) {
                    updatePhoto();
                }
            }
        });






        progressBar.setVisibility(View.INVISIBLE);
        mInterval = story.getStoryPhotos().get(0).getTimeout() * 1000;
        storyImages = new ArrayList<Bitmap>();
        downloadHandler = new Handler();
        mHandler = new Handler();
        progressBarHandler = new Handler();

        new Thread(new downloader()).start();




    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPhotoShow();
    }




    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updatePhoto(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };



    void updatePhoto(){

        ImageView imageView= (ImageView) findViewById(R.id.photoView);
        int currentPicNum = photoNum % story.getStoryPhotos().size();
        if(story.getStoryPhotos().get(currentPicNum).getPhotoPath()!=null) {

            int timeToRun = story.getStoryPhotos().get(currentPicNum).getTimeout();

            new Thread(new progressBarTask(timeToRun)).start();
            imageView.setImageBitmap(storyImages.get(currentPicNum));

        }


        mInterval = story.getStoryPhotos().get(photoNum%story.getStoryPhotos().size()).getTimeout() * 1000;
        photoNum++;


    }


    void startPhotoShow() {
        loadBar = (ProgressBar) findViewById(R.id.loadBar);
        loadBar.setVisibility(View.INVISIBLE);
        progressBar = (ProgressBar) findViewById(R.id.timeLeft);
        progressBar.setVisibility(View.VISIBLE);
        ImageView imageView= (ImageView) findViewById(R.id.photoView);
        imageView.setVisibility(View.VISIBLE);
        TextView loadText = (TextView)findViewById(R.id.loadText);
        loadText.setVisibility(View.INVISIBLE);
        mStatusChecker.run();
    }

    void stopPhotoShow() {
        mHandler.removeCallbacks(mStatusChecker);
    }



}
