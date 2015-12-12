package org.kirillius.friendslist.core;

/**
 * Created by Kirill on 12.12.2015.
 */
public class AndroidUtilities {

    public static float density = 1;

    static {
        density = AppLoader.getAppContext().getResources().getDisplayMetrics().density;
    }

    public static int dp(float value) {
        return (int)Math.ceil(density * value);
    }

}