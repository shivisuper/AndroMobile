package com.shivisuper.alachat_mobile.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shivisuper.alachat_mobile.R;
import com.shivisuper.alachat_mobile.models.Photo;
import com.shivisuper.alachat_mobile.models.Story;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by Umer on 10/12/2016.
 */

public class MemoryAdapter extends ArrayAdapter<Photo> {
    private int resourceId;

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




    public MemoryAdapter(Context context, int resource, List<Photo> objects) {
        super(context, resource, objects);
        this.resourceId =resource;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        // get one object content
        Photo photo = getItem(position);

        ViewHolder viewHolder;
        View view;
        if (convertView==null){
            //create a new ViewHolder
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            // store instance components into the viewHolder
            viewHolder.svImage = (ImageView) view.findViewById(R.id.memmoryImage);
            // store the viewHolder into the view
            view.setTag(viewHolder);
        }else {
            view = convertView;
            //recover the viewHolder that store the previous instance components again
            viewHolder = (ViewHolder) view.getTag();
        }




        if(photo.getPhotoPath()!=null) {
            ImageDownloader imageDownLoader = new ImageDownloader(viewHolder.svImage);
            imageDownLoader.execute(photo.getPhotoPath());
        }

        //viewHolder.svImage.set
        return view;
    }
    // Define a ViewHolder class
    class ViewHolder{
        ImageView svImage;

    }

}
