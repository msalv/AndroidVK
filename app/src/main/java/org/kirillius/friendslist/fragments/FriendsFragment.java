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

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import org.kirillius.friendslist.R;
import org.kirillius.friendslist.ui.FriendsAdapter;

public class FriendsFragment extends Fragment {

    public static final String TAG = "FriendsFragment";

    private RecyclerView mFriendsList;
    private FriendsAdapter mAdapter;

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

        mAdapter = new FriendsAdapter( getActivity() );

        mFriendsList.setLayoutManager(new LinearLayoutManager( getActivity() ));
        mFriendsList.setAdapter(mAdapter);

        fetchFriends();

        return rootView;
    }

    /**
     * Fetches friends list of the current user
     */
    private void fetchFriends() {
        VKRequest request = VKApi.friends().get(VKParameters.from(
                "order", "hints",
                "fields", "online,photo_50,photo_100,photo_200,photo_400"
        ));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                if (response.parsedModel instanceof VKList) {
                    mAdapter.setItems((VKList<VKApiUserFull>) response.parsedModel);
                }
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getActivity(), "API error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, error.toString());
            }
        });

    }
}
