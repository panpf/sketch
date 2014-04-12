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

import me.xiaopan.android.imageloader.task.TaskRequest;

import java.io.File;

/**
 * 磁盘缓存器
 */
public interface DiskCache {
	/**
	 * 设置缓存目录
	 * @param cacheDir 缓存目录，在之后通过getCacheFile()方法获取缓存文件的时候就会在此文件夹下创建新文件
	 */
	public void setDir(File cacheDir);

    /**
     * 创建一个新的缓存文件
     * @param request 请求
     * @return 根据请求创建的缓存文件
     */
    public File createFile(TaskRequest request);

	/**
	 * 设置容量
	 * @param size 容量，在使用applyForSpace()方法申请空间的时候会计算当前缓存目录的容量是否超过此限制，如果超过就会按照活跃度（最后一次修改时间，每一次访问都会更新最后一次修改时间）来删除文件，直到腾出足够的空间
	 */
	public void setSize(long size);

	/**
	 * 申请空间
	 * @param length 尝试腾出足够的空间，删除的原则是按照活跃度（最后一次修改时间，每一次访问都会更新最后一次修改时间）来删除文件，直到腾出足够的空间
     * @return true：申请空间成功；false：申请空间失败
	 */
	public boolean applyForSpace(long length);

    /**
     * 清除缓存
     */
    public void clear();
}