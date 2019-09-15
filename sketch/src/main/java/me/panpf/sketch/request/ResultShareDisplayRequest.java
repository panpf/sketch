/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.MemoryCache;
import me.panpf.sketch.drawable.SketchBitmapDrawable;
import me.panpf.sketch.drawable.SketchRefBitmap;
import me.panpf.sketch.uri.UriModel;
import me.panpf.sketch.util.SketchUtils;

@SuppressWarnings("WeakerAccess")
public class ResultShareDisplayRequest extends DisplayRequest implements ResultShareDisplay {

    @Nullable
    private Set<ResultShareDisplay> resultShareDisplays;

    public ResultShareDisplayRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel,
                                     @NonNull String key, @NonNull DisplayOptions displayOptions,
                                     boolean useSmallerThumbnails, @NonNull RequestAndViewBinder requestAndViewBinder,
                                     @Nullable DisplayListener displayListener,
                                     @Nullable DownloadProgressListener downloadProgressListener) {
        super(sketch, uri, uriModel, key, displayOptions, useSmallerThumbnails, requestAndViewBinder, displayListener, downloadProgressListener);
    }

    @NonNull
    @Override
    public String getDisplayResultShareLog() {
        return String.format("%s@%s", SketchUtils.toHexString(this), getKey());
    }

    @NonNull
    @Override
    public String getDisplayResultShareKey() {
        return getKey();
    }

    /**
     * 可以坐顺风车？条件是内存缓存 key 一样并且内存缓存可以用，没有单独关闭内存缓存，不解码 gif 图片，没有开同步执行，请求执行器可以用
     */
    @Override
    public boolean canByDisplayResultShare() {
        MemoryCache memoryCache = getConfiguration().getMemoryCache();
        return !memoryCache.isClosed() && !memoryCache.isDisabled()
                && !getOptions().isCacheInMemoryDisabled()
                && !getOptions().isDecodeGifImage()
                && !isSync() && !getConfiguration().getExecutor().isShutdown();
    }

    @Override
    protected void submitRunLoad() {
        // 可以坐顺风车的话，就先尝试坐别人的，坐不上就自己成为顺风车主让别人坐
        if (canByDisplayResultShare()) {
            ResultShareManager resultShareManager = getConfiguration().getExecutor().getResultShareManager();
            if (resultShareManager.byDisplayResultShare(this)) {
                return;
            } else {
                resultShareManager.registerDisplayResultShareProvider(this);
            }
        }

        super.submitRunLoad();
    }

    @Override
    protected void runLoad() {
        super.runLoad();

        // 由于在submitRunLoad中会将自己注册成为顺风车主，因此一定要保证在这里取消注册
        if (canByDisplayResultShare()) {
            ResultShareManager resultShareManager = getConfiguration().getExecutor().getResultShareManager();
            resultShareManager.unregisterDisplayResultShareProvider(this);
        }
    }

    @Override
    public synchronized void byDisplayResultShare(ResultShareDisplay request) {
        if (resultShareDisplays == null) {
            synchronized (this) {
                if (resultShareDisplays == null) {
                    resultShareDisplays = new HashSet<>();
                }
            }
        }

        resultShareDisplays.add(request);
    }

    @Nullable
    @Override
    public Set<ResultShareDisplay> getDisplayResultShareSet() {
        return resultShareDisplays;
    }

    @Override
    public synchronized boolean processDisplayResultShare() {
        if (!getOptions().isCacheInDiskDisabled()) {
            MemoryCache memoryCache = getConfiguration().getMemoryCache();
            SketchRefBitmap cachedRefBitmap = memoryCache.get(getMemoryCacheKey());
            if (cachedRefBitmap != null && cachedRefBitmap.isRecycled()) {
                memoryCache.remove(getMemoryCacheKey());
                SLog.emf(getLogName(), "memory cache drawable recycled. processResultShareRequests. bitmap=%s. %s. %s",
                        cachedRefBitmap.getInfo(), getThreadName(), getKey());
                cachedRefBitmap = null;
            }

            if (cachedRefBitmap != null) {
                // 当 isDecodeGifImage 为 true 时是要播放 gif 的，而内存缓存里的 gif 图都是第一帧静态图片，所以不能用
                if (!(getOptions().isDecodeGifImage() && "image/gif".equalsIgnoreCase(cachedRefBitmap.getAttrs().getMimeType()))) {
                    // 立马标记等待使用，防止被回收
                    cachedRefBitmap.setIsWaitingUse(String.format("%s:waitingUse:fromMemory", getLogName()), true);

                    Drawable drawable = new SketchBitmapDrawable(cachedRefBitmap, ImageFrom.MEMORY_CACHE);
                    displayResult = new DisplayResult(drawable, ImageFrom.MEMORY_CACHE, cachedRefBitmap.getAttrs());
                    displayCompleted();
                    return true;
                }
            }
        }

        submitRunLoad();
        return false;
    }
}