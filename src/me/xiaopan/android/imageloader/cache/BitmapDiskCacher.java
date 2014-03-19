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

package me.xiaopan.android.imageloader.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;

import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StatFs;

public abstract class BitmapDiskCacher implements BitmapCacher {
	private static final String DEFAULT_DIRECTORY_NAME = "image_loader";
	private File diskCacheDirectory;	//磁盘缓存目录
	private long diskCacheMaxSize = -1;
	
	@Override
	public synchronized void clearDiskCache(Context context) {
		ImageLoaderUtils.delete(diskCacheDirectory);
		ImageLoaderUtils.delete(new File(context.getCacheDir(), DEFAULT_DIRECTORY_NAME));
		ImageLoaderUtils.delete(new File(context.getExternalCacheDir(), DEFAULT_DIRECTORY_NAME));
	}

	@Override
	public synchronized void clearAllCache(Context context) {
		clearMenoryCache();
		clearDiskCache(context);
	}
	
	@Override
	public synchronized void setDiskCacheDirectory(File diskCacheDirectory) {
		if(diskCacheDirectory != null && !diskCacheDirectory.isDirectory()){
			throw new IllegalArgumentException(diskCacheDirectory.getPath() + "not a directory");
		}
		this.diskCacheDirectory = diskCacheDirectory;
	}
	
	@Override
	public void setDiskCacheMaxSize(long diskCacheMaxSize) {
		this.diskCacheMaxSize = diskCacheMaxSize;
	}

	@Override
	public File getDiskCacheFile(Context context, String fileName){
		if(diskCacheDirectory != null){
			return new File(diskCacheDirectory, fileName);
		}else{
			return new File(ImageLoaderUtils.getDynamicCacheDir(context).getPath() + File.separator + DEFAULT_DIRECTORY_NAME + File.separator + fileName);
		}
	}
	
	@Override
	public synchronized void setCacheFileLength(File cacheFile, long cacheFileLength) throws IOException{
		if(cacheFile.exists()){
			/* 尝试腾出足够的空间 */
			File directory = cacheFile.getParentFile();
			File[] files = directory.listFiles();
			if(files != null){
				//如果已经超过最大限制，就按修改日期一个一个删除直到容量小于最大限制为止
				long storageAvailableSize = Math.abs(getAvailableSize(directory.getPath()));
				long maxSize = diskCacheMaxSize > 0 && diskCacheMaxSize <= storageAvailableSize?diskCacheMaxSize:storageAvailableSize;
				long usedSize = countFileLength(files);	//计算当前目录下所有文件的大小
				if(usedSize + cacheFileLength > maxSize){
					Arrays.sort(files, new FileComparator());
					File currentFile;
					for(int w = 0; w < files.length; w++){
						currentFile = files[w];
						long currentFileLength = currentFile.length();
						if(currentFile.delete()){
							usedSize -= currentFileLength;
							if(usedSize + cacheFileLength <= maxSize){
								break;
							}
						}
					}
				}
			}
			
			RandomAccessFile randomAccessFile = new RandomAccessFile(cacheFile, "rw");
			try {
				randomAccessFile.setLength(cacheFileLength);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}finally{
				ImageLoaderUtils.close(randomAccessFile);
			}
		}else{
			throw new FileNotFoundException(cacheFile.getPath());
		}
	}
	
	/**
	 * 获取文件长度，此方法的关键点在于，他也能获取目录的长度
	 * @param file
	 * @return
	 */
	public static long countFileLength(File file){
		long length = 0;
		if(file.isFile()){
			length += file.length();
		}else{
			File[] files = file.listFiles();
			for(File childFile : files){
				length += countFileLength(childFile);
			}
		}
		return length;
	}
	
	/**
	 * 获取SD卡中可用的容量
	 * @return 可用的容量；-1：SD卡不可用
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static long getAvailableSize(String directoryPath){
		StatFs statFs = new StatFs(directoryPath);
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
			return statFs.getAvailableBlocks() * statFs.getBlockSize();
		}else{
			return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
		}
	}
	
	/**
	 * 计算给定的多个文件的长度，此方法的关键点在于，他也能获取目录的长度
	 * @param files 给定的多个文件
	 * @return
	 */
	public static long countFileLength(File...files){
		int length = 0;																	
		for(File file : files){
			length += countFileLength(file);
		}
		return length;
	}
	
	private class FileComparator implements Comparator<File>{
		@Override
		public int compare(File lhs, File rhs) {
			return (int) (lhs.lastModified() - rhs.lastModified());
		}
	}
}
