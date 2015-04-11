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

package me.xiaopan.android.spear.cache;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.util.FileLastModifiedComparator;

/**
 * 默认实现的磁盘缓存器
 */
public class LruDiskCache implements DiskCache {
	private static final String NAME = "LruDiskCache";
    private static final String DEFAULT_DIRECTORY_NAME = "spear";
    private static final int DEFAULT_RESERVE_SIZE = 100 * 1024 * 1024;
    private static final int DEFAULT_MAX_SIZE = 100 * 1024 * 1024;
	private File cacheDir;	//缓存目录
    private Context context;
    private FileLastModifiedComparator fileLastModifiedComparator;
    private int reserveSize = DEFAULT_RESERVE_SIZE;
    private int maxSize = DEFAULT_MAX_SIZE;

    public LruDiskCache(Context context, File cacheDir){
        this.context = context;
        this.fileLastModifiedComparator = new FileLastModifiedComparator();
        setCacheDir(cacheDir);
    }

    public LruDiskCache(Context context) {
        this.context = context;
        this.fileLastModifiedComparator = new FileLastModifiedComparator();
    }

    @Override
	public synchronized void setCacheDir(File cacheDir) {
		if(cacheDir != null && !cacheDir.isDirectory()){
			throw new IllegalArgumentException(cacheDir.getPath() + "not a directory");
		}
		this.cacheDir = cacheDir;
	}

    @Override
    public synchronized File getCacheDir() {
        // 首先尝试使用cacheDir参数指定的位置
        if(cacheDir != null) {
            if(cacheDir.exists() || cacheDir.mkdirs()){
                return cacheDir;
            }else if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "create cache dir failed："+ cacheDir.getPath());
            }
        }

        // 然后尝试使用SD卡的默认缓存文件夹
        File superDir = context.getExternalCacheDir();
        if(superDir != null){
            cacheDir = new File(superDir, DEFAULT_DIRECTORY_NAME);
            if(cacheDir.exists() || cacheDir.mkdirs()) {
                return cacheDir;
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
            return countFileLength(finalCacheDir);
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
                usedSize = Math.abs(countFileLength(finalCacheDir));
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
                if(Spear.isDebugMode()){
                    Log.w(Spear.TAG, NAME + " - " + "deleted cache file：" + file.getPath());
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
        if(Spear.isDebugMode()){
            Log.e(Spear.TAG, NAME + " - " + "apply for space failed, remaining space：" + Formatter.formatFileSize(context, totalAvailableSize) + "; reserve size：" + Formatter.formatFileSize(context, reserveSize) + " - " + finalCacheDir.getPath());
        }
        return false;
	}

    @Override
    public String encodeFileName(String uri){
        try {
            return URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

	@Override
	public synchronized File getCacheFile(String uri) {
        String fileName = encodeFileName(uri);
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
        superDir = context.getExternalCacheDir();
        finalCacheDir = superDir!=null?new File(superDir, DEFAULT_DIRECTORY_NAME):null;
        if(finalCacheDir != null && finalCacheDir.exists()){
            cacheFile = new File(finalCacheDir, fileName);
            if(cacheFile.exists()){
                return cacheFile;
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
        String fileName = encodeFileName(uri);
        if(fileName == null){
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
            deleteFile(superDir);
        }

        superDir = context.getExternalCacheDir();
        finalCacheDir = superDir!=null?new File(superDir, DEFAULT_DIRECTORY_NAME):null;
        if(finalCacheDir != null && finalCacheDir.exists()){
            deleteFile(superDir);
        }

        superDir = context.getCacheDir();
        finalCacheDir = superDir!=null?new File(superDir, DEFAULT_DIRECTORY_NAME):null;
        if(finalCacheDir != null && finalCacheDir.exists()){
            deleteFile(superDir);
        }
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
}
