package org.kirillius.friendslist.ui;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKList;

import org.kirillius.friendslist.R;
import org.kirillius.friendslist.core.AndroidUtilities;

/**
 * Created by Kirill on 09.12.2015.
 */
public class MessagesAdapter extends RecyclerView.Adapter {

    public static final int ITEM_VIEW_TYPE = 1;
    public static final int PROGRESS_VIEW_TYPE = 2;

    private VKList<VKApiMessage> mItems = new VKList<>();
    private Picasso mImageLoader;
    private boolean mIsLoading = false;
    private int totalCount = 0;

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    private static OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public MessagesAdapter() {
    }

    /**
     * Sets new collection of items
     * @param items
     */
    public void setItems(VKList<VKApiMessage> items) {
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
    public void addItems(VKList<VKApiMessage> items) {
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
                view = new DialogCellView(parent.getContext());
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

            DialogCellView view = (DialogCellView) vh.itemView;
            VKApiMessage msg = mItems.get(position);

            view.setGravity(msg.out ? Gravity.RIGHT : Gravity.LEFT);

            view.setText(msg.body);

            String photo_url = null;

            if ( msg.attachments.size() > 0 ) {
                for (VKAttachments.VKApiAttachment attachment : msg.attachments) {
                    if ( attachment instanceof VKApiPhoto) {
                        VKApiPhoto photo = (VKApiPhoto)attachment;
                        //todo: view.setImageSize(photo.width, photo.height);

                        photo_url = photo.src.getImageForDimension(AndroidUtilities.dp(100), AndroidUtilities.dp(100));
                        break;
                    }
                }
            }

            if ( photo_url != null ) {
                view.getImageView().setVisibility(View.VISIBLE);

                this.mImageLoader.load(photo_url)
                        .placeholder(R.drawable.ic_image)
                        .into(view.getImageView());
            }
            else {
                view.getImageView().setVisibility(View.GONE);
            }
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
     * Returns object at specified position
     * @param position index
     * @return VKApiMessage object
     */
    public VKApiMessage getItem(int position) {
        if ( position >= 0 && position < mItems.size() ) {
            return mItems.get(position);
        }
        return null;
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
        return totalCount != 0 ? totalCount : mItems.getCount();
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

        public ItemHolder(final View itemView) {
            super(itemView);

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
