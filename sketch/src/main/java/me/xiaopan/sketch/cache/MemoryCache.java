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

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.drawable.RefBitmap;

/**
 * 内存缓存器
 */
public interface MemoryCache extends Identifier {
    /**
     * 放进去一张图片
     */
    void put(String key, RefBitmap refBitmap);

    /**
     * 根据给定的key获取图片
     */
    RefBitmap get(String key);

    /**
     * 根据给定的key删除图片
     */
    RefBitmap remove(String key);

    /**
     * 获取已用容量
     */
    long getSize();

    /**
     * 获取最大容量
     */
    long getMaxSize();

    /**
     * 根据level修剪内存
     *
     * @param level 修剪级别，对应APP的不同状态
     * @see android.content.ComponentCallbacks2
     */
    void trimMemory(int level);

    /**
     * 禁用了？
     */
    @SuppressWarnings("unused")
    boolean isDisabled();

    /**
     * 设置禁用
     *
     * @param disabled 禁用
     */
    void setDisabled(boolean disabled);

    /**
     * 清除缓存
     */
    void clear();

    /**
     * 是否已关闭
     */
    @SuppressWarnings("unused")
    boolean isClosed();

    /**
     * 关闭
     */
    void close();
}