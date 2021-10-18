/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.request;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import com.github.panpf.sketch.SLog;
import com.github.panpf.sketch.Sketch;
import com.github.panpf.sketch.SketchView;
import com.github.panpf.sketch.cache.BitmapPool;
import com.github.panpf.sketch.cache.MemoryCache;
import com.github.panpf.sketch.display.ImageDisplayer;
import com.github.panpf.sketch.drawable.SketchBitmapDrawable;
import com.github.panpf.sketch.drawable.SketchDrawable;
import com.github.panpf.sketch.drawable.SketchGifDrawable;
import com.github.panpf.sketch.drawable.SketchRefBitmap;
import com.github.panpf.sketch.drawable.SketchRefDrawable;
import com.github.panpf.sketch.drawable.SketchShapeBitmapDrawable;
import com.github.panpf.sketch.state.StateImage;
import com.github.panpf.sketch.uri.UriModel;

@SuppressWarnings("WeakerAccess")
public class DisplayRequest extends LoadRequest {

    @Nullable
    private DisplayListener displayListener;
    private boolean useSmallerThumbnails;

    @NonNull
    private RequestAndViewBinder requestAndViewBinder;
    @Nullable
    private DisplayResult displayResult;
    @Nullable
    private List<DisplayRequest> waitingDisplayShareRequests;

