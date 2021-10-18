package com.github.panpf.sketch.sample.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.View;

public class DeviceUtils {
    private static final String ATTR_NAME_STATUS_BAR_HEIGHT = "status_bar_height";
    private static final String ATTR_NAME_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String ATTR_NAME_NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";

    public static int getStatusBarHeight(Resources resources) {
        return getInternalDimensionSize(resources, ATTR_NAME_STATUS_BAR_HEIGHT);
    }

    public static int getNavigationBarHeight(Resources resources) {
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return getInternalDimensionSize(resources, ATTR_NAME_NAVIGATION_BAR_HEIGHT);
        } else if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return getInternalDimensionSize(resources, ATTR_NAME_NAVIGATION_BAR_HEIGHT_LANDSCAPE);
        } else {
            return getInternalDimensionSize(resources, ATTR_NAME_NAVIGATION_BAR_HEIGHT);
        }
    }

    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @SuppressLint("LongLogTag")
    public static int getWindowHeightSupplement(Activity activity) {
        int uiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        int navigationBarHeight = getNavigationBarHeight(activity.getResources());

        String uiVisibilityName = null;
        switch (uiVisibility) {
            case View.SYSTEM_UI_FLAG_FULLSCREEN:
                uiVisibilityName = "SYSTEM_UI_FLAG_FULLSCREEN";
                break;
            case View.SYSTEM_UI_FLAG_HIDE_NAVIGATION:
                uiVisibilityName = "SYSTEM_UI_FLAG_HIDE_NAVIGATION";
                break;
            case View.SYSTEM_UI_FLAG_IMMERSIVE:
                uiVisibilityName = "SYSTEM_UI_FLAG_IMMERSIVE";
                break;
            case View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY:
                uiVisibilityName = "SYSTEM_UI_FLAG_IMMERSIVE_STICKY";
                break;
            case View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN:
                uiVisibilityName = "SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN";
                break;
            case View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION:
                uiVisibilityName = "SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION";
                break;
            case View.SYSTEM_UI_FLAG_LAYOUT_STABLE:
                uiVisibilityName = "SYSTEM_UI_FLAG_LAYOUT_STABLE";
                break;
            case View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR:
                uiVisibilityName = "SYSTEM_UI_FLAG_LIGHT_STATUS_BAR";
                break;
            case View.SYSTEM_UI_FLAG_LOW_PROFILE:
                uiVisibilityName = "SYSTEM_UI_FLAG_LOW_PROFILE";
                break;
            case View.SYSTEM_UI_FLAG_VISIBLE:
                uiVisibilityName = "SYSTEM_UI_FLAG_VISIBLE";
                break;
        }

        Log.d("getWindowHeightSupplement", Build.BRAND + " " + Build.MODEL + ". uiVisibilityName: " + uiVisibilityName + ", navigationBarHeight: " + navigationBarHeight);

        if (uiVisibility == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) {
            return navigationBarHeight;
        }
        return 0;
    }
}
