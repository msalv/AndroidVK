package org.kirillius.friendslist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.kirillius.friendslist.core.AppLoader;
import org.kirillius.friendslist.core.OnNavigationListener;
import org.kirillius.friendslist.fragments.LoginErrorFragment;
import org.kirillius.friendslist.fragments.FriendsFragment;

public class MainActivity extends AppCompatActivity implements LoginErrorFragment.OnLoginAttemptListener,
        OnNavigationListener {

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ( "org.kirillius.friendslist.ACCESS_TOKEN_INVALID".equals(intent.getAction()) ) {
                login();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        this.login();
    }

    /**
     * Logs into VK or shows friends if already logged in
     */
    private void login() {
        if (!VKSdk.isLoggedIn()) {
            VKSdk.login(this, "friends,offline");
        }
        else {
            showFriends();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                showFriends();
            }

            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                showLoginError();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Shows friends list
     */
    private void showFriends() {
        replaceFragmentWith(new FriendsFragment(), FriendsFragment.TAG);
    }

    /**
     * Shows login error fragment
     */
    private void showLoginError() {
        replaceFragmentWith(new LoginErrorFragment(), LoginErrorFragment.TAG);
        Toast.makeText(AppLoader.getAppContext(), R.string.auth_error, Toast.LENGTH_LONG).show();
    }

    /**
     * Replaces current fragment with a specified one
     * @param fragment Fragment instance
     * @param tag String tag
     */
    private void replaceFragmentWith(Fragment fragment, String tag) {
        showFragment(fragment, tag, false);
    }

    /**
     * Shows a fragment. Allows to add specified fragment to the back stack
     * @param fragment Fragment instance
     * @param tag String tag
     * @param addToBackStack Boolean flag whether to remember transaction's state or not
     */
    private void showFragment(Fragment fragment, String tag, boolean addToBackStack) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragments_container, fragment, tag);

        if ( addToBackStack ) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }

    @Override
    public void OnNavigatedTo(Fragment fragment, String tag) {
        showFragment(fragment, tag, true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLoginAttempt() {
        this.login();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(AppLoader.getAppContext()).registerReceiver(
                mReceiver,
                new IntentFilter("org.kirillius.friendslist.ACCESS_TOKEN_INVALID")
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(AppLoader.getAppContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        if ( getFragmentManager().getBackStackEntryCount() > 0 ) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }
}
