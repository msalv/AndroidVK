package org.kirillius.friendslist.core;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Kirill on 12.12.2015.
 */
public class AndroidUtilities {

    public static float density = 1;
    public static Point displaySize = new Point();
    public static DisplayMetrics displayMetrics = new DisplayMetrics();

    static {
        density = AppLoader.getAppContext().getResources().getDisplayMetrics().density;
        checkDisplaySize();
    }

    public static int dp(float value) {
        return (int)Math.ceil(density * value);
    }

    public static void checkDisplaySize() {
        try {
            WindowManager manager = (WindowManager) AppLoader.getAppContext().getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    if (android.os.Build.VERSION.SDK_INT < 13) {
                        displaySize.set(display.getWidth(), display.getHeight());
                    } else {
                        display.getSize(displaySize);
                    }
                }
            }
        }
        catch (Exception e) {
            Log.e("friends", e.getMessage());
        }
    }

}