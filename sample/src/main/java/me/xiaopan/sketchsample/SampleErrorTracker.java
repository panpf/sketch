package me.xiaopan.sketchsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.text.format.Formatter;

import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import me.xiaopan.sketch.ErrorTracker;
import me.xiaopan.sketch.drawable.SketchRefDrawable;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.util.UnableCreateDirException;
import me.xiaopan.sketch.util.UnableCreateFileException;
import me.xiaopan.sketch.viewfun.large.Tile;

class SampleErrorTracker extends ErrorTracker {

    private static final int INSTALL_FAILED_RETRY_TIME_INTERVAL = 30 * 60 * 1000;

    private Context context;
    private long lastUploadInstallFailedTime;
    private long lastUploadDecodeNormalImageFailedTime;
    private long lastUploadDecodeGifImageFailedTime;
    private long lastUploadProcessImageFailedTime;
    private boolean uploadNotFoundGidSoError;

    public SampleErrorTracker(Context context) {
        super(context);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onNotFoundGifSoError(Throwable e) {
        super.onNotFoundGifSoError(e);

        // 每次运行只上报一次
        if (uploadNotFoundGidSoError) {
            return;
        }
        uploadNotFoundGidSoError = true;

        String abis;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Arrays.toString(Build.SUPPORTED_ABIS);
        } else {
            abis = Arrays.toString(new String[]{Build.CPU_ABI, Build.CPU_ABI2});
        }
        String message = String.format("Didn't find “libpl_droidsonroids_gif.so” file, abis=%s", abis);

        CrashReport.postCatchedException(new Exception(message, e));
    }

    @Override
    public void onDecodeGifImageError(Throwable throwable, LoadRequest request, int outWidth, int outHeight, String outMimeType) {
        super.onDecodeGifImageError(throwable, request, outWidth, outHeight, outMimeType);

        // 其它异常每半小时上报一次
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUploadDecodeGifImageFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
            return;
        }
        lastUploadDecodeGifImageFailedTime = currentTime;

        StringBuilder builder = new StringBuilder();

        builder.append("Sketch")
                .append(" - ").append("DecodeGifImageFailed")
                .append(" - ").append(throwable.getClass().getSimpleName())
                .append(" - ").append(decodeUri(context, request.getUri()));

        builder.append("\n").append("exceptionMessage: ").append(throwable.getMessage());

