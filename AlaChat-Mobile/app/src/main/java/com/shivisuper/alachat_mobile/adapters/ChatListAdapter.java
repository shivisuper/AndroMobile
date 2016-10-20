package com.shivisuper.alachat_mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.shivisuper.alachat_mobile.Constants;
import com.shivisuper.alachat_mobile.R;
import com.shivisuper.alachat_mobile.models.ChatMessage;
import com.squareup.picasso.Picasso;

public class ChatListAdapter extends BaseAdapter {

    private ArrayList<ChatMessage> chatMessages;
    private Context context;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    public ChatListAdapter(ArrayList<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;

    }

    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ChatMessage message = chatMessages.get(position);
        ViewHolder holder;
        //ViewHolder2 holder2;
        holder = new ViewHolder();
        if (!message.getSendTo().equalsIgnoreCase(Constants.userToSend)) {
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.chat_user_from, null, false);
                setupHolder(holder, view);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
        } else {
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.chat_user_to, null, false);
                setupHolder(holder, view);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
        }
        if (message.getMessage().isEmpty()) {
            holder.messageImageView.setVisibility(View.VISIBLE);
            holder.messageTextView.setVisibility(View.GONE);
            try {
                Picasso.with(context).
                        load(message.getImageUri()).
                        error(R.drawable.wrong).
                        placeholder( R.drawable.progress_animation ).
                        resize(150, 150).
                        into(holder.messageImageView);
            } catch(Exception f) {
                f.printStackTrace();
            }
        }
        else {
            holder.messageImageView.setVisibility(View.GONE);
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.messageTextView.setText(message.getMessage());
        }
        return view;
    }

    public void setupHolder (ViewHolder h, View v) {
        h.messageImageView = (ImageView) v.findViewById(R.id.image_view);
        h.messageTextView = (TextView) v.findViewById(R.id.message_text);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int retVal;
        ChatMessage message = chatMessages.get(position);
        if (!message.getSendTo().equalsIgnoreCase(Constants.userToSend))
            retVal = 1;
        else
            retVal = 0;
        return retVal;
    }

    private class ViewHolder {
        public TextView messageTextView;
        public ImageView messageImageView;
    }

    /*private class ViewHolder2 {
        public ImageView messageImageView;
        public TextView messageTextView;
    }*/
}
