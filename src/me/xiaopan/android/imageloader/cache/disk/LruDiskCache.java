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
import me.xiaopan.android.imageloader.task.TaskRequest;
import me.xiaopan.android.imageloader.util.FileLastModifiedComparator;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import android.content.Context;
import android.util.Log;

/**
 * 默认实现的磁盘缓存器
 */
public class LruDiskCache implements DiskCache {
	private static final String DEFAULT_DIRECTORY_NAME = "image_loader";
	private long size = -1;
	private File dir;	//缓存目录
    private Context context;

    public LruDiskCache(Context context) {
        this.context = context;
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
	public synchronized void setSize(long size) {
		this.size = size;
	}

	@Override
	public synchronized boolean applyForSpace(long cacheFileLength){
        long sdcardAvailableSize = Math.abs(ImageLoaderUtils.getSDCardAvailableSize());

        if(dir != null && dir.exists()){
            File[] files = dir.listFiles();
            if(files != null){
                // 计算可用空间和已用空间
                long availableSize = size > 0 && size <= sdcardAvailableSize ? size : sdcardAvailableSize;
                long usedSize = ImageLoaderUtils.countFileLength(files);

                // 如果剩余空间已经不够用了
                if((availableSize - usedSize) < cacheFileLength){
                    // 把所有文件按照最后修改日期排序，然后删除最不活跃的来腾出空间
                    Arrays.sort(files, new FileLastModifiedComparator());
                    for(File file : files){
                        long currentFileLength = file.length();
                        if(file.delete()){
                            usedSize -= currentFileLength;
                            if((availableSize - usedSize) >= cacheFileLength){
                                break;
                            }
                        }
                    }
                }
            }
        }
        return true;
	}
	
	@Override
	public synchronized File createFile(TaskRequest request) {
		if(!request.isEnableDiskCache()){
			return null;
		}
		
		File cacheFile = new File(getDir().getPath() + File.separator + ImageLoaderUtils.encodeUrl(request.getUri()));

		//是否存在
		if(!cacheFile.exists()) return cacheFile;

		//是否永久有效
		if(request.getDiskCachePeriodOfValidity() <= 0) return cacheFile;

		//是否过期
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MILLISECOND, (int) -request.getDiskCachePeriodOfValidity());
		if(calendar.getTimeInMillis() >= cacheFile.lastModified()){
			cacheFile.delete();
			if(request.getConfiguration().isDebugMode()){
				Log.w(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件过期已删除").append("；").append("文件地址").append("=").append(cacheFile.getPath()).append("；").append(request.getName()).toString());
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
