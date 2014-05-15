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

package me.xiaopan.android.imageloader.cache.disk;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.task.download.DownloadRequest;
import me.xiaopan.android.imageloader.util.FileLastModifiedComparator;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import android.content.Context;
import android.util.Log;

/**
 * 默认实现的磁盘缓存器
 */
public class LruDiskCache implements DiskCache {
	private static final String LOG_NAME = LruDiskCache.class.getSimpleName();
    private static final String DEFAULT_DIRECTORY_NAME = "image_loader";
	private File dir;	//缓存目录
    private Context context;
    private FileLastModifiedComparator fileLastModifiedComparator;
    private int reserveSize = 20 * 1024 * 1024;

    public LruDiskCache(Context context) {
        this.context = context;
        this.fileLastModifiedComparator = new FileLastModifiedComparator();
    }

    private synchronized File getDir() {
        if(dir == null){
            this.dir = new File(ImageLoaderUtils.getDynamicCacheDir(context).getPath() + File.separator + DEFAULT_DIRECTORY_NAME);
        }
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }

    @Override
	public synchronized void setDir(File cacheDir) {
		if(cacheDir != null && !cacheDir.isDirectory()){
			throw new IllegalArgumentException(cacheDir.getPath() + "not a directory");
		}
		this.dir = cacheDir;
	}

    @Override
    public void setReserveSize(int reserveSize) {
        this.reserveSize = reserveSize;
    }

    @Override
	public synchronized boolean applyForSpace(long cacheFileLength){
        File cacheDir = getDir();

        // 总的可用空间
        long totalAvailableSize = Math.abs(ImageLoaderUtils.getAvailableSize(cacheDir.getPath()));

        // 如果剩余空间够用
        if(cacheFileLength <= totalAvailableSize-reserveSize){
            return true;
        }

        // 获取所有缓存文件
        File[] cacheFiles = null;
        if(cacheDir != null && cacheDir.exists()){
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
		return new File(getDir().getPath() + File.separator + ImageLoaderUtils.encodeUrl(uri));
	}

	@Override
	public synchronized File createFile(DownloadRequest request) {
		if(request.getDownloadOptions() == null || !request.getDownloadOptions().isEnableDiskCache()){
			return null;
		}

		File cacheFile = getCacheFileByUri(request.getUri());

		//如果不存在就直接返回
		if(!cacheFile.exists()){
            return cacheFile;
        }

		//是否永久有效
		long diskCachePeriodOfValidity = request.getDownloadOptions() != null?request.getDownloadOptions().getDiskCachePeriodOfValidity():0;
		if(diskCachePeriodOfValidity <= 0) return cacheFile;

		//是否过期
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MILLISECOND, (int) -diskCachePeriodOfValidity);
		if(calendar.getTimeInMillis() >= cacheFile.lastModified()){
			cacheFile.delete();
			if(request.getConfiguration().isDebugMode()){
				Log.w(ImageLoader.LOG_TAG, new StringBuilder("AvailableOfFile").append("：").append("文件过期已删除").append("；").append("文件地址").append("=").append(cacheFile.getPath()).append("；").append(request.getName()).toString());
			}
			return cacheFile;
		}
		return cacheFile;
	}

    @Override
    public synchronized void clear() {
        ImageLoaderUtils.deleteFile(dir);
        ImageLoaderUtils.deleteFile(new File(context.getCacheDir(), DEFAULT_DIRECTORY_NAME));
        ImageLoaderUtils.deleteFile(new File(context.getExternalCacheDir(), DEFAULT_DIRECTORY_NAME));
    }
}
