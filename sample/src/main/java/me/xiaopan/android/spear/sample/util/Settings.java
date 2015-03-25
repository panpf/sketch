package me.xiaopan.android.spear.sample.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    private static final String PREFERENCE_SCROLLING_PAUSE_LOAD_NEW_IMAGE = "PREFERENCE_SCROLLING_PAUSE_LOAD_NEW_IMAGE";
    private static final String PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS = "PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS";
    private static final String PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD_NEW_IMAGE = "PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD_NEW_IMAGE";

    private static Settings settingsInstance;

    private boolean scrollingPauseLoadNewImage;
    private boolean showImageDownloadProgress;
    private boolean mobileNetworkPauseDownloadNewImage;

    private SharedPreferences.Editor editor;

    private Settings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = preferences.edit();

        this.scrollingPauseLoadNewImage = preferences.getBoolean(PREFERENCE_SCROLLING_PAUSE_LOAD_NEW_IMAGE, false);
        this.showImageDownloadProgress = preferences.getBoolean(PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS, false);
        this.mobileNetworkPauseDownloadNewImage = preferences.getBoolean(PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD_NEW_IMAGE, false);
    }

    public static Settings with(Context context){
        if(settingsInstance == null){
            synchronized (Settings.class){
                if(settingsInstance == null){
                    settingsInstance = new Settings(context);
                }
            }
        }
        return settingsInstance;
    }

    /**
     * 滚动的时候是否暂停加载新图片
     * @return 滚动的时候是否暂停加载新图片
     */
    public boolean isScrollingPauseLoadNewImage(){
        return scrollingPauseLoadNewImage;
    }

    /**
     * 设置滚动的时候是否暂停加载新图片
     * @param scrollingPauseLoadNewImage 滚动的时候是否暂停加载新图片
     */
    public void setScrollingPauseLoadNewImage(boolean scrollingPauseLoadNewImage){
        this.scrollingPauseLoadNewImage = scrollingPauseLoadNewImage;
        editor.putBoolean(PREFERENCE_SCROLLING_PAUSE_LOAD_NEW_IMAGE, scrollingPauseLoadNewImage).apply();
    }

    /**
     * 是否显示图片下载进度
     * @return 是否显示图片下载进度
     */
    public boolean isShowImageDownloadProgress() {
        return showImageDownloadProgress;
    }

    /**
     * 设置是否显示图片下载进度
     * @param showImageDownloadProgress 是否显示图片下载进度
     */
    public void setShowImageDownloadProgress(boolean showImageDownloadProgress) {
        this.showImageDownloadProgress = showImageDownloadProgress;
        editor.putBoolean(PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS, showImageDownloadProgress).apply();
    }

    /**
     * 移动网络下是否暂停下载新图片
     * @return 移动网络下是否暂停下载新图片
     */
    public boolean isMobileNetworkPauseDownloadNewImage() {
        return mobileNetworkPauseDownloadNewImage;
    }

    /**
     * 设置移动网络下是否暂停下载新图片
     * @param mobileNetworkPauseDownloadNewImage 移动网络下是否暂停下载新图片
     */
    public void setMobileNetworkPauseDownloadNewImage(boolean mobileNetworkPauseDownloadNewImage) {
        this.mobileNetworkPauseDownloadNewImage = mobileNetworkPauseDownloadNewImage;
        editor.putBoolean(PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD_NEW_IMAGE, mobileNetworkPauseDownloadNewImage).apply();
    }
}
