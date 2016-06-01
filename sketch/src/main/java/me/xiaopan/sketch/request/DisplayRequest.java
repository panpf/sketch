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
import android.util.Log;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.MemoryCache;
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
            DownloadProgressListener progressListener) {
        super(sketch, requestAttrs, displayOptions, null, progressListener);

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
    @SuppressWarnings("WeakerAccess")
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

        // 绑定关系已经断了的话就直接取消请求
        if (displayBinder.isBroken()) {
            canceled(CancelCause.NORMAL);
            return true;
        }

        return false;
    }

    @Override
    public void failed(FailedCause failedCause) {
        // 显示请求里 失败的时候不能直接改状态为失败，要等到在主线程中显示了失败图片后才能改为失败状态，因此这里恢复一下
        Status oldStatus = getStatus();
        super.failed(failedCause);
        setStatus(oldStatus);

        // 不能过滤displayListener != null，因为还要显示失败图片呢
        postRunFailed();
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
    protected void submitRunDispatch() {
        setStatus(Status.WAIT_DISPATCH);
        super.submitRunDispatch();
    }

    @Override
    protected void submitRunDownload() {
        setStatus(Status.WAIT_DOWNLOAD);
        super.submitRunDownload();
    }

    @Override
    protected void submitRunLoad() {
        setStatus(Status.WAIT_LOAD);
        super.submitRunLoad();
    }

    @Override
    protected void runLoad() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runLoad",
                        " - ", "canceled",
                        " - ", "startLoad",
                        " - ", getAttrs().getId()));
            }
            return;
        }

        setStatus(Status.LOADING);

        // 检查内存缓存中是否已经存在了
        if (displayOptions.isCacheInMemory()) {
            Drawable cacheDrawable = getSketch().getConfiguration().getMemoryCache().get(getAttrs().getId());
            if (cacheDrawable != null) {
                RecycleDrawable recycleDrawable = (RecycleDrawable) cacheDrawable;
                if (!recycleDrawable.isRecycled()) {
                    if (Sketch.isDebugMode()) {
                        Log.i(Sketch.TAG, SketchUtils.concat(getLogName(),
                                " - ", "runLoad",
                                " - ", "from memory get drawable",
                                " - ", recycleDrawable.getInfo(),
                                " - ", getAttrs().getId()));
                    }
                    this.displayResult = new DisplayResult(cacheDrawable, ImageFrom.MEMORY_CACHE, recycleDrawable.getMimeType());
                    displayCompleted();
                    return;
                } else {
                    getSketch().getConfiguration().getMemoryCache().remove(getAttrs().getId());
                    if (Sketch.isDebugMode()) {
                        Log.e(Sketch.TAG, SketchUtils.concat(getLogName(),
                                " - ", "runLoad", "memory cache drawable recycled",
                                " - ", recycleDrawable.getInfo(),
                                " - ", getAttrs().getId()));
                    }
                }
            }
        }

        super.runLoad();
    }

    @Override
    protected void loadCompleted() {
        LoadResult loadResult = getLoadResult();
        if (loadResult == null || (loadResult.getBitmap() == null && loadResult.getGifDrawable() == null)) {
            failed(FailedCause.DECODE_FAIL);
            return;
        }

        if (loadResult.getBitmap() != null) {
            if (loadResult.getBitmap().isRecycled()) {
                if (Sketch.isDebugMode()) {
                    Log.e(Sketch.TAG, SketchUtils.concat(getLogName(),
                            " - ", "loadCompleted",
                            " - ", "bitmap recycled",
                            " - ", loadResult.getGifDrawable().getInfo(),
                            " - ", getAttrs().getId()));
                }
                failed(FailedCause.DECODE_FAIL);
                return;
            }

            // 包装Bitmap并放入内存缓存池
            RecycleBitmapDrawable bitmapDrawable = new RecycleBitmapDrawable(loadResult.getBitmap());
            bitmapDrawable.setMimeType(loadResult.getMimeType());
            if (displayOptions.isCacheInMemory() && getAttrs().getId() != null) {
                getSketch().getConfiguration().getMemoryCache().put(getAttrs().getId(), bitmapDrawable);
            }

            displayResult = new DisplayResult(bitmapDrawable, loadResult.getImageFrom(), loadResult.getMimeType());
            displayCompleted();
            return;
        }

        if (loadResult.getGifDrawable() != null) {
            if (loadResult.getGifDrawable().isRecycled()) {
                if (Sketch.isDebugMode()) {
                    Log.e(Sketch.TAG, SketchUtils.concat(getLogName(),
                            " - ", "loadCompleted",
                            " - ", "gif drawable recycled",
                            " - ", loadResult.getGifDrawable().getInfo(),
                            " - ", getAttrs().getId()));
                }
                failed(FailedCause.DECODE_FAIL);
                return;
            }

            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "loadCompleted",
                        " - ", "new gif drawable",
                        " - ", loadResult.getGifDrawable().getInfo(),
                        " - ", getAttrs().getId()));
            }

            // 将GIF图放入内存缓存
            loadResult.getGifDrawable().setMimeType(loadResult.getMimeType());
            if (displayOptions.isCacheInMemory() && getAttrs().getId() != null) {
                MemoryCache memoryCache = getSketch().getConfiguration().getMemoryCache();
                memoryCache.put(getAttrs().getId(), loadResult.getGifDrawable());
            }

            displayResult = new DisplayResult(loadResult.getGifDrawable(), loadResult.getImageFrom(), loadResult.getMimeType());
            displayCompleted();
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

    @SuppressWarnings("WeakerAccess")
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
    protected void runCanceledInMainThread() {
        if (displayListener != null) {
            displayListener.onCanceled(getCancelCause());
        }
    }

    @Override
    protected void runCompletedInMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runCompletedInMainThread",
                        " - ", "canceled",
                        " - ", getAttrs().getId()));
            }

            // 更新等待显示的引用计数
            if (displayResult != null && displayResult.getDrawable() instanceof RecycleDrawable) {
                RecycleDrawable recycleDrawable = (RecycleDrawable) displayResult.getDrawable();
                recycleDrawable.setIsWaitDisplay("completedCallback:cancel", false);
            }
            return;
        }

        setStatus(Status.DISPLAYING);

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
                Log.d(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runCompletedInMainThread",
                        " - ", "completedDrawable is null",
                        " - ", getAttrs().getId()));
            }
        }

        // 更新等待显示的引用计数
        if (displayResult.getDrawable() instanceof RecycleDrawable) {
            RecycleDrawable recycleDrawable = (RecycleDrawable) displayResult.getDrawable();
            recycleDrawable.setIsWaitDisplay("completedCallback", false);
        }

        setStatus(Status.COMPLETED);

        if (displayListener != null) {
            displayListener.onCompleted(displayResult.getImageFrom(), displayResult.getMimeType());
        }
    }

    @Override
    protected void runFailedInMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runFailedInMainThread",
                        " - ", "canceled",
                        " - ", getAttrs().getId()));
            }
            return;
        }

        setStatus(Status.DISPLAYING);

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
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runFailedInMainThread",
                        " - ", "failedDrawable is null",
                        " - ", getAttrs().getId()));
            }
        }

        setStatus(Status.FAILED);

        if (displayListener != null) {
            displayListener.onFailed(getFailedCause());
        }
    }
}