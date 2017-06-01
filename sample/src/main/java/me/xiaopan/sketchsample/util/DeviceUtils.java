package me.xiaopan.sketchsample.util;

import android.content.res.Configuration;
import android.content.res.Resources;

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

}
