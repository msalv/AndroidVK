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
public class FriendsAdapter extends RecyclerView.Adapter {

    public static final int ITEM_VIEW_TYPE = 1;
    public static final int PROGRESS_VIEW_TYPE = 2;

    private VKList<VKApiUserFull> mItems = new VKList<>();
    private Picasso mImageLoader;
    private boolean mIsLoading = false;
    private StringBuilder mStringBuilder;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    private static OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public FriendsAdapter() {
        mStringBuilder = new StringBuilder();
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Holder holder = null;

        switch (viewType) {
            case ITEM_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_item, parent, false);
                holder = new ItemHolder(view);
                break;

            case PROGRESS_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_progress, parent, false);
                holder = new Holder(view);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof ItemHolder) {
            ItemHolder holder = (ItemHolder) vh;

            VKApiUserFull friend = mItems.get(position);

            int size = AndroidUtilities.dp(48);
            String photo = friend.photo.getImageForDimension(size, size);
            this.mImageLoader.load(photo)
                    .placeholder(R.drawable.ic_person)
                    .fit().centerCrop()
                    .into(holder.photoView);

            //holder.nameView.setText(friend.toString());
            mStringBuilder.setLength(0);
            mStringBuilder.append(friend.first_name).append(' ').append(friend.last_name);
            holder.nameView.setText(mStringBuilder);

            Context context = AppLoader.getAppContext();
            holder.onlineView.setText(friend.online ? context.getString(R.string.online) : context.getString(R.string.offline));
        }
    }

    @Override
    public int getItemCount() {
        int size = mItems.size();
        return !mIsLoading ? size : size + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mIsLoading && mItems.size() == position ? PROGRESS_VIEW_TYPE : ITEM_VIEW_TYPE;
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

    public void setIsLoading(boolean loading) {
        mIsLoading = loading;

        if (mIsLoading) {
            notifyItemInserted( mItems.size() );
        }
        else {
            notifyItemRemoved( mItems.size() );
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public static class ItemHolder extends Holder {
        public ImageView photoView;
        public TextView nameView;
        public TextView onlineView;

        public ItemHolder(final View itemView) {
            super(itemView);

            photoView = (ImageView) itemView.findViewById(R.id.photo);
            nameView = (TextView) itemView.findViewById(R.id.name);
            onlineView = (TextView) itemView.findViewById(R.id.online);

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onItemClick(itemView, getLayoutPosition());
                    }
                });
            }
        }
    }
}
