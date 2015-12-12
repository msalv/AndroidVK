package org.kirillius.friendslist.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import org.kirillius.friendslist.R;

/**
 * Created by Kirill on 09.12.2015.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private Context mContext;
    private VKList<VKApiUserFull> mItems;
    private Picasso mImageLoader;

    public FriendsAdapter(Context context) {
        mContext = context;
    }

    public void setItems(VKList<VKApiUserFull> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        VKApiUserFull friend = mItems.get(position);

        // fixme: choose appropriate image size
        this.mImageLoader.load(friend.photo_100).into(holder.photoView);

        holder.nameView.setText(friend.toString());
        holder.onlineView.setText(friend.online ? mContext.getString(R.string.online) : mContext.getString(R.string.offline));
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public void setImageLoader(Picasso imageLoader) {
        this.mImageLoader = imageLoader;
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
}
