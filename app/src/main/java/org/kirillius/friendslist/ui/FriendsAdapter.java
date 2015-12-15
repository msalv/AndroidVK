package org.kirillius.friendslist.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
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
public class FriendsAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final int ITEM_VIEW_TYPE = 1;
    public static final int PROGRESS_VIEW_TYPE = 2;

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
        View v;

        switch (viewType) {
            case PROGRESS_VIEW_TYPE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friends_progress, parent, false);
                return new ProgressViewHolder(v);

            case ITEM_VIEW_TYPE:
            default:
               v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friends_item, parent, false);

               return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {

        if (vh instanceof ProgressViewHolder) {
            return;
        }

        ItemViewHolder holder = (ItemViewHolder) vh;

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

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position) == null ? PROGRESS_VIEW_TYPE : ITEM_VIEW_TYPE;
    }

    /**
     * Sets internal Picasso instance
     * @param imageLoader
     */
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

    public void showLoader() {
        mItems.add(null);
        notifyItemInserted( mItems.size() - 1 );
    }

    public void hideLoader() {
        int position = mItems.size() - 1;
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public static class ItemViewHolder extends ViewHolder {
        public ImageView photoView;
        public TextView nameView;
        public TextView onlineView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            photoView = (ImageView) itemView.findViewById(R.id.photo);
            nameView = (TextView) itemView.findViewById(R.id.name);
            onlineView = (TextView) itemView.findViewById(R.id.online);
        }
    }

    public static class ProgressViewHolder extends ViewHolder {

        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }
}
