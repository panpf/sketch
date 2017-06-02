package me.xiaopan.sketch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.text.format.Formatter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import me.xiaopan.sketch.drawable.SketchRefDrawable;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.DownloadRequest;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.viewfun.large.Tile;

public class ErrorTracker implements Identifier {
    private static final String LOG_NAME = "ErrorTracker";

    private Context context;

    public ErrorTracker(Context context) {
        context = context.getApplicationContext();
        this.context = context;
    }

    /**
     * 安装磁盘缓存失败
     *
     * @param e        NoSpaceException：空间不足；
     *                 UnableCreateDirException：无法创建缓存目录；
     *                 UnableCreateFileException：无法在缓存目录中创建文件
     * @param cacheDir 默认的缓存目录
     */
    public void onInstallDiskCacheError(Exception e, File cacheDir) {
        SLog.fe(LOG_NAME, "onInstallDiskCacheError. %s: %s. SDCardState: %s. cacheDir: %s",
                e.getClass().getSimpleName(), e.getMessage(), Environment.getExternalStorageState(), cacheDir.getPath());
    }

    /**
     * 找不到libpl_droidsonroids_gif.so文件错误
     *
     * @param e UnsatisfiedLinkError或ExceptionInInitializerError：找不到对应到的so文件
     */
    public void onNotFoundGifSoError(Throwable e) {
        SLog.e(LOG_NAME, "Didn't find “libpl_droidsonroids_gif.so” file, unable decode the GIF images. " +
                "Please go to “https://github.com/xiaopansky/sketch” find how to import the sketch-gif library");

        String abis;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Arrays.toString(Build.SUPPORTED_ABIS);
        } else {
            abis = Arrays.toString(new String[]{Build.CPU_ABI, Build.CPU_ABI2});
        }
        SLog.fe(LOG_NAME, "abis=%s", abis);
    }

    /**
     * 解码GIF图片失败
     *
     * @param throwable   Throwable
     * @param request     请求
     * @param outWidth    图片原始宽
     * @param outHeight   图片原始高
     * @param outMimeType 图片类型
     */
    public void onDecodeGifImageError(Throwable throwable, LoadRequest request, int outWidth, int outHeight, String outMimeType) {
        SLog.fe(LOG_NAME, "onDecodeGifImageError. outWidth=%d, outHeight=%d + outMimeType=%s. %s",
                outWidth, outHeight, outMimeType, request.getKey());
    }

    /**
     * 解码普通图片失败
     *
     * @param throwable   OutOfMemoryError：内存溢出
     * @param request     请求
     * @param outWidth    图片原始宽
     * @param outHeight   图片原始高
     * @param outMimeType 图片类型
     */
    public void onDecodeNormalImageError(Throwable throwable, LoadRequest request, int outWidth, int outHeight, String outMimeType) {
        if (throwable instanceof OutOfMemoryError) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            String maxMemoryFormatted = Formatter.formatFileSize(context, maxMemory);
            String freeMemoryFormatted = Formatter.formatFileSize(context, freeMemory);
            String totalMemoryFormatted = Formatter.formatFileSize(context, totalMemory);
            SLog.fe(LOG_NAME, "OutOfMemoryError. appMemoryInfo: maxMemory=%s, freeMemory=%s, totalMemory=%s",
                    maxMemoryFormatted, freeMemoryFormatted, totalMemoryFormatted);
        }

        SLog.fe(LOG_NAME, "onDecodeNormalImageError. outWidth=%d, outHeight=%d, outMimeType=%s. %s",
                outWidth, outHeight, outMimeType, request.getKey());
    }

    /**
     * 处理图片失败
     *
     * @param e         OutOfMemoryError：内存溢出
     * @param imageUri  图片uri
     * @param processor 所使用的处理器
     */
    public void onProcessImageError(Throwable e, String imageUri, ImageProcessor processor) {
        if (e instanceof OutOfMemoryError) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            String maxMemoryFormatted = Formatter.formatFileSize(context, maxMemory);
            String freeMemoryFormatted = Formatter.formatFileSize(context, freeMemory);
            String totalMemoryFormatted = Formatter.formatFileSize(context, totalMemory);
            SLog.fd("OutOfMemoryError. appMemoryInfo: maxMemory=%s, freeMemory=%s, totalMemory=%s",
                    maxMemoryFormatted, freeMemoryFormatted, totalMemoryFormatted);
        }

        SLog.fe(LOG_NAME, "onProcessImageError. imageUri: %s. processor: %s",
                imageUri, processor.getKey());
    }

    /**
     * 下载出现错误
     *
     * @param request   出错的下载请求
     * @param throwable 异常
     */
    public void onDownloadError(@SuppressWarnings("UnusedParameters") DownloadRequest request,
                                @SuppressWarnings("UnusedParameters") Throwable throwable) {

    }

    /**
     * 碎片排序错误，Java7的排序算法在检测到A大于B, B小于C, 但是A小于等于C的时候就会抛出异常
     *
     * @param e                  异常
     * @param tileList           碎片列表
     * @param useLegacyMergeSort 当前是否使用旧的排序算法
     */
    public void onTileSortError(@SuppressWarnings("UnusedParameters") IllegalArgumentException e, List<Tile> tileList,
                                @SuppressWarnings("UnusedParameters") boolean useLegacyMergeSort) {
        String legacy = useLegacyMergeSort ? "useLegacyMergeSort. " : "";
        SLog.fw(LOG_NAME, "onTileSortError. %s%s", legacy, SketchUtils.tileListToString(tileList));
    }

    /**
     * 在即将显示时发现Bitmap被回收
     */
    public void onBitmapRecycledOnDisplay(DisplayRequest request, SketchRefDrawable refDrawable) {
        SLog.fw(LOG_NAME, "onBitmapRecycledOnDisplay. imageUri=%s, drawable=%s",
                request.getUri(), refDrawable.getInfo());
    }

    /**
     * 使用inBitmap解码时发生异常
     *
     * @param imageUri      图片uri
     * @param imageWidth    图片宽
     * @param imageHeight   图片高
     * @param imageMimeType 图片类型
     * @param throwable     异常
     * @param inSampleSize  缩放比例
     * @param inBitmap      复用的inBitmap
     */
    public void onInBitmapDecodeError(String imageUri, int imageWidth, int imageHeight,
                                      String imageMimeType, Throwable throwable, int inSampleSize, Bitmap inBitmap) {
        SLog.fw(LOG_NAME, "onInBitmapException. imageUri=%s, imageSize=%dx%d, imageMimeType= %s, " +
                        "inSampleSize=%d, inBitmapSize=%dx%d, inBitmapByteCount=%d",
                imageUri, imageWidth, imageHeight, imageMimeType, inSampleSize,
                inBitmap.getWidth(), inBitmap.getHeight(), SketchUtils.getByteCount(inBitmap));
    }

    /**
     * 使用BitmapRegionDecoder解码图片碎片时发生错误
     *
     * @param imageUri      图片uri
     * @param imageWidth    图片宽
     * @param imageHeight   图片高
     * @param imageMimeType 图片类型
     * @param throwable     异常
     * @param srcRect       碎片区域
     */
    public void onDecodeRegionError(String imageUri, int imageWidth, int imageHeight,
                                    String imageMimeType, Throwable throwable, Rect srcRect, int inSampleSize) {
        SLog.fw(LOG_NAME, "onDecodeRegionError. imageUri=%s, imageSize=%dx%d, imageMimeType= %s, srcRect=%s, inSampleSize=%d",
                imageUri, imageWidth, imageHeight, imageMimeType, srcRect.toString(), inSampleSize);
    }

    @Override
    public String getKey() {
        return LOG_NAME;
    }
}
