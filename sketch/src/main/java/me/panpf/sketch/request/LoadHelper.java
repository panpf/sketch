/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView.ScaleType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.panpf.sketch.Configuration;
import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.decode.ImageType;
import me.panpf.sketch.decode.ProcessedResultCacheProcessor;
import me.panpf.sketch.decode.ThumbnailModeDecodeHelper;
import me.panpf.sketch.process.ImageProcessor;
import me.panpf.sketch.uri.UriModel;
import me.panpf.sketch.util.SketchUtils;

/**
 * 加载 Helper，负责组织、收集、初始化加载参数，最后执行 commit() 提交请求
 */
public class LoadHelper {
    private static final String NAME = "LoadHelper";

    private Sketch sketch;
    private boolean sync;

    private String uri;
    private UriModel uriModel;
    private String key;
    private LoadOptions loadOptions = new LoadOptions();
    private LoadListener loadListener;
    private DownloadProgressListener downloadProgressListener;

    public LoadHelper(@NonNull Sketch sketch, @NonNull String uri, @NonNull LoadListener loadListener) {
        this.sketch = sketch;
        this.uri = uri;
        this.uriModel = UriModel.match(sketch, uri);
        this.loadListener = loadListener;
    }

    /**
     * 设置请求 level，限制请求处理深度，参考 {@link RequestLevel}
     *
     * @param requestLevel {@link RequestLevel}
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper requestLevel(@Nullable RequestLevel requestLevel) {
        if (requestLevel != null) {
            loadOptions.setRequestLevel(requestLevel);
        }
        return this;
    }

    /**
     * 禁用磁盘缓存
     *
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper disableCacheInDisk() {
        loadOptions.setCacheInDiskDisabled(true);
        return this;
    }

    /**
     * 禁止从 {@link BitmapPool} 中寻找可复用的 {@link Bitmap}
     *
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper disableBitmapPool() {
        loadOptions.setBitmapPoolDisabled(true);
        return this;
    }

    /**
     * 解码 gif 图片并自动循环播放
     *
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper decodeGifImage() {
        loadOptions.setDecodeGifImage(true);
        return this;
    }

    /**
     * 设置最大尺寸，用于计算 inSimpleSize 缩小图片
     *
     * @param maxSize 最大尺寸
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper maxSize(@Nullable MaxSize maxSize) {
        loadOptions.setMaxSize(maxSize);
        return this;
    }

    /**
     * 设置最大尺寸，用于计算 inSimpleSize 缩小图片
     *
     * @param maxWidth  最大宽
     * @param maxHeight 最大高
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper maxSize(int maxWidth, int maxHeight) {
        loadOptions.setMaxSize(maxWidth, maxHeight);
        return this;
    }

    /**
     * 调整图片的尺寸
     *
     * @param resize 新的尺寸
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    public LoadHelper resize(@Nullable Resize resize) {
        loadOptions.setResize(resize);
        return this;
    }

    /**
     * 调调整图片的尺寸
     *
     * @param reWidth  新的宽
     * @param reHeight 新的高
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    public LoadHelper resize(int reWidth, int reHeight) {
        loadOptions.setResize(reWidth, reHeight);
        return this;
    }

    /**
     * 调整图片的尺寸
     *
     * @param reWidth   新的宽
     * @param reHeight  新的高
     * @param scaleType 指定如何生成新图片
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    public LoadHelper resize(int reWidth, int reHeight, @NonNull ScaleType scaleType) {
        loadOptions.setResize(reWidth, reHeight, scaleType);
        return this;
    }

    /**
     * 在解码或创建 {@link Bitmap} 的时候尽量使用低质量的 {@link Bitmap.Config}，优先级低于 {@link #bitmapConfig(Bitmap.Config)}，参考 {@link ImageType#getConfig(boolean)}
     *
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    public LoadHelper lowQualityImage() {
        loadOptions.setLowQualityImage(true);
        return this;
    }

    /**
     * 设置图片处理器，在图片读取到内存后对图片进行修改
     *
     * @param processor {@link ImageProcessor}
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper processor(@Nullable ImageProcessor processor) {
        loadOptions.setProcessor(processor);
        return this;
    }

    /**
     * 设置解码时使用的 {@link Bitmap.Config}，KITKAT 以上 {@link Bitmap.Config#ARGB_4444} 会被强制替换为 {@link Bitmap.Config#ARGB_8888}，优先级高于 {@link #lowQualityImage()}，对应 {@link android.graphics.BitmapFactory.Options#inPreferredConfig} 属性
     *
     * @param bitmapConfig {@link Bitmap.Config}
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper bitmapConfig(@Nullable Bitmap.Config bitmapConfig) {
        loadOptions.setBitmapConfig(bitmapConfig);
        return this;
    }

    /**
     * 设置解码时优先考虑速度还是质量，对应 {@link android.graphics.BitmapFactory.Options#inPreferQualityOverSpeed} 属性
     *
     * @param inPreferQualityOverSpeed true：质量优先；false：速度优先
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper inPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        loadOptions.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
        return this;
    }

    /**
     * 开启缩略图模式，配合 resize 可以得到更清晰的缩略图，参考 {@link ThumbnailModeDecodeHelper}
     *
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper thumbnailMode() {
        loadOptions.setThumbnailMode(true);
        return this;
    }

    /**
     * 为了加快速度，将经过 {@link #processor(ImageProcessor)}、{@link #resize(Resize)} 或 {@link #thumbnailMode()} 处理过的图片保存到磁盘缓存中，下次就直接读取，参考 {@link ProcessedResultCacheProcessor}
     *
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper cacheProcessedImageInDisk() {
        loadOptions.setCacheProcessedImageInDisk(true);
        return this;
    }

    /**
     * 禁止纠正图片方向
     *
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper disableCorrectImageOrientation() {
        loadOptions.setCorrectImageOrientationDisabled(true);
        return this;
    }

    /**
     * 批量设置加载参数（完全覆盖）
     *
     * @param newOptions {@link LoadOptions}
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    public LoadHelper options(@Nullable LoadOptions newOptions) {
        loadOptions.copy(newOptions);
        return this;
    }

    /**
     * 设置下载进度监听器
     *
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper downloadProgressListener(@Nullable DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
        return this;
    }

    /**
     * 同步执行
     *
     * @return {@link LoadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper sync() {
        this.sync = true;
        return this;
    }

    /**
     * 提交
     *
     * @return {@link LoadRequest}
     */
    @Nullable
    public LoadRequest commit() {
        if (sync && SketchUtils.isMainThread()) {
            throw new IllegalStateException("Cannot sync perform the load in the UI thread ");
        }

        if (!checkParams()) {
            return null;
        }

        if (!checkRequestLevel()) {
            return null;
        }

        return submitRequest();
    }

