package org.kirillius.friendslist.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.model.VKApiUserFull;

import org.kirillius.friendslist.R;
import org.kirillius.friendslist.ui.ReplyTextView;

public class DialogFragment extends Fragment {
    public static String TAG = "DialogFragment";
    private static final String ARG_FRIEND = "ARG_FRIEND";

    private VKApiUserFull mFriend;
    private ReplyTextView mInputField;

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

        actionBar.setTitle(mFriend.toString());

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

            actionBar.setSubtitle(getString(R.string.last_seen, last_seen));
        }

        actionBar.setDisplayHomeAsUpEnabled(true);

        View view = inflater.inflate(R.layout.fragment_dialog, container, false);

        mInputField = (ReplyTextView) view.findViewById(R.id.input_field);
        mInputField.setOnSendClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, mInputField.getText()); // todo: send message
                mInputField.clearInput();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        mFriend = null;
        super.onDestroyView();
    }
}
