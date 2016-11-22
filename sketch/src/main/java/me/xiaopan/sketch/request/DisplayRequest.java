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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.drawable.RefBitmap;
import me.xiaopan.sketch.drawable.RefBitmapDrawable;
import me.xiaopan.sketch.drawable.RefDrawable;
import me.xiaopan.sketch.drawable.ShapeBitmapDrawable;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.feature.ExceptionMonitor;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 显示请求
 */
public class DisplayRequest extends LoadRequest {
    private DisplayOptions displayOptions;
    private DisplayListener displayListener;

    private ViewInfo viewInfo;
    private RequestAndViewBinder requestAndViewBinder;

    private DisplayResult displayResult;

    public DisplayRequest(Sketch sketch, DisplayInfo requestInfo,DisplayOptions displayOptions,
            ViewInfo viewInfo, RequestAndViewBinder requestAndViewBinder, DisplayListener displayListener,
            DownloadProgressListener downloadProgressListener) {
        super(sketch, requestInfo, displayOptions, null, downloadProgressListener);

        this.viewInfo = viewInfo;
        this.displayOptions = displayOptions;
        this.requestAndViewBinder = requestAndViewBinder;
        this.displayListener = displayListener;

        this.requestAndViewBinder.setDisplayRequest(this);
        setLogName("DisplayRequest");
    }

    /**
     * 获取内存缓存key
     */
    public String getMemoryCacheKey() {
        return ((DisplayInfo) info).getMemoryCacheKey();
    }

    /**
     * 获取View信息
     */
    public ViewInfo getViewInfo() {
        return viewInfo;
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
        if (requestAndViewBinder.isBroken()) {
            canceled(CancelCause.BIND_DISCONNECT);
            return true;
        }

        return false;
    }

    @Override
    public void error(ErrorCause errorCause) {
        if (displayListener != null || displayOptions.getErrorImage() != null) {
            setErrorCause(errorCause);
            postRunError();
        } else {
            super.error(errorCause);
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
    protected void postRunError() {
        setStatus(Status.WAIT_DISPLAY);
        super.postRunError();
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
                printLogW("canceled", "runLoad", "display request just started");
            }
            return;
        }

        // 先检查内存缓存，检查的时候要先上锁
        boolean finished = false;
        if (!displayOptions.isDisableCacheInDisk()) {
            setStatus(Status.GET_MEMORY_CACHE_EDIT_LOCK);

            MemoryCache memoryCache = getSketch().getConfiguration().getMemoryCache();
            ReentrantLock memoryCacheEditLock = memoryCache.getEditLock(getMemoryCacheKey());
            if (memoryCacheEditLock != null) {
                memoryCacheEditLock.lock();
            }

            finished = checkMemoryCache();

            if (memoryCacheEditLock != null) {
                memoryCacheEditLock.unlock();
            }
        }

        if (!finished) {
            super.runLoad();
        }
    }

