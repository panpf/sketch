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

package me.xiaopan.android.imageloader.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.xiaopan.android.imageloader.task.download.DownloadListener;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

public class ImageLoaderUtils {
	/**
	 * 判断给定的字符串是否为null或者是空的
	 * @param string 给定的字符串
	 */
	public static boolean isEmpty(String string){
		return string == null || "".equals(string.trim());
	}
	
	/**
	 * 判断给定的字符串是否不为null且不为空
	 * @param string 给定的字符串
	 */
	public static boolean isNotEmpty(String string){
		return !isEmpty(string);
	}
	
	/**
	 * SD卡是否可用
	 * @return 只有当SD卡已经安装并且准备好了才返回true
	 */
	public static boolean isAvailable(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 编码URL
	 * @param url 待编码的URL
	 * @return 经过URL编码规则编码后的URL
	 */
	public static String encodeUrl(String url){
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return url;
		}
	}
	
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

	/**
	 * 关闭流
	 * @param stream 要关闭的流
	 */
	public static void close(Closeable stream) {
		if(stream != null){
			if(stream instanceof OutputStream){
				try {
					((OutputStream) stream).flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				stream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * 创建文件，一定要保证给定的文件是存在的，创建的关键是如果文件所在的目录不存在的话就先创建目录
     * @param file 要创建的文件
     * @return true：创建好了
     */
    public static boolean createFile(File file){
        if(!file.exists()){
            File parentDir = file.getParentFile();
            if(!parentDir.exists()){
                parentDir.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.exists();
    }
    
	/**
	 * 删除给定的文件，如果当前文件是目录则会删除其包含的所有的文件或目录
	 * @param file 给定的文件
     * @return true：删除成功；false：删除失败
	 */
	public static boolean deleteFile(File file){
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
    public static File getDynamicCacheDir(Context context){
        if(isAvailable()){
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
     * 计算文件长度，此方法的关键点在于，他也能获取目录的长度
     */
    public static long countFileLength(File file){
        long length = 0;
        if(file.isFile()){
            length += file.length();
        }else{
            File[] files = file.listFiles();
            if(files != null){
                for(File childFile : files){
                    length += countFileLength(childFile);
                }
            }
        }
        return length;
    }

    /**
     * 获取SD卡可用容量
     * @param path 路径
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getAvailableSize(String path){
        StatFs statFs = new StatFs(path);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
            return statFs.getAvailableBlocks() * statFs.getBlockSize();
        }else{
            return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
        }
    }

    /**
     * 计算给定的多个文件的长度，此方法的关键点在于，他也能获取目录的长度
     * @param files 给定的多个文件
     */
    public static long countFileLength(File...files){
        int length = 0;
        for(File file : files){
            length += countFileLength(file);
        }
        return length;
    }

    public static long copy(InputStream inputStream, OutputStream outputStream, DownloadListener downloadListener, long totalLength) throws IOException{
        int readNumber;	//读取到的字节的数量
        long completedLength = 0;
        byte[] cacheBytes = new byte[1024];//数据缓存区
        while((readNumber = inputStream.read(cacheBytes)) != -1){
            completedLength += readNumber;
            outputStream.write(cacheBytes, 0, readNumber);
            if(downloadListener != null){
                downloadListener.onUpdateProgress(totalLength, completedLength);
            }
        }
        outputStream.flush();
        return completedLength;
    }
    
    public static final void printHttpResponse(HttpResponse httpResponse){
    	Header[] headers = httpResponse.getAllHeaders();
    	for(Header header : headers){
    		System.out.println(header.getName()+": "+header.getValue());
    	}
    }
}
