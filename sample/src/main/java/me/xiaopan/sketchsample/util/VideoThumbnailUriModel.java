package me.xiaopan.sketchsample.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.decode.ByteArrayDataSource;
import me.xiaopan.sketch.decode.DataSource;
import me.xiaopan.sketch.decode.DiskCacheDataSource;
import me.xiaopan.sketch.decode.ImageSizeCalculator;
import me.xiaopan.sketch.request.DownloadResult;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.MaxSize;
import me.xiaopan.sketch.uri.UriModel;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoThumbnailUriModel extends UriModel {

    public static final String SCHEME = "video.thumbnail://";
    private static final String NAME = "VideoThumbnailUriModel";

    public static String makeUri(String filePath) {
        return SCHEME + filePath;
    }

    @Override
    protected boolean match(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    @Override
    public String getUriContent(@NonNull String uri) {
        return match(uri) ? uri.substring(SCHEME.length()) : uri;
    }

    /**
     * 获取 uri 所真正包含的内容部分，例如 "video.thumbnail:///sdcard/test.mp4"，就会返回 "/sdcard/test.mp4"
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，例如 "video.thumbnail:///sdcard/test.mp4"，就会返回 "/sdcard/test.mp4"
     */
    @Override
    public DataSource getDataSource(@NonNull Context context, @NonNull String uri, DownloadResult downloadResult) {
        // TODO: 2017/9/1 这里的磁盘缓存key，想办法改一下
        String path = getUriContent(uri);

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        long lastModifyTime = file.lastModified();
        String diskCacheKey = path + "." + lastModifyTime;

        DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();

        DiskCache.Entry cacheEntry = diskCache.get(diskCacheKey);
        if (cacheEntry != null) {
            return new DiskCacheDataSource(cacheEntry, ImageFrom.DISK_CACHE);
        }

        ReentrantLock diskCacheEditLock = diskCache.getEditLock(diskCacheKey);
        diskCacheEditLock.lock();

        DataSource dataSource;
        try {
            cacheEntry = diskCache.get(diskCacheKey);
            if (cacheEntry != null) {
                dataSource = new DiskCacheDataSource(cacheEntry, ImageFrom.DISK_CACHE);
            } else {
                FFmpegMediaMetadataRetriever mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(path);
                try {
                    dataSource = readVideoThumbnail(context, uri, diskCache, diskCacheKey, mediaMetadataRetriever);
                } finally {
                    mediaMetadataRetriever.release();
                }
            }
        } finally {
            diskCacheEditLock.unlock();
        }

        return dataSource;
    }

    private DataSource readVideoThumbnail(Context context, String uri, DiskCache diskCache,
                                          String diskCacheKey, FFmpegMediaMetadataRetriever mediaMetadataRetriever) {
        FFmpegMediaMetadataRetriever.Metadata metadata = mediaMetadataRetriever.getMetadata();
        int videoWidth = metadata.getInt(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        int videoHeight = metadata.getInt(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

        // 限制读取的帧的尺寸
        ImageSizeCalculator sizeCalculator = Sketch.with(context).getConfiguration().getSizeCalculator();
        MaxSize maxSize = sizeCalculator.getDefaultImageMaxSize(context);
        int inSampleSize = sizeCalculator.calculateInSampleSize(videoWidth, videoHeight, maxSize.getWidth(), maxSize.getHeight(), false);
        int finalWidth = SketchUtils.ceil(videoWidth, inSampleSize);
        int finalHeight = SketchUtils.ceil(videoHeight, inSampleSize);

        // 大于30分钟的一般是电影或电视剧，这类视频开头一般都一样显示出来也没有意义，所以显示中间的部分
        long timeUs;
        long duration = metadata.getLong(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
        int second = (int) (duration / 1000);
        int minute = second / 60;
        if (minute > 30) {
            timeUs = duration / 2 * 1000;
        } else {
            timeUs = -1;
        }

        Bitmap frameBitmap = mediaMetadataRetriever.getScaledFrameAtTime(timeUs, finalWidth, finalHeight);

        // 偶尔会有读取中间帧失败的情况，这时候换到三分之一处再读一次
        if (frameBitmap == null && timeUs != -1) {
            timeUs = duration / 3 * 1000;
            frameBitmap = mediaMetadataRetriever.getScaledFrameAtTime(timeUs, finalWidth, finalHeight);
        }

        if (frameBitmap == null) {
            return null;
        }
        if (frameBitmap.isRecycled()) {
            SLog.e(NAME, "Video thumbnail bitmap recycled. %s", uri);
            return null;
        }

        BitmapPool bitmapPool = Sketch.with(context).getConfiguration().getBitmapPool();
        DiskCache.Editor diskCacheEditor = diskCache.edit(diskCacheKey);
        OutputStream outputStream;
        if (diskCacheEditor != null) {
            try {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
                BitmapPoolUtils.freeBitmapToPool(frameBitmap, bitmapPool);
                diskCacheEditor.abort();
                return null;
            }
        } else {
            outputStream = new ByteArrayOutputStream();
        }

        try {
            frameBitmap.compress(SketchUtils.bitmapConfigToCompressFormat(frameBitmap.getConfig()), 100, outputStream);

            if (diskCacheEditor != null) {
                diskCacheEditor.commit();
            }
        } catch (DiskLruCache.EditorChangedException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (DiskLruCache.ClosedException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (DiskLruCache.FileNotExistException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } finally {
            BitmapPoolUtils.freeBitmapToPool(frameBitmap, bitmapPool);
            SketchUtils.close(outputStream);
        }

        if (diskCacheEditor != null) {
            DiskCache.Entry cacheEntry = diskCache.get(diskCacheKey);
            if (cacheEntry != null) {
                return new DiskCacheDataSource(cacheEntry, ImageFrom.LOCAL);
            } else {
                SLog.e(NAME, "Not found video thumbnail cache file. %s", uri);
                return null;
            }
        } else {
            return new ByteArrayDataSource(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.LOCAL);
        }
    }
}
