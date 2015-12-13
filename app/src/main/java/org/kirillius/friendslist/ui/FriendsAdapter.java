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
import org.kirillius.friendslist.core.AndroidUtilities;
import org.kirillius.friendslist.core.AppLoader;

/**
 * Created by Kirill on 09.12.2015.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private VKList<VKApiUserFull> mItems = new VKList<>();
    private Picasso mImageLoader;

    public FriendsAdapter() {
    }

    /**
     * Sets new collection of items
     * @param items
     */
    public void setItems(VKList<VKApiUserFull> items) {
        if (items == null) {
            throw new NullPointerException("items == null");
        }
        mItems = items;
        notifyDataSetChanged();
    }

    /**
     * Adds new items to the collection
     * @param items
     */
    public void addItems(VKList<VKApiUserFull> items) {
        int position = mItems.size();
        mItems.addAll(items);
        notifyItemRangeInserted(position, items.size());
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

        int size = AndroidUtilities.dp(48);
        String photo = friend.photo.getImageForDimension(size, size);
        this.mImageLoader.load(photo).into(holder.photoView);

        holder.nameView.setText(friend.toString());

        Context context = AppLoader.getAppContext();
        holder.onlineView.setText(friend.online ? context.getString(R.string.online) : context.getString(R.string.offline));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setImageLoader(Picasso imageLoader) {
        this.mImageLoader = imageLoader;
    }

    /**
     * Returns total number of friends
     * @return count
     */
    public int getTotalCount() {
        return mItems.getCount();
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
