package org.kirillius.friendslist.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kirillius.friendslist.R;

import java.io.InputStream;

/**
 * Created by Kirill on 09.12.2015.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private JSONArray mItems;

    public FriendsAdapter() {

    }

    public void setItems(JSONArray items) {
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject friend = mItems.getJSONObject(position);

            // todo: cache images
            new DownloadImageTask(holder.photoView).execute(friend.getString("photo_100"));

            holder.nameView.setText(friend.getString("first_name") + " " + friend.getString("last_name") );
            holder.onlineView.setText(friend.getInt("online") != 0 ? "Online" : "Offline");

        } catch (JSONException e) {
            Log.e("friends", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.length() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoView;
        public TextView nameView;
        public TextView onlineView;

        public ViewHolder(View itemView) {
            super(itemView);

            photoView = (ImageView) itemView.findViewById(R.id.photo);
            nameView = (TextView) itemView.findViewById(R.id.name);
            onlineView = (TextView) itemView.findViewById(R.id.online);
        }
    }

    /**
     * http://stackoverflow.com/a/9288544
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
