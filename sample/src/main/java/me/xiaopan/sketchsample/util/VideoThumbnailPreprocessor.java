package me.xiaopan.sketchsample.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.decode.ImageSizeCalculator;
import me.xiaopan.sketch.preprocess.PreProcessResult;
import me.xiaopan.sketch.preprocess.Preprocessor;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.MaxSize;
import me.xiaopan.sketch.request.UriInfo;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoThumbnailPreprocessor implements Preprocessor {

    private static final String URI_HOST = "video";
    private static final String PARAM_PATH = "path";
    private static final String LOG_NAME = "VideoThumbnailPreprocessor";

    /**
     * 创建用于显示视频缩略图的uri
     *
     * @param path 视频文件路径
     */
    public static String createUri(String path) {
        return String.format("%s%s?%s=%s", UriScheme.FILE.getSecondaryUriPrefix(), URI_HOST, PARAM_PATH, path);
    }

    @Override
    public boolean match(Context context, UriInfo uriInfo) {
        return uriInfo.getScheme() == UriScheme.FILE
                && uriInfo.getContent() != null
                && uriInfo.getContent().startsWith(URI_HOST);
    }

    @Override
    public PreProcessResult process(Context context, UriInfo uriInfo) {
        Uri uri = Uri.parse(uriInfo.getUri());
        String path = uri.getQueryParameter(PARAM_PATH);

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        long lastModifyTime = file.lastModified();
        String diskCacheKey = path + "." + lastModifyTime;

        DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();

        DiskCache.Entry cacheEntry = diskCache.get(diskCacheKey);
        if (cacheEntry != null) {
            return new PreProcessResult(cacheEntry, ImageFrom.DISK_CACHE);
        }

        ReentrantLock diskCacheEditLock = diskCache.getEditLock(diskCacheKey);
        diskCacheEditLock.lock();

        PreProcessResult result;
        try {
            cacheEntry = diskCache.get(diskCacheKey);
            if (cacheEntry != null) {
                result = new PreProcessResult(cacheEntry, ImageFrom.DISK_CACHE);
            } else {
                FFmpegMediaMetadataRetriever mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(path);
                try {
                    result = readVideoThumbnail(context, uriInfo, diskCache, diskCacheKey, mediaMetadataRetriever);
                } finally {
                    mediaMetadataRetriever.release();
                }
            }
        } finally {
            diskCacheEditLock.unlock();
        }

        return result;
    }

    private PreProcessResult readVideoThumbnail(Context context, UriInfo uriInfo, DiskCache diskCache,
                                                String diskCacheKey, FFmpegMediaMetadataRetriever mediaMetadataRetriever) {
        FFmpegMediaMetadataRetriever.Metadata metadata = mediaMetadataRetriever.getMetadata();
        int videoWidth = metadata.getInt(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        int videoHeight = metadata.getInt(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

        // 限制读取的帧的尺寸
        ImageSizeCalculator sizeCalculator = Sketch.with(context).getConfiguration().getImageSizeCalculator();
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
            if (SLogType.REQUEST.isEnabled()) {
                SLog.fw(SLogType.REQUEST, LOG_NAME, "video thumbnail bitmap recycled. %s", uriInfo.getUri());
            }
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
                return new PreProcessResult(cacheEntry, ImageFrom.LOCAL);
            } else {
                if (SLogType.REQUEST.isEnabled()) {
                    SLog.fw(SLogType.REQUEST, LOG_NAME, "not found video thumbnail cache file. %s", uriInfo.getUri());
                }
                return null;
            }
        } else {
            return new PreProcessResult(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.LOCAL);
        }
    }
}
