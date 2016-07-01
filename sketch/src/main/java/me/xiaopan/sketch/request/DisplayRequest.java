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

package me.xiaopan.sketch.request;

import android.graphics.drawable.Drawable;

import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.drawable.FixedRecycleBitmapDrawable;
import me.xiaopan.sketch.drawable.RecycleBitmapDrawable;
import me.xiaopan.sketch.drawable.RecycleDrawable;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 显示请求
 */
public class DisplayRequest extends LoadRequest {
    private DisplayAttrs displayAttrs;
    private DisplayOptions displayOptions;
    private DisplayBinder displayBinder;
    private DisplayListener displayListener;

    private DisplayResult displayResult;

    public DisplayRequest(
            Sketch sketch, RequestAttrs requestAttrs,
            DisplayAttrs displayAttrs, DisplayOptions displayOptions,
            DisplayBinder displayBinder, DisplayListener displayListener,
            DownloadProgressListener downloadProgressListener) {
        super(sketch, requestAttrs, displayOptions, null, downloadProgressListener);

        this.displayAttrs = displayAttrs;
        this.displayOptions = displayOptions;
        this.displayBinder = displayBinder;
        this.displayListener = displayListener;

        this.displayBinder.setDisplayRequest(this);
        setLogName("DisplayRequest");
    }

    /**
     * 获取显示属性
     */
    public DisplayAttrs getDisplayAttrs() {
        return displayAttrs;
    }

    /**
     * 获取显示选项
     */
    @Override
    public DisplayOptions getOptions() {
        return displayOptions;
    }

    @Override
    public boolean isCanceled() {
        if (super.isCanceled()) {
            return true;
        }

        // 绑定关系已经断了就直接取消请求
        if (displayBinder.isBroken()) {
            canceled(CancelCause.BIND_DISCONNECT);
            return true;
        }

        return false;
    }

    @Override
    public void failed(FailedCause failedCause) {
        if (displayListener != null || displayOptions.getFailedImageHolder() != null) {
            setFailedCause(failedCause);
            postRunFailed();
        } else {
            super.failed(failedCause);
        }
    }

    @Override
    public void canceled(CancelCause cancelCause) {
        super.canceled(cancelCause);

        if (displayListener != null) {
            postRunCanceled();
        }
    }

    @Override
    protected void postRunFailed() {
        setStatus(Status.WAIT_DISPLAY);
        super.postRunFailed();
    }

    @Override
    protected void postRunCompleted() {
        setStatus(Status.WAIT_DISPLAY);
        super.postRunCompleted();
    }

