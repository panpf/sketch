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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchMonitor;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.decode.DecodeResult;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.feature.ImagePreprocessor;
import me.xiaopan.sketch.feature.PreProcessResult;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 加载请求
 */
public class LoadRequest extends FreeRideDownloadRequest {
    private LoadOptions loadOptions;
    private LoadListener loadListener;

    private DataSource dataSource;
    private LoadResult loadResult;

    public LoadRequest(
            Sketch sketch, LoadInfo info,
            LoadOptions loadOptions, LoadListener loadListener,
            DownloadProgressListener downloadProgressListener) {
        super(sketch, info, loadOptions, null, downloadProgressListener);

        this.loadOptions = loadOptions;
        this.loadListener = loadListener;

        setLogName("LoadRequest");
    }

    /**
     * 获取磁盘缓存key
     */
    public String getProcessedImageDiskCacheKey() {
        return ((LoadInfo) info).getProcessedImageDiskCacheKey();
    }

    /**
     * 获取加载选项
     */
    @Override
    public LoadOptions getOptions() {
        return loadOptions;
    }

    /**
     * 获取加载结果
     */
    public LoadResult getLoadResult() {
        return loadResult;
    }

    /**
     * 设置加载结果
     */
    @SuppressWarnings("unused")
    protected void setLoadResult(LoadResult loadResult) {
        this.loadResult = loadResult;
    }

