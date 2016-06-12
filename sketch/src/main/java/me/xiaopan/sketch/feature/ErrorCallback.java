package me.xiaopan.sketch.feature;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;
import java.util.Arrays;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.util.SketchUtils;

public class ErrorCallback implements Identifier {
    protected String logName = "DefaultErrorCallback";

    public void onInstallDiskCacheFailed(Exception e, File cacheDir) {
        Log.e(Sketch.TAG, SketchUtils.concat("InstallDiskCacheFailed. ", "SDCardState", "=", Environment.getExternalStorageState()));
        Log.e(Sketch.TAG, SketchUtils.concat(logName, " - ", "InstallDiskCacheFailed", ": ", e.getMessage(), " - ", cacheDir.getPath()));
    }

    public void onDecodeGifImageFailed(Throwable throwable, LoadRequest request, BitmapFactory.Options boundsOptions) {
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
                " - ", "DecodeGifImageFailed",
                " - ", "outWidth", "=", boundsOptions.outWidth, ", ", "outHeight", "=", boundsOptions.outHeight, ", " +
                        "", "outMimeType", "=", boundsOptions.outMimeType,
                " - ", request.getAttrs().getId()));
    }

    public void onDecodeNormalImageFailed(Throwable throwable, LoadRequest request, BitmapFactory.Options boundsOptions) {
        if (throwable instanceof OutOfMemoryError) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            Context context = request.getSketch().getConfiguration().getContext();
            String maxMemoryFormatted = Formatter.formatFileSize(context, maxMemory);
            String freeMemoryFormatted = Formatter.formatFileSize(context, freeMemory);
            String totalMemoryFormatted = Formatter.formatFileSize(context, totalMemory);
            Log.d(Sketch.TAG, SketchUtils.concat("OutOfMemoryError. appMemoryInfo: ",
                    "maxMemory", "=", maxMemoryFormatted,
                    "freeMemory", "=", freeMemoryFormatted,
                    "totalMemory", "=", totalMemoryFormatted));
        }

        Log.e(Sketch.TAG, SketchUtils.concat(logName,
                " - ", "DecodeNormalImageFailed",
                " - ", "outWidth", "=", boundsOptions.outWidth, ", ", "outHeight", "=", boundsOptions.outHeight, ", " +
                        "", "outMimeType", "=", boundsOptions.outMimeType,
                " - ", request.getAttrs().getId()));
    }

    @Override
    public String getIdentifier() {
        return logName;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(logName);
    }
}
