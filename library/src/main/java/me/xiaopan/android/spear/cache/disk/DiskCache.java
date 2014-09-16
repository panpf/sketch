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

import java.io.File;

import me.xiaopan.android.spear.request.DownloadRequest;

/**
 * 磁盘缓存器
 */
public interface DiskCache {
	/**
	 * 设置缓存目录
	 * @param cacheDir 缓存目录，在之后通过getCacheFile()方法获取缓存文件的时候就会在此文件夹下创建新文件
	 */
	public void setDiskCacheDir(File cacheDir);

    /**
     * 创建一个新的缓存文件
     * @param request 请求
     * @return 根据请求创建的缓存文件
     */
    public File createCacheFile(DownloadRequest request);

	/**
	 * 申请空间
	 * @param length 尝试腾出足够的空间，删除的原则是按照活跃度（最后一次修改时间，每一次访问都会更新最后一次修改时间）来删除文件，直到腾出足够的空间
     * @return true：申请空间成功；false：申请空间失败
	 */
	public boolean applyForSpace(long length);

    /**
     * 设置保留空间
     * @param reserveSize 保留空间，当设备剩余存储空间小于保留空间时就要返回申请失败，默认为20M
     */
    public void setReserveSize(int reserveSize);

    /**
     * 清除缓存
     */
    public void clear();
    
    /**
     * 根据URI获取缓存文件
     */
    public File getCacheFileByUri(String uri);
}