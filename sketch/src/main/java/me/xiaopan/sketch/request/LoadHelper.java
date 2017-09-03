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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.uri.UriModel;
import me.xiaopan.sketch.util.SketchUtils;

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
     * 禁用磁盘缓存
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper disableCacheInDisk() {
        loadOptions.setCacheInDiskDisabled(true);
        return this;
    }

    /**
     * 禁用 BitmapPool
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper disableBitmapPool() {
        loadOptions.setBitmapPoolDisabled(true);
        return this;
    }

    /**
     * 设置请求 Level
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper requestLevel(@Nullable RequestLevel requestLevel) {
        if (requestLevel != null) {
            loadOptions.setRequestLevel(requestLevel);
            loadOptions.setRequestLevelFrom(null);
        }
        return this;
    }

    /**
     * 解码Gif图片
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper decodeGifImage() {
        loadOptions.setDecodeGifImage(true);
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此 Size 来计算 inSimpleSize
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper maxSize(int width, int height) {
        loadOptions.setMaxSize(width, height);
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此 Size 来计算 inSimpleSize
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper maxSize(@Nullable MaxSize maxSize) {
        loadOptions.setMaxSize(maxSize);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据 resize 进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟 resize 一样的，
     * 但尺寸不一定会等于 resize，也有可能小于 resize，如果需要必须同 resize 一致可以设置 forceUseResize
     */
    @NonNull
    public LoadHelper resize(@Nullable Resize resize) {
        loadOptions.setResize(resize);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据 resize 进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟 resize 一样的，
     * 但尺寸不一定会等于 resize，也有可能小于 resize，如果需要必须同 resize 一致可以设置 forceUseResize
     */
    @NonNull
    public LoadHelper resize(int width, int height) {
        loadOptions.setResize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据 resize 进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟 resize 一样的，
     * 但尺寸不一定会等于 resize，也有可能小于 resize，如果需要必须同 resize 一致可以设置 forceUseResize
     */
    @NonNull
    public LoadHelper resize(int width, int height, @NonNull ScaleType scaleType) {
        loadOptions.setResize(width, height, scaleType);
        return this;
    }

    /**
     * 强制使经过 resize 处理后的图片同 resize 的尺寸一致
     */
    @NonNull
    public LoadHelper forceUseResize() {
        loadOptions.setForceUseResize(true);
        return this;
    }

    /**
     * 返回低质量的图片
     */
    @NonNull
    public LoadHelper lowQualityImage() {
        loadOptions.setLowQualityImage(true);
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据 resize 创建一张新的图片
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper processor(@Nullable ImageProcessor processor) {
        loadOptions.setImageProcessor(processor);
        return this;
    }

    /**
     * 设置图片质量
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper bitmapConfig(@Nullable Bitmap.Config config) {
        loadOptions.setBitmapConfig(config);
        return this;
    }

    /**
     * 设置优先考虑质量还是速度
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper inPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        loadOptions.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
        return this;
    }

    /**
     * 开启缩略图模式
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper thumbnailMode() {
        loadOptions.setThumbnailMode(true);
        return this;
    }

    /**
     * 为了加快速度，将经过 ImageProcessor、resize 或 thumbnailMode 处理过的图片保存到磁盘缓存中，下次就直接读取
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper cacheProcessedImageInDisk() {
        loadOptions.setCacheProcessedImageInDisk(true);
        return this;
    }

    /**
     * 禁用纠正图片方向功能
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper disableCorrectImageOrientation() {
        loadOptions.setCorrectImageOrientationDisabled(true);
        return this;
    }

    /**
     * 批量设置加载参数（完全覆盖）
     */
    @NonNull
    public LoadHelper options(@Nullable LoadOptions newOptions) {
        loadOptions.copy(newOptions);
        return this;
    }

    /**
     * 设置下载进度监听器
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper downloadProgressListener(@Nullable DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
        return this;
    }

    /**
     * 同步处理
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper sync() {
        this.sync = true;
        return this;
    }

    /**
     * 提交
     */
    @Nullable
    public LoadRequest commit() {
        if (sync && SketchUtils.isMainThread()) {
            throw new IllegalStateException("Cannot sync perform the load in the UI thread ");
        }

        if (!checkParam()) {
            return null;
        }

        preProcess();

        if (!checkRequestLevel()) {
            return null;
        }

        return submitRequest();
    }

    private boolean checkParam() {
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

        return true;
    }

    /**
     * 对属性进行预处理
     */
    protected void preProcess() {
        Configuration configuration = sketch.getConfiguration();

        // 检查Resize的宽高都必须大于0
        Resize resize = loadOptions.getResize();
        if (resize != null && (resize.getWidth() == 0 || resize.getHeight() == 0)) {
            throw new IllegalArgumentException("Resize width and height must be > 0");
        }


        // 没有设置 maxSize 的话，就用默认的 maxSize
        if (loadOptions.getMaxSize() == null) {
            loadOptions.setMaxSize(configuration.getSizeCalculator().getDefaultImageMaxSize(configuration.getContext()));
        }

        // 检查 maxSize 的宽或高大于0即可
        MaxSize maxSize = loadOptions.getMaxSize();
        if (maxSize != null && maxSize.getWidth() <= 0 && maxSize.getHeight() <= 0) {
            throw new IllegalArgumentException("MaxSize width or height must be > 0");
        }


        // 没有 ImageProcessor 但有 resize 的话就需要设置一个默认的图片裁剪处理器
        if (loadOptions.getImageProcessor() == null && resize != null) {
            loadOptions.setImageProcessor(configuration.getResizeProcessor());
        }


        // 如果设置了全局使用低质量图片的话就强制使用低质量的图片
        if (configuration.isGlobalLowQualityImage()) {
            loadOptions.setLowQualityImage(true);
        }

        // 如果设置了全局解码质量优先
        if (sketch.getConfiguration().isGlobalInPreferQualityOverSpeed()) {
            loadOptions.setInPreferQualityOverSpeed(true);
        }

        // 如果没有设置请求 Level 的话就跟据暂停下载和暂停加载功能来设置请求 Level
        if (loadOptions.getRequestLevel() == null) {
            if (configuration.isGlobalPauseDownload()) {
                loadOptions.setRequestLevel(RequestLevel.LOCAL);
                loadOptions.setRequestLevelFrom(RequestLevelFrom.PAUSE_DOWNLOAD);
            }

            // 暂停加载对于加载请求并不起作用，因此这里不予处理
        }

        // 根据 URI 和加载选项生成请求 ID
        key = SketchUtils.makeRequestKey(uri, uriModel, loadOptions);
    }

    private boolean checkRequestLevel() {
        // 如果只从本地加载并且是网络请求并且磁盘中没有缓存就结束吧
        if (loadOptions.getRequestLevel() == RequestLevel.LOCAL && uriModel.isFromNet()
                && !sketch.getConfiguration().getDiskCache().exist(uriModel.getDiskCacheKey(uri))) {
            boolean isPauseDownload = loadOptions.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "canceled. %s. %s",
                        isPauseDownload ? "pause download" : "requestLevel is local", key);
            }

            CancelCause cancelCause = isPauseDownload ? CancelCause.PAUSE_DOWNLOAD : CancelCause.REQUEST_LEVEL_IS_LOCAL;
            CallbackHandler.postCallbackCanceled(loadListener, cancelCause, sync);
            return false;
        }

        return true;
    }

    private LoadRequest submitRequest() {
        CallbackHandler.postCallbackStarted(loadListener, sync);

        RequestFactory requestFactory = sketch.getConfiguration().getRequestFactory();
        LoadRequest request = requestFactory.newLoadRequest(sketch, uri, uriModel, key, loadOptions, loadListener, downloadProgressListener);
        request.setSync(sync);
        request.submit();

        return request;
    }
}
