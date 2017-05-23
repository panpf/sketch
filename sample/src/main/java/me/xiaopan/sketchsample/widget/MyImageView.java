package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.util.AttributeSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.event.AppConfigChangedEvent;
import me.xiaopan.sketchsample.util.AppConfig;

public class MyImageView extends SketchImageView {
    private boolean useInList;    // 用于列表
    private boolean disabledRedisplay;

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            EventBus.getDefault().register(this);
        } catch (EventBusException e) {
            e.printStackTrace();
        }

        if (!isInEditMode()) {
            disabledRedisplay = true;
            onEvent(new AppConfigChangedEvent(AppConfig.Key.SHOW_GIF_FLAG));
            onEvent(new AppConfigChangedEvent(AppConfig.Key.SHOW_IMAGE_FROM_FLAG));
            onEvent(new AppConfigChangedEvent(AppConfig.Key.CLICK_SHOW_PRESSED_STATUS));
            onEvent(new AppConfigChangedEvent(AppConfig.Key.SHOW_IMAGE_DOWNLOAD_PROGRESS));
            onEvent(new AppConfigChangedEvent(AppConfig.Key.CLICK_RETRY_ON_PAUSE_DOWNLOAD));
            onEvent(new AppConfigChangedEvent(AppConfig.Key.CLICK_RETRY_ON_FAILED));
            disabledRedisplay = false;
        }
    }

    @Override
    public void onReadyDisplay(UriScheme uriScheme) {
        super.onReadyDisplay(uriScheme);

        disabledRedisplay = true;
        onEvent(new AppConfigChangedEvent(AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.PLAY_GIF_ON_LIST));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.THUMBNAIL_MODE));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.CACHE_PROCESSED_IMAGE));
        disabledRedisplay = false;
    }

    @SuppressWarnings("unused")
    public boolean isUseInList() {
        return useInList;
    }

    public void setUseInList(boolean useInList) {
        this.useInList = useInList;
    }

    @Override
    public boolean redisplay() {
        return !disabledRedisplay && super.redisplay();
    }

    @Subscribe
    public void onEvent(AppConfigChangedEvent event) {
        if (AppConfig.Key.SHOW_GIF_FLAG.equals(event.key)) {
            if (useInList) {
                boolean showGifFlag = AppConfig.getBoolean(getContext(), AppConfig.Key.SHOW_GIF_FLAG);
                setShowGifFlag(showGifFlag ? getResources().getDrawable(R.drawable.ic_gif) : null);
            }
        } else if (AppConfig.Key.SHOW_IMAGE_FROM_FLAG.equals(event.key)) {
            setShowImageFrom(AppConfig.getBoolean(getContext(), AppConfig.Key.SHOW_IMAGE_FROM_FLAG));
        } else if (AppConfig.Key.CLICK_SHOW_PRESSED_STATUS.equals(event.key)) {
            if (useInList) {
                setShowPressedStatus(AppConfig.getBoolean(getContext(), AppConfig.Key.CLICK_SHOW_PRESSED_STATUS));
            }
        } else if (AppConfig.Key.SHOW_IMAGE_DOWNLOAD_PROGRESS.equals(event.key)) {
            if (useInList) {
                setShowDownloadProgress(AppConfig.getBoolean(getContext(), AppConfig.Key.SHOW_IMAGE_DOWNLOAD_PROGRESS));
            }
        } else if (AppConfig.Key.CLICK_RETRY_ON_PAUSE_DOWNLOAD.equals(event.key)) {
            if (useInList) {
                setClickRetryOnPauseDownload(AppConfig.getBoolean(getContext(), AppConfig.Key.CLICK_RETRY_ON_PAUSE_DOWNLOAD));
            }
        } else if (AppConfig.Key.CLICK_RETRY_ON_FAILED.equals(event.key)) {
            if (useInList) {
                setClickRetryOnError(AppConfig.getBoolean(getContext(), AppConfig.Key.CLICK_RETRY_ON_FAILED));
            }


        } else if (AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD.equals(event.key)) {
            redisplay();
        } else if (AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK.equals(event.key)) {
            redisplay();
        } else if (AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL.equals(event.key)) {
            redisplay();
        } else if (AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY.equals(event.key)) {
            redisplay();


        } else if (AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION.equals(event.key)) {
            boolean correctImageOrientationDisabled = AppConfig.getBoolean(getContext(), AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION);
            if (correctImageOrientationDisabled != getOptions().isCorrectImageOrientationDisabled()) {
                getOptions().setCorrectImageOrientationDisabled(correctImageOrientationDisabled);
                redisplay();
            }
        } else if (AppConfig.Key.PLAY_GIF_ON_LIST.equals(event.key)) {
            if (useInList) {
                boolean playGifOnList = AppConfig.getBoolean(getContext(), AppConfig.Key.PLAY_GIF_ON_LIST);
                if (playGifOnList != getOptions().isDecodeGifImage()) {
                    getOptions().setDecodeGifImage(playGifOnList);
                    redisplay();
                }
            }
        } else if (AppConfig.Key.THUMBNAIL_MODE.equals(event.key)) {
            if (useInList) {
                boolean thumbnailMode = AppConfig.getBoolean(getContext(), AppConfig.Key.THUMBNAIL_MODE);
                if (thumbnailMode != getOptions().isThumbnailMode()) {
                    getOptions().setThumbnailMode(thumbnailMode);
                    if (thumbnailMode) {
                        if (getOptions().getResize() == null && !getOptions().isResizeByFixedSize()) {
                            getOptions().setResizeByFixedSize(true);
                        }
                    } else {
                        getOptions().setResizeByFixedSize(false);
                    }
                    redisplay();
                }
            }
        } else if (AppConfig.Key.CACHE_PROCESSED_IMAGE.equals(event.key)) {
            boolean cacheProcessedImageInDisk = AppConfig.getBoolean(getContext(), AppConfig.Key.CACHE_PROCESSED_IMAGE);
            if (cacheProcessedImageInDisk != getOptions().isCacheProcessedImageInDisk()) {
                getOptions().setCacheProcessedImageInDisk(cacheProcessedImageInDisk);
                redisplay();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
    }
}
