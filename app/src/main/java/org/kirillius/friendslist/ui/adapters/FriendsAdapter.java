package org.kirillius.friendslist.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiUserFull;

import org.kirillius.friendslist.R;
import org.kirillius.friendslist.core.AndroidUtilities;
import org.kirillius.friendslist.core.AppLoader;
import org.kirillius.friendslist.ui.adapters.EndlessScrollAdapter;

/**
 * Created by Kirill on 09.12.2015.
 */
public class FriendsAdapter extends EndlessScrollAdapter<VKApiUserFull> {

    private Picasso mImageLoader;
    private StringBuilder mStringBuilder;

    public FriendsAdapter() {
        mStringBuilder = new StringBuilder();
    }

    @Override
    public EndlessScrollAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Holder holder;

        switch (viewType) {
            case ITEM_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_item, parent, false);
                holder = new ItemHolder(view);
                break;

            default:
                holder = super.onCreateViewHolder(parent, viewType);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof ItemHolder) {
            ItemHolder holder = (ItemHolder) vh;

            VKApiUserFull friend = this.getItem(position);

            int size = AndroidUtilities.dp(48);
            String photo = friend.photo.getImageForDimension(size, size);
            this.mImageLoader.load(photo)
                    .placeholder(R.drawable.ic_person)
                    .fit().centerCrop()
                    .into(holder.photoView);

            mStringBuilder.setLength(0);
            mStringBuilder.append(friend.first_name).append(' ').append(friend.last_name);
            holder.nameView.setText(mStringBuilder);

            Context context = AppLoader.getAppContext();
            holder.onlineView.setText(friend.online ? context.getString(R.string.online) : context.getString(R.string.offline));
        }
    }

    /**
     * Sets internal Picasso instance
     * @param imageLoader
     */
    public void setImageLoader(Picasso imageLoader) {
        this.mImageLoader = imageLoader;
    }

    public static class ItemHolder extends EndlessScrollAdapter.ItemHolder {
        public ImageView photoView;
        public TextView nameView;
        public TextView onlineView;

        public ItemHolder(final View itemView) {
            super(itemView);

            photoView = (ImageView) itemView.findViewById(R.id.photo);
            nameView = (TextView) itemView.findViewById(R.id.name);
            onlineView = (TextView) itemView.findViewById(R.id.online);
        }
    }
}
