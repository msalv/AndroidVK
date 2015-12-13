package org.kirillius.friendslist.fragments;

import android.os.Bundle;
import android.app.Fragment;
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
import org.kirillius.friendslist.ui.FriendsAdapter;

public class FriendsFragment extends Fragment {

    public static final String TAG = "FriendsFragment";
    private static final int FRIENDS_COUNT = 1000;
    private static final String REQUEST_FIELDS = "online,photo_50,photo_100,photo_200,photo_400";

    private LinearLayoutManager mLayoutManager;
    private RecyclerView mFriendsList;
    private FriendsAdapter mAdapter;

    private VKRequest mCurrentRequest;
    private Picasso mPicasso;

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

        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) rootView.findViewById(R.id.friends_list);
        mFriendsList.setHasFixedSize(true);

        mPicasso = new Picasso.Builder(getActivity()).build();

        mAdapter = new FriendsAdapter();
        mAdapter.setImageLoader(mPicasso);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mFriendsList.setLayoutManager(mLayoutManager);
        mFriendsList.setAdapter(mAdapter);

        mFriendsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {

                    if (mAdapter.getItemCount() >= mAdapter.getTotalCount()) {
                        return;
                    }

                    int onScreen = mLayoutManager.getChildCount();
                    int scrolled = mLayoutManager.findFirstVisibleItemPosition();
                    int border = (int)Math.floor((mAdapter.getItemCount() - onScreen) * 0.75);

                    // fetch more when scroll over 75% of already shown items
                    if (scrolled >= border) {

                        if( mCurrentRequest == null) {
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

        mCurrentRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                if (response.parsedModel instanceof VKList) {
                    updateFriendsList((VKList<VKApiUserFull>) response.parsedModel);
                } else {
                    showError(null);
                }
            }

            @Override
            public void onError(VKError error) {
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

        mCurrentRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                if (response.parsedModel instanceof VKList) {
                    appendToFriendsList((VKList<VKApiUserFull>) response.parsedModel);
                } else {
                    showError(null);
                }
            }

            @Override
            public void onError(VKError error) {
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
    }

    /**
     * Adds more friends to the list
     * @param items List of friends
     */
    private void appendToFriendsList(VKList<VKApiUserFull> items) {
        mCurrentRequest = null;
        mAdapter.addItems(items);
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
                // todo: show more detailed info in a convenient way
                Toast.makeText(AppLoader.getAppContext(), "Error during request", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCurrentRequest != null) {
            mCurrentRequest.cancel();
        }
    }

    @Override
    public void onDestroyView() {
        mFriendsList = null;
        mAdapter = null;
        mLayoutManager = null;

        if ( mPicasso != null ) {
            mPicasso.shutdown();
        }
        mPicasso = null;

        super.onDestroyView();
    }
}
