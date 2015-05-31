package me.xiaopan.sketch.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import me.xiaopan.sketch.Sketch;

public class SketchUtils {

    /**
     * 解压APK的图标
     * @param context 上下文
     * @param apkFilePath APK文件的位置
     * @return  APK的图标
     */
    public static Bitmap decodeIconFromApk(Context context, String apkFilePath, boolean lowQualityImage, String logName){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
        if(packageInfo == null){
            return null;
        }

        packageInfo.applicationInfo.sourceDir = apkFilePath;
        packageInfo.applicationInfo.publicSourceDir = apkFilePath;

        Drawable drawable = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        if(drawable == null){
            return null;
        }
        if(drawable instanceof BitmapDrawable && ((BitmapDrawable) drawable).getBitmap() == ((BitmapDrawable) packageManager.getDefaultActivityIcon()).getBitmap()){
            if(Sketch.isDebugMode()){
                Log.w(Sketch.TAG, SketchUtils.concat(logName, " - ", "icon not found", " - ", apkFilePath));
            }
            return null;
        }
        return drawableToBitmap(drawable, lowQualityImage);
    }

    /**
     * Drawable转成Bitmap
     * @param drawable drawable
     * @return bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable, boolean lowQualityImage){
        if(drawable == null ){
            return null;
        }else if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }else{
            if(drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0){
                return null;
            }

            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), lowQualityImage? Bitmap.Config.ARGB_4444: Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /**
     * 创建文件
     * @param file
     * @return
     */
    public static boolean createFile(File file){
        if(file.exists()){
            return true;
        }

        File parentDir = file.getParentFile();
        if(!parentDir.exists() && !parentDir.mkdirs()){
            return false;
        }
        try {
            if(!file.createNewFile()){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 计算文件长度，此方法的关键点在于，他也能获取目录的长度
     * @param file 要计算的文件
     * @return 长度
     */
    public static long countFileLength(File file){
        if(!file.exists()){
            return 0;
        }

        if(file.isFile()){
            return file.length();
        }

        File[] childFiles = file.listFiles();
        if(childFiles == null || childFiles.length <= 0){
            return 0;
        }

        List<File> fileList = new LinkedList<File>();
        Collections.addAll(fileList, childFiles);
        long length = 0;
        for(File childFile : fileList){
            if(childFile.isFile()){
                length += childFile.length();
            }else{
                childFiles = childFile.listFiles();
                if(childFiles == null || childFiles.length <= 0){
                    continue;
                }
                Collections.addAll(fileList, childFiles);
            }
        }
        return length;
    }

    /**
     * 删除给定的文件，如果当前文件是目录则会删除其包含的所有的文件或目录
     * @param file 给定的文件
     * @return true：删除成功；false：删除失败
     */
    public static boolean deleteFile(File file){
        if(!file.exists()){
            return true;
        }

        if(file.isFile()){
            return file.delete();
        }

        File[] files = file.listFiles();
        boolean deleteSuccess = true;
        if(files != null){
            for(File tempFile : files){
                if(!deleteFile(tempFile)){
                    deleteSuccess = false;
                }
            }
        }
        if(deleteSuccess){
            deleteSuccess = file.delete();
        }
        return deleteSuccess;
    }

    public static boolean checkSuffix(String name, String suffix){
        if(name == null){
            return false;
        }

        // 截取后缀名
        String fileNameSuffix;
        int lastIndex = name.lastIndexOf(".");
        if(lastIndex > -1){
            fileNameSuffix = name.substring(lastIndex);
        }else{
            return false;
        }

        return suffix.equalsIgnoreCase(fileNameSuffix);
    }

    public static String concat(Object... strings){
        if(strings == null || strings.length == 0){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for(Object string : strings){
            builder.append(string);
        }
        return builder.toString();
    }

    public static void mapping(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight, Rect rect){
        float widthScale = (float)sourceWidth/targetWidth;
        float heightScale = (float)sourceHeight/targetHeight;
        float finalScale = widthScale<heightScale?widthScale:heightScale;
        int srcWidth = (int)(targetWidth*finalScale);
        int srcHeight = (int)(targetHeight*finalScale);
        int srcLeft = (sourceWidth - srcWidth)/2;
        int srcTop = (sourceHeight - srcHeight)/2;
        rect.set(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
    }
}
