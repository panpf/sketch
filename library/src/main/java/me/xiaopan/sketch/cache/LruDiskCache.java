/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.cache;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 默认实现的磁盘缓存器
 */
public class LruDiskCache implements DiskCache {
	private static final String NAME = "LruDiskCache";
    private static final String DEFAULT_DIRECTORY_NAME = "sketch";
    private static final int DEFAULT_RESERVE_SIZE = 100 * 1024 * 1024;
    private static final int DEFAULT_MAX_SIZE = 100 * 1024 * 1024;

    private File cacheDir;
    private Context context;
    private FileLastModifiedComparator fileLastModifiedComparator;
    private int reserveSize = DEFAULT_RESERVE_SIZE;
    private int maxSize = DEFAULT_MAX_SIZE;

    public LruDiskCache(Context context, File cacheDir){
        this.context = context;
        this.cacheDir = cacheDir;
        this.fileLastModifiedComparator = new FileLastModifiedComparator();
    }

    public LruDiskCache(Context context) {
        this(context, null);
    }

    @Override
	public synchronized void setCacheDir(File cacheDir) {
		this.cacheDir = cacheDir;
	}

    @Override
    public synchronized File getCacheDir() {
        // 首先尝试使用cacheDir参数指定的位置
        if(cacheDir != null) {
            if(cacheDir.exists() || cacheDir.mkdirs()){
                return cacheDir;
            }else if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "create cache dir failed", " - ", cacheDir.getPath()));
            }
        }

        File superDir;

        // 然后尝试使用SD卡的默认缓存文件夹
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO){
            superDir = context.getExternalCacheDir();
            if(superDir != null){
                cacheDir = new File(superDir, DEFAULT_DIRECTORY_NAME);
                if(cacheDir.exists() || cacheDir.mkdirs()) {
                    return cacheDir;
                }
            }
        }

        // 最后尝试使用系统的默认缓存文件夹
        superDir = context.getCacheDir();
        if(superDir != null){
            cacheDir = new File(superDir, DEFAULT_DIRECTORY_NAME);
            if(cacheDir.exists() || cacheDir.mkdirs()) {
                return cacheDir;
            }
        }

        if(Sketch.isDebugMode()){
            Log.e(Sketch.TAG, SketchUtils.concat(NAME, "get cache dir failed"));
        }
        cacheDir = null;
        return null;
    }

    @Override
    public void setReserveSize(int reserveSize) {
        if(reserveSize > DEFAULT_RESERVE_SIZE){
            this.reserveSize = reserveSize;
        }
    }

    @Override
    public long getReserveSize() {
        return reserveSize;
    }

    @Override
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public long getMaxSize() {
        return maxSize;
    }

    @Override
    public long getSize() {
        File finalCacheDir = getCacheDir();
        if(finalCacheDir != null && finalCacheDir.exists()){
            return SketchUtils.countFileLength(finalCacheDir);
        }else{
            return 0;
        }
    }

    @Override
	public synchronized boolean applyForSpace(long cacheFileLength){
        File finalCacheDir = getCacheDir();
        if(finalCacheDir == null){
            return false;
        }

        // 总的可用空间
        long totalAvailableSize = Math.abs(getAvailableSize(finalCacheDir.getPath()));
        long usedSize = 0;
        // 如果剩余空间够用
        if(totalAvailableSize-reserveSize > cacheFileLength){
            if(maxSize > 0){
                usedSize = Math.abs(SketchUtils.countFileLength(finalCacheDir));
                if(usedSize+cacheFileLength < maxSize){
                    return true;
                }
            }else{
                return true;
            }
        }

        // 获取所有缓存文件
        File[] cacheFiles = null;
        if(finalCacheDir.exists()){
            cacheFiles = finalCacheDir.listFiles();
        }

        if(cacheFiles != null){
            // 把所有文件按照最后修改日期排序
            Arrays.sort(cacheFiles, fileLastModifiedComparator);

            // 然后按照顺序来删除文件直到腾出足够的空间或文件删完为止
            for(File file : cacheFiles){
                if(Sketch.isDebugMode()){
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "deleted cache file", " - ", file.getPath()));
                }
                long currentFileLength = file.length();
                if(file.delete()){
                    totalAvailableSize += currentFileLength;
                    if(totalAvailableSize-reserveSize > cacheFileLength){
                        if(maxSize > 0){
                            usedSize -= currentFileLength;
                            if(usedSize+cacheFileLength < maxSize){
                                return true;
                            }
                        }else{
                            return true;
                        }
                    }
                }
            }
        }

        // 返回申请空间失败
        if(Sketch.isDebugMode()){
            Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "apply for space failed", " - ", "remaining space：", Formatter.formatFileSize(context, totalAvailableSize), "; reserve size：", Formatter.formatFileSize(context, reserveSize), " - ", finalCacheDir.getPath()));
        }
        return false;
	}

    @Override
    public String uriToFileName(String uri){
        if(SketchUtils.checkSuffix(uri, ".apk")){
            uri += ".png";
        }
        try {
            return URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

	@Override
	public synchronized File getCacheFile(String uri) {
        String fileName = uriToFileName(uri);
        if(fileName == null){
            return null;
        }

        File cacheFile;
        File superDir;
        File finalCacheDir;

        // 先从cacheDir参数指定的位置中找
        superDir = this.cacheDir;
        finalCacheDir = superDir;
        if(finalCacheDir != null && finalCacheDir.exists()){
            cacheFile = new File(finalCacheDir, fileName);
            if(cacheFile.exists()){
                return cacheFile;
            }
        }

        // 再从SD卡的默认缓存目录找
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO){
            superDir = context.getExternalCacheDir();
            finalCacheDir = superDir!=null?new File(superDir, DEFAULT_DIRECTORY_NAME):null;
            if(finalCacheDir != null && finalCacheDir.exists()){
                cacheFile = new File(finalCacheDir, fileName);
                if(cacheFile.exists()){
                    return cacheFile;
                }
            }
        }

        // 最后从系统的默认缓存目录找
        superDir = context.getCacheDir();
        finalCacheDir = superDir!=null?new File(superDir, DEFAULT_DIRECTORY_NAME):null;
        if(finalCacheDir != null && finalCacheDir.exists()){
            cacheFile = new File(finalCacheDir, fileName);
            if(cacheFile.exists()){
                return cacheFile;
            }
        }

        // 都没有就返回null
        return null;
	}

    @Override
    public File generateCacheFile(String uri) {
        String fileName = uriToFileName(uri);
        if(fileName == null){
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, "encode uri failed", " - ", uri));
            }
            return null;
        }
        File finalCacheDir = getCacheDir();
        if(finalCacheDir == null){
            return null;
        }
        return new File(finalCacheDir, fileName);
    }

    @Override
    public synchronized void clear() {
        File superDir;
        File finalCacheDir;

        superDir = cacheDir;
        finalCacheDir = superDir;
        if(finalCacheDir != null && finalCacheDir.exists()){
            SketchUtils.deleteFile(superDir);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO){
            superDir = context.getExternalCacheDir();
            finalCacheDir = superDir!=null?new File(superDir, DEFAULT_DIRECTORY_NAME):null;
            if(finalCacheDir != null && finalCacheDir.exists()){
                SketchUtils.deleteFile(superDir);
            }
        }

        superDir = context.getCacheDir();
        finalCacheDir = superDir!=null?new File(superDir, DEFAULT_DIRECTORY_NAME):null;
        if(finalCacheDir != null && finalCacheDir.exists()){
            SketchUtils.deleteFile(superDir);
        }
    }

    @Override
    public synchronized File saveBitmap(Bitmap bitmap, String uri) {
        if(bitmap == null || bitmap.isRecycled()){
            return null;
        }

        File cacheFile = generateCacheFile(uri);
        if(cacheFile == null){
            return null;
        }

        // 申请空间
        int bitmapSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bitmapSize = bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            bitmapSize =  bitmap.getByteCount();
        }else{
            bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
        }
        if(!applyForSpace(bitmapSize)){
            return null;
        }

        File tempFile = new File(cacheFile.getPath()+".temp");

        // 创建文件
        if(!SketchUtils.createFile(tempFile)) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, "create file failed", " - ", tempFile.getPath()));
            }
            return null;
        }

        // 写出
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tempFile, false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            if(tempFile.exists()){
                if(!tempFile.delete() && Sketch.isDebugMode()){
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "delete temp cache file failed", " - ", "tempFilePath:", tempFile.getPath(), " - ", uri));
                }
            }
            return null;
        } finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(!tempFile.renameTo(cacheFile)){
            if(Sketch.isDebugMode()){
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "rename failed", " - ", "tempFilePath:", tempFile.getPath(), " - ", uri));
            }
            tempFile.delete();
            return null;
        }
        return cacheFile;
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        builder.append(NAME).append(" - ");
        File cacheDir = getCacheDir();
        if(cacheDir != null){
            builder.append("cacheDir").append("=").append(cacheDir.getPath())
                    .append(", ");
        }
        builder.append("maxSize").append("=").append(Formatter.formatFileSize(context, maxSize))
        .append(", ")
        .append("reserveSize").append("=").append(Formatter.formatFileSize(context, reserveSize));
        return builder;
    }

    /**
     * 获取SD卡可用容量
     * @param path 路径
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private long getAvailableSize(String path){
        StatFs statFs = new StatFs(path);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
            return (long)statFs.getAvailableBlocks() * statFs.getBlockSize();
        }else{
            return statFs.getAvailableBytes();
        }
    }

    /**
     * 文件最后修改日期比较器
     */
    public static class FileLastModifiedComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            long lhsTime = lhs.lastModified();
            long rhsTime = rhs.lastModified();
            if(lhsTime == rhsTime){
                return 0;
            }else if(lhsTime > rhsTime){
                return 1;
            }else{
                return -1;
            }
        }
    }
}
