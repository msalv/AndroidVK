package org.kirillius.friendslist.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kirillius.friendslist.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill on 25.01.2016.
 */
public abstract class EndlessScrollAdapter<E> extends RecyclerView.Adapter {

    private static final int NO_COUNT = -1;

    public static final int ITEM_VIEW_TYPE = 1;
    public static final int PROGRESS_VIEW_TYPE = 2;
    public static final int ERROR_VIEW_TYPE = 3;

    protected List<E> mItems = new ArrayList<>();
    protected boolean mIsLoading = false;
    protected boolean mHasError = false;

    protected int totalCount = NO_COUNT;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    private static OnItemClickListener clickListener;
    private static OnItemClickListener errorClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }
    public void setOnErrorClickListener(OnItemClickListener listener) {
        errorClickListener = listener;
    }

    /**
     * Sets new collection of items
     * @param items
     */
    public void setItems(List<E> items) {
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
    public void addItems(List<E> items) {
        int position = mItems.size();
        mItems.addAll(items);
        notifyItemRangeInserted(position, items.size());
    }

    /**
     * Adds a message to the begging of the list
     * @param object
     */
    public void prependItem(E object) {
        mItems.add(0, object);
        notifyItemInserted(0);
    }

    @Override
    public EndlessScrollAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Holder holder = null;

        switch (viewType) {
            case PROGRESS_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_spinner, parent, false);
                holder = new Holder(view);
                break;

            case ERROR_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_error_item, parent, false);
                holder = new ErrorHolder(view);
                break;
        }

        return holder;
    }

    @Override
    public int getItemCount() {
        int size = mItems.size();
        return !(mIsLoading || mHasError) ? size : (size + 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (mHasError && mItems.size() == position) {
            return ERROR_VIEW_TYPE;
        }
        return mIsLoading && mItems.size() == position ? PROGRESS_VIEW_TYPE : ITEM_VIEW_TYPE;
    }

    /**
     * Returns object at specified position
     * @param position index
     * @return object
     */
    public E getItem(int position) {
        if ( position >= 0 && position < mItems.size() ) {
            return mItems.get(position);
        }
        return null;
    }

    /**
     * Returns total number of items
     * @return count
     */
    public int getTotalCount() {
        return totalCount != NO_COUNT ? totalCount : mItems.size();
    }

    /**
     * Sets total number of messages
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * Sets loading state
     * @param loading
     */
    public void setIsLoading(boolean loading) {
        if (mIsLoading == loading) {
            return;
        }

        mIsLoading = loading;

        if (mIsLoading) {
            if (mHasError) {
                mHasError = false;
                notifyItemRemoved( mItems.size() );
            }
            notifyItemInserted( mItems.size() );
        }
        else {
            notifyItemRemoved( mItems.size() );
        }
    }

    /**
     * Returns current loading state
     * @return boolean
     */
    public boolean isLoading() {
        return mIsLoading;
    }

    /**
     * Returns whether we got a problem or not
     * @return
     */
    public boolean hasError() {
        return mHasError;
    }

    /**
     * Triggers adapter to show error view
     */
    public void onError() {
        if (mHasError) {
            return;
        }

        mHasError = true;

        notifyItemInserted( mItems.size() );
    }

    /**
     * Basic view holder
     */
    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    /**
     * Holder for an error view
     */
    public static class ErrorHolder extends Holder {
        public ErrorHolder(View itemView) {
            super(itemView);

            View loadMoreBtn = itemView.findViewById(R.id.load_more);

            if ( errorClickListener != null ) {
                loadMoreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        errorClickListener.onItemClick(v, getLayoutPosition());
                    }
                });
            }
        }
    }

    /**
     * View holder for an list item with click listener
     */
    public static class ItemHolder extends Holder {

        public ItemHolder(View itemView) {
            super(itemView);

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onItemClick(v, getLayoutPosition());
                    }
                });
            }
        }
    }
}
