package me.xiaopan.sketchsample.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.drawable.SketchLoadingDrawable;
import me.xiaopan.sketch.feature.ImageOrientationCorrector;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.event.AppConfigChangedEvent;
import me.xiaopan.sketchsample.util.AppConfig;

public class MyImageView extends SketchImageView {
    private boolean useInList;    // 用于列表
    private boolean disabledRedisplay;
    private boolean disabledLongClickShowImageInfo;

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnLongClickListener(new LongClickShowDrawableInfoListener());
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

    public boolean isDisabledLongClickShowImageInfo() {
        return disabledLongClickShowImageInfo;
    }

    public void setDisabledLongClickShowImageInfo(boolean disabledLongClickShowImageInfo) {
        this.disabledLongClickShowImageInfo = disabledLongClickShowImageInfo;
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

    private class LongClickShowDrawableInfoListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            if (disabledLongClickShowImageInfo) {
                return false;
            }

            if (v.getContext() instanceof Activity) {
                showInfo((Activity) v.getContext());
            }
            return true;
        }

        private void showInfo(Activity activity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            Drawable drawable = SketchUtils.getLastDrawable(getDrawable());

            String imageInfo;
            if (drawable instanceof SketchLoadingDrawable) {
                imageInfo = "图片正在加载，请稍后";
            } else if (drawable instanceof SketchDrawable) {
                imageInfo = makeImageInfo(drawable, (SketchDrawable) drawable);
            } else {
                imageInfo = "未知来源图片";
            }
            builder.setMessage(imageInfo);

            builder.setNegativeButton("取消", null);
            builder.show();
        }

        private String makeImageInfo(Drawable drawable, SketchDrawable sketchDrawable) {
            StringBuilder messageBuilder = new StringBuilder();

            messageBuilder.append("\n");
            messageBuilder.append(sketchDrawable.getUri());

            long imageLength = 0;
            UriScheme uriScheme = UriScheme.valueOfUri(sketchDrawable.getUri());
            if (uriScheme == UriScheme.FILE) {
                imageLength = new File(UriScheme.FILE.crop(sketchDrawable.getUri())).length();
            } else if (uriScheme == UriScheme.NET) {
                DiskCache.Entry diskCacheEntry = Sketch.with(getContext()).getConfiguration().getDiskCache().get(sketchDrawable.getUri());
                if (diskCacheEntry != null) {
                    imageLength = diskCacheEntry.getFile().length();
                }
            } else if (uriScheme == UriScheme.ASSET) {
                AssetFileDescriptor assetFileDescriptor = null;
                try {
                    assetFileDescriptor = getContext().getAssets().openFd(UriScheme.ASSET.crop(sketchDrawable.getUri()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageLength = assetFileDescriptor != null ? assetFileDescriptor.getLength() : 0;
            } else if (uriScheme == UriScheme.DRAWABLE) {
                AssetFileDescriptor assetFileDescriptor = getContext().getResources().openRawResourceFd(Integer.valueOf(UriScheme.DRAWABLE.crop(sketchDrawable.getUri())));
                imageLength = assetFileDescriptor != null ? assetFileDescriptor.getLength() : 0;
            } else if (uriScheme == UriScheme.CONTENT) {
                AssetFileDescriptor assetFileDescriptor = null;
                try {
                    assetFileDescriptor = getContext().getContentResolver().openAssetFileDescriptor(Uri.parse(sketchDrawable.getUri()), "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageLength = assetFileDescriptor != null ? assetFileDescriptor.getLength() : 0;
            }

            String needDiskSpace = imageLength > 0 ? Formatter.formatFileSize(getContext(), imageLength) : "未知";

            int previewDrawableByteCount = sketchDrawable.getByteCount();
            int pixelByteCount = previewDrawableByteCount / drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
            int originImageByteCount = sketchDrawable.getOriginWidth() * sketchDrawable.getOriginHeight() * pixelByteCount;
            String needMemory = Formatter.formatFileSize(getContext(), originImageByteCount);

            messageBuilder.append("\n");
            messageBuilder.append("\n");
            messageBuilder.append("原始图：")
                    .append(sketchDrawable.getOriginWidth()).append("x").append(sketchDrawable.getOriginHeight())
                    .append("/").append(sketchDrawable.getMimeType().substring(6))
                    .append("/").append(needDiskSpace);

            messageBuilder.append("\n");
            messageBuilder.append("方向/内存：").append(ImageOrientationCorrector.toName(sketchDrawable.getExifOrientation()))
                    .append("/").append(needMemory);

            messageBuilder.append("\n");
            messageBuilder.append("预览图：")
                    .append(drawable.getIntrinsicWidth()).append("x").append(drawable.getIntrinsicHeight())
                    .append("/").append(sketchDrawable.getBitmapConfig())
                    .append("/").append(Formatter.formatFileSize(getContext(), previewDrawableByteCount));

            messageBuilder.append("\n");

            return messageBuilder.toString();
        }
    }
}
