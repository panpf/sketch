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
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.request.DownloadRequest;
import me.xiaopan.android.spear.util.FileLastModifiedComparator;

/**
 * 默认实现的磁盘缓存器
 */
public class LruDiskCache implements DiskCache {
	private static final String LOG_NAME = "LruDiskCache";
    private static final String DEFAULT_DIRECTORY_NAME = "spear";
    private static final int DEFAULT_RESERVE_SIZE = 100 * 1024 * 1024;
	private File diskCacheDir;	//缓存目录
    private Context context;
    private FileLastModifiedComparator fileLastModifiedComparator;
    private int reserveSize = DEFAULT_RESERVE_SIZE;
    private int maxsize = -1;

    public LruDiskCache(Context context, File diskCacheDir){
        this.context = context;
        this.fileLastModifiedComparator = new FileLastModifiedComparator();
        setDiskCacheDir(diskCacheDir);
    }

    public LruDiskCache(Context context) {
        this.context = context;
        this.fileLastModifiedComparator = new FileLastModifiedComparator();
    }

    private synchronized File getDiskCacheDir() {
        if(diskCacheDir == null){
            this.diskCacheDir = new File(getDynamicCacheDir(context).getPath() + File.separator + DEFAULT_DIRECTORY_NAME);
        }
        if(!diskCacheDir.exists()){
            if(!diskCacheDir.mkdirs()){
                Log.e(Spear.TAG, "创建缓存文件夹失败："+ diskCacheDir.getPath());
                this.diskCacheDir = new File(getDynamicCacheDir(context).getPath() + File.separator + DEFAULT_DIRECTORY_NAME);
                if(!diskCacheDir.exists()){
                    if(!diskCacheDir.mkdirs()){
                        Log.e(Spear.TAG, "再次创建缓存文件夹失败："+ diskCacheDir.getPath());
                        diskCacheDir = null;
                    }
                }
            }
        }

        return diskCacheDir;
    }

    @Override
	public synchronized void setDiskCacheDir(File cacheDir) {
		if(cacheDir != null && !cacheDir.isDirectory()){
			throw new IllegalArgumentException(cacheDir.getPath() + "not a directory");
		}
		this.diskCacheDir = cacheDir;
	}

    @Override
    public void setReserveSize(int reserveSize) {
        if(reserveSize > DEFAULT_RESERVE_SIZE){
            this.reserveSize = reserveSize;
        }
    }

    @Override
    public void setMaxsize(int maxsize) {
        this.maxsize = maxsize;
    }

    @Override
	public synchronized boolean applyForSpace(long cacheFileLength){
        File cacheDir = getDiskCacheDir();
        if(cacheDir == null){
            return false;
        }

        // 总的可用空间
        long totalAvailableSize = Math.abs(getAvailableSize(cacheDir.getPath()));
        long usedSize = 0;
        // 如果剩余空间够用
        if(totalAvailableSize-reserveSize > cacheFileLength){
            if(maxsize > 0){
                usedSize = Math.abs(countFileLength(cacheDir));
                if(usedSize+cacheFileLength < maxsize){
                    return true;
                }
            }else{
                return true;
            }
        }

        // 获取所有缓存文件
        File[] cacheFiles = null;
        if(cacheDir.exists()){
            cacheFiles = cacheDir.listFiles();
        }

        if(cacheFiles != null){
            // 把所有文件按照最后修改日期排序
            Arrays.sort(cacheFiles, fileLastModifiedComparator);

            // 然后按照顺序来删除文件直到腾出足够的空间或文件删完为止
            for(File file : cacheFiles){
                Log.w(LOG_NAME, "删除缓存文件：" + file.getPath());
                long currentFileLength = file.length();
                if(file.delete()){
                    totalAvailableSize += currentFileLength;
                    if(totalAvailableSize-reserveSize > cacheFileLength){
                        if(maxsize > 0){
                            usedSize -= currentFileLength;
                            if(usedSize+cacheFileLength < maxsize){
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
        Log.e(LOG_NAME, "申请空间失败，剩余空间："+(totalAvailableSize/1024/1024)+"M"+"; 保留空间："+(reserveSize/1024/1024)+"M; "+"; "+cacheDir.getPath());
        return false;
	}

	@Override
	public synchronized File getCacheFileByUri(String uri) {
        File cacheDir = getDiskCacheDir();
        if(cacheDir == null){
            return null;
        }
        try {
            uri =  URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		return new File(cacheDir, uri);
	}

	@Override
	public synchronized File createCacheFile(DownloadRequest request) {
		return getCacheFileByUri(request.getUri());
	}

    @Override
    public synchronized void clear() {
        deleteFile(diskCacheDir);
        deleteFile(new File(context.getCacheDir(), DEFAULT_DIRECTORY_NAME));
        deleteFile(new File(context.getExternalCacheDir(), DEFAULT_DIRECTORY_NAME));
    }

    /**
     * 获取动态获取缓存目录
     * @param context 上下文
     * @return 如果SD卡可用，就返回外部缓存目录，否则返回机身自带缓存目录
     */
    private File getDynamicCacheDir(Context context){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File dir = context.getExternalCacheDir();
            if(dir == null){
                dir = context.getCacheDir();
            }
            return dir;
        }else{
            return context.getCacheDir();
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