        if (throwable instanceof OutOfMemoryError) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            String maxMemoryFormatted = Formatter.formatFileSize(this.context, maxMemory);
            String freeMemoryFormatted = Formatter.formatFileSize(this.context, freeMemory);
            String totalMemoryFormatted = Formatter.formatFileSize(this.context, totalMemory);
            builder.append("\n")
                    .append("memoryInfo: ")
                    .append("maxMemory=").append(maxMemoryFormatted)
                    .append(", freeMemory=").append(freeMemoryFormatted)
                    .append(", totalMemory=").append(totalMemoryFormatted);
        }

        builder.append("\n")
                .append("imageInfo: ")
                .append("outWidth=").append(outWidth)
                .append(", outHeight=").append(outHeight)
                .append(", outMimeType=").append(outMimeType);

        CrashReport.postCatchedException(new Exception(builder.toString(), throwable));
    }

    @Override
    public void onDecodeNormalImageError(Throwable throwable, LoadRequest request, int outWidth, int outHeight, String outMimeType) {
        super.onDecodeNormalImageError(throwable, request, outWidth, outHeight, outMimeType);

        // 每半小时上报一次
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUploadDecodeNormalImageFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
            return;
        }
        lastUploadDecodeNormalImageFailedTime = currentTime;

        StringBuilder builder = new StringBuilder();

        builder.append("Sketch")
                .append(" - ").append("DecodeNormalImageFailed")
                .append(" - ").append(throwable.getClass().getSimpleName())
                .append(" - ").append(decodeUri(context, request.getUri()));

        builder.append("\n").append("exceptionMessage: ").append(throwable.getMessage());

        if (throwable instanceof OutOfMemoryError) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            String maxMemoryFormatted = Formatter.formatFileSize(this.context, maxMemory);
            String freeMemoryFormatted = Formatter.formatFileSize(this.context, freeMemory);
            String totalMemoryFormatted = Formatter.formatFileSize(this.context, totalMemory);
            builder.append("\n").append("memoryInfo: ")
                    .append("maxMemory=").append(maxMemoryFormatted)
                    .append(", freeMemory=").append(freeMemoryFormatted)
                    .append(", totalMemory=").append(totalMemoryFormatted);
        }

        builder.append("\n").append("imageInfo: ")
                .append("outWidth=").append(outWidth)
                .append(", outHeight=").append(outHeight)
                .append(", outMimeType=").append(outMimeType);

        CrashReport.postCatchedException(new Exception(builder.toString(), throwable));
    }

    @Override
    public void onInstallDiskCacheError(Exception e, File cacheDir) {
        super.onInstallDiskCacheError(e, cacheDir);

        // 每半小时上传一次
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUploadInstallFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
            return;
        }
        lastUploadInstallFailedTime = currentTime;

        StringBuilder builder = new StringBuilder();

        builder.append("Sketch")
                .append(" - ").append("InstallDiskCacheFailed");
        if (e instanceof UnableCreateDirException) {
            builder.append(" - ").append("UnableCreateDirException");
        } else if (e instanceof UnableCreateFileException) {
            builder.append(" - ").append("UnableCreateFileException");
        } else {
            builder.append(" - ").append(e.getClass().getSimpleName());
        }
        builder.append(" - ").append(cacheDir.getPath());

        builder.append("\n").append("exceptionMessage: ").append(e.getMessage());

        String sdcardState = Environment.getExternalStorageState();
        builder.append("\n").append("sdcardState: ").append(sdcardState);

        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            long totalBytes = SketchUtils.getTotalBytes(sdcardDir);
            long availableBytes = SketchUtils.getAvailableBytes(sdcardDir);
            builder.append("\n")
                    .append("sdcardSize: ")
                    .append(Formatter.formatFileSize(context, availableBytes))
                    .append("/")
                    .append(Formatter.formatFileSize(context, totalBytes));
        }

        CrashReport.postCatchedException(new Exception(builder.toString(), e));
    }

    @Override
    public void onProcessImageError(Throwable throwable, String imageUri, ImageProcessor processor) {
        super.onProcessImageError(throwable, imageUri, processor);

        // 每半小时上报一次
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUploadProcessImageFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
            return;
        }
        lastUploadProcessImageFailedTime = currentTime;

        String outOfMemoryInfo = throwable instanceof OutOfMemoryError ? String.format("\nmemoryState: %s", getSystemState()) : "";
        CrashReport.postCatchedException(new Exception(String.format(
                "Sketch - %s - " +
                        "%s" +
                        "\n%s",
                processor.getKey(),
                decodeUri(context, imageUri),
                outOfMemoryInfo
        ), throwable));
    }

    @Override
    public void onTileSortError(IllegalArgumentException e, List<Tile> tileList, boolean useLegacyMergeSort) {
        super.onTileSortError(e, tileList, useLegacyMergeSort);

        CrashReport.postCatchedException(new Exception(String.format(
                "Sketch - TileSortError - " +
                        "%s " +
                        "\ntiles: %s",
                useLegacyMergeSort ? "useLegacyMergeSort. " : "",
                SketchUtils.tileListToString(tileList)
        ), e));
    }

    @Override
    public void onBitmapRecycledOnDisplay(DisplayRequest request, SketchRefDrawable refDrawable) {
        super.onBitmapRecycledOnDisplay(request, refDrawable);

        CrashReport.postCatchedException(new Exception(String.format(
                "Sketch - BitmapRecycledOnDisplay - " +
                        "%s " +
                        "\ndrawable: %s",
                decodeUri(context, request.getUri()),
                refDrawable.getInfo())));
    }

    @Override
    public void onInBitmapDecodeError(String imageUri, int imageWidth, int imageHeight, String imageMimeType,
                                      Throwable throwable, int inSampleSize, Bitmap inBitmap) {
        super.onInBitmapDecodeError(imageUri, imageWidth, imageHeight, imageMimeType, throwable, inSampleSize, inBitmap);

        CrashReport.postCatchedException(new Exception(String.format(
                "Sketch - InBitmapDecodeError - " +
                        "%s" +
                        "\nimage：%dx%d/%s" +
                        "\ninSampleSize：%d" +
                        "\ninBitmap：%dx%d, %d, %s" +
                        "\nsystemState：%s",
                decodeUri(context, imageUri),
                imageWidth, imageHeight, imageMimeType,
                inSampleSize,
                inBitmap.getWidth(), inBitmap.getHeight(), SketchUtils.getByteCount(inBitmap), inBitmap.getConfig(),
                getSystemState()
        ), throwable));
    }

    @Override
    public void onDecodeRegionError(String imageUri, int imageWidth, int imageHeight, String imageMimeType,
                                    Throwable throwable, Rect srcRect, int inSampleSize) {
        super.onDecodeRegionError(imageUri, imageWidth, imageHeight, imageMimeType, throwable, srcRect, inSampleSize);

        CrashReport.postCatchedException(new Exception(String.format(
                "Sketch - DecodeRegionError - " +
                        "%s" +
                        "\nimage：%dx%d/%s" +
                        "\nsrcRect：%s" +
                        "\ninSampleSize：%d" +
                        "\nsrcRect：%s" +
                        "\nsystemState：%s",
                decodeUri(context, imageUri),
                imageWidth, imageHeight, imageMimeType,
                srcRect.toString(),
                inSampleSize,
                srcRect.toShortString(),
                getSystemState()
        ), throwable));
    }

    private String decodeUri(Context context, String imageUri) {
        UriScheme scheme = UriScheme.valueOfUri(imageUri);
        if (scheme != null && scheme == UriScheme.DRAWABLE) {
            try {
                int resId = Integer.parseInt(UriScheme.DRAWABLE.cropContent(imageUri));
                return context.getResources().getResourceName(resId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return imageUri;
    }

    private String getSystemInfo() {
        return String.format(Locale.getDefault(), "%s, %d", Build.MODEL, Build.VERSION.SDK_INT);
    }

    private String getMemoryInfo() {
        String freeMemory = Formatter.formatFileSize(context, Runtime.getRuntime().freeMemory());
        String maxMemory = Formatter.formatFileSize(context, Runtime.getRuntime().maxMemory());
        return String.format("%s/%s", freeMemory, maxMemory);
    }

    private String getSystemState() {
        return String.format(Locale.getDefault(), "%s, %s", getSystemInfo(), getMemoryInfo());
    }
}
