package com.shivisuper.alachat_mobile.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.squareup.picasso.Picasso;

/**
 * Created by Umer on 10/12/2016.
 */

public class MemoryAdapter extends ArrayAdapter<Photo> {
    private int resourceId;
    private Context context;
    public MemoryAdapter(Context context, int resource, List<Photo> objects) {
        super(context, resource, objects);
        this.resourceId =resource;
        this.context = context;
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
            /*ImageDownloader imageDownLoader = new ImageDownloader(viewHolder.svImage);
            imageDownLoader.execute(photo.getPhotoPath());*/
            try {
                Picasso.with(context).
                        load(Uri.parse(photo.getPhotoPath())).
                        error(R.drawable.wrong).
                        placeholder( R.drawable.progress_animation ).
                        resize(150, 230).
                        into(viewHolder.svImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //viewHolder.svImage.set
        return view;
    }
    // Define a ViewHolder class
    class ViewHolder{
        ImageView svImage;

    }

}