    @Override
    protected void runLoad() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("runLoad", "canceled", "get memory cache edit lock before");
            }
            return;
        }

        // 要使用内存缓存就必须上锁
        ReentrantLock memoryCacheEditLock = null;
        if (!displayOptions.isDisableCacheInDisk()) {
            setStatus(Status.GET_MEMORY_CACHE_EDIT_LOCK);
            memoryCacheEditLock = getSketch().getConfiguration().getMemoryCache().getEditLock(getAttrs().getId());
            if (memoryCacheEditLock != null) {
                memoryCacheEditLock.lock();
            }
        }

        load();

        // 解锁
        if (memoryCacheEditLock != null) {
            memoryCacheEditLock.unlock();
        }
    }

    private void load() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("runDownload", "canceled", "get memory cache edit lock after");
            }
            return;
        }

        // 检查内存缓存
        if (!displayOptions.isDisableCacheInMemory()) {
            setStatus(Status.CHECK_MEMORY_CACHE);
            Drawable cacheDrawable = getSketch().getConfiguration().getMemoryCache().get(getAttrs().getId());
            if (cacheDrawable != null) {
                RecycleDrawable recycleDrawable = (RecycleDrawable) cacheDrawable;
                if (!recycleDrawable.isRecycled()) {
                    if (Sketch.isDebugMode()) {
                        printLogI("runLoad", "from memory get drawable", "drawableInfo: " + recycleDrawable.getInfo());
                    }
                    displayResult = new DisplayResult(cacheDrawable, ImageFrom.MEMORY_CACHE, recycleDrawable.getMimeType());
                    displayCompleted();
                    return;
                } else {
                    getSketch().getConfiguration().getMemoryCache().remove(getAttrs().getId());
                    if (Sketch.isDebugMode()) {
                        printLogE("runLoad", "memory cache drawable recycled", "drawableInfo: " + recycleDrawable.getInfo());
                    }
                }
            }
        }

        // 加载
        super.runLoad();
    }

    @Override
    protected void loadCompleted() {
        LoadResult loadResult = getLoadResult();
        if (loadResult != null && loadResult.getBitmap() != null) {
            if (loadResult.getBitmap().isRecycled()) {
                if (Sketch.isDebugMode()) {
                    printLogE("loadCompleted", "decode failed", "bitmap recycled", "bitmapInfo: " + loadResult.getGifDrawable().getInfo());
                }
                failed(FailedCause.BITMAP_RECYCLED);
                return;
            }

            RecycleBitmapDrawable bitmapDrawable = new RecycleBitmapDrawable(loadResult.getBitmap());
            bitmapDrawable.setMimeType(loadResult.getMimeType());

            // 放入内存缓存中
            if (!displayOptions.isDisableCacheInMemory() && getAttrs().getId() != null) {
                getSketch().getConfiguration().getMemoryCache().put(getAttrs().getId(), bitmapDrawable);
            }

            displayResult = new DisplayResult(bitmapDrawable, loadResult.getImageFrom(), loadResult.getMimeType());
            displayCompleted();
        } else if (loadResult != null && loadResult.getGifDrawable() != null) {
            if (loadResult.getGifDrawable().isRecycled()) {
                if (Sketch.isDebugMode()) {
                    printLogE("loadCompleted", "decode failed", "gif drawable recycled", "gifInfo: " + loadResult.getGifDrawable().getInfo());
                }
                failed(FailedCause.GIF_DRAWABLE_RECYCLED);
                return;
            }

            // GifDrawable不能放入内存缓存中，因为GifDrawable需要依赖Callback才能播放，
            // 如果缓存的话就会出现一个GifDrawable被显示在多个ImageView上的情况，这时候就只有最后一个能正常播放

            displayResult = new DisplayResult(loadResult.getGifDrawable(), loadResult.getImageFrom(), loadResult.getMimeType());
            displayCompleted();
        } else {
            if (Sketch.isDebugMode()) {
                printLogE("loadCompleted", "are all null");
            }
            failed(FailedCause.DECODE_FAIL);
        }
    }

    protected void displayCompleted() {
        if (displayResult.getDrawable() instanceof RecycleDrawable) {
            RecycleDrawable recycleDrawable = (RecycleDrawable) displayResult.getDrawable();
            boolean fromMemoryCache = displayResult.getImageFrom() == ImageFrom.MEMORY_CACHE;
            String callingStation = fromMemoryCache ? "displayCompleted:fromMemory" : "displayCompleted:new";
            recycleDrawable.setIsWaitDisplay(callingStation, true);
        }

        postRunCompleted();
    }

    @Override
    protected void runCompletedInMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("runCompletedInMainThread", "canceled");
            }

            // 更新等待显示的引用计数
            if (displayResult != null && displayResult.getDrawable() instanceof RecycleDrawable) {
                RecycleDrawable recycleDrawable = (RecycleDrawable) displayResult.getDrawable();
                recycleDrawable.setIsWaitDisplay("completedCallback:cancel", false);
            }
            return;
        }

        setStatus(Status.COMPLETED);

        // 显示图片
        if (displayResult != null && displayResult.getDrawable() != null) {
            Drawable completedDrawable = displayResult.getDrawable();
            boolean isFixedSize = SketchUtils.isFixedSize(
                    displayOptions.getImageDisplayer(),
                    displayAttrs.getFixedSize(),
                    displayAttrs.getScaleType());
            if (completedDrawable instanceof RecycleBitmapDrawable && isFixedSize) {
                RecycleBitmapDrawable recycleCompletedDrawable = (RecycleBitmapDrawable) completedDrawable;
                completedDrawable = new FixedRecycleBitmapDrawable(recycleCompletedDrawable, displayAttrs.getFixedSize());
            }
            displayOptions.getImageDisplayer().display(displayBinder.getImageViewInterface(), completedDrawable);
        } else {
            if (Sketch.isDebugMode()) {
                printLogD("runCompletedInMainThread", "completedDrawable is null");
            }
        }

        // 更新等待显示的引用计数
        if (displayResult.getDrawable() instanceof RecycleDrawable) {
            RecycleDrawable recycleDrawable = (RecycleDrawable) displayResult.getDrawable();
            recycleDrawable.setIsWaitDisplay("completedCallback", false);
        }

        if (displayListener != null) {
            displayListener.onCompleted(displayResult.getImageFrom(), displayResult.getMimeType());
        }
    }

    @Override
    protected void runFailedInMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("runFailedInMainThread", "canceled");
            }
            return;
        }

        setStatus(Status.FAILED);

        // 显示失败图片
        if (displayOptions.getFailedImageHolder() != null) {
            Drawable failedDrawable = displayOptions.getFailedImageHolder().getDrawable(
                    getSketch().getConfiguration().getContext(),
                    displayOptions.getImageDisplayer(),
                    displayAttrs.getFixedSize(),
                    displayAttrs.getScaleType());
            displayOptions.getImageDisplayer().display(displayBinder.getImageViewInterface(), failedDrawable);
        } else {
            if (Sketch.isDebugMode()) {
                printLogW("runFailedInMainThread", "failedDrawable is null");
            }
        }

        if (displayListener != null) {
            displayListener.onFailed(getFailedCause());
        }
    }

    @Override
    protected void runCanceledInMainThread() {
        if (displayListener != null) {
            displayListener.onCanceled(getCancelCause());
        }
    }
}