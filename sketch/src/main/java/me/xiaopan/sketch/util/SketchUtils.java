package me.xiaopan.sketch.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.decode.ImageFormat;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.drawable.RecycleDrawable;
import me.xiaopan.sketch.request.FixedSize;

public class SketchUtils {

    /**
     * 读取APK的图标
     */
    public static Bitmap readApkIcon(Context context, String apkFilePath, boolean lowQualityImage, String logName) {
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

        Drawable drawable = null;
        try {
            drawable = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (drawable == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName, " - ", "app icon is null", " - ", apkFilePath));
            }
            return null;
        }

        // 不过滤系统默认app icon了，艹，谁他妈想到三星A5上packageManager.getDefaultActivityIcon()还会返回NinePathDrawable
//        if (drawable instanceof BitmapDrawable) {
//            Drawable defaultActivityIcon = packageManager.getDefaultActivityIcon();
//            if (defaultActivityIcon == null) {
//                if (errorCallback != null) {
//                    errorCallback.onNotFoundDefaultActivityIcon();
//                }
//                return null;
//            }
//            if (defaultActivityIcon instanceof BitmapDrawable
//                    && ((BitmapDrawable) drawable).getBitmap() == ((BitmapDrawable) defaultActivityIcon).getBitmap()) {
//                if (Sketch.isDebugMode()) {
//                    Log.w(Sketch.TAG, SketchUtils.concat(logName, " - ", "icon not found", " - ", apkFilePath));
//                }
//                return null;
//            }
//        }

        return drawableToBitmap(drawable, lowQualityImage);
    }

    /**
     * Drawable转成Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable, boolean lowQualityImage) {
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            return null;
        }

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                lowQualityImage ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 清空目录
     *
     * @return true：清空成功；false：清空失败
     */
    public static boolean cleanDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return true;
        }

        File[] files = dir.listFiles();
        boolean cleanSuccess = true;
        if (files != null) {
            for (File tempFile : files) {
                if (tempFile.isDirectory()) {
                    cleanSuccess &= cleanDir(tempFile);
                }
                cleanSuccess &= tempFile.delete();
            }
        }
        return cleanSuccess;
    }

    /**
     * 删除给定的文件，如果当前文件是目录则会删除其包含的所有的文件或目录
     *
     * @param file 给定的文件
     * @return true：删除成功；false：删除失败
     */
    @SuppressWarnings("unused")
    public static boolean deleteFile(File file) {
        if (file == null || !file.exists()) {
            return true;
        }

        if(file.isDirectory()){
            cleanDir(file);
        }
        return file.delete();
    }

    /**
     * 检查文件名是不是指定的后缀
     *
     * @param fileName 例如：test.txt
     * @param suffix   例如：.txt
     */
    public static boolean checkSuffix(String fileName, String suffix) {
        if (fileName == null) {
            return false;
        }

        // 截取后缀名
        String fileNameSuffix;
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex > -1) {
            fileNameSuffix = fileName.substring(lastIndex);
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

    /**
     * 是不是主线程
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * 获取当前进程的名字
     */
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

    /**
     * 当前进程是不是主进程
     */
    @SuppressWarnings("unused")
    public static boolean isMainProcess(Context context) {
        return context.getPackageName().equalsIgnoreCase(getProcessName(context));
    }

    /**
     * 获取短的当前进程的名字，例如进程名字为com.my.app:push，那么短名字就是:push
     */
    @SuppressWarnings("unused")
    public static String getSimpleProcessName(Context context) {
        String processName = getProcessName(context);
        if (processName == null) {
            return null;
        }
        String packageName = context.getPackageName();
        int lastIndex = processName.lastIndexOf(packageName);
        return lastIndex != -1 ? processName.substring(lastIndex + packageName.length()) : null;
    }

    /**
     * 获取App缓存目录，优先考虑SDCard上的缓存目录
     */
    public static File getAppCacheDir(Context context) {
        File appCacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appCacheDir = context.getExternalCacheDir();
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    /**
     * 获取给定目录的可用大小
     */
    public static long getAvailableBytes(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            return 0;
        }
        StatFs dirStatFs = new StatFs(dir.getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return dirStatFs.getAvailableBytes();
        } else {
            return (long) dirStatFs.getAvailableBlocks() * dirStatFs.getBlockSize();
        }
    }

    /**
     * 获取给定目录的总大小
     */
    @SuppressWarnings("unused")
    public static long getTotalBytes(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            return 0;
        }
        StatFs dirStatFs = new StatFs(dir.getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return dirStatFs.getTotalBytes();
        } else {
            return (long) dirStatFs.getBlockCount() * dirStatFs.getBlockSize();
        }
    }

    /**
     * 获取一个缓存目录
     *
     * @param dirName            目录名称
     * @param compatManyProcess  是否兼容多进程，兼容的话会在目录名字后面加上进程名字，以达到不同的进程不同的目录名字
     * @param minSpaceSize       最小空间
     * @param cleanOnNoSpace     空间不够用时就尝试清理一下
     * @param cleanOldCacheFiles 清除旧的缓存文件
     * @param expandNumber       当dirName无法使用时就会尝试dirName1、dirName2、dirName3...
     * @throws NoSpaceException 目录空间小于minSize，无法使用
     */
    public static File getCacheDir(Context context, String dirName,
                                   boolean compatManyProcess, long minSpaceSize, boolean cleanOnNoSpace,
                                   boolean cleanOldCacheFiles, int expandNumber) throws NoSpaceException {
        // 目录名字加上进程名字的后缀，不同的进程不同目录，以兼容多进程
        if (compatManyProcess) {
            String simpleProcessName = SketchUtils.getSimpleProcessName(context);
            if (simpleProcessName != null) {
                try {
                    dirName += URLEncoder.encode(simpleProcessName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        int expandCount = 0;
        while (expandCount <= expandNumber) {
            File appCacheDir = SketchUtils.getAppCacheDir(context);
            File diskCacheDir = new File(appCacheDir, dirName + (expandCount > 0 ? expandCount : ""));
            if (diskCacheDir.exists()) {
                // 目录已存在的话就尝试清除旧的缓存文件
                if (cleanOldCacheFiles) {
                    File journalFile = new File(diskCacheDir, DiskLruCache.JOURNAL_FILE);
                    if (!journalFile.exists()) {
                        SketchUtils.cleanDir(diskCacheDir);
                    }
                }
            } else {
                // 目录不存在就创建，创建结果返回false后检查还是不存在就说明创建失败
                if (!diskCacheDir.mkdirs() && !diskCacheDir.exists()) {
                    expandCount++;
                    continue;
                }
            }

            // 检查空间，少于minSpaceSize就不能用了
            long availableBytes = SketchUtils.getAvailableBytes(diskCacheDir);
            if (availableBytes < minSpaceSize) {
                // 空间不够用的时候直接清空，然后再次计算可用空间
                if (cleanOnNoSpace) {
                    SketchUtils.cleanDir(diskCacheDir);
                    availableBytes = SketchUtils.getAvailableBytes(diskCacheDir);
                }

                // 依然不够用，那不好意思了
                if (availableBytes < minSpaceSize) {
                    String availableFormatted = Formatter.formatFileSize(context, availableBytes);
                    throw new NoSpaceException(diskCacheDir, "available space is " + availableFormatted +
                            ", dir path is " + diskCacheDir.getPath());
                }
            }

            return diskCacheDir;
        }

        return new File(SketchUtils.getAppCacheDir(context), dirName);
    }

}
