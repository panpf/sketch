package me.xiaopan.sketchsample.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.decode.DataSource;
import me.xiaopan.sketch.decode.DataSourceFactory;
import me.xiaopan.sketch.decode.DecodeException;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.drawable.SketchLoadingDrawable;
import me.xiaopan.sketch.feature.ImageOrientationCorrector;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.RedisplayListener;
import me.xiaopan.sketch.request.UriInfo;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.event.AppConfigChangedEvent;
import me.xiaopan.sketchsample.event.CacheCleanEvent;
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

    @SuppressWarnings("unused")
    public boolean isDisabledLongClickShowImageInfo() {
        return disabledLongClickShowImageInfo;
    }

    public void setDisabledLongClickShowImageInfo(boolean disabledLongClickShowImageInfo) {
        this.disabledLongClickShowImageInfo = disabledLongClickShowImageInfo;
    }

    @Override
    public boolean redisplay(RedisplayListener listener) {
        return !disabledRedisplay && super.redisplay(listener);
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
            redisplay(null);
        } else if (AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK.equals(event.key)) {
            redisplay(null);
        } else if (AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL.equals(event.key)) {
            redisplay(null);
        } else if (AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY.equals(event.key)) {
            redisplay(null);


        } else if (AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION.equals(event.key)) {
            final boolean correctImageOrientationDisabled = AppConfig.getBoolean(getContext(), AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION);
            getOptions().setCorrectImageOrientationDisabled(correctImageOrientationDisabled);

            redisplay(new RedisplayListener() {
                @Override
                public void onPreCommit(String cacheUri, DisplayOptions cacheOptions) {
                    cacheOptions.setCorrectImageOrientationDisabled(correctImageOrientationDisabled);
                }
            });
        } else if (AppConfig.Key.PLAY_GIF_ON_LIST.equals(event.key)) {
            if (useInList) {
                final boolean playGifOnList = AppConfig.getBoolean(getContext(), AppConfig.Key.PLAY_GIF_ON_LIST);
                getOptions().setDecodeGifImage(playGifOnList);

                redisplay(new RedisplayListener() {
                    @Override
                    public void onPreCommit(String cacheUri, DisplayOptions cacheOptions) {
                        cacheOptions.setDecodeGifImage(playGifOnList);
                    }
                });
            }
        } else if (AppConfig.Key.THUMBNAIL_MODE.equals(event.key)) {
            if (useInList) {
                final boolean thumbnailMode = AppConfig.getBoolean(getContext(), AppConfig.Key.THUMBNAIL_MODE);
                getOptions().setThumbnailMode(thumbnailMode);
                if (thumbnailMode) {
                    if (getOptions().getResize() == null && !getOptions().isResizeByFixedSize()) {
                        getOptions().setResizeByFixedSize(true);
                    }
                } else {
                    getOptions().setResizeByFixedSize(false);
                }

                redisplay(new RedisplayListener() {
                    @Override
                    public void onPreCommit(String cacheUri, DisplayOptions cacheOptions) {
                        cacheOptions.setThumbnailMode(thumbnailMode);
                        if (thumbnailMode) {
                            if (cacheOptions.getResize() == null && !cacheOptions.isResizeByFixedSize()) {
                                cacheOptions.setResizeByFixedSize(true);
                            }
                        } else {
                            cacheOptions.setResizeByFixedSize(false);
                        }
                    }
                });
            }
        } else if (AppConfig.Key.CACHE_PROCESSED_IMAGE.equals(event.key)) {
            final boolean cacheProcessedImageInDisk = AppConfig.getBoolean(getContext(), AppConfig.Key.CACHE_PROCESSED_IMAGE);
            getOptions().setCacheProcessedImageInDisk(cacheProcessedImageInDisk);

            redisplay(new RedisplayListener() {
                @Override
                public void onPreCommit(String cacheUri, DisplayOptions cacheOptions) {
                    cacheOptions.setCacheProcessedImageInDisk(cacheProcessedImageInDisk);
                }
            });
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(CacheCleanEvent event) {
        redisplay(null);
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

            UriInfo uriInfo = UriInfo.make(sketchDrawable.getUri());
            DataSource dataSource = null;
            try {
                dataSource = DataSourceFactory.makeDataSource(getContext(), uriInfo, null);
            } catch (DecodeException e) {
                e.printStackTrace();
            }
            long imageLength = 0;
            try {
                imageLength = dataSource != null ? dataSource.getLength() : 0;
            } catch (IOException e) {
                e.printStackTrace();
            }

            String needDiskSpace = imageLength > 0 ? Formatter.formatFileSize(getContext(), imageLength) : "未知";

            int previewDrawableByteCount = sketchDrawable.getByteCount();
            int pixelByteCount = previewDrawableByteCount / drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
            int originImageByteCount = sketchDrawable.getOriginWidth() * sketchDrawable.getOriginHeight() * pixelByteCount;
            String needMemory = Formatter.formatFileSize(getContext(), originImageByteCount);
            String mimeType = sketchDrawable.getMimeType();

            messageBuilder.append("\n");
            messageBuilder.append("\n");
            messageBuilder.append("原始图：")
                    .append(sketchDrawable.getOriginWidth()).append("x").append(sketchDrawable.getOriginHeight())
                    .append("/").append(mimeType != null && mimeType.startsWith("image/") ? mimeType.substring(6) : "未知")
                    .append("/").append(needDiskSpace);

            messageBuilder.append("\n                ");
            messageBuilder.append(ImageOrientationCorrector.toName(sketchDrawable.getExifOrientation()))
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
