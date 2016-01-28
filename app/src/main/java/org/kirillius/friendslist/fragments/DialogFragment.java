package org.kirillius.friendslist.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiGetMessagesResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import org.kirillius.friendslist.R;
import org.kirillius.friendslist.core.AppLoader;
import org.kirillius.friendslist.ui.ErrorView;
import org.kirillius.friendslist.ui.adapters.EndlessScrollAdapter;
import org.kirillius.friendslist.ui.adapters.MessagesAdapter;
import org.kirillius.friendslist.ui.ReplyEditText;

public class DialogFragment extends Fragment {
    public static String TAG = "DialogFragment";
    private static final String ARG_FRIEND = "ARG_FRIEND";
    private static final int MESSAGES_COUNT = 30;

    private VKApiUserFull mFriend;
    private ReplyEditText mInputField;

    private LinearLayoutManager mLayoutManager;
    private MessagesAdapter mAdapter;

    private VKRequest mCurrentRequest;
    private Toast mCurrentToast;
    private Picasso mPicasso;

    private View mLoadingView;
    private ErrorView mErrorView;

    public DialogFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of this fragment using the provided parameters.
     *
     * @param friend A friend object.
     * @return A new instance of fragment DialogFragment.
     */
    public static DialogFragment newInstance(VKApiUserFull friend) {
        DialogFragment fragment = new DialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_FRIEND, friend);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFriend = getArguments().getParcelable(ARG_FRIEND);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        StringBuilder sb = new StringBuilder();
        sb.append(mFriend.first_name).append(' ').append(mFriend.last_name);
        actionBar.setTitle(sb);

        if ( mFriend.online ) {
            actionBar.setSubtitle(R.string.online);
        }
        else if (mFriend.last_seen == 0) {
            actionBar.setSubtitle(R.string.offline);
        }
        else {
            CharSequence last_seen = getString(R.string.just_now);
            long now = System.currentTimeMillis();

            if ( now - mFriend.last_seen * 1000 > 60000 ) {
                last_seen = DateUtils.getRelativeTimeSpanString(mFriend.last_seen * 1000, now, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
            }

            int string_id = (mFriend.sex == VKApiUserFull.Sex.MALE) ? R.string.last_seen : R.string.last_seen_female;
            actionBar.setSubtitle(getString(string_id, last_seen));
        }

        actionBar.setDisplayHomeAsUpEnabled(true);

        View rootView = inflater.inflate(R.layout.fragment_dialog, container, false);

        mLoadingView = rootView.findViewById(R.id.loading_view);
        mErrorView = (ErrorView)rootView.findViewById(R.id.error_view);

        RecyclerView messagesListView = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mPicasso = new Picasso.Builder(getActivity()).build();

        mAdapter = new MessagesAdapter();
        mAdapter.setImageLoader(mPicasso);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);

        messagesListView.setLayoutManager(mLayoutManager);
        messagesListView.setAdapter(mAdapter);

        messagesListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mAdapter.isLoading() || mAdapter.hasError()) {
                    return;
                }

                if (dy < 0) {
                    if (mAdapter.getItemCount() >= mAdapter.getTotalCount()) {
                        return;
                    }

                    int onScreen = mLayoutManager.getChildCount();
                    int scrolled = mLayoutManager.findFirstVisibleItemPosition();
                    int border = (int) Math.floor((mAdapter.getItemCount() - onScreen) * 0.75);

                    // fetch more when scroll over 75% of already shown items
                    if (scrolled >= border) {

                        if (mCurrentRequest == null) {
                            fetchMoreMessages();
                        }
                    }
                }
            }
        });

        mAdapter.setOnErrorClickListener(new EndlessScrollAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                fetchMoreMessages();
            }
        });

        mInputField = (ReplyEditText) rootView.findViewById(R.id.input_field);
        mInputField.setOnSendClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mInputField.getText().trim();
                if ( text.length() > 0 ) {
                    sendMessage(text);
                }
            }
        });

        mErrorView.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMessages();
            }
        });

        fetchMessages();

        return rootView;
    }

    /**
     * Fetches messages associated with current dialog
     */
    private void fetchMessages() {
        mCurrentRequest = new VKRequest("messages.getHistory", VKParameters.from(
                "peer_id", mFriend.id,
                "offset", 0,
                "count", MESSAGES_COUNT
        ), VKApiGetMessagesResponse.class);

        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);

        mCurrentRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                mLoadingView.setVisibility(View.GONE);

                if (response.parsedModel instanceof VKApiGetMessagesResponse) {
                    VKApiGetMessagesResponse data = (VKApiGetMessagesResponse) response.parsedModel;
                    updateMessagesList(data.items, data.count);
                } else {
                    mErrorView.setVisibility(View.VISIBLE);
                    showError(null);
                }
            }

            @Override
            public void onError(VKError error) {
                mLoadingView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.VISIBLE);
                showError(error);
            }
        });
    }

    /**
     * Updates messages list with new items
     * @param items List of messages
     * @param total Total count of messages
     */
    private void updateMessagesList(VKList<VKApiMessage> items, int total) {
        mCurrentRequest = null;
        mAdapter.setTotalCount(total);
        mAdapter.setItems(items);
    }

    /**
     * Fetches more messages from the current dialog
     */
    private void fetchMoreMessages() {
        mCurrentRequest = new VKRequest("messages.getHistory", VKParameters.from(
                "peer_id", mFriend.id,
                "offset", mAdapter.getItemCount(),
                "count", MESSAGES_COUNT
        ), VKApiGetMessagesResponse.class);

        mAdapter.setIsLoading(true);

        mCurrentRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                mAdapter.setIsLoading(false);

                if (response.parsedModel instanceof VKApiGetMessagesResponse) {
                    VKApiGetMessagesResponse data = (VKApiGetMessagesResponse) response.parsedModel;
                    appendToMessagesList(data.items, data.count);
                } else {
                    mAdapter.onError();
                    showError(null);
                }
            }

            @Override
            public void onError(VKError error) {
                mAdapter.setIsLoading(false);
                mAdapter.onError();
                showError(error);
            }
        });
    }

    /**
     * Appends messages to the list
     * @param items List of messages
     * @param total Total count of messages
     */
    private void appendToMessagesList(VKList<VKApiMessage> items, int total) {
        mCurrentRequest = null;
        mAdapter.setTotalCount(total);
        mAdapter.addItems(items);
    }

    /**
     * Sends message
     * @param text
     */
    private void sendMessage(final String text) {
        mCurrentRequest = new VKRequest("messages.send", VKParameters.from(
                "user_id", mFriend.id,
                "message", text
        ));

        mInputField.clearInput();

        mCurrentRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                mCurrentRequest = null;
                prependMessage(text);
            }

            @Override
            public void onError(VKError error) {
                showError(error);
            }
        });
    }

    /**
     * Prepends outgoing message to the list
     * @param text Body of the message
     */
    private void prependMessage(String text) {
        VKApiMessage msg = new VKApiMessage();

        msg.body = text;
        msg.out = true;

        mAdapter.prependItem(msg);
        mLayoutManager.scrollToPosition(0);
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
    public void onDestroy() {
        super.onDestroy();

        if (mCurrentRequest != null) {
            mCurrentRequest.cancel();
        }
        mCurrentRequest = null;
    }

    @Override
    public void onDestroyView() {
        if (mCurrentRequest != null) {
            mCurrentRequest.setRequestListener(null);
        }
        mAdapter = null;
        mLayoutManager = null;

        mFriend = null;
        mInputField = null;
        mLoadingView = null;
        mErrorView = null;

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
