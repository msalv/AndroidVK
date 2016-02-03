package org.kirillius.friendslist.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vk.sdk.VKSdk;

import org.kirillius.friendslist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public static final String TAG = "LoginFragment";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle(null);

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        TextView tryAgainText = (TextView)rootView.findViewById(R.id.sign_in_text);

        tryAgainText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        return rootView;
    }

    /**
     * Starts VK SDK login activity
     */
    private void login() {
        VKSdk.login(getActivity(), "friends,messages,offline");
    }
}
