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

package me.xiaopan.android.spear.cache.disk;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.request.DisplayHelper;
import me.xiaopan.android.spear.request.DownloadRequest;
import me.xiaopan.android.spear.util.FileLastModifiedComparator;

/**
 * 默认实现的磁盘缓存器
 */
public class LruDiskCache implements DiskCache {
	private static final String LOG_NAME = LruDiskCache.class.getSimpleName();
    private static final String DEFAULT_DIRECTORY_NAME = "spear";
	private File diskCacheDir;	//缓存目录
    private Context context;
    private FileLastModifiedComparator fileLastModifiedComparator;
    private int reserveSize = 20 * 1024 * 1024;

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
                Log.e(Spear.LOG_TAG, "创建缓存文件夹失败："+ diskCacheDir.getPath());
                this.diskCacheDir = new File(getDynamicCacheDir(context).getPath() + File.separator + DEFAULT_DIRECTORY_NAME);
                if(!diskCacheDir.exists()){
                    if(!diskCacheDir.mkdirs()){
                        Log.e(Spear.LOG_TAG, "再次创建缓存文件夹失败："+ diskCacheDir.getPath());
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
        this.reserveSize = reserveSize;
    }

    @Override
	public synchronized boolean applyForSpace(long cacheFileLength){
        File cacheDir = getDiskCacheDir();
        if(cacheDir == null){
            return false;
        }

        // 总的可用空间
        long totalAvailableSize = Math.abs(getAvailableSize(cacheDir.getPath()));

        // 如果剩余空间够用
        if(cacheFileLength <= totalAvailableSize-reserveSize){
            return true;
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
                    if(cacheFileLength <= totalAvailableSize-reserveSize){
                        return true;
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
		return new File(cacheDir.getPath() + File.separator + DisplayHelper.encodeUrl(uri));
	}

	@Override
	public synchronized File createCacheFile(DownloadRequest request) {
		File cacheFile = getCacheFileByUri(request.getUri());

        if(cacheFile == null){
            return null;
        }

		//如果不存在就直接返回
		if(!cacheFile.exists()){
            return cacheFile;
        }

		//是否永久有效
		long diskCacheTimeout = request.getDiskCacheTimeout();
		if(diskCacheTimeout <= 0) return cacheFile;

		//是否过期
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MILLISECOND, (int) -diskCacheTimeout);
		if(calendar.getTimeInMillis() >= cacheFile.lastModified()){
			if(!cacheFile.delete()){
                Log.e(Spear.LOG_TAG, "删除文件失败：" + cacheFile.getPath());
            }
			if(request.getSpear().isDebugMode()){
				Log.w(Spear.LOG_TAG, LOG_NAME + "：" + "AvailableOfFile" + "：" + "文件过期已删除" + "；" + "文件地址" + "=" + cacheFile.getPath() + "；" + request.getName());
			}
			return cacheFile;
		}
		return cacheFile;
	}

    @Override
    public synchronized void clear() {
        deleteFile(diskCacheDir);
        deleteFile(new File(context.getCacheDir(), DEFAULT_DIRECTORY_NAME));
        deleteFile(new File(context.getExternalCacheDir(), DEFAULT_DIRECTORY_NAME));
    }

    /**
     * 删除给定的文件，如果当前文件是目录则会删除其包含的所有的文件或目录
     * @param file 给定的文件
     * @return true：删除成功；false：删除失败
     */
    private boolean deleteFile(File file){
        if(file.exists()){
            if(file.isFile()){
                return file.delete();
            }else{
                File[] files = file.listFiles();
                boolean deleteSuccess = true;
                if(files != null){
                    for(File tempFile : files){
                        if(!deleteFile(tempFile)){
                            deleteSuccess = false;
                            break;
                        }
                    }
                }
                if(deleteSuccess){
                    deleteSuccess = file.delete();
                }
                return deleteSuccess;
            }
        }else{
            return true;
        }
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
            return statFs.getAvailableBlocks() * statFs.getBlockSize();
        }else{
            return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
        }
    }
}
