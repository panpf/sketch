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
import android.util.Log;

import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 加载请求
 */
public class DefaultLoadRequest extends SketchRequest implements LoadRequest {
    private static final String NAME = "DefaultLoadRequest";

    private RequestAttrs attrs;
    private LoadOptions options;

    private LoadListener loadListener;
    private DownloadProgressListener progressListener;
    private DownloadResult downloadResult;
    private LoadResult loadResult;

    public DefaultLoadRequest(RequestAttrs attrs, LoadOptions options, LoadListener loadListener, DownloadProgressListener progressListener) {
        super(attrs.getConfiguration().getRequestExecutor());
        this.attrs = attrs;
        this.options = options;
        this.loadListener = loadListener;
        this.progressListener = progressListener;
    }

    @Override
    public RequestAttrs getAttrs() {
        return attrs;
    }

    @Override
    public LoadOptions getOptions() {
        return options;
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
    public void updateProgress(int totalLength, int completedLength) {
        if (progressListener != null) {
            postRunUpdateProgress(totalLength, completedLength);
        }
    }

    @Override
    public void failed(FailedCause failedCause) {
        super.failed(failedCause);

        if (loadListener != null) {
            postRunFailed();
        }
    }

    @Override
    public void canceled(CancelCause cancelCause) {
        super.canceled(cancelCause);

        if (loadListener != null) {
            postRunCanceled();
        }
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
        if (attrs.getUriScheme() != UriScheme.HTTP && attrs.getUriScheme() != UriScheme.HTTPS) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "local", " - ", attrs.getName()));
            }
            submitRunLoad();
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
                submitRunLoad();
                return;
            }
        }

        // 在下载之前判断如果请求Level限制只能从本地加载的话就取消了
        if (options.getRequestLevel() == RequestLevel.LOCAL) {
            canceled(options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD ? CancelCause.PAUSE_DOWNLOAD : CancelCause.LEVEL_IS_LOCAL);
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "canceled", " - ", options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD ? "pause download" : "requestLevel is local", " - ", attrs.getName()));
            }
            return;
        }

        // 执行下载
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "download", " - ", attrs.getName()));
        }
        submitRunDownload();
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
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "canceled", " - ", "startLoad", " - ", attrs.getName()));
            }
            return;
        }

        setStatus(Status.LOADING);

        // 尝试用本地图片预处理器处理一下特殊的本地图片，并得到他们的缓存
        if (attrs.getConfiguration().getLocalImagePreprocessor().isSpecific(this)) {
            DiskCache.Entry specificLocalImageDiskCacheEntry = attrs.getConfiguration().getLocalImagePreprocessor().getDiskCacheEntry(this);
            if (specificLocalImageDiskCacheEntry != null) {
                this.downloadResult = new DownloadResult(specificLocalImageDiskCacheEntry, false);
            } else {
                failed(FailedCause.NOT_GET_SPECIFIC_LOCAL_IMAGE_CACHE_FILE);
                return;
            }
        }

        // 解码
        DecodeResult decodeResult = attrs.getConfiguration().getImageDecoder().decode(this);
        if (decodeResult == null) {
            failed(FailedCause.DECODE_FAIL);
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
                postRunCompleted();
            } else {
                failed(FailedCause.DECODE_FAIL);
            }
        } else if (decodeResult.getResultGifDrawable() != null) {
            RecycleGifDrawable gifDrawable = decodeResult.getResultGifDrawable();

            if (!gifDrawable.isRecycled()) {
                gifDrawable.setMimeType(decodeResult.getMimeType());

                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "new gif drawable", " - ", gifDrawable.getInfo(), " - ", attrs.getName()));
                }

                loadResult = new LoadResult(gifDrawable, decodeResult.getImageFrom(), decodeResult.getMimeType());
                postRunCompleted();
            }else{
                if (Sketch.isDebugMode()) {
                    Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runLoad", " - ", "gif drawable recycled", " - ", gifDrawable.getInfo(), " - ", attrs.getName()));
                }

                failed(FailedCause.DECODE_FAIL);
            }
        } else {
            failed(FailedCause.DECODE_FAIL);
        }
    }

    @Override
    protected void runUpdateProgressInMainThread(int totalLength, int completedLength) {
        if (isFinished()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runUpdateProgressInMainThread", " - ", "finished", " - ", attrs.getName()));
            }
            return;
        }

        if (progressListener != null) {
            progressListener.onUpdateDownloadProgress(totalLength, completedLength);
        }
    }

    @Override
    protected void runCanceledInMainThread() {
        if (loadListener != null) {
            loadListener.onCanceled(getCancelCause());
        }
    }

    @Override
    protected void runCompletedInMainThread() {
        if (isCanceled()) {
            // 已经取消了就直接把图片回收了
            if (loadResult != null && loadResult.getDrawable() instanceof RecycleDrawableInterface) {
                ((RecycleDrawableInterface) loadResult.getDrawable()).recycle();
            }
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runCompletedInMainThread", " - ", "canceled", " - ", attrs.getName()));
            }
            return;
        }

        setStatus(Status.COMPLETED);

        if (loadListener != null && loadResult != null) {
            loadListener.onCompleted(loadResult.getDrawable(), loadResult.getImageFrom(), loadResult.getMimeType());
        }
    }

    @Override
    protected void runFailedInMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runFailedInMainThread", " - ", "canceled", " - ", attrs.getName()));
            }
            return;
        }

        if (loadListener != null) {
            loadListener.onFailed(getFailedCause());
        }
    }
}
