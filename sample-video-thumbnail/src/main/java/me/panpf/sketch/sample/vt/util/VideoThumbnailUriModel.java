package me.panpf.sketch.sample.vt.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.decode.ImageSizeCalculator;
import me.panpf.sketch.request.MaxSize;
import me.panpf.sketch.uri.AbsBitmapDiskCacheUriModel;
import me.panpf.sketch.uri.GetDataSourceException;
import me.panpf.sketch.util.SketchUtils;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoThumbnailUriModel extends AbsBitmapDiskCacheUriModel {

    public static final String SCHEME = "video.thumbnail://";
    private static final String NAME = "VideoThumbnailUriModel";

    public static String makeUri(@NonNull String filePath) {
        return SCHEME + filePath;
    }

    @Override
    protected boolean match(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    /**
     * 获取 uri 所真正包含的内容部分，例如 "video.thumbnail:///sdcard/test.mp4"，就会返回 "/sdcard/test.mp4"
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，例如 "video.thumbnail:///sdcard/test.mp4"，就会返回 "/sdcard/test.mp4"
     */
    @NonNull
    @Override
    public String getUriContent(@NonNull String uri) {
        return match(uri) ? uri.substring(SCHEME.length()) : uri;
    }

    @NonNull
    @Override
    public String getDiskCacheKey(@NonNull String uri) {
        return SketchUtils.createFileUriDiskCacheKey(uri, getUriContent(uri));
    }

    @NonNull
    @Override
    protected Bitmap getContent(@NonNull Context context, @NonNull String uri) throws GetDataSourceException {
        FFmpegMediaMetadataRetriever mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(getUriContent(uri));
        try {
            return readVideoThumbnail(context, uri, mediaMetadataRetriever);
        } finally {
            mediaMetadataRetriever.release();
        }
    }

    @NonNull
    private Bitmap readVideoThumbnail(Context context, String uri, FFmpegMediaMetadataRetriever mediaMetadataRetriever) throws GetDataSourceException {
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

        if (frameBitmap == null || frameBitmap.isRecycled()) {
            String cause = String.format("Video thumbnail bitmap invalid. %s", uri);
            SLog.e(NAME, cause);
            throw new GetDataSourceException(cause);
        }
        return frameBitmap;
    }
}
