package org.kirillius.friendslist.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import org.kirillius.friendslist.R;
import org.kirillius.friendslist.core.AppLoader;
import org.kirillius.friendslist.core.OnNavigationListener;
import org.kirillius.friendslist.ui.adapters.FriendsAdapter;

public class FriendsFragment extends Fragment {

    public static final String TAG = "FriendsFragment";
    private static final int FRIENDS_COUNT = 1000;
    private static final String REQUEST_FIELDS = "online,last_seen,photo_50,photo_100,photo_200,photo_400";

    private LinearLayoutManager mLayoutManager;
    private FriendsAdapter mAdapter;

    private VKRequest mCurrentRequest;
    private Toast mCurrentToast;
    private Picasso mPicasso;
    private OnNavigationListener mOnNavigationListener;

    private View mLoadingView;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of
     * this fragment using the provided arguments.
     *
     * @param args Arguments
     * @return A new instance of fragment FriendsFragment.
     */
    public static FriendsFragment newInstance(Bundle args) {
        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle(null);
        actionBar.setDisplayHomeAsUpEnabled(false);

        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mLoadingView = rootView.findViewById(R.id.loading_view);

        RecyclerView friendsListView = (RecyclerView) rootView.findViewById(R.id.friends_list);
        friendsListView.setHasFixedSize(true);

        mPicasso = new Picasso.Builder(getActivity()).build();

        mAdapter = new FriendsAdapter();
        mAdapter.setImageLoader(mPicasso);

        mAdapter.setOnItemClickListener(new FriendsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                VKApiUserFull user = mAdapter.getItem(position);

                if ( user == null ) {
                    return;
                }

                DialogFragment fragment = DialogFragment.newInstance(user);
                mOnNavigationListener.OnNavigatedTo(fragment, DialogFragment.TAG);
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());

        friendsListView.setLayoutManager(mLayoutManager);
        friendsListView.setAdapter(mAdapter);

        friendsListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {

                    if (mAdapter.isLoading() || mAdapter.hasError()) {
                        return;
                    }

                    if (mAdapter.getItemCount() >= mAdapter.getTotalCount()) {
                        return;
                    }

                    int onScreen = mLayoutManager.getChildCount();
                    int scrolled = mLayoutManager.findFirstVisibleItemPosition();
                    int border = (int) Math.floor((mAdapter.getItemCount() - onScreen) * 0.75);

                    // fetch more when scroll over 75% of already shown items
                    if (scrolled >= border) {

                        if (mCurrentRequest == null) {
                            fetchMoreFriends();
                        }

                    }
                }
            }
        });

        fetchFriends();

        return rootView;
    }

    /**
     * Fetches friends list of the current user
     */
    private void fetchFriends() {
        mCurrentRequest = VKApi.friends().get(VKParameters.from(
                "order", "hints",
                "fields", REQUEST_FIELDS,
                "offset", 0,
                "count", FRIENDS_COUNT
        ));

        mLoadingView.setVisibility(View.VISIBLE);

        mCurrentRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                mLoadingView.setVisibility(View.GONE);

                if (response.parsedModel instanceof VKList) {
                    updateFriendsList((VKList<VKApiUserFull>) response.parsedModel);
                } else {
                    showError(null);
                }
            }

            @Override
            public void onError(VKError error) {
                mLoadingView.setVisibility(View.GONE);
                showError(error);
            }
        });
    }

    /**
     * Fetches more friends of the current user
     */
    private void fetchMoreFriends() {
        mCurrentRequest = VKApi.friends().get(VKParameters.from(
                "order", "hints",
                "fields", REQUEST_FIELDS,
                "offset", mAdapter.getItemCount(),
                "count", FRIENDS_COUNT
        ));

        mAdapter.setIsLoading(true);

        mCurrentRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                mAdapter.setIsLoading(false);
                if (response.parsedModel instanceof VKList) {
                    appendToFriendsList((VKList<VKApiUserFull>) response.parsedModel);
                } else {
                    showError(null);
                }
            }

            @Override
            public void onError(VKError error) {
                mAdapter.setIsLoading(false);
                showError(error);
            }
        });
    }

    /**
     * Updates friends list with new data
     * @param items List of friends
     */
    private void updateFriendsList(VKList<VKApiUserFull> items) {
        mCurrentRequest = null;
        mAdapter.setItems(items);
        mAdapter.setTotalCount(items.getCount());
    }

    /**
     * Adds more friends to the list
     * @param items List of friends
     */
    private void appendToFriendsList(VKList<VKApiUserFull> items) {
        mCurrentRequest = null;
        mAdapter.addItems(items);
        mAdapter.setTotalCount(items.getCount());
    }

    /**
     * Shows error if something went wrong during API request
     * @param error VKError
     */
    private void showError(VKError error) {
        mCurrentRequest = null;

        if (error != null) {
            Log.e(TAG, error.toString());

            if ( error.errorCode != VKError.VK_CANCELED ) {
                if ( mCurrentToast != null ) {
                    mCurrentToast.cancel();
                }
                mCurrentToast = Toast.makeText(AppLoader.getAppContext(), R.string.request_error, Toast.LENGTH_SHORT);
                mCurrentToast.show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnNavigationListener) {
            mOnNavigationListener = (OnNavigationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNavigationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnNavigationListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCurrentRequest != null) {
            mCurrentRequest.cancel();
        }
        mCurrentRequest = null;
    }

    @Override
    public void onDestroyView() {
        mAdapter = null;
        mLayoutManager = null;

        if ( mPicasso != null ) {
            mPicasso.shutdown();
        }
        mPicasso = null;

        if ( mCurrentToast != null ) {
            mCurrentToast.cancel();
        }
        mCurrentToast = null;

        super.onDestroyView();
    }
}
