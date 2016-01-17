package org.kirillius.friendslist.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.kirillius.friendslist.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLoginAttemptListener} interface
 * to handle interaction events.
 */
public class LoginErrorFragment extends Fragment {

    public static final String TAG = "LoginErrorFragment";
    private OnLoginAttemptListener mListener;

    public LoginErrorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_auth_error, container, false);

        TextView tryAgainText = (TextView)rootView.findViewById(R.id.sign_in_text);

        tryAgainText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onLoginAttempt();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnLoginAttemptListener) {
            mListener = (OnLoginAttemptListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginAttemptListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLoginAttemptListener {
        void onLoginAttempt();
    }
}
