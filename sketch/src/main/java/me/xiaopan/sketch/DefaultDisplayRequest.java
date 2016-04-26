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
import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 显示请求
 */
public class DefaultDisplayRequest implements DisplayRequest, Runnable {
    private static final int WHAT_CALLBACK_COMPLETED = 102;
    private static final int WHAT_CALLBACK_FAILED = 103;
    private static final int WHAT_CALLBACK_CANCELED = 104;
    private static final int WHAT_CALLBACK_PROGRESS = 105;
    private static final int WHAT_CALLBACK_PAUSE_DOWNLOAD = 106;
    private static final String NAME = "DefaultDisplayRequest";

    private RequestAttrs attrs;

    private String memoryCacheId;    // 内存缓存ID
    private ScaleType scaleType;
    private FixedSize fixedSize;    // 固定尺寸
    private DisplayOptions options;
    private DisplayListener displayListener;    // 监听器
    private DownloadProgressListener downloadProgressListener;  // 下载进度监听器

    private DownloadResult downloadResult;
    private String mimeType;
    private Drawable resultDrawable;    // 最终的图片
    private ImageFrom imageFrom;    // 图片来自哪里
    private FailCause failCause;    // 失败原因
    private RunStatus runStatus = RunStatus.DISPATCH;    // 运行状态，用于在执行run方法时知道该干什么
    private CancelCause cancelCause;  // 取消原因
    private RequestStatus requestStatus = RequestStatus.WAIT_DISPATCH;  // 状态
    private SketchImageViewInterfaceHolder sketchImageViewInterfaceHolder;    // 绑定ImageView

    public DefaultDisplayRequest(RequestAttrs attrs, String memoryCacheId, FixedSize fixedSize, SketchImageViewInterface sketchImageViewInterface, DisplayOptions options, DisplayListener listener) {
        this.attrs = attrs;
        this.memoryCacheId = memoryCacheId;
        this.fixedSize = fixedSize;
        this.sketchImageViewInterfaceHolder = new SketchImageViewInterfaceHolder(sketchImageViewInterface, this);
        this.scaleType = sketchImageViewInterface.getScaleType();
        this.options = options;
        this.displayListener = listener;
    }

    @Override
    public RequestAttrs getAttrs() {
        return attrs;
    }

