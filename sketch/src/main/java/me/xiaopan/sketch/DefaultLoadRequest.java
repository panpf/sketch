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
import android.os.Message;
import android.util.Log;

import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 加载请求
 */
public class DefaultLoadRequest extends SketchRequest implements LoadRequest {
    private static final int WHAT_CALLBACK_COMPLETED = 202;
    private static final int WHAT_CALLBACK_FAILED = 203;
    private static final int WHAT_CALLBACK_CANCELED = 204;
    private static final int WHAT_CALLBACK_PROGRESS = 205;
    private static final String NAME = "DefaultLoadRequest";

    private RequestAttrs attrs;
    private LoadOptions options;
    private LoadListener loadListener;
    private DownloadProgressListener downloadProgressListener;

    private DownloadResult downloadResult;
    private LoadResult loadResult;
    private FailCause failCause;    // 失败原因
    private CancelCause cancelCause;  // 取消原因
    private RequestStatus requestStatus = RequestStatus.WAIT_DISPATCH;  // 状态

    public DefaultLoadRequest(RequestAttrs attrs, LoadOptions options, LoadListener loadListener) {
        super(attrs.getConfiguration().getRequestExecutor());
        this.attrs = attrs;
        this.options = options;
        this.loadListener = loadListener;
    }

    @Override
    public RequestAttrs getAttrs() {
        return attrs;
    }

    @Override
    public LoadOptions getOptions() {
        return options;
    }

