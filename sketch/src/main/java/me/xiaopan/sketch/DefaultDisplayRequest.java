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

package me.xiaopan.sketch;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 显示请求
 */
public class DefaultDisplayRequest extends SketchRequest implements DisplayRequest {
    private static final int WHAT_RUN_PAUSE_DOWNLOAD = 44006;
    private static final String NAME = "DefaultDisplayRequest";

    private RequestAttrs requestAttrs;
    private DisplayAttrs displayAttrs;
    private DisplayOptions displayOptions;

    private LoadResult loadResult;
    private DownloadResult downloadResult;
    private DisplayListener displayListener;
    private DownloadProgressListener progressListener;

    public DefaultDisplayRequest(RequestAttrs requestAttrs, DisplayAttrs displayAttrs, DisplayOptions displayOptions, DisplayListener listener, DownloadProgressListener progressListener) {
        super(requestAttrs.getConfiguration().getRequestExecutor());
        this.requestAttrs = requestAttrs;
        this.displayAttrs = displayAttrs;
        this.displayOptions = displayOptions;
        this.displayListener = listener;
        this.progressListener = progressListener;

        displayAttrs.getSketchBinder().setDisplayRequest(this);
    }

    @Override
    public RequestAttrs getAttrs() {
        return requestAttrs;
    }

    @Override
    public DisplayAttrs getDisplayAttrs() {
        return displayAttrs;
    }

    @Override
    public DisplayOptions getOptions() {
        return displayOptions;
    }

    @Override
    public LoadResult getLoadResult() {
        return loadResult;
    }

    @Override
    public DownloadResult getDownloadResult() {
        return downloadResult;
    }

    @Override
    public boolean isCanceled() {
        if (super.isCanceled()) {
            return true;
        }

        // 绑定关系已经断了的话就直接取消请求
        if (displayAttrs.getSketchBinder().isBroken()) {
            canceled(CancelCause.NORMAL);
            return true;
        }

        return false;
    }

    public Drawable getFailureDrawable() {
        if (displayOptions.getFailureImage() == null) {
            return null;
        } else if (displayOptions.getImageDisplayer() != null
                && displayOptions.getImageDisplayer() instanceof TransitionImageDisplayer
                && displayAttrs.getFixedSize() != null
                && displayAttrs.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
            return new FixedRecycleBitmapDrawable(displayOptions.getFailureImage().getRecycleBitmapDrawable(requestAttrs.getConfiguration().getContext()), displayAttrs.getFixedSize());
        } else {
            return displayOptions.getFailureImage().getRecycleBitmapDrawable(requestAttrs.getConfiguration().getContext());
        }
    }

