package me.xiaopan.sketch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import me.xiaopan.sketch.drawable.RefDrawable;
import me.xiaopan.sketch.feature.large.Tile;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.DownloadRequest;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.util.SketchUtils;

public class SketchMonitor implements Identifier {
    protected String logName = "SketchMonitor";

    private Context context;

    public SketchMonitor(Context context) {
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
        //noinspection StringBufferReplaceableByString
        StringBuilder builder = new StringBuilder();
        builder.append(logName);
        builder.append(". onInstallDiskCacheError");
        builder.append(". ").append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
        builder.append(". SDCardState: ").append(Environment.getExternalStorageState());
        builder.append(". cacheDir: ").append(cacheDir.getPath());
        Log.e(Sketch.TAG, builder.toString());
    }

    /**
     * 解码GIF图片失败
     *
     * @param throwable   UnsatisfiedLinkError或ExceptionInInitializerError：找不到对应到的so文件
     * @param request     请求
     * @param outWidth    图片原始宽
     * @param outHeight   图片原始高
     * @param outMimeType 图片类型
     */
    public void onDecodeGifImageError(Throwable throwable, LoadRequest request, int outWidth, int outHeight, String outMimeType) {
        if (throwable instanceof UnsatisfiedLinkError || throwable instanceof ExceptionInInitializerError) {
            Log.e(Sketch.TAG, "Didn't find “libpl_droidsonroids_gif.so” file, " +
                    "unable to process the GIF images. If you need to decode the GIF figure " +
                    "please go to “https://github.com/xiaopansky/sketch” " +
                    "download “libpl_droidsonroids_gif.so” file and put in your project");
            String abiInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                abiInfo = String.format("abis=%s", Arrays.toString(Build.SUPPORTED_ABIS));
            } else {
                //noinspection deprecation
                abiInfo = String.format("abi1=%s, abi2%s", Build.CPU_ABI, Build.CPU_ABI2);
            }
            Log.e(Sketch.TAG, SketchUtils.concat("Didn't find libpl_droidsonroids_gif.so. ", abiInfo));
        }

        Log.e(Sketch.TAG, SketchUtils.concat(logName,
                ". onDecodeGifImageError",
                ". outWidth", "=", outWidth, ", ", "outHeight", "=", outHeight, ", ", "outMimeType", "=", outMimeType,
                ". ", request.getId()));
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
            Log.d(Sketch.TAG, SketchUtils.concat("OutOfMemoryError" +
                            ". appMemoryInfo: ",
                    "maxMemory", "=", maxMemoryFormatted,
                    ", freeMemory", "=", freeMemoryFormatted,
                    ", totalMemory", "=", totalMemoryFormatted));
        }

        Log.e(Sketch.TAG, SketchUtils.concat(logName,
                ". onDecodeNormalImageError",
                ". outWidth", "=", outWidth, ", ", "outHeight", "=", outHeight, ", ", "outMimeType", "=", outMimeType,
                ". ", request.getId()));
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
            Log.d(Sketch.TAG, SketchUtils.concat("OutOfMemoryError. appMemoryInfo: ",
                    "maxMemory", "=", maxMemoryFormatted,
                    ", freeMemory", "=", freeMemoryFormatted,
                    ", totalMemory", "=", totalMemoryFormatted));
        }

        Log.e(Sketch.TAG, SketchUtils.concat(logName,
                ". onProcessImageError",
                ". imageUri", ": ", imageUri,
                ". processor", ": ", processor.getIdentifier()));
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
    public void onTileSortError(@SuppressWarnings("UnusedParameters") IllegalArgumentException e, List<Tile> tileList, @SuppressWarnings("UnusedParameters") boolean useLegacyMergeSort) {
        Log.w(Sketch.TAG, String.format("%s. onTileSortError. %s%s",
                logName, useLegacyMergeSort ? "useLegacyMergeSort. " : "", SketchUtils.tileListToString(tileList)));
    }

    /**
     * 在即将显示时发现Bitmap被回收
     */
    public void onBitmapRecycledOnDisplay(DisplayRequest request, RefDrawable refDrawable) {
        Log.w(Sketch.TAG, String.format("%s. onBitmapRecycledOnDisplay. imageUri=%s, drawable=%s",
                logName, request.getUri(), refDrawable.getInfo()));
    }

    /**
     * 在BitmapRegionDecoder中使用inBitmap是发生异常
     *
     * @param imageUri     图片url
     * @param imageWidth   图片宽
     * @param imageHeight  图片高
     * @param srcRect      读取区域
     * @param inSampleSize 缩放比例
     * @param inBitmap     复用的inBitmap
     */
    public void onInBitmapExceptionForRegionDecoder(String imageUri, int imageWidth, int imageHeight, Rect srcRect, int inSampleSize, Bitmap inBitmap) {
        Log.w(Sketch.TAG, String.format("%s. onInBitmapExceptionForRegionDecoder. imageUri=%s, imageSize=%dx%d, srcRect=%s, inSampleSize=%d, inBitmapSize=%dx%d, inBitmapByteCount=%d",
                logName, imageUri, imageWidth, imageHeight, srcRect.toString(), inSampleSize, inBitmap.getWidth(), inBitmap.getHeight(), SketchUtils.getBitmapByteSize(inBitmap)));
    }

    /**
     * 在BitmapFactory中使用inBitmap是发生异常
     *
     * @param imageUri     图片url
     * @param imageWidth   图片宽
     * @param imageHeight  图片高
     * @param inSampleSize 缩放比例
     * @param inBitmap     复用的inBitmap
     */
    public void onInBitmapException(String imageUri, int imageWidth, int imageHeight, int inSampleSize, Bitmap inBitmap) {
        Log.w(Sketch.TAG, String.format("%s. onInBitmapException. imageUri=%s, imageSize=%dx%d, inSampleSize=%d, inBitmapSize=%dx%d, inBitmapByteCount=%d",
                logName, imageUri, imageWidth, imageHeight, inSampleSize, inBitmap.getWidth(), inBitmap.getHeight(), SketchUtils.getBitmapByteSize(inBitmap)));
    }

    @Override
    public String getIdentifier() {
        return logName;
    }

    @Override
    public StringBuilder appendIdentifier(String join, StringBuilder builder) {
        if (!TextUtils.isEmpty(join)) {
            builder.append(join);
        }
        return builder.append(logName);
    }
}