    public void setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
    }

    @Override
    public DownloadResult getDownloadResult() {
        return downloadResult;
    }

    @Override
    public LoadResult getLoadResult() {
        return loadResult;
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
    public boolean isCanceled() {
        return requestStatus == RequestStatus.CANCELED;
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
    public void updateProgress(int totalLength, int completedLength) {
        if (downloadProgressListener != null) {
            attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_PROGRESS, totalLength, completedLength, this).sendToTarget();
        }
    }

    @Override
    public void toFailedStatus(FailCause failCause) {
        this.failCause = failCause;
        attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_FAILED, this).sendToTarget();
    }

    @Override
    public void toCanceledStatus(CancelCause cancelCause) {
        this.cancelCause = cancelCause;
        setRequestStatus(RequestStatus.CANCELED);
        attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_CANCELED, this).sendToTarget();
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
            default:
                new IllegalArgumentException("unknown message what: " + msg.what).printStackTrace();
                break;
        }
    }

    @Override
    protected void onPostRunDispatch() {
        super.onPostRunDispatch();
        setRequestStatus(RequestStatus.WAIT_DISPATCH);
    }

    @Override
    protected void onPostRunDownload() {
        super.onPostRunDownload();
        setRequestStatus(RequestStatus.WAIT_DOWNLOAD);
    }

    @Override
    protected void onPostRunLoad() {
        super.onPostRunLoad();
        setRequestStatus(RequestStatus.WAIT_LOAD);
    }

    @Override
    protected void runDispatch() {
        setRequestStatus(RequestStatus.DISPATCHING);

        // 本地请求直接执行加载
        if (attrs.getUriScheme() != UriScheme.HTTP && attrs.getUriScheme() != UriScheme.HTTPS) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "local", " - ", attrs.getName()));
            }
            postRunLoad();
            return;
        }

        // 然后从磁盘缓存中找缓存文件
        if (options.isCacheInDisk()) {
            DiskCache.Entry diskCacheEntry = attrs.getConfiguration().getDiskCache().get(attrs.getUri());
            if (diskCacheEntry != null) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "diskCache", " - ", attrs.getName()));
                }
                downloadResult = new DownloadResult(diskCacheEntry, false);
                postRunLoad();
                return;
            }
        }

        // 在下载之前判断如果请求Level限制只能从本地加载的话就取消了
        if (options.getRequestLevel() == RequestLevel.LOCAL) {
            toCanceledStatus(options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD ? CancelCause.PAUSE_DOWNLOAD : CancelCause.LEVEL_IS_LOCAL);
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD ? "pause download" : "requestLevel is local", " - ", attrs.getName()));
            }
            return;
        }

        // 执行下载
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "download", " - ", attrs.getName()));
        }
        postRunDownload();
    }

    @Override
    protected void runDownload() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDownload", " - ", "canceled", " - ", "startDownload", " - ", attrs.getName()));
            }
            return;
        }

        // 调用下载器下载
        DownloadResult justDownloadResult = attrs.getConfiguration().getImageDownloader().download(this);

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDownload", " - ", "canceled", " - ", "downloadAfter", " - ", attrs.getName()));
            }
            return;
        }

        // 都是空的就算下载失败
        if (justDownloadResult == null || (justDownloadResult.getDiskCacheEntry() == null && justDownloadResult.getImageData() == null)) {
            toFailedStatus(FailCause.DOWNLOAD_FAIL);
            return;
        }

        // 下载成功了，执行加载
        downloadResult = justDownloadResult;
        postRunLoad();
    }

    @Override
    protected void runLoad() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "canceled", " - ", "startLoad", " - ", attrs.getName()));
            }
            return;
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
        DecodeResult decodeResult = attrs.getConfiguration().getImageDecoder().decode(this);
        if (decodeResult == null) {
            toFailedStatus(FailCause.DECODE_FAIL);
            return;
        }

        if (decodeResult.getResultBitmap() != null) {
            Bitmap bitmap = decodeResult.getResultBitmap();
            if (!bitmap.isRecycled()) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "new bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, decodeResult.getMimeType()), " - ", attrs.getName()));
                }
            } else {
                if (Sketch.isDebugMode()) {
                    Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "decode failed bitmap recycled", " - ", "decode after", " - ", RecycleBitmapDrawable.getInfo(bitmap, decodeResult.getMimeType()), " - ", attrs.getName()));
                }
            }

            if (isCanceled()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "canceled", " - ", "decode after", " - ", "recycle bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, decodeResult.getMimeType()), " - ", attrs.getName()));
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
                        Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "process after", " - ", "newBitmap", " - ", RecycleBitmapDrawable.getInfo(newBitmap, decodeResult.getMimeType()), " - ", "recycled old bitmap", " - ", attrs.getName()));
                    }
                    if (newBitmap == null || newBitmap != bitmap) {
                        bitmap.recycle();
                    }
                    bitmap = newBitmap;
                }
            }

            if (isCanceled()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "canceled", " - ", "process after", " - ", "recycle bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, decodeResult.getMimeType()), " - ", attrs.getName()));
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
                return;
            }

            if (bitmap != null && !bitmap.isRecycled()) {
                RecycleBitmapDrawable bitmapDrawable = new RecycleBitmapDrawable(bitmap);
                bitmapDrawable.setMimeType(decodeResult.getMimeType());

                loadResult = new LoadResult(bitmapDrawable, decodeResult.getImageFrom(), decodeResult.getMimeType());

                attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
            } else {
                toFailedStatus(FailCause.DECODE_FAIL);
            }
        } else if (decodeResult.getResultGifDrawable() != null) {
            RecycleGifDrawable gifDrawable = decodeResult.getResultGifDrawable();

            if (!gifDrawable.isRecycled()) {
                gifDrawable.setMimeType(decodeResult.getMimeType());

                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "new gif drawable", " - ", gifDrawable.getInfo(), " - ", attrs.getName()));
                }

                loadResult = new LoadResult(gifDrawable, decodeResult.getImageFrom(), decodeResult.getMimeType());

                attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
            }else{
                if (Sketch.isDebugMode()) {
                    Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "gif drawable recycled", " - ", gifDrawable.getInfo(), " - ", attrs.getName()));
                }

                toFailedStatus(FailCause.DECODE_FAIL);
            }
        } else {
            toFailedStatus(FailCause.DECODE_FAIL);
        }
    }

    private void handleCompletedOnMainThread() {
        if (isCanceled()) {
            if (loadResult != null && loadResult.getDrawable() instanceof RecycleDrawableInterface) {
                ((RecycleDrawableInterface) loadResult.getDrawable()).recycle();
            }
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handleCompletedOnMainThread", " - ", "canceled", " - ", attrs.getName()));
            }
            return;
        }

        setRequestStatus(RequestStatus.COMPLETED);
        if (loadListener != null && loadResult != null) {
            loadListener.onCompleted(loadResult.getDrawable(), loadResult.getImageFrom(), loadResult.getMimeType());
        }
    }

    private void handleFailedOnMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handleFailedOnMainThread", " - ", "canceled", " - ", attrs.getName()));
            }
            return;
        }

        setRequestStatus(RequestStatus.FAILED);
        if (loadListener != null) {
            loadListener.onFailed(failCause);
        }
    }

    private void handleCanceledOnMainThread() {
        if (loadListener != null) {
            loadListener.onCanceled(cancelCause);
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