    private boolean checkParams() {
        Configuration configuration = sketch.getConfiguration();

        // load 请求不能使用 Resize.ByViewFixedSizeResize
        Resize resize = loadOptions.getResize();
        if (resize instanceof Resize.ByViewFixedSizeResize) {
            resize = null;
            loadOptions.setResize(null);
        }

        // Resize 的宽高都必须大于 0
        if (resize != null && (resize.getWidth() <= 0 || resize.getHeight() <= 0)) {
            throw new IllegalArgumentException("Resize width and height must be > 0");
        }


        // 没有设置 MaxSize 的话，就用默认的 MaxSize
        MaxSize maxSize = loadOptions.getMaxSize();
        if (maxSize == null) {
            maxSize = configuration.getSizeCalculator().getDefaultImageMaxSize(configuration.getContext());
            loadOptions.setMaxSize(maxSize);
        }

        // MaxSize 的宽或高大于 0 即可
        if (maxSize != null && maxSize.getWidth() <= 0 && maxSize.getHeight() <= 0) {
            throw new IllegalArgumentException("MaxSize width or height must be > 0");
        }


        // 没有 ImageProcessor 但有 Resize 的话就需要设置一个默认的图片裁剪处理器
        if (loadOptions.getProcessor() == null && resize != null) {
            loadOptions.setProcessor(configuration.getResizeProcessor());
        }

        configuration.getOptionsFilterManager().filter(loadOptions);

        // LoadRequest 没有内存缓存，加载结果必须通过 Listener 回调才有意义
        if (loadListener == null) {
            SLog.e(NAME, "Load request must have LoadListener. %s", uri);
        }

        if (TextUtils.isEmpty(uri)) {
            SLog.e(NAME, "Uri is empty");
            CallbackHandler.postCallbackError(loadListener, ErrorCause.URI_INVALID, sync);
            return false;
        }

        if (uriModel == null) {
            SLog.e(NAME, "Not support uri. %s", uri);
            CallbackHandler.postCallbackError(loadListener, ErrorCause.URI_NO_SUPPORT, sync);
            return false;
        }

        // 根据 URI 和加载选项生成请求 ID
        key = SketchUtils.makeRequestKey(uri, uriModel, loadOptions.makeKey());

        return true;
    }

    private boolean checkRequestLevel() {
        // 如果只从本地加载并且是网络请求并且磁盘中没有缓存就结束吧
        if (loadOptions.getRequestLevel() == RequestLevel.LOCAL && uriModel.isFromNet()
                && !sketch.getConfiguration().getDiskCache().exist(uriModel.getDiskCacheKey(uri))) {

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "Request cancel. %s. %s", CancelCause.PAUSE_DOWNLOAD, key);
            }

            CallbackHandler.postCallbackCanceled(loadListener, CancelCause.PAUSE_DOWNLOAD, sync);
            return false;
        }

        return true;
    }

    private LoadRequest submitRequest() {
        CallbackHandler.postCallbackStarted(loadListener, sync);

        RequestFactory requestFactory = sketch.getConfiguration().getRequestFactory();
        LoadRequest request = requestFactory.newLoadRequest(sketch, uri, uriModel, key, loadOptions, loadListener, downloadProgressListener);
        request.setSync(sync);

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
            SLog.d(NAME, "Run dispatch submitted. %s", key);
        }
        request.submit();

        return request;
    }
}