    /**
     * 获取数据源
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    protected boolean canUseCacheProcessedImageFunction() {
        if (!loadOptions.isCacheProcessedImageInDisk()) {
            return false;
        }
        if (loadOptions.getMaxSize() != null || loadOptions.getResize() != null) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (loadOptions.getImageProcessor() != null) {
            return true;
        }
        return loadOptions.isThumbnailMode() && loadOptions.getResize() != null;
    }

    @Override
    public void error(ErrorCause errorCause) {
        super.error(errorCause);

        if (loadListener != null) {
            postRunError();
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
    protected void runDispatch() {
        if (isCanceled()) {
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("canceled", "runDispatch", "load request just start");
            }
            return;
        }

        setStatus(Status.INTERCEPT_LOCAL_TASK);

        if (getUriScheme() != UriScheme.NET) {
            // 本地请求直接执行加载
            if (SLogType.REQUEST.isEnabled()) {
                printLogD("local thread", "local image", "runDispatch");
            }
            submitRunLoad();
            return;
        } else {
            // 是网络图片但是本地已经有缓存好的且经过处理的缓存图片可以直接用
            if (canUseCacheProcessedImageFunction() && existProcessedImageDiskCache()) {
                if (SLogType.REQUEST.isEnabled()) {
                    printLogD("local thread", "disk cache image", "runDispatch");
                }
                submitRunLoad();
                return;
            }
        }

        super.runDispatch();
    }

    private boolean existProcessedImageDiskCache() {
        DiskCache diskCache = getSketch().getConfiguration().getDiskCache();
        ReentrantLock editLock = diskCache.getEditLock(getProcessedImageDiskCacheKey());
        editLock.lock();

        boolean exist = diskCache.exist(getProcessedImageDiskCacheKey());

        editLock.unlock();
        return exist;
    }

    @Override
    protected void downloadCompleted() {
        DownloadResult downloadResult = getDownloadResult();
        if (downloadResult != null && downloadResult.getDiskCacheEntry() != null) {
            dataSource = new DataSource(downloadResult.getDiskCacheEntry(), downloadResult.getImageFrom());
            submitRunLoad();
        } else if (downloadResult != null && downloadResult.getImageData() != null && downloadResult.getImageData().length > 0) {
            dataSource = new DataSource(downloadResult.getImageData(), downloadResult.getImageFrom());
            submitRunLoad();
        } else {
            if (SLogType.REQUEST.isEnabled()) {
                printLogE("are all null", "downloadCompleted");
            }
            error(ErrorCause.DOWNLOAD_FAIL);
        }
    }

    @Override
    protected void runLoad() {
        if (isCanceled()) {
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("canceled", "runLoad", "load request just start");
            }
            return;
        }

        boolean dataSourceFromProcessedCache = false;
        if (dataSource == null && canUseCacheProcessedImageFunction()) {
            dataSource = checkProcessedImageDiskCache();
            dataSourceFromProcessedCache = true;
        }

        // 预处理
        ImagePreprocessor imagePreprocessor = getSketch().getConfiguration().getImagePreprocessor();
        if (dataSource == null && imagePreprocessor.isSpecific(this)) {
            setStatus(Status.PRE_PROCESS);
            PreProcessResult prePrecessResult = imagePreprocessor.process(this);
            if (prePrecessResult != null && prePrecessResult.diskCacheEntry != null) {
                dataSource = new DataSource(prePrecessResult.diskCacheEntry, prePrecessResult.imageFrom);
            } else if (prePrecessResult != null && prePrecessResult.imageData != null) {
                dataSource = new DataSource(prePrecessResult.imageData, prePrecessResult.imageFrom);
            } else {
                error(ErrorCause.PRE_PROCESS_RESULT_IS_NULL);
                return;
            }
        }

        // 解码
        setStatus(Status.DECODING);
        DecodeResult decodeResult = getSketch().getConfiguration().getImageDecoder().decode(this);

        // 如果是缓存的已处理图片就需要恢复图片类型以及原始尺寸
        if (dataSourceFromProcessedCache && decodeResult != null) {
            BitmapFactory.Options options = getSketch().getConfiguration().getImageDecoder().decodeBounds(this);
            if (options != null && !TextUtils.isEmpty(options.outMimeType)) {
                decodeResult.getImageAttrs().reset(options.outMimeType, options.outWidth, options.outHeight);
            }
        }

        if (decodeResult != null && decodeResult.getBitmap() != null) {
            Bitmap bitmap = decodeResult.getBitmap();

            if (bitmap.isRecycled()) {
                if (SLogType.REQUEST.isEnabled()) {
                    printLogE("decode failed", "runLoad", "bitmap recycled", "bitmapInfo: "
                            + SketchUtils.makeImageInfo(null, bitmap, decodeResult.getImageAttrs().getMimeType()));
                }
                error(ErrorCause.BITMAP_RECYCLED);
                return;
            }

            if (SLogType.REQUEST.isEnabled()) {
                printLogI("decode success", "runLoad", "bitmapInfo: "
                        + SketchUtils.makeImageInfo(null, bitmap, decodeResult.getImageAttrs().getMimeType()));
            }

            if (isCanceled()) {
                if (SLogType.REQUEST.isEnabled()) {
                    printLogW("canceled", "runLoad", "decode after", "bitmapInfo: "
                            + SketchUtils.makeImageInfo(null, bitmap, decodeResult.getImageAttrs().getMimeType()));
                }
                BitmapPoolUtils.freeBitmapToPool(bitmap, getSketch().getConfiguration().getBitmapPool());
                return;
            }

            // 处理一下
            boolean allowProcess = dataSource == null || !dataSource.isDisableProcess();
            boolean canCacheInDiskCache = decodeResult.isCanCacheInDiskCache();
            if (allowProcess) {
                ImageProcessor imageProcessor = loadOptions.getImageProcessor();
                if (imageProcessor != null) {
                    setStatus(Status.PROCESSING);

                    Bitmap newBitmap = null;
                    try {
                        newBitmap = imageProcessor.process(
                                getSketch(), bitmap,
                                loadOptions.getResize(), loadOptions.isForceUseResize(),
                                loadOptions.isLowQualityImage());
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        SketchMonitor sketchMonitor = getSketch().getConfiguration().getMonitor();
                        sketchMonitor.onProcessImageError(e, getKey(), imageProcessor);
                    }

                    // 确实是一张新图片，就替换掉旧图片
                    if (newBitmap != null && !newBitmap.isRecycled() && newBitmap != bitmap) {
                        if (SLogType.REQUEST.isEnabled()) {
                            printLogW("process new bitmap", "runLoad", "bitmapInfo: "
                                    + SketchUtils.makeImageInfo(null, newBitmap, decodeResult.getImageAttrs().getMimeType()));
                        }

                        BitmapPoolUtils.freeBitmapToPool(bitmap, getSketch().getConfiguration().getBitmapPool());
                        bitmap = newBitmap;
                        canCacheInDiskCache |= true;
                    } else {
                        // 有可能处理后没得到新图片旧图片也没了，这叫赔了夫人又折兵
                        if (bitmap.isRecycled()) {
                            error(ErrorCause.SOURCE_BITMAP_RECYCLED);
                            return;
                        }
                    }

                    if (isCanceled()) {
                        if (SLogType.REQUEST.isEnabled()) {
                            printLogW("canceled", "runLoad", "process after", "bitmapInfo: "
                                    + SketchUtils.makeImageInfo(null, bitmap, decodeResult.getImageAttrs().getMimeType()));
                        }
                        BitmapPoolUtils.freeBitmapToPool(bitmap, getSketch().getConfiguration().getBitmapPool());
                        return;
                    }
                }
            }

            // 缓存经过处理的图片
            if (allowProcess && canCacheInDiskCache && canUseCacheProcessedImageFunction()) {
                saveProcessedImageToDiskCache(bitmap);
            }

            loadResult = new LoadResult(bitmap, decodeResult);
            loadCompleted();
        } else if (decodeResult != null && decodeResult.getGifDrawable() != null) {
            SketchGifDrawable gifDrawable = decodeResult.getGifDrawable();

            if (gifDrawable.isRecycled()) {
                if (SLogType.REQUEST.isEnabled()) {
                    printLogE("decode failed", "runLoad", "gif drawable recycled", "gifInfo: " + gifDrawable.getInfo());
                }
                error(ErrorCause.GIF_DRAWABLE_RECYCLED);
                return;
            }

            if (SLogType.REQUEST.isEnabled()) {
                printLogI("decode gif success", "runLoad", "gifInfo: " + gifDrawable.getInfo());
            }

            if (isCanceled()) {
                if (SLogType.REQUEST.isEnabled()) {
                    printLogW("runLoad", "runLoad", "decode after", "gifInfo: " + gifDrawable.getInfo());
                }
                gifDrawable.recycle();
                return;
            }

            loadResult = new LoadResult(gifDrawable, decodeResult);
            loadCompleted();
        } else {
            if (SLogType.REQUEST.isEnabled()) {
                printLogE("are all null", "runLoad");
            }
            error(ErrorCause.DECODE_FAIL);
        }
    }

    /**
     * 开启了缓存已处理图片功能，如果磁盘缓存中已经有了缓存就直接读取
     */
    private DataSource checkProcessedImageDiskCache() {
        DiskCache diskCache = getSketch().getConfiguration().getDiskCache();
        ReentrantLock editLock = diskCache.getEditLock(getProcessedImageDiskCacheKey());
        editLock.lock();

        DiskCache.Entry diskCacheEntry = diskCache.get(getProcessedImageDiskCacheKey());

        editLock.unlock();

        if (diskCacheEntry != null) {
            DataSource dataSource = new DataSource(diskCacheEntry, ImageFrom.DISK_CACHE);
            dataSource.setDisableProcess(true);
            return dataSource;
        }

        return null;
    }

