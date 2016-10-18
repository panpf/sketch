package me.xiaopan.sketch.feature;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;
import java.util.Arrays;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.request.DownloadRequest;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.util.SketchUtils;

public class ExceptionMonitor implements Identifier {
    protected String logName = "ExceptionMonitor";

    private Context context;

    public ExceptionMonitor(Context context) {
        this.context = context;
    }

    /**
     * 安装磁盘缓存失败
     *
     * @param e        NoSpaceException：空间不足；UnableCreateDirException：无法创建缓存目录；UnableCreateFileException：无法在缓存目录中创建文件
     * @param cacheDir 默认的缓存目录
     */
    public void onInstallDiskCacheFailed(Exception e, File cacheDir) {
        //noinspection StringBufferReplaceableByString
        StringBuilder builder = new StringBuilder();
        builder.append(logName);
        builder.append(". InstallDiskCacheFailed");
        builder.append(". ").append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
        builder.append(". SDCardState: ").append(Environment.getExternalStorageState());
        builder.append(". cacheDir: ").append(cacheDir.getPath());
        Log.e(Sketch.TAG, builder.toString());
    }

    /**
     * 解码GIF图片失败
     *
     * @param throwable     UnsatisfiedLinkError或ExceptionInInitializerError：找不到对应到的so文件
     * @param request       请求
     * @param outWidth      图片原始宽
     * @param outHeight     图片原始高
     * @param outMimeType   图片类型
     */
    public void onDecodeGifImageFailed(Throwable throwable, LoadRequest request, int outWidth, int outHeight, String outMimeType) {
        if (throwable instanceof UnsatisfiedLinkError || throwable instanceof ExceptionInInitializerError) {
            Log.e(Sketch.TAG, "Didn't find “libpl_droidsonroids_gif.so” file, " +
                    "unable to process the GIF images. If you need to decode the GIF figure " +
                    "please go to “https://github.com/xiaopansky/sketch” " +
                    "download “libpl_droidsonroids_gif.so” file and put in your project");
            String abiInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                abiInfo = "abis=" + Arrays.toString(Build.SUPPORTED_ABIS);
            } else {
                abiInfo = "abi1=" + Build.CPU_ABI + ", abi2" + Build.CPU_ABI2;
            }
            Log.e(Sketch.TAG, SketchUtils.concat("Didn't find libpl_droidsonroids_gif.so. ", abiInfo));
        }

        Log.e(Sketch.TAG, SketchUtils.concat(logName,
                ". DecodeGifImageFailed",
                ". outWidth", "=", outWidth, ", ", "outHeight", "=", outHeight, ", ", "outMimeType", "=", outMimeType,
                ". ", request.getAttrs().getId()));
    }

    /**
     * 解码普通图片失败
     *
     * @param throwable     OutOfMemoryError：内存溢出
     * @param request       请求
     * @param outWidth      图片原始宽
     * @param outHeight     图片原始高
     * @param outMimeType   图片类型
     */
    public void onDecodeNormalImageFailed(Throwable throwable, LoadRequest request, int outWidth, int outHeight, String outMimeType) {
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
                ". DecodeNormalImageFailed",
                ". outWidth", "=", outWidth, ", ", "outHeight", "=", outHeight, ", ", "outMimeType", "=", outMimeType,
                ". ", request.getAttrs().getId()));
    }

    /**
     * 处理图片失败
     *
     * @param e         OutOfMemoryError：内存溢出
     * @param imageUri  图片uri
     * @param processor 所使用的处理器
     */
    public void onProcessImageFailed(Throwable e, String imageUri, ImageProcessor processor) {
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
                ". onProcessImageFailed",
                ". imageUri", ": ", imageUri,
                ". processor", ": ", processor.getIdentifier()));
    }

    public void onDownloadFailed(DownloadRequest request, Throwable throwable) {

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