    public void setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
    }

    @Override
    public String getMemoryCacheId() {
        return memoryCacheId;
    }

    @Override
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    @Override
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public FailCause getFailCause() {
        return failCause;
    }

    @Override
    public CancelCause getCancelCause() {
        return cancelCause;
    }

    @Override
    public boolean isFinished() {
        return requestStatus == RequestStatus.COMPLETED || requestStatus == RequestStatus.CANCELED || requestStatus == RequestStatus.FAILED;
    }

    @Override
    public boolean cancel() {
        if (isFinished()) {
            return false;
        }
        toCanceledStatus(CancelCause.NORMAL);
        return true;
    }

    @Override
    public DisplayOptions getOptions() {
        return options;
    }

    @Override
    public DownloadResult getDownloadResult() {
        return downloadResult;
    }

    @Override
    public boolean isCanceled() {
        boolean isCanceled = requestStatus == RequestStatus.CANCELED;
        if (!isCanceled) {
            isCanceled = sketchImageViewInterfaceHolder != null && sketchImageViewInterfaceHolder.isCollected();
            if (isCanceled) {
                toCanceledStatus(CancelCause.NORMAL);
            }
        }
        return isCanceled;
    }

    public Drawable getFailureDrawable() {
        if (options.getFailureImage() == null) {
            return null;
        } else if (options.getImageDisplayer() != null && options.getImageDisplayer() instanceof TransitionImageDisplayer && fixedSize != null && scaleType == ImageView.ScaleType.CENTER_CROP) {
            return new FixedRecycleBitmapDrawable(options.getFailureImage().getRecycleBitmapDrawable(attrs.getConfiguration().getContext()), fixedSize);
        } else {
            return options.getFailureImage().getRecycleBitmapDrawable(attrs.getConfiguration().getContext());
        }
    }

    public Drawable getPauseDownloadDrawable() {
        if (options.getPauseDownloadImage() == null) {
            return null;
        } else if (options.getImageDisplayer() != null && options.getImageDisplayer() instanceof TransitionImageDisplayer && fixedSize != null && scaleType == ImageView.ScaleType.CENTER_CROP) {
            return new FixedRecycleBitmapDrawable(options.getPauseDownloadImage().getRecycleBitmapDrawable(attrs.getConfiguration().getContext()), fixedSize);
        } else {
            return options.getPauseDownloadImage().getRecycleBitmapDrawable(attrs.getConfiguration().getContext());
        }
    }

    @Override
    public void toFailedStatus(FailCause failCause) {
        this.failCause = failCause;
        setRequestStatus(RequestStatus.WAIT_DISPLAY);
        attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_FAILED, this).sendToTarget();
    }

    @Override
    public void toCanceledStatus(CancelCause cancelCause) {
        this.cancelCause = cancelCause;
        setRequestStatus(RequestStatus.CANCELED);
        if (displayListener != null) {
            attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_CANCELED, this).sendToTarget();
        }
    }

    @Override
    public void invokeInMainThread(Message msg) {
        switch (msg.what) {
            case WHAT_CALLBACK_COMPLETED:
                handleCompletedOnMainThread();
                break;
            case WHAT_CALLBACK_PROGRESS:
                updateProgressOnMainThread(msg.arg1, msg.arg2);
                break;
            case WHAT_CALLBACK_FAILED:
                handleFailedOnMainThread();
                break;
            case WHAT_CALLBACK_CANCELED:
                handleCanceledOnMainThread();
                break;
            case WHAT_CALLBACK_PAUSE_DOWNLOAD:
                handlePauseDownloadOnMainThread();
                break;
            default:
                new IllegalArgumentException("unknown message what: " + msg.what).printStackTrace();
                break;
        }
    }

    @Override
    public void updateProgress(int totalLength, int completedLength) {
        if (downloadProgressListener != null) {
            attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_PROGRESS, totalLength, completedLength, this).sendToTarget();
        }
    }

    @Override
    public void postRunDispatch() {
        setRequestStatus(RequestStatus.WAIT_DISPATCH);
        this.runStatus = RunStatus.DISPATCH;
        attrs.getConfiguration().getRequestExecutor().getRequestDispatchExecutor().execute(this);
    }

    @Override
    public void postRunDownload() {
        setRequestStatus(RequestStatus.WAIT_DOWNLOAD);
        this.runStatus = RunStatus.DOWNLOAD;
        attrs.getConfiguration().getRequestExecutor().getNetRequestExecutor().execute(this);
    }

    @Override
    public void postRunLoad() {
        setRequestStatus(RequestStatus.WAIT_LOAD);
        this.runStatus = RunStatus.LOAD;
        attrs.getConfiguration().getRequestExecutor().getLocalRequestExecutor().execute(this);
    }

    @Override
    public void run() {
        switch (runStatus) {
            case DISPATCH:
                executeDispatch();
                break;
            case LOAD:
                executeLoad();
                break;
            case DOWNLOAD:
                executeDownload();
                break;
            default:
                new IllegalArgumentException("unknown runStatus: " + runStatus.name()).printStackTrace();
                break;
        }
    }

    /**
     * 执行分发
     */
    private void executeDispatch() {
        setRequestStatus(RequestStatus.DISPATCHING);
        if (attrs.getUriScheme() == UriScheme.HTTP || attrs.getUriScheme() == UriScheme.HTTPS) {
            DiskCache.Entry diskCacheEntry = options.isCacheInDisk() ? attrs.getConfiguration().getDiskCache().get(attrs.getUri()) : null;
            if (diskCacheEntry != null) {
                this.downloadResult = new DownloadResult(diskCacheEntry, false);
                this.imageFrom = ImageFrom.DISK_CACHE;
                postRunLoad();
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "diskCache", " - ", attrs.getName()));
                }
            } else {
                if (options.getRequestLevel() == RequestLevel.LOCAL) {
                    if (options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD) {
                        setRequestStatus(RequestStatus.WAIT_DISPLAY);
                        attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_PAUSE_DOWNLOAD, this).sendToTarget();
                        if (Sketch.isDebugMode()) {
                            Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "pause download", " - ", attrs.getName()));
                        }
                    } else {
                        toCanceledStatus(CancelCause.LEVEL_IS_LOCAL);
                        if (Sketch.isDebugMode()) {
                            Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "requestLevel is local", " - ", attrs.getName()));
                        }
                    }
                    return;
                }

                postRunDownload();
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "download", " - ", attrs.getName()));
                }
            }
        } else {
            this.imageFrom = ImageFrom.LOCAL;
            postRunLoad();
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "local", " - ", attrs.getName()));
            }
        }
    }

    /**
     * 执行下载
     */
    private void executeDownload() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "startDownload", " - ", attrs.getName()));
            }
            return;
        }

        DownloadResult justDownloadResult = attrs.getConfiguration().getImageDownloader().download(this);

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "downloadAfter", " - ", attrs.getName()));
            }
            return;
        }

        if (justDownloadResult != null && (justDownloadResult.getDiskCacheEntry() != null || justDownloadResult.getImageData() != null)) {
            this.downloadResult = justDownloadResult;
            this.imageFrom = justDownloadResult.isFromNetwork() ? ImageFrom.NETWORK : ImageFrom.DISK_CACHE;

            postRunLoad();
        } else {
            toFailedStatus(FailCause.DOWNLOAD_FAIL);
        }
    }

    /**
     * 执行加载
     */
    private void executeLoad() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "canceled", " - ", "startLoad", " - ", attrs.getName()));
            }
            return;
        }

        // 检查内存缓存中是否已经存在了
        if (options.isCacheInMemory()) {
            Drawable cacheDrawable = attrs.getConfiguration().getMemoryCache().get(memoryCacheId);
            if (cacheDrawable != null) {
                RecycleDrawableInterface recycleDrawable = (RecycleDrawableInterface) cacheDrawable;
                if (!recycleDrawable.isRecycled()) {
                    if (Sketch.isDebugMode()) {
                        Log.i(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "from memory get drawable", " - ", recycleDrawable.getInfo(), " - ", attrs.getName()));
                    }
                    this.resultDrawable = cacheDrawable;
                    imageFrom = ImageFrom.MEMORY_CACHE;
                    setRequestStatus(RequestStatus.WAIT_DISPLAY);
                    recycleDrawable.setIsWaitDisplay("executeLoad:fromMemory", true);
                    attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
                    return;
                } else {
                    attrs.getConfiguration().getMemoryCache().remove(memoryCacheId);
                    if (Sketch.isDebugMode()) {
                        Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", "bitmap recycled", " - ", recycleDrawable.getInfo(), " - ", attrs.getName()));
                    }
                }
            }
        }

        setRequestStatus(RequestStatus.LOADING);

        // 尝试用本地图片预处理器处理一下特殊的本地图片，并得到他们的缓存
        if (attrs.getConfiguration().getLocalImagePreprocessor().isSpecific(this)) {
            DiskCache.Entry specificLocalImageDiskCacheEntry = attrs.getConfiguration().getLocalImagePreprocessor().getDiskCacheEntry(this);
            if (specificLocalImageDiskCacheEntry != null) {
                this.downloadResult = new DownloadResult(specificLocalImageDiskCacheEntry, false);
            } else {
                toFailedStatus(FailCause.NOT_GET_SPECIFIC_LOCAL_IMAGE_CACHE_FILE);
                return;
            }
        }

        // 解码
        Object decodeResult = attrs.getConfiguration().getImageDecoder().decode(this);
        if (decodeResult == null) {
            toFailedStatus(FailCause.DECODE_FAIL);
            return;
        }

        if (decodeResult instanceof Bitmap) {
            Bitmap bitmap = (Bitmap) decodeResult;
            if (!bitmap.isRecycled()) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "new bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", attrs.getName()));
                }
            } else {
                if (Sketch.isDebugMode()) {
                    Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "decode failed bitmap recycled", " - ", "decode after", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", attrs.getName()));
                }
            }

            if (isCanceled()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "canceled", " - ", "decode after", " - ", "recycle bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", attrs.getName()));
                }
                bitmap.recycle();
                return;
            }

            //处理
            if (!bitmap.isRecycled()) {
                ImageProcessor imageProcessor = options.getImageProcessor();
                if (imageProcessor != null) {
                    Bitmap newBitmap = imageProcessor.process(attrs.getSketch(), bitmap, options.getResize(), options.isForceUseResize(), options.isLowQualityImage());
                    if (newBitmap != null && newBitmap != bitmap && Sketch.isDebugMode()) {
                        Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "process after", " - ", "newBitmap", " - ", RecycleBitmapDrawable.getInfo(newBitmap, mimeType), " - ", "recycled old bitmap", " - ", attrs.getName()));
                    }
                    if (newBitmap == null || newBitmap != bitmap) {
                        bitmap.recycle();
                    }
                    bitmap = newBitmap;
                }
            }

            if (isCanceled()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "canceled", " - ", "process after", " - ", "recycle bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", attrs.getName()));
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
                return;
            }

            if (bitmap != null && !bitmap.isRecycled()) {
                RecycleBitmapDrawable bitmapDrawable = new RecycleBitmapDrawable(bitmap);
                if (options.isCacheInMemory() && memoryCacheId != null) {
                    attrs.getConfiguration().getMemoryCache().put(memoryCacheId, bitmapDrawable);
                }
                bitmapDrawable.setMimeType(mimeType);
                this.resultDrawable = bitmapDrawable;
                setRequestStatus(RequestStatus.WAIT_DISPLAY);
                bitmapDrawable.setIsWaitDisplay("executeLoad:new", true);
                attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
            } else {
                toFailedStatus(FailCause.DECODE_FAIL);
            }
        } else if (decodeResult instanceof RecycleGifDrawable) {
            RecycleGifDrawable gifDrawable = (RecycleGifDrawable) decodeResult;
            gifDrawable.setMimeType(mimeType);

            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "new gif drawable", " - ", gifDrawable.getInfo(), " - ", attrs.getName()));
            }

            if (!gifDrawable.isRecycled()) {
                if (options.isCacheInMemory() && memoryCacheId != null) {
                    attrs.getConfiguration().getMemoryCache().put(memoryCacheId, gifDrawable);
                }
                this.resultDrawable = gifDrawable;
                setRequestStatus(RequestStatus.WAIT_DISPLAY);
                gifDrawable.setIsWaitDisplay("executeLoad:new", true);
                attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
            } else {
                toFailedStatus(FailCause.DECODE_FAIL);
            }
        } else {
            toFailedStatus(FailCause.DECODE_FAIL);
        }
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    private void handleCompletedOnMainThread() {
        if (isCanceled()) {
            if (resultDrawable != null && resultDrawable instanceof RecycleDrawableInterface) {
                ((RecycleDrawableInterface) resultDrawable).setIsWaitDisplay("completedCallback:cancel", false);
            }
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handleCompletedOnMainThread", " - ", "canceled", " - ", attrs.getName()));
            }
            return;
        }

        setRequestStatus(RequestStatus.DISPLAYING);

        ImageDisplayer displayer = options.getImageDisplayer();

        // Set FixedSize
        Drawable finalDrawable;
        if (displayer != null && displayer instanceof TransitionImageDisplayer && resultDrawable instanceof RecycleBitmapDrawable && fixedSize != null && scaleType == ImageView.ScaleType.CENTER_CROP) {
            finalDrawable = new FixedRecycleBitmapDrawable((RecycleBitmapDrawable) resultDrawable, fixedSize);
        } else {
            finalDrawable = resultDrawable;
        }

        if (displayer == null) {
            displayer = attrs.getConfiguration().getDefaultImageDisplayer();
        }
        displayer.display(sketchImageViewInterfaceHolder.getSketchImageViewInterface(), finalDrawable);
        ((RecycleDrawableInterface) resultDrawable).setIsWaitDisplay("completedCallback", false);
        setRequestStatus(RequestStatus.COMPLETED);
        if (displayListener != null) {
            displayListener.onCompleted(imageFrom, mimeType);
        }
    }

    private void handleFailedOnMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handleFailedOnMainThread", " - ", "canceled", " - ", attrs.getName()));
            }
            return;
        }

        setRequestStatus(RequestStatus.DISPLAYING);

        ImageDisplayer displayer = options.getImageDisplayer();
        if (displayer == null) {
            displayer = attrs.getConfiguration().getDefaultImageDisplayer();
        }
        displayer.display(sketchImageViewInterfaceHolder.getSketchImageViewInterface(), getFailureDrawable());
        setRequestStatus(RequestStatus.FAILED);
        if (displayListener != null) {
            displayListener.onFailed(failCause);
        }
    }

    private void handleCanceledOnMainThread() {
        if (displayListener != null) {
            displayListener.onCanceled(cancelCause);
        }
    }

    private void handlePauseDownloadOnMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handlePauseDownloadOnMainThread", " - ", "canceled", " - ", attrs.getName()));
            }
            return;
        }

        if (options.getPauseDownloadImage() != null) {
            setRequestStatus(RequestStatus.DISPLAYING);
            ImageDisplayer displayer = options.getImageDisplayer();
            if (displayer == null) {
                displayer = attrs.getConfiguration().getDefaultImageDisplayer();
            }
            displayer.display(sketchImageViewInterfaceHolder.getSketchImageViewInterface(), getPauseDownloadDrawable());
        }

        cancelCause = CancelCause.PAUSE_DOWNLOAD;
        setRequestStatus(RequestStatus.CANCELED);
        if (displayListener != null) {
            displayListener.onCanceled(cancelCause);
        }
    }

    private void updateProgressOnMainThread(int totalLength, int completedLength) {
        if (isFinished()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "updateProgressOnMainThread", " - ", "finished", " - ", attrs.getName()));
            }
            return;
        }

        if (downloadProgressListener != null) {
            downloadProgressListener.onUpdateDownloadProgress(totalLength, completedLength);
        }
    }
}