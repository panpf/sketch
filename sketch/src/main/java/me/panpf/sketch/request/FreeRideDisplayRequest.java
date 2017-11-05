/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.request;

import android.graphics.drawable.Drawable;

import java.util.HashSet;
import java.util.Set;

import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.MemoryCache;
import me.panpf.sketch.drawable.SketchBitmapDrawable;
import me.panpf.sketch.drawable.SketchRefBitmap;
import me.panpf.sketch.uri.UriModel;
import me.panpf.sketch.util.SketchUtils;

/**
 * 支持显示顺风车的请求
 */
public class FreeRideDisplayRequest extends DisplayRequest implements FreeRideManager.DisplayFreeRide {
    private Set<FreeRideManager.DisplayFreeRide> displayFreeRideSet;

    public FreeRideDisplayRequest(Sketch sketch, String uri, UriModel uriModel, String key, DisplayOptions displayOptions,
                                  ViewInfo viewInfo, RequestAndViewBinder requestAndViewBinder,
                                  DisplayListener displayListener, DownloadProgressListener downloadProgressListener) {
        super(sketch, uri, uriModel, key, displayOptions, viewInfo, requestAndViewBinder, displayListener, downloadProgressListener);
    }

    @Override
    public String getDisplayFreeRideLog() {
        return String.format("%s@%s", SketchUtils.toHexString(this), getKey());
    }

    @Override
    public String getDisplayFreeRideKey() {
        return getKey();
    }

    /**
     * 可以坐顺风车？条件是内存缓存 key 一样并且内存缓存可以用，没有单独关闭内存缓存，不解码 gif 图片，没有开同步执行，请求执行器可以用
     */
    @Override
    public boolean canByDisplayFreeRide() {
        MemoryCache memoryCache = getConfiguration().getMemoryCache();
        return !memoryCache.isClosed() && !memoryCache.isDisabled()
                && !getOptions().isCacheInMemoryDisabled()
                && !getOptions().isDecodeGifImage()
                && !isSync() && !getConfiguration().getExecutor().isShutdown();
    }

    @Override
    protected void submitRunLoad() {
        // 可以坐顺风车的话，就先尝试坐别人的，坐不上就自己成为顺风车主让别人坐
        if (canByDisplayFreeRide()) {
            FreeRideManager freeRideManager = getConfiguration().getFreeRideManager();
            if (freeRideManager.byDisplayFreeRide(this)) {
                return;
            } else {
                freeRideManager.registerDisplayFreeRideProvider(this);
            }
        }

        super.submitRunLoad();
    }

    @Override
    protected void runLoad() {
        super.runLoad();

        // 由于在submitRunLoad中会将自己注册成为顺风车主，因此一定要保证在这里取消注册
        if (canByDisplayFreeRide()) {
            FreeRideManager freeRideManager = getConfiguration().getFreeRideManager();
            freeRideManager.unregisterDisplayFreeRideProvider(this);
        }
    }

    @Override
    public synchronized void byDisplayFreeRide(FreeRideManager.DisplayFreeRide request) {
        if (displayFreeRideSet == null) {
            synchronized (this) {
                if (displayFreeRideSet == null) {
                    displayFreeRideSet = new HashSet<>();
                }
            }
        }

        displayFreeRideSet.add(request);
    }

    @Override
    public Set<FreeRideManager.DisplayFreeRide> getDisplayFreeRideSet() {
        return displayFreeRideSet;
    }

    @Override
    public synchronized boolean processDisplayFreeRide() {
        if (!getOptions().isCacheInDiskDisabled()) {
            MemoryCache memoryCache = getConfiguration().getMemoryCache();
            SketchRefBitmap cachedRefBitmap = memoryCache.get(getMemoryCacheKey());
            if (cachedRefBitmap != null && cachedRefBitmap.isRecycled()) {
                memoryCache.remove(getMemoryCacheKey());
                SLog.e(getLogName(), "memory cache drawable recycled. processFreeRideRequests. bitmap=%s. %s. %s",
                        cachedRefBitmap.getInfo(), getThreadName(), getKey());
                cachedRefBitmap = null;
            }

            if (cachedRefBitmap != null) {
                // 立马标记等待使用，防止被回收
                cachedRefBitmap.setIsWaitingUse(String.format("%s:waitingUse:fromMemory", getLogName()), true);

                Drawable drawable = new SketchBitmapDrawable(cachedRefBitmap, ImageFrom.MEMORY_CACHE);
                displayResult = new DisplayResult(drawable, ImageFrom.MEMORY_CACHE, cachedRefBitmap.getAttrs());
                displayCompleted();
                return true;
            }
        }

        submitRunLoad();
        return false;
    }
}