package me.panpf.sketch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.text.format.Formatter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import me.panpf.sketch.drawable.SketchDrawable;
import me.panpf.sketch.process.ImageProcessor;
import me.panpf.sketch.request.DisplayRequest;
import me.panpf.sketch.request.DownloadRequest;
import me.panpf.sketch.request.LoadRequest;
import me.panpf.sketch.util.SketchUtils;
import me.panpf.sketch.zoom.block.Block;

/**
 * 负责输出错误信息，你可借此记录错误日志
 */
public class ErrorTracker {
    private static final String NAME = "ErrorTracker";

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
    public void onInstallDiskCacheError(@NonNull Exception e, @NonNull File cacheDir) {
        SLog.e(NAME, "onInstallDiskCacheError. %s: %s. SDCardState: %s. cacheDir: %s",
                e.getClass().getSimpleName(), e.getMessage(), Environment.getExternalStorageState(), cacheDir.getPath());
    }

    /**
     * 找不到libpl_droidsonroids_gif.so文件错误
     *
     * @param e UnsatisfiedLinkError或ExceptionInInitializerError：找不到对应到的so文件
     */
    public void onNotFoundGifSoError(@NonNull Throwable e) {
        SLog.e(NAME, "Didn't find “libpl_droidsonroids_gif.so” file, unable decode the GIF images. " +
                "Please go to “https://github.com/panpf/sketch” find how to import the sketch-gif library");

        String abis;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Arrays.toString(Build.SUPPORTED_ABIS);
        } else {
            abis = Arrays.toString(new String[]{Build.CPU_ABI, Build.CPU_ABI2});
        }
        SLog.e(NAME, "abis=%s", abis);
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
    public void onDecodeGifImageError(@NonNull Throwable throwable, @NonNull LoadRequest request,
                                      int outWidth, int outHeight, @NonNull String outMimeType) {
        SLog.e(NAME, "onDecodeGifImageError. outWidth=%d, outHeight=%d + outMimeType=%s. %s",
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
    public void onDecodeNormalImageError(@NonNull Throwable throwable, @NonNull LoadRequest request,
                                         int outWidth, int outHeight, @NonNull String outMimeType) {
        if (throwable instanceof OutOfMemoryError) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            String maxMemoryFormatted = Formatter.formatFileSize(context, maxMemory);
            String freeMemoryFormatted = Formatter.formatFileSize(context, freeMemory);
            String totalMemoryFormatted = Formatter.formatFileSize(context, totalMemory);
            SLog.e(NAME, "OutOfMemoryError. appMemoryInfo: maxMemory=%s, freeMemory=%s, totalMemory=%s",
                    maxMemoryFormatted, freeMemoryFormatted, totalMemoryFormatted);
        }

        SLog.e(NAME, "onDecodeNormalImageError. outWidth=%d, outHeight=%d, outMimeType=%s. %s",
                outWidth, outHeight, outMimeType, request.getKey());
    }

    /**
     * 处理图片失败
     *
     * @param e         OutOfMemoryError：内存溢出
     * @param imageUri  图片uri
     * @param processor 所使用的处理器
     */
    public void onProcessImageError(@NonNull Throwable e, @NonNull String imageUri, @NonNull ImageProcessor processor) {
        if (e instanceof OutOfMemoryError) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            String maxMemoryFormatted = Formatter.formatFileSize(context, maxMemory);
            String freeMemoryFormatted = Formatter.formatFileSize(context, freeMemory);
            String totalMemoryFormatted = Formatter.formatFileSize(context, totalMemory);
            SLog.d(NAME, "OutOfMemoryError. appMemoryInfo: maxMemory=%s, freeMemory=%s, totalMemory=%s",
                    maxMemoryFormatted, freeMemoryFormatted, totalMemoryFormatted);
        }

        SLog.e(NAME, "onProcessImageError. imageUri: %s. processor: %s",
                imageUri, processor.toString());
    }

    /**
     * 下载出现错误
     *
     * @param request   出错的下载请求
     * @param throwable 异常
     */
    @SuppressWarnings("UnusedParameters")
    public void onDownloadError(@NonNull DownloadRequest request, @NonNull Throwable throwable) {

    }

    /**
     * 碎片排序错误，Java7的排序算法在检测到A大于B, B小于C, 但是A小于等于C的时候就会抛出异常
     *
     * @param e                  异常
     * @param blockList           碎片列表
     * @param useLegacyMergeSort 当前是否使用旧的排序算法
     */
    public void onBlockSortError(@NonNull IllegalArgumentException e, @NonNull List<Block> blockList, boolean useLegacyMergeSort) {
        String legacy = useLegacyMergeSort ? "useLegacyMergeSort. " : "";
        SLog.e(NAME, "onBlockSortError. %s%s", legacy, SketchUtils.blockListToString(blockList));
    }

    /**
     * 在即将显示时发现 Bitmap 被回收
     */
    public void onBitmapRecycledOnDisplay(@NonNull DisplayRequest request, @NonNull SketchDrawable sketchDrawable) {
        SLog.e(NAME, "onBitmapRecycledOnDisplay. imageUri=%s, drawable=%s",
                request.getUri(), sketchDrawable.getInfo());
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
    public void onInBitmapDecodeError(@NonNull String imageUri, int imageWidth, int imageHeight, @NonNull String imageMimeType,
                                      @NonNull Throwable throwable, int inSampleSize, @NonNull Bitmap inBitmap) {
        SLog.e(NAME, "onInBitmapException. imageUri=%s, imageSize=%dx%d, imageMimeType= %s, " +
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
    public void onDecodeRegionError(@NonNull String imageUri, int imageWidth, int imageHeight, @NonNull String imageMimeType,
                                    @NonNull Throwable throwable, @NonNull Rect srcRect, int inSampleSize) {
        SLog.e(NAME, "onDecodeRegionError. imageUri=%s, imageSize=%dx%d, imageMimeType= %s, srcRect=%s, inSampleSize=%d",
                imageUri, imageWidth, imageHeight, imageMimeType, srcRect.toString(), inSampleSize);
    }

    @NonNull
    @Override
    public String toString() {
        return NAME;
    }
}
