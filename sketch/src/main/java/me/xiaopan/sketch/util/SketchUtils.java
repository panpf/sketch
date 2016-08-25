/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.sketch.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.decode.ImageFormat;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.drawable.BindDrawable;
import me.xiaopan.sketch.drawable.RecyclerDrawable;
import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.FixedSize;
import me.xiaopan.sketch.request.ImageViewInterface;
import pl.droidsonroids.gif.GifDrawable;

public class SketchUtils {

    /**
     * 读取APK的图标
     */
    public static Bitmap readApkIcon(Context context, String apkFilePath, boolean lowQualityImage, String logName) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName, ". get packageInfo is null", ". ", apkFilePath));
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
                Log.w(Sketch.TAG, SketchUtils.concat(logName, ". app icon is null", ". ", apkFilePath));
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
//                    Log.w(Sketch.TAG, SketchUtils.concat(logName, ". icon not found", ". ", apkFilePath));
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

        if (file.isDirectory()) {
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
            return drawable instanceof RecyclerDrawable && ImageFormat.GIF.getMimeType().equals(((RecyclerDrawable) drawable).getMimeType());
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
     * 获取所有可用的SD卡的路径
     *
     * @return 所有可用的SD卡的路径
     */
    @SuppressLint("LongLogTag")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String[] getAllAvailableSdcardPath(Context context) {
        // 获取所有的存储器的路径
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return new String[]{Environment.getExternalStorageDirectory().getPath()};
            } else {
                return null;
            }
        }

        String[] paths;
        Method getVolumePathsMethod;
        try {
            getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
        } catch (NoSuchMethodException e) {
            Log.e("getAllAvailableSdcardPath", "not found StorageManager.getVolumePaths() method");
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return new String[]{Environment.getExternalStorageDirectory().getPath()};
            } else {
                return null;
            }
        }

        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            paths = (String[]) getVolumePathsMethod.invoke(sm);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        if (paths == null || paths.length == 0) {
            return null;
        }

        // 去掉不可用的存储器
        List<String> storagePathList = new LinkedList<String>();
        Collections.addAll(storagePathList, paths);
        Iterator<String> storagePathIterator = storagePathList.iterator();

        String path;
        Method getVolumeStateMethod = null;
        while (storagePathIterator.hasNext()) {
            path = storagePathIterator.next();
            if (getVolumeStateMethod == null) {
                try {
                    getVolumeStateMethod = StorageManager.class.getMethod("getVolumeState", String.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            String status;
            try {
                status = (String) getVolumeStateMethod.invoke(sm, path);
            } catch (Exception e) {
                e.printStackTrace();
                storagePathIterator.remove();
                continue;
            }
            if (!(Environment.MEDIA_MOUNTED.equals(status) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(status))) {
                storagePathIterator.remove();
            }
        }
        return storagePathList.toArray(new String[storagePathList.size()]);
    }

    public static String addProcessName(Context context, String dirName) {
        // 目录名字加上进程名字的后缀，不同的进程不同目录，以兼容多进程
        String simpleProcessName = SketchUtils.getSimpleProcessName(context);
        if (simpleProcessName != null) {
            try {
                dirName += URLEncoder.encode(simpleProcessName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return dirName;
    }

    public static File getDefaultSketchCacheDir(Context context, String dirName, boolean compatManyProcess) {
        File appCacheDir = SketchUtils.getAppCacheDir(context);
        return new File(appCacheDir, compatManyProcess ? addProcessName(context, dirName) : dirName);
    }

    public static boolean testCreateFile(File cacheDir) throws Exception {
        File parentDir = cacheDir;
        while (parentDir != null) {
            // 先向上找到一个已存在的目录
            if (!parentDir.exists()) {
                parentDir = cacheDir.getParentFile();
                continue;
            }

            // 然后尝试创建文件
            File file = new File(parentDir, "create_test.temp");

            // 已存在就先删除，删除失败就抛异常
            if (file.exists() && !file.delete()) {
                throw new Exception("Delete old test file failed: " + file.getPath());
            }

            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();

            if (file.exists()) {
                if (file.delete()) {
                    return true;
                } else {
                    throw new Exception("Delete test file failed: " + file.getPath());
                }
            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * 创建缓存目录，会优先在sdcard上创建
     *
     * @param dirName            目录名称
     * @param compatManyProcess  目录名称是否加上进程名
     * @param minSpaceSize       最小空间
     * @param cleanOnNoSpace     空间不够用时就尝试清理一下
     * @param cleanOldCacheFiles 清除旧的缓存文件
     * @param expandNumber       当dirName无法使用时就会尝试dirName1、dirName2、dirName3...
     * @return 你应当以返回的目录为最终可用的目录
     * @throws NoSpaceException：可用空间小于minSpaceSize；UnableCreateDirException：无法创建缓存目录；UnableCreateFileException：无法在缓存目录中创建文件
     */
    public static File buildCacheDir(Context context, String dirName, boolean compatManyProcess, long minSpaceSize, boolean cleanOnNoSpace,
                                     boolean cleanOldCacheFiles, int expandNumber) throws NoSpaceException, UnableCreateDirException, UnableCreateFileException {
        List<File> appCacheDirs = new LinkedList<File>();

        String[] sdcardPaths = getAllAvailableSdcardPath(context);
        if (sdcardPaths != null && sdcardPaths.length > 0) {
            for (String sdcardPath : sdcardPaths) {
                appCacheDirs.add(new File(sdcardPath, "Android" + File.separator + "data" + File.separator + context.getPackageName() + File.separator + "cache"));
            }
        }
        appCacheDirs.add(context.getCacheDir());

        String diskCacheDirName = compatManyProcess ? addProcessName(context, dirName) : dirName;

        NoSpaceException noSpaceException = null;
        UnableCreateFileException unableCreateFileException = null;
        File diskCacheDir = null;
        int expandCount;

        for (File appCacheDir : appCacheDirs) {
            expandCount = 0;
            while (expandCount <= expandNumber) {
                diskCacheDir = new File(appCacheDir, diskCacheDirName + (expandCount > 0 ? expandCount : ""));

                if (diskCacheDir.exists()) {
                    // 目录已存在的话就尝试清除旧的缓存文件
                    if (cleanOldCacheFiles) {
                        File journalFile = new File(diskCacheDir, DiskLruCache.JOURNAL_FILE);
                        if (!journalFile.exists()) {
                            cleanDir(diskCacheDir);
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
                long availableBytes = getAvailableBytes(diskCacheDir);
                if (availableBytes < minSpaceSize) {
                    // 空间不够用的时候直接清空，然后再次计算可用空间
                    if (cleanOnNoSpace) {
                        cleanDir(diskCacheDir);
                        availableBytes = getAvailableBytes(diskCacheDir);
                    }

                    // 依然不够用，那不好意思了
                    if (availableBytes < minSpaceSize) {
                        String availableFormatted = Formatter.formatFileSize(context, availableBytes);
                        String minSpaceFormatted = Formatter.formatFileSize(context, minSpaceSize);
                        noSpaceException = new NoSpaceException("Need " + availableFormatted + ", with only " + minSpaceFormatted + " in " + diskCacheDir.getPath());
                        break;
                    }
                }

                // 创建文件测试
                try {
                    if (testCreateFile(diskCacheDir)) {
                        return diskCacheDir;
                    } else {
                        unableCreateFileException = new UnableCreateFileException("Unable create file in " + diskCacheDir.getPath());
                        expandCount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    unableCreateFileException = new UnableCreateFileException(e.getClass().getSimpleName() + ": " + e.getMessage());
                    expandCount++;
                }
            }
        }

        if (noSpaceException != null) {
            throw noSpaceException;
        } else if (unableCreateFileException != null) {
            throw unableCreateFileException;
        } else {
            throw new UnableCreateDirException("Unable create dir: " + (diskCacheDir != null ? diskCacheDir.getPath() : "null"));
        }
    }

    /**
     * 从ImageViewInterface上查找DisplayRequest
     */
    public static DisplayRequest findDisplayRequest(ImageViewInterface imageViewInterface) {
        if (imageViewInterface != null) {
            final Drawable drawable = imageViewInterface.getDrawable();
            if (drawable != null && drawable instanceof BindDrawable) {
                return ((BindDrawable) drawable).getRequest();
            }
        }
        return null;
    }

    public static int getBitmapByteCount(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    public static String getInfo(String type, Bitmap bitmap, String mimeType, long byteCount) {
        if (bitmap != null) {
            if(TextUtils.isEmpty(type)){
                type = "Bitmap";
            }
            return SketchUtils.concat(type
                    , "(", "mimeType=", mimeType
                    , "; ", "hashCode=", Integer.toHexString(bitmap.hashCode())
                    , "; ", "size=", bitmap.getWidth(), "x", bitmap.getHeight()
                    , "; ", "config=", bitmap.getConfig() != null ? bitmap.getConfig().name() : null
                    , "; ", "byteCount=", byteCount, ")");
        } else {
            return null;
        }
    }

    public static String getInfo(String type, Bitmap bitmap, String mimeType) {
        return getInfo(type, bitmap, mimeType, getBitmapByteCount(bitmap));
    }

    public static String getInfo(GifDrawable gifDrawable){
        Bitmap bitmap = gifDrawable.getBitmap();
        return getInfo("GifDrawable", bitmap, "image/gif", (int) gifDrawable.getAllocationByteCount());
    }

    public static Drawable getLastDrawable(Drawable drawable) {
        if (drawable != null) {
            if (drawable instanceof LayerDrawable) {
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                for (int i = layerDrawable.getNumberOfLayers() - 1; i >= 0; i--) {
                    Drawable childDrawable = getLastDrawable(layerDrawable.getDrawable(i));
                    if (childDrawable != null) {
                        return childDrawable;
                    }
                }
            } else {
                return drawable;
            }
        }
        return null;
    }
}