    /**
     * 保存bitmap到磁盘缓存
     */
    private void saveProcessedImageToDiskCache(Bitmap bitmap) {
        DiskCache diskCache = getSketch().getConfiguration().getDiskCache();
        ReentrantLock editLock = diskCache.getEditLock(getProcessedImageDiskCacheKey());
        editLock.lock();

        DiskCache.Entry diskCacheEntry = diskCache.get(getProcessedImageDiskCacheKey());

        if (diskCacheEntry != null) {
            diskCacheEntry.delete();
        }

        DiskCache.Editor diskCacheEditor = diskCache.edit(getProcessedImageDiskCacheKey());
        if (diskCacheEditor != null) {
            BufferedOutputStream outputStream = null;
            try {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
                bitmap.compress(SketchUtils.bitmapConfigToCompressFormat(bitmap.getConfig()), 100, outputStream);
                diskCacheEditor.commit();
            } catch (DiskLruCache.EditorChangedException e) {
                e.printStackTrace();
                diskCacheEditor.abort();
            } catch (IOException e) {
                e.printStackTrace();
                diskCacheEditor.abort();
            } catch (DiskLruCache.ClosedException e) {
                e.printStackTrace();
                diskCacheEditor.abort();
            } catch (DiskLruCache.FileNotExistException e) {
                e.printStackTrace();
                diskCacheEditor.abort();
            } finally {
                SketchUtils.close(outputStream);
            }
        }

        editLock.unlock();
    }

    protected void loadCompleted() {
        postRunCompleted();
    }

    @Override
    protected void runCompletedInMainThread() {
        if (isCanceled()) {
            // 已经取消了就直接把图片回收了
            if (loadResult != null) {
                if (loadResult.getBitmap() != null) {
                    BitmapPoolUtils.freeBitmapToPool(loadResult.getBitmap(), getSketch().getConfiguration().getBitmapPool());
                }
                if (loadResult.getGifDrawable() != null) {
                    loadResult.getGifDrawable().recycle();
                }
            }
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("canceled", "runCompletedInMainThread");
            }
            return;
        }

        setStatus(Status.COMPLETED);

        if (loadListener != null && loadResult != null) {
            loadListener.onCompleted(loadResult);
        }
    }

    @Override
    protected void runErrorInMainThread() {
        if (isCanceled()) {
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("canceled", "runErrorInMainThread");
            }
            return;
        }

        if (loadListener != null) {
            loadListener.onError(getErrorCause());
        }
    }

    @Override
    protected void runCanceledInMainThread() {
        if (loadListener != null) {
            loadListener.onCanceled(getCancelCause());
        }
    }
}