    private boolean checkMemoryCache() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("canceled", "runDownload", "get memory cache edit lock after");
            }
            return true;
        }

        // 检查内存缓存
        if (!displayOptions.isDisableCacheInMemory()) {
            setStatus(Status.CHECK_MEMORY_CACHE);
            MemoryCache memoryCache = getSketch().getConfiguration().getMemoryCache();
            RefBitmap cachedRefBitmap = memoryCache.get(getMemoryCacheKey());
            if (cachedRefBitmap != null) {
                if (!cachedRefBitmap.isRecycled()) {
                    if (Sketch.isDebugMode()) {
                        printLogI("from memory get drawable", "runLoad", "drawableInfo=" + cachedRefBitmap.getInfo());
                    }
                    displayResult = new DisplayResult(new RefBitmapDrawable(cachedRefBitmap),
                            ImageFrom.MEMORY_CACHE, cachedRefBitmap.getMimeType());
                    displayCompleted();
                    return true;
                } else {
                    memoryCache.remove(getMemoryCacheKey());
                    if (Sketch.isDebugMode()) {
                        printLogE("memory cache drawable recycled", "runLoad", "drawableInfo=" + cachedRefBitmap.getInfo());
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected void loadCompleted() {
        LoadResult loadResult = getLoadResult();
        if (loadResult != null && loadResult.getBitmap() != null) {
            Bitmap bitmap = loadResult.getBitmap();

            if (bitmap.isRecycled()) {
                if (Sketch.isDebugMode()) {
                    printLogE("decode failed", "loadCompleted", "bitmap recycled",
                            "bitmapInfo=", SketchUtils.getImageInfo(null, bitmap, loadResult.getMimeType()),
                            loadResult.getImageFrom());
                }
                error(ErrorCause.BITMAP_RECYCLED);
                return;
            }

            RefBitmap refBitmap = new RefBitmap(bitmap, getId(), getUri(),
                    loadResult.getOriginWidth(), loadResult.getOriginHeight(), loadResult.getMimeType());

            // 放入内存缓存中
            if (!displayOptions.isDisableCacheInMemory() && getMemoryCacheKey() != null) {
                getSketch().getConfiguration().getMemoryCache().put(getMemoryCacheKey(), refBitmap);
            }

            displayResult = new DisplayResult(new RefBitmapDrawable(refBitmap),
                    loadResult.getImageFrom(), loadResult.getMimeType());
            displayCompleted();
        } else if (loadResult != null && loadResult.getGifDrawable() != null) {
            SketchGifDrawable gifDrawable = loadResult.getGifDrawable();

            if (gifDrawable.isRecycled()) {
                if (Sketch.isDebugMode()) {
                    printLogE("decode failed", "loadCompleted", "gif drawable recycled",
                            "gifInfo=", SketchUtils.getGifImageInfo(gifDrawable), loadResult.getImageFrom());
                }
                error(ErrorCause.GIF_DRAWABLE_RECYCLED);
                return;
            }

            // GifDrawable不能放入内存缓存中，因为GifDrawable需要依赖Callback才能播放，
            // 如果缓存的话就会出现一个GifDrawable被显示在多个ImageView上的情况，这时候就只有最后一个能正常播放

            displayResult = new DisplayResult(gifDrawable, loadResult.getImageFrom(), loadResult.getMimeType());
            displayCompleted();
        } else {
            if (Sketch.isDebugMode()) {
                printLogE("are all null", "loadCompleted");
            }
            error(ErrorCause.DECODE_FAIL);
        }
    }

    protected void displayCompleted() {
        if (displayResult.getDrawable() instanceof RefDrawable) {
            RefDrawable refDrawable = (RefDrawable) displayResult.getDrawable();
            boolean fromMemoryCache = displayResult.getImageFrom() == ImageFrom.MEMORY_CACHE;
            String callingStation = fromMemoryCache ? "displayCompleted:fromMemory" : "displayCompleted:new";
            refDrawable.setIsWaitDisplay(callingStation, true);
        }

        if (displayResult.getDrawable() instanceof SketchDrawable) {
            SketchDrawable sketchDrawable = (SketchDrawable) displayResult.getDrawable();
            sketchDrawable.setImageFrom(displayResult.getImageFrom());
        }

        postRunCompleted();
    }

    @Override
    protected void runCompletedInMainThread() {
        Drawable drawable = displayResult.getDrawable();
        if (drawable == null) {
            if (Sketch.isDebugMode()) {
                printLogD("completedDrawable is null", "runCompletedInMainThread");
            }
            return;
        }

        displayImage(drawable);

        // 更新等待显示的引用计数
        if (drawable instanceof RefDrawable) {
            RefDrawable refDrawable = (RefDrawable) drawable;
            refDrawable.setIsWaitDisplay("completedCallback", false);
        }
    }

    private void displayImage(Drawable drawable){
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("canceled", "runCompletedInMainThread");
            }
            return;
        }

        // 过滤可能已回收的图片
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap().isRecycled()) {
                // TODO: 2016/11/22 走到这里的几率还是挺高的，得弄清楚到底是咋回事，比如记录下bitmap的操作记录，当出现问题时打印记录看看
                // TODO: 2016/11/22 初步怀疑是异步的wait display引用造成的，解决方案加锁
                ExceptionMonitor exceptionMonitor = getSketch().getConfiguration().getExceptionMonitor();
                exceptionMonitor.onBitmapRecycledOnDisplay(this, drawable instanceof RefDrawable ? (RefDrawable) drawable : null);

                // 图片不可用
                printLogD("image display exception", "bitmap recycled",
                        ((SketchDrawable) drawable).getInfo(), displayResult.getImageFrom());

                runErrorInMainThread();
                return;
            }
        }

        // 显示图片
        if ((displayOptions.getShapeSize() != null || displayOptions.getImageShaper() != null)
                && drawable instanceof BitmapDrawable) {
            drawable = new ShapeBitmapDrawable((BitmapDrawable) drawable,
                    displayOptions.getShapeSize(), displayOptions.getImageShaper());
        }

        ImageViewInterface viewInterface = requestAndViewBinder.getImageViewInterface();
        if (Sketch.isDebugMode()) {
            String drawableInfo = "unknown";
            if (drawable instanceof RefDrawable) {
                drawableInfo = ((RefDrawable) drawable).getInfo();
            }
            printLogI("image display completed", "runCompletedInMainThread",
                    displayResult.getImageFrom().name(), drawableInfo,
                    "viewHashCode=" + Integer.toHexString(viewInterface.hashCode()));
        }

        displayOptions.getImageDisplayer().display(viewInterface, drawable);

        setStatus(Status.COMPLETED);

        if (displayListener != null) {
            displayListener.onCompleted(displayResult.getImageFrom(), displayResult.getMimeType());
        }
    }

    @Override
    protected void runErrorInMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("canceled", "runErrorInMainThread");
            }
            return;
        }

        setStatus(Status.FAILED);

        // 显示失败图片
        if (displayOptions.getErrorImage() != null) {
            Context context = getSketch().getConfiguration().getContext();
            Drawable errorDrawable = displayOptions.getErrorImage().getDrawable(context, requestAndViewBinder.getImageViewInterface(), displayOptions);
            displayOptions.getImageDisplayer().display(requestAndViewBinder.getImageViewInterface(), errorDrawable);
        } else {
            if (Sketch.isDebugMode()) {
                printLogW("failedDrawable is null", "runErrorInMainThread");
            }
        }

        if (displayListener != null) {
            displayListener.onError(getErrorCause());
        }
    }

    @Override
    protected void runCanceledInMainThread() {
        if (displayListener != null) {
            displayListener.onCanceled(getCancelCause());
        }
    }
}