    public DisplayRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel,
                          @NonNull String key, @NonNull DisplayOptions displayOptions,
                          boolean useSmallerThumbnails, @NonNull RequestAndViewBinder requestAndViewBinder,
                          @Nullable DisplayListener displayListener,
                          @Nullable DownloadProgressListener downloadProgressListener) {
        super(sketch, uri, uriModel, key, displayOptions, null, downloadProgressListener, "DisplayRequest");
        this.useSmallerThumbnails = useSmallerThumbnails;
        this.requestAndViewBinder = requestAndViewBinder;
        this.displayListener = displayListener;
        this.requestAndViewBinder.setDisplayRequest(this);
    }

    @NonNull
    @Override
    public DisplayOptions getOptions() {
        return (DisplayOptions) super.getOptions();
    }

    @NonNull
    public String getMemoryCacheKey() {
        return getKey();
    }

    public boolean isUseSmallerThumbnails() {
        return useSmallerThumbnails;
    }

    @Override
    public boolean isCanceled() {
        if (super.isCanceled()) {
            return true;
        }

        if (requestAndViewBinder.isBroken()) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "The request and the connection to the view are interrupted. %s. %s", getThreadName(), getKey());
            }
            doCancel(CancelCause.BIND_DISCONNECT);
            return true;
        }

        return false;
    }

    @Override
    protected void doError(@NonNull ErrorCause errorCause) {
        if (displayListener != null || getOptions().getErrorImage() != null) {
            setErrorCause(errorCause);
            postToMainRunError();
        } else {
            super.doError(errorCause);
        }
    }

    @Override
    protected void doCancel(@NonNull CancelCause cancelCause) {
        super.doCancel(cancelCause);

        if (displayListener != null) {
            postToMainRunCanceled();
        }
    }

    @Override
    protected void postToMainRunError() {
        setStatus(Status.WAIT_DISPLAY);
        super.postToMainRunError();
    }

    @Override
    protected void postRunCompleted() {
        setStatus(Status.WAIT_DISPLAY);
        super.postRunCompleted();
    }

    @Nullable
    @Override
    LoadResult runLoad() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request end before decode. %s. %s", getThreadName(), getKey());
            }
            return null;
        }

        // Check memory cache
        DisplayOptions displayOptions = getOptions();
        if (!displayOptions.isCacheInDiskDisabled()) {
            setStatus(Status.CHECK_MEMORY_CACHE);
            MemoryCache memoryCache = getConfiguration().getMemoryCache();
            SketchRefBitmap cachedRefBitmap = memoryCache.get(getMemoryCacheKey());
            if (cachedRefBitmap != null) {
                // 当 isDecodeGifImage 为 true 时是要播放 gif 的，而内存缓存里的 gif 图都是第一帧静态图片，所以不能用
                if (!(getOptions().isDecodeGifImage() && "image/gif".equalsIgnoreCase(cachedRefBitmap.getAttrs().getMimeType()))) {
                    if (!cachedRefBitmap.isRecycled()) {
                        if (SLog.isLoggable(SLog.DEBUG)) {
                            SLog.dmf(getLogName(), "From memory get drawable. bitmap=%s. %s. %s",
                                    cachedRefBitmap.getInfo(), getThreadName(), getKey());
                        }
                        cachedRefBitmap.setIsWaitingUse(String.format("%s:waitingUse:fromMemory", getLogName()), true); // 立马标记等待使用，防止被回收
                        return new CacheBitmapLoadResult(cachedRefBitmap, cachedRefBitmap.getAttrs(), ImageFrom.MEMORY_CACHE);
                    } else {
                        memoryCache.remove(getMemoryCacheKey());
                        SLog.emf(getLogName(), "Memory cache drawable recycled. bitmap=%s. %s. %s", cachedRefBitmap.getInfo(), getThreadName(), getKey());
                    }
                }
            }
        }

        return super.runLoad();
    }

    @Override
    void onRunLoadFinished(@Nullable LoadResult result) {
        if (result instanceof BitmapLoadResult) {
            BitmapPool bitmapPool = getConfiguration().getBitmapPool();
            Bitmap bitmap = ((BitmapLoadResult) result).getBitmap();
            SketchRefBitmap refBitmap = new SketchRefBitmap(bitmap, getKey(), getUri(), result.getImageAttrs(), bitmapPool);
            refBitmap.setIsWaitingUse(String.format("%s:waitingUse:new", getLogName()), true);  // 立马标记等待使用，防止刚放入内存缓存就被挤出去回收掉
            DisplayOptions displayOptions = getOptions();
            if (!displayOptions.isCacheInMemoryDisabled()) {
                getConfiguration().getMemoryCache().put(getMemoryCacheKey(), refBitmap);
            }

            Drawable drawable = new SketchBitmapDrawable(refBitmap, result.getImageFrom());
            onDisplayFinished(new DisplayResult(drawable, result.getImageFrom(), result.getImageAttrs()));
        } else if (result instanceof GifLoadResult) {
            // GifDrawable 不能放入内存缓存中，因为GifDrawable需要依赖Callback才能播放，
            // 如果缓存的话就会出现一个GifDrawable被显示在多个ImageView上的情况，这时候就只有最后一个能正常播放
            SketchGifDrawable gifDrawable = ((GifLoadResult) result).getGifDrawable();
            onDisplayFinished(new DisplayResult((Drawable) gifDrawable, result.getImageFrom(), result.getImageAttrs()));
        } else if (result instanceof CacheBitmapLoadResult) {
            Drawable drawable = new SketchBitmapDrawable(((CacheBitmapLoadResult) result).getRefBitmap(), result.getImageFrom());
            onDisplayFinished(new DisplayResult(drawable, result.getImageFrom(), result.getImageAttrs()));
        } else {
            SLog.emf(getLogName(), "Not found data after load completed. %s. %s", getThreadName(), getKey());
            doError(ErrorCause.DATA_LOST_AFTER_LOAD_COMPLETED);
            onDisplayFinished(null);
        }
    }

    protected void onDisplayFinished(@Nullable DisplayResult result) {
        this.displayResult = result;
        if (result != null) {
            postRunCompleted();
        }
    }

    @Override
    protected void runCompletedInMain() {
        Drawable drawable = displayResult != null ? displayResult.getDrawable() : null;
        if (drawable == null) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Drawable is null before call completed. %s. %s", getThreadName(), getKey());
            }
            return;
        }

        displayImage(displayResult, drawable);

        // 使用完毕更新等待使用的引用计数
        if (drawable instanceof SketchRefDrawable) {
            ((SketchRefDrawable) drawable).setIsWaitingUse(String.format("%s:waitingUse:finish", getLogName()), false);
        }
    }

    private void displayImage(@NonNull DisplayResult displayResult, @NonNull Drawable drawable) {
        SketchView sketchView = requestAndViewBinder.getView();
        if (isCanceled() || sketchView == null) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request end before call completed. %s. %s", getThreadName(), getKey());
            }
            return;
        }

        // 过滤可能已回收的图片
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap().isRecycled()) {
                // 这里应该不会再出问题了
                SLog.emf(getLogName(), "Bitmap recycled on display. imageUri=%s, drawable=%s",
                        getUri(), ((SketchDrawable) drawable).getInfo());
                getConfiguration().getCallback().onError(new BitmapRecycledOnDisplayException(this, (SketchDrawable) drawable));

                // 图片不可用
                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(getLogName(), "Display image exception. bitmap recycled. %s. %s. %s. %s",
                            ((SketchDrawable) drawable).getInfo(), displayResult.getImageFrom(), getThreadName(), getKey());
                }

                runErrorInMain();
                return;
            }
        }

        // 显示图片
        DisplayOptions displayOptions = getOptions();
        if ((displayOptions.getShapeSize() != null || displayOptions.getShaper() != null) && drawable instanceof BitmapDrawable) {
            drawable = new SketchShapeBitmapDrawable(getConfiguration().getContext(), (BitmapDrawable) drawable,
                    displayOptions.getShapeSize(), displayOptions.getShaper());
        }

        if (SLog.isLoggable(SLog.DEBUG)) {
            String drawableInfo = "unknown";
            if (drawable instanceof SketchRefDrawable) {
                drawableInfo = ((SketchRefDrawable) drawable).getInfo();
            }
            SLog.dmf(getLogName(), "Display image completed. %s. %s. view(%s). %s. %s",
                    displayResult.getImageFrom().name(), drawableInfo, Integer.toHexString(sketchView.hashCode()), getThreadName(), getKey());
        }

        // 一定要在 ImageDisplayer().display 之前执行
        setStatus(Status.COMPLETED);

        ImageDisplayer imageDisplayer = displayOptions.getDisplayer();
        if (imageDisplayer == null) {
            imageDisplayer = getConfiguration().getDefaultDisplayer();
        }
        imageDisplayer.display(sketchView, drawable);

        if (displayListener != null) {
            displayListener.onCompleted(displayResult.getDrawable(), displayResult.getImageFrom(), displayResult.getImageAttrs());
        }
    }

    @Override
    protected void runErrorInMain() {
        SketchView sketchView = requestAndViewBinder.getView();
        if (isCanceled() || sketchView == null) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request end before call error. %s. %s", getThreadName(), getKey());
            }
            return;
        }

        setStatus(Status.FAILED);

        DisplayOptions displayOptions = getOptions();
        ImageDisplayer displayer = displayOptions.getDisplayer();
        StateImage errorImage = displayOptions.getErrorImage();
        if (displayer != null && errorImage != null) {
            Drawable errorDrawable = errorImage.getDrawable(getContext(), sketchView, displayOptions);
            if (errorDrawable != null) {
                displayer.display(sketchView, errorDrawable);
            }
        }

        if (displayListener != null && getErrorCause() != null) {
            displayListener.onError(getErrorCause());
        }
    }

    @Override
    protected void runCanceledInMain() {
        if (displayListener != null && getCancelCause() != null) {
            displayListener.onCanceled(getCancelCause());
        }
    }


    /* ************************************** Display Share ************************************ */

    public boolean canUseDisplayShare() {
        MemoryCache memoryCache = getConfiguration().getMemoryCache();
        return !memoryCache.isClosed() && !memoryCache.isDisabled()
                && !getOptions().isCacheInMemoryDisabled()
                && !getOptions().isDecodeGifImage()
                && !isSync() && !getConfiguration().getExecutor().isShutdown();
    }

    @Nullable
    public List<DisplayRequest> getWaitingDisplayShareRequests() {
        return waitingDisplayShareRequests;
    }

    public void setWaitingDisplayShareRequests(@Nullable List<DisplayRequest> waitingDisplayShareRequests) {
        this.waitingDisplayShareRequests = waitingDisplayShareRequests;
    }
}