package org.kirillius.friendslist.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

/**
 * Created by Kirill on 09.12.2015.
 */
public class AppLoader extends Application {

    private static Context appContext;

    VKAccessTokenTracker tokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Intent intent = new Intent("org.kirillius.friendslist.ACCESS_TOKEN_INVALID");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                Log.e("friends", "VKAccessToken is invalid");
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        tokenTracker.startTracking();
        VKSdk.initialize(this);
        appContext = this.getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }

}
