package org.kirillius.friendslist.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiMessage;

import org.kirillius.friendslist.ui.DialogCellView;
import org.kirillius.friendslist.ui.adapters.EndlessScrollAdapter;

/**
 * Created by Kirill on 09.12.2015.
 */
public class MessagesAdapter extends EndlessScrollAdapter<VKApiMessage> {

    private Picasso mImageLoader;

    public MessagesAdapter() {
    }

    @Override
    public EndlessScrollAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Holder holder;

        switch (viewType) {
            case ITEM_VIEW_TYPE:
                view = new DialogCellView(parent.getContext());
                ((DialogCellView)view).setImageLoader(mImageLoader);
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

            DialogCellView view = (DialogCellView) vh.itemView;

            VKApiMessage msg = this.getItem(position);

            view.setGravity(msg.out ? Gravity.RIGHT : Gravity.LEFT);
            view.setText(msg.body);

            view.setAttachments(msg.attachments);
        }
    }

    /**
     * Sets internal Picasso instance
     * @param imageLoader
     */
    public void setImageLoader(Picasso imageLoader) {
        this.mImageLoader = imageLoader;
    }

    public static class ItemHolder extends EndlessScrollAdapter.Holder {
        public ItemHolder(final View itemView) {
            super(itemView);
        }
    }
}