    public Drawable getPauseDownloadDrawable() {
        if (displayOptions.getPauseDownloadImage() == null) {
            return null;
        } else if (displayOptions.getImageDisplayer() != null
                && displayOptions.getImageDisplayer() instanceof TransitionImageDisplayer
                && displayAttrs.getFixedSize() != null
                && displayAttrs.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
            return new FixedRecycleBitmapDrawable(displayOptions.getPauseDownloadImage().getRecycleBitmapDrawable(requestAttrs.getConfiguration().getContext()), displayAttrs.getFixedSize());
        } else {
            return displayOptions.getPauseDownloadImage().getRecycleBitmapDrawable(requestAttrs.getConfiguration().getContext());
        }
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
    public void updateProgress(int totalLength, int completedLength) {
        if (progressListener != null) {
            postRunUpdateProgress(totalLength, completedLength);
        }
    }

    @Override
    protected void postRunFailed() {
        setStatus(Status.WAIT_DISPLAY);
        super.postRunFailed();
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
    protected void runDispatch() {
        setStatus(Status.DISPATCHING);

        // 本地请求直接执行加载
        if (requestAttrs.getUriScheme() != UriScheme.HTTP && requestAttrs.getUriScheme() != UriScheme.HTTPS) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "local", " - ", requestAttrs.getName()));
            }
            submitRunLoad();
            return;
        }

        // 然后从磁盘缓存中找缓存文件
        if (displayOptions.isCacheInDisk()) {
            DiskCache.Entry diskCacheEntry = requestAttrs.getConfiguration().getDiskCache().get(requestAttrs.getUri());
            if (diskCacheEntry != null) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "diskCache", " - ", requestAttrs.getName()));
                }
                downloadResult = new DownloadResult(diskCacheEntry, false);
                submitRunLoad();
                return;
            }
        }

        // 在下载之前判断如果请求Level限制只能从本地加载的话就取消了
        if (displayOptions.getRequestLevel() == RequestLevel.LOCAL) {
            if (displayOptions.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "canceled", " - ", "pause download", " - ", requestAttrs.getName()));
                }
                postRunPauseDownload();
            } else {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "canceled", " - ", "requestLevel is local", " - ", requestAttrs.getName()));
                }
                canceled(CancelCause.LEVEL_IS_LOCAL);
            }
            return;
        }

        // 执行下载
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "download", " - ", requestAttrs.getName()));
        }
        submitRunDownload();
    }

    @Override
    protected void runDownload() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDownload", " - ", "canceled", " - ", "startDownload", " - ", requestAttrs.getName()));
            }
            return;
        }

        // 调用下载器下载
        DownloadResult justDownloadResult = requestAttrs.getConfiguration().getImageDownloader().download(this);

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDownload", " - ", "canceled", " - ", "downloadAfter", " - ", requestAttrs.getName()));
            }
            return;
        }

        // 都是空的就算下载失败
        if (justDownloadResult == null || (justDownloadResult.getDiskCacheEntry() == null && justDownloadResult.getImageData() == null)) {
            failed(FailedCause.DOWNLOAD_FAIL);
            return;
        }

        // 下载成功了，执行加载
        downloadResult = justDownloadResult;
        submitRunLoad();
    }

    @Override
    protected void runLoad() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "canceled", " - ", "startLoad", " - ", requestAttrs.getName()));
            }
            return;
        }

        // 检查内存缓存中是否已经存在了
        if (displayOptions.isCacheInMemory()) {
            Drawable cacheDrawable = requestAttrs.getConfiguration().getMemoryCache().get(displayAttrs.getMemoryCacheId());
            if (cacheDrawable != null) {
                RecycleDrawableInterface recycleDrawable = (RecycleDrawableInterface) cacheDrawable;
                if (!recycleDrawable.isRecycled()) {
                    if (Sketch.isDebugMode()) {
                        Log.i(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "from memory get drawable", " - ", recycleDrawable.getInfo(), " - ", requestAttrs.getName()));
                    }
                    this.loadResult = new LoadResult(cacheDrawable, ImageFrom.MEMORY_CACHE, recycleDrawable.getMimeType());
                    setStatus(Status.WAIT_DISPLAY);
                    recycleDrawable.setIsWaitDisplay("executeLoad:fromMemory", true);
                    postRunCompleted();
                    return;
                } else {
                    requestAttrs.getConfiguration().getMemoryCache().remove(displayAttrs.getMemoryCacheId());
                    if (Sketch.isDebugMode()) {
                        Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", "bitmap recycled", " - ", recycleDrawable.getInfo(), " - ", requestAttrs.getName()));
                    }
                }
            }
        }

        setStatus(Status.LOADING);

        // 尝试用本地图片预处理器处理一下特殊的本地图片，并得到他们的缓存
        if (requestAttrs.getConfiguration().getLocalImagePreprocessor().isSpecific(this)) {
            DiskCache.Entry specificLocalImageDiskCacheEntry = requestAttrs.getConfiguration().getLocalImagePreprocessor().getDiskCacheEntry(this);
            if (specificLocalImageDiskCacheEntry != null) {
                this.downloadResult = new DownloadResult(specificLocalImageDiskCacheEntry, false);
            } else {
                failed(FailedCause.NOT_GET_SPECIFIC_LOCAL_IMAGE_CACHE_FILE);
                return;
            }
        }

        // 解码
        DecodeResult decodeResult = requestAttrs.getConfiguration().getImageDecoder().decode(this);
        if (decodeResult == null) {
            failed(FailedCause.DECODE_FAIL);
            return;
        }

        if (decodeResult.getResultBitmap() != null) {
            Bitmap bitmap = decodeResult.getResultBitmap();
            if (!bitmap.isRecycled()) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "new bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, decodeResult.getMimeType()), " - ", requestAttrs.getName()));
                }
            } else {
                if (Sketch.isDebugMode()) {
                    Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "decode failed bitmap recycled", " - ", "decode after", " - ", RecycleBitmapDrawable.getInfo(bitmap, decodeResult.getMimeType()), " - ", requestAttrs.getName()));
                }
            }

            if (isCanceled()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "canceled", " - ", "decode after", " - ", "recycle bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, decodeResult.getMimeType()), " - ", requestAttrs.getName()));
                }
                bitmap.recycle();
                return;
            }

            //处理
            if (!bitmap.isRecycled()) {
                ImageProcessor imageProcessor = displayOptions.getImageProcessor();
                if (imageProcessor != null) {
                    Bitmap newBitmap = imageProcessor.process(requestAttrs.getSketch(), bitmap, displayOptions.getResize(), displayOptions.isForceUseResize(), displayOptions.isLowQualityImage());
                    if (newBitmap != null && newBitmap != bitmap && Sketch.isDebugMode()) {
                        Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "process after", " - ", "newBitmap", " - ", RecycleBitmapDrawable.getInfo(newBitmap, decodeResult.getMimeType()), " - ", "recycled old bitmap", " - ", requestAttrs.getName()));
                    }
                    if (newBitmap == null || newBitmap != bitmap) {
                        bitmap.recycle();
                    }
                    bitmap = newBitmap;
                }
            }

            if (isCanceled()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "canceled", " - ", "process after", " - ", "recycle bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, decodeResult.getMimeType()), " - ", requestAttrs.getName()));
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
                return;
            }

            if (bitmap != null && !bitmap.isRecycled()) {
                RecycleBitmapDrawable bitmapDrawable = new RecycleBitmapDrawable(bitmap);
                if (displayOptions.isCacheInMemory() && displayAttrs.getMemoryCacheId() != null) {
                    requestAttrs.getConfiguration().getMemoryCache().put(displayAttrs.getMemoryCacheId(), bitmapDrawable);
                }
                bitmapDrawable.setMimeType(decodeResult.getMimeType());

                loadResult = new LoadResult(bitmapDrawable, decodeResult.getImageFrom(), decodeResult.getMimeType());

                setStatus(Status.WAIT_DISPLAY);
                bitmapDrawable.setIsWaitDisplay("executeLoad:new", true);
                postRunCompleted();
            } else {
                failed(FailedCause.DECODE_FAIL);
            }
        } else if (decodeResult.getResultGifDrawable() != null) {
            RecycleGifDrawable gifDrawable = decodeResult.getResultGifDrawable();

            if (!gifDrawable.isRecycled()) {
                gifDrawable.setMimeType(decodeResult.getMimeType());

                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "new gif drawable", " - ", gifDrawable.getInfo(), " - ", requestAttrs.getName()));
                }

                if (displayOptions.isCacheInMemory() && displayAttrs.getMemoryCacheId() != null) {
                    requestAttrs.getConfiguration().getMemoryCache().put(displayAttrs.getMemoryCacheId(), gifDrawable);
                }

                loadResult = new LoadResult(gifDrawable, decodeResult.getImageFrom(), decodeResult.getMimeType());

                gifDrawable.setIsWaitDisplay("executeLoad:new", true);
                setStatus(Status.WAIT_DISPLAY);
                postRunCompleted();
            } else {
                if (Sketch.isDebugMode()) {
                    Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "gif drawable recycled", " - ", gifDrawable.getInfo(), " - ", requestAttrs.getName()));
                }

                failed(FailedCause.DECODE_FAIL);
            }
        } else {
            failed(FailedCause.DECODE_FAIL);
        }
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
            if (loadResult != null && loadResult.getDrawable() instanceof RecycleDrawableInterface) {
                ((RecycleDrawableInterface) loadResult.getDrawable()).setIsWaitDisplay("completedCallback:cancel", false);
            }
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runCompletedInMainThread", " - ", "canceled", " - ", requestAttrs.getName()));
            }
            return;
        }

        setStatus(Status.DISPLAYING);

        // 显示图片
        ImageDisplayer displayer = displayOptions.getImageDisplayer();

        // Set FixedSize
        Drawable finalDrawable;
        if (displayer != null
                && displayer instanceof TransitionImageDisplayer
                && loadResult.getDrawable() instanceof RecycleBitmapDrawable
                && displayAttrs.getFixedSize() != null && displayAttrs.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
            finalDrawable = new FixedRecycleBitmapDrawable((RecycleBitmapDrawable) loadResult.getDrawable(), displayAttrs.getFixedSize());
        } else {
            finalDrawable = loadResult.getDrawable();
        }

        if (displayer == null) {
            displayer = requestAttrs.getConfiguration().getDefaultImageDisplayer();
        }
        displayer.display(displayAttrs.getSketchBinder().getImageViewInterface(), finalDrawable);
        ((RecycleDrawableInterface) loadResult.getDrawable()).setIsWaitDisplay("completedCallback", false);

        setStatus(Status.COMPLETED);

        if (displayListener != null) {
            displayListener.onCompleted(loadResult.getImageFrom(), loadResult.getMimeType());
        }
    }

    @Override
    protected void runFailedInMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runFailedInMainThread", " - ", "canceled", " - ", requestAttrs.getName()));
            }
            return;
        }

        setStatus(Status.DISPLAYING);

        // 显示失败图片
        ImageDisplayer displayer = displayOptions.getImageDisplayer();
        if (displayer == null) {
            displayer = requestAttrs.getConfiguration().getDefaultImageDisplayer();
        }
        displayer.display(displayAttrs.getSketchBinder().getImageViewInterface(), getFailureDrawable());

        setStatus(Status.FAILED);

        if (displayListener != null) {
            displayListener.onFailed(getFailedCause());
        }
    }

    @Override
    protected void runUpdateProgressInMainThread(int totalLength, int completedLength) {
        if (isFinished()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runUpdateProgressInMainThread", " - ", "finished", " - ", requestAttrs.getName()));
            }
            return;
        }

        if (progressListener != null) {
            progressListener.onUpdateDownloadProgress(totalLength, completedLength);
        }
    }

    /**
     * 推到主线程处理暂停下载
     */
    protected void postRunPauseDownload() {
        handler.obtainMessage(WHAT_RUN_PAUSE_DOWNLOAD, this).sendToTarget();
    }

    @Override
    protected void runInMainThread(int what, Message msg) {
        if (msg.what == WHAT_RUN_PAUSE_DOWNLOAD) {
            runPauseDownloadOnMainThread();
        } else {
            super.runInMainThread(what, msg);
        }
    }

    /**
     * 在主线程处理暂停下载
     */
    private void runPauseDownloadOnMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runPauseDownloadOnMainThread", " - ", "canceled", " - ", requestAttrs.getName()));
            }
            return;
        }

        setStatus(Status.DISPLAYING);

        // 显示暂停下载图片
        if (displayOptions.getPauseDownloadImage() != null) {
            setStatus(Status.DISPLAYING);
            ImageDisplayer displayer = displayOptions.getImageDisplayer();
            if (displayer == null) {
                displayer = requestAttrs.getConfiguration().getDefaultImageDisplayer();
            }
            displayer.display(displayAttrs.getSketchBinder().getImageViewInterface(), getPauseDownloadDrawable());
        }

        setStatus(Status.CANCELED);
        setCancelCause(CancelCause.PAUSE_DOWNLOAD);

        if (displayListener != null) {
            displayListener.onCanceled(getCancelCause());
        }
    }
}