package me.xiaopan.sketch.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.decode.ImageFormat;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.drawable.RecycleDrawable;
import me.xiaopan.sketch.feture.ErrorCallback;
import me.xiaopan.sketch.request.FixedSize;

public class SketchUtils {

    /**
     * 读取APK的图标
     */
    public static Bitmap decodeIconFromApk(Context context, String apkFilePath, boolean lowQualityImage, String logName, ErrorCallback errorCallback) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName, " - ", "get packageInfo is null", " - ", apkFilePath));
            }
            return null;
        }

        packageInfo.applicationInfo.sourceDir = apkFilePath;
        packageInfo.applicationInfo.publicSourceDir = apkFilePath;

        Drawable drawable = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        if (drawable == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName, " - ", "get application icon is null", " - ", apkFilePath));
            }
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            Drawable defaultActivityIcon = packageManager.getDefaultActivityIcon();
            if (defaultActivityIcon == null) {
                if (errorCallback != null) {
                    errorCallback.onNotFoundDefaultActivityIcon();
                }
                return null;
            }
            if (defaultActivityIcon instanceof BitmapDrawable
                    && ((BitmapDrawable) drawable).getBitmap() == ((BitmapDrawable) defaultActivityIcon).getBitmap()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(logName, " - ", "icon not found", " - ", apkFilePath));
                }
                return null;
            }
        }
        return drawableToBitmap(drawable, lowQualityImage);
    }

    /**
     * Drawable转成Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable, boolean lowQualityImage) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            if (drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
                return null;
            }

            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    lowQualityImage ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /**
     * 删除给定的文件，如果当前文件是目录则会删除其包含的所有的文件或目录
     *
     * @param file 给定的文件
     * @return true：删除成功；false：删除失败
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        }

        if (file.isFile()) {
            return file.delete();
        }

        File[] files = file.listFiles();
        boolean deleteSuccess = true;
        if (files != null) {
            for (File tempFile : files) {
                if (!deleteFile(tempFile)) {
                    deleteSuccess = false;
                }
            }
        }
        if (deleteSuccess) {
            deleteSuccess = file.delete();
        }
        return deleteSuccess;
    }

    public static boolean checkSuffix(String name, String suffix) {
        if (name == null) {
            return false;
        }

        // 截取后缀名
        String fileNameSuffix;
        int lastIndex = name.lastIndexOf(".");
        if (lastIndex > -1) {
            fileNameSuffix = name.substring(lastIndex);
        } else {
            return false;
        }

        return suffix.equalsIgnoreCase(fileNameSuffix);
    }

    public static String concat(Object... strings) {
        if (strings == null || strings.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Object string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }

    public static void mapping(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight, Rect rect) {
        float widthScale = (float) sourceWidth / targetWidth;
        float heightScale = (float) sourceHeight / targetHeight;
        float finalScale = widthScale < heightScale ? widthScale : heightScale;
        int srcWidth = (int) (targetWidth * finalScale);
        int srcHeight = (int) (targetHeight * finalScale);
        int srcLeft = (sourceWidth - srcWidth) / 2;
        int srcTop = (sourceHeight - srcHeight) / 2;
        rect.set(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight);
    }

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        if (closeable instanceof OutputStream) {
            try {
                ((OutputStream) closeable).flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteOldCacheFiles(File cacheDir) {
        if (cacheDir.exists()) {
            File journalFile = new File(cacheDir, DiskLruCache.JOURNAL_FILE);
            if (!journalFile.exists()) {
                deleteFile(cacheDir);
            }
        }
    }

    public static boolean isGifDrawable(Drawable drawable) {
        if (drawable != null) {
            LayerDrawable layerDrawable;
            while (drawable instanceof LayerDrawable) {
                layerDrawable = (LayerDrawable) drawable;
                if (layerDrawable.getNumberOfLayers() > 0) {
                    drawable = layerDrawable.getDrawable(layerDrawable.getNumberOfLayers() - 1);
                } else {
                    drawable = null;
                }
            }
            return drawable instanceof RecycleDrawable && ImageFormat.GIF.getMimeType().equals(((RecycleDrawable) drawable).getMimeType());
        }

        return false;
    }

    public static String viewLayoutFormatted(int size) {
        if (size >= 0) {
            return String.valueOf(size);
        } else if (size == ViewGroup.LayoutParams.MATCH_PARENT) {
            return "MATCH_PARENT";
        } else if (size == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return "WRAP_CONTENT";
        } else {
            return "Unknown";
        }
    }

    public static boolean isFixedSize(ImageDisplayer imageDisplayer, FixedSize fixedSize, ImageView.ScaleType scaleType) {
        return imageDisplayer instanceof TransitionImageDisplayer
                && fixedSize != null
                && scaleType == ImageView.ScaleType.CENTER_CROP;
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static boolean isMainProcess(Context context) {
        return context.getPackageName().equalsIgnoreCase(getProcessName(context));
    }

    public static String getSimpleProcessName(Context context) {
        String processName = getProcessName(context);
        if (processName == null) {
            return null;
        }
        String packageName = context.getPackageName();
        int lastIndex = processName.lastIndexOf(packageName);
        return lastIndex != -1 ? processName.substring(lastIndex + packageName.length()) : null;
    }
}
