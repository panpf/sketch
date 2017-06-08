package me.xiaopan.sketchsample.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
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
import me.xiaopan.sketch.decode.ImageOrientationCorrector;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.drawable.SketchLoadingDrawable;
import me.xiaopan.sketch.drawable.SketchShapeBitmapDrawable;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.RedisplayListener;
import me.xiaopan.sketch.request.UriInfo;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.event.AppConfigChangedEvent;
import me.xiaopan.sketchsample.event.CacheCleanEvent;
import me.xiaopan.sketchsample.util.AppConfig;

public class SampleImageView extends SketchImageView {
    private Page page;
    private boolean disabledRedisplay;

    public SampleImageView(Context context) {
        super(context);
    }

    public SampleImageView(Context context, AttributeSet attrs) {
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
            onEvent(new AppConfigChangedEvent(AppConfig.Key.CLICK_PLAY_GIF));
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

    public void setOptions(@ImageOptions.Type int optionsId) {
        setOptions(ImageOptions.getDisplayOptions(getContext(), optionsId));
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public boolean redisplay(RedisplayListener listener) {
        return !disabledRedisplay && super.redisplay(listener);
    }

    @Subscribe
    public void onEvent(AppConfigChangedEvent event) {
        if (AppConfig.Key.SHOW_GIF_FLAG.equals(event.key)) {
            if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                setShowGifFlagEnabled(AppConfig.getBoolean(getContext(), AppConfig.Key.SHOW_GIF_FLAG) ? R.drawable.ic_gif : 0);
            }
        } else if (AppConfig.Key.SHOW_IMAGE_FROM_FLAG.equals(event.key)) {
            setShowImageFromEnabled(AppConfig.getBoolean(getContext(), event.key));
        } else if (AppConfig.Key.CLICK_SHOW_PRESSED_STATUS.equals(event.key)) {
            if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                setShowPressedStatusEnabled(AppConfig.getBoolean(getContext(), event.key));
            }
        } else if (AppConfig.Key.SHOW_IMAGE_DOWNLOAD_PROGRESS.equals(event.key)) {
            if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                setShowDownloadProgressEnabled(AppConfig.getBoolean(getContext(), event.key));
            }
        } else if (AppConfig.Key.CLICK_RETRY_ON_PAUSE_DOWNLOAD.equals(event.key)) {
            if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                setClickRetryOnPauseDownloadEnabled(AppConfig.getBoolean(getContext(), event.key));
            }
        } else if (AppConfig.Key.CLICK_RETRY_ON_FAILED.equals(event.key)) {
            if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                setClickRetryOnDisplayErrorEnabled(AppConfig.getBoolean(getContext(), event.key));
            }
        } else if (AppConfig.Key.CLICK_PLAY_GIF.equals(event.key)) {
            if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                setClickPlayGifEnabled(AppConfig.getBoolean(getContext(), event.key) ? R.drawable.ic_video_play : 0);
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
            final boolean correctImageOrientationDisabled = AppConfig.getBoolean(getContext(), event.key);
            getOptions().setCorrectImageOrientationDisabled(correctImageOrientationDisabled);

            redisplay(new RedisplayListener() {
                @Override
                public void onPreCommit(String cacheUri, DisplayOptions cacheOptions) {
                    cacheOptions.setCorrectImageOrientationDisabled(correctImageOrientationDisabled);
                }
            });
        } else if (AppConfig.Key.PLAY_GIF_ON_LIST.equals(event.key)) {
            if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST) {
                final boolean playGifOnList = AppConfig.getBoolean(getContext(), event.key);
                getOptions().setDecodeGifImage(playGifOnList);

                redisplay(new RedisplayListener() {
                    @Override
                    public void onPreCommit(String cacheUri, DisplayOptions cacheOptions) {
                        cacheOptions.setDecodeGifImage(playGifOnList);
                    }
                });
            }
        } else if (AppConfig.Key.THUMBNAIL_MODE.equals(event.key)) {
            if (page == Page.PHOTO_LIST) {
                final boolean thumbnailMode = AppConfig.getBoolean(getContext(), event.key);
                getOptions().setThumbnailMode(thumbnailMode);
                if (getOptions().getResize() == null) {
                    getOptions().setResizeByFixedSize(thumbnailMode);
                }

                redisplay(new RedisplayListener() {
                    @Override
                    public void onPreCommit(String cacheUri, DisplayOptions cacheOptions) {
                        cacheOptions.setThumbnailMode(thumbnailMode);
                        if (cacheOptions.getResize() == null) {
                            cacheOptions.setResizeByFixedSize(thumbnailMode);
                        }
                    }
                });
            }
        } else if (AppConfig.Key.CACHE_PROCESSED_IMAGE.equals(event.key)) {
            final boolean cacheProcessedImageInDisk = AppConfig.getBoolean(getContext(), event.key);
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

    public enum Page {
        PHOTO_LIST, UNSPLASH_LIST, SEARCH_LIST, APP_LIST, DETAIL, DEMO;
    }

    private class LongClickShowDrawableInfoListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            if (v.getContext() instanceof Activity) {
                showInfo((Activity) v.getContext());
                return true;
            }
            return false;
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
    }

    public String makeImageInfo(Drawable drawable, SketchDrawable sketchDrawable) {
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
        int pixelByteCount;
        if (drawable instanceof SketchShapeBitmapDrawable) {
            Bitmap bitmap = ((SketchShapeBitmapDrawable) drawable).getBitmapDrawable().getBitmap();
            pixelByteCount = previewDrawableByteCount / bitmap.getWidth() / bitmap.getHeight();
        } else {
            pixelByteCount = previewDrawableByteCount / drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
        }
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
        messageBuilder.append("\n");
        messageBuilder.append("KEY：")
                .append(sketchDrawable.getKey());

        return messageBuilder.toString();
    }
}
