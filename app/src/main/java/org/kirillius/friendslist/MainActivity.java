package org.kirillius.friendslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import org.kirillius.friendslist.ui.FriendsAdapter;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mFriendsList;
    private FriendsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.app_name);

        mFriendsList = (RecyclerView) findViewById(R.id.friends_list);
        mFriendsList.setHasFixedSize(true);

        mAdapter = new FriendsAdapter();

        mFriendsList.setLayoutManager(new LinearLayoutManager(this));
        mFriendsList.setAdapter(mAdapter);

        if (!VKSdk.isLoggedIn()) {
            VKSdk.login(this, "friends,offline");
        }
        else {
            fetchFriends();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                fetchFriends();
            }

            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Toast.makeText(getApplicationContext(), "Authorization error", Toast.LENGTH_LONG).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                    mAdapter.setItems((VKList<VKApiUserFull>)response.parsedModel);
                }
            }
            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "API error", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
