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
import android.util.Log;
import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feature.RequestFactory;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

public class LoadHelper {
    protected String logName = "LoadHelper";

    protected Sketch sketch;

    protected boolean sync;
    protected RequestAttrs requestAttrs = new RequestAttrs();
    protected LoadOptions loadOptions = new LoadOptions();
    protected LoadListener loadListener;
    protected DownloadProgressListener downloadProgressListener;

    /**
     * 支持以下几种图片Uri
     * <blockQuote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"file:///mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockQuote>
     */
    public LoadHelper(Sketch sketch, String uri) {
        this.sketch = sketch;
        this.requestAttrs.reset(uri);
    }

    /**
     * 禁用磁盘缓存
     */
    @SuppressWarnings("unused")
    public LoadHelper disableCacheInDisk() {
        loadOptions.setDisableCacheInDisk(true);
        return this;
    }

    /**
     * 设置请求Level
     */
    @SuppressWarnings("unused")
    public LoadHelper requestLevel(RequestLevel requestLevel) {
        if (requestLevel != null) {
            loadOptions.setRequestLevel(requestLevel);
            loadOptions.setRequestLevelFrom(null);
        }
        return this;
    }

    /**
     * 解码Gif图片
     */
    @SuppressWarnings("unused")
    public LoadHelper decodeGifImage() {
        loadOptions.setDecodeGifImage(true);
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     */
    @SuppressWarnings("unused")
    public LoadHelper maxSize(int width, int height) {
        loadOptions.setMaxSize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，
     * 但尺寸不一定会等于resi，也有可能小于resize，如果需要必须同resize一致可以设置forceUseResize
     */
    public LoadHelper resize(int width, int height) {
        loadOptions.setResize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，
     * 但尺寸不一定会等于resize，也有可能小于resize，如果需要必须同resize一致可以设置forceUseResize
     */
    public LoadHelper resize(int width, int height, ScaleType scaleType) {
        loadOptions.setResize(new Resize(width, height, scaleType));
        return this;
    }

    /**
     * 强制使经过resize处理后的图片同resize的尺寸一致
     */
    public LoadHelper forceUseResize() {
        loadOptions.setForceUseResize(true);
        return this;
    }

    /**
     * 返回低质量的图片
     */
    public LoadHelper lowQualityImage() {
        loadOptions.setLowQualityImage(true);
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize创建一张新的图片
     */
    @SuppressWarnings("unused")
    public LoadHelper processor(ImageProcessor processor) {
        loadOptions.setImageProcessor(processor);
        return this;
    }

    /**
     * 设置图片质量
     */
    @SuppressWarnings("unused")
    public LoadHelper bitmapConfig(Bitmap.Config config) {
        loadOptions.setBitmapConfig(config);
        return this;
    }

    /**
     * 设置优先考虑质量还是速度
     */
    @SuppressWarnings("unused")
    public LoadHelper inPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        loadOptions.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
        return this;
    }

    /**
     * 开启缩略图模式
     */
    @SuppressWarnings("unused")
    public LoadHelper thumbnailMode(){
        loadOptions.setThumbnailMode(true);
        return this;
    }

    /**
     * 为了加快速度，将经过ImageProcessor、resize或thumbnailMode处理过的图片保存到磁盘缓存中，下次就直接读取
     */
    @SuppressWarnings("unused")
    public LoadHelper cacheProcessedImageInDisk(){
        loadOptions.setCacheProcessedImageInDisk(true);
        return this;
    }

    /**
     * 批量设置加载参数，这会是一个合并的过程，并不会完全覆盖
     */
    public LoadHelper options(LoadOptions newOptions) {
        loadOptions.apply(newOptions);
        return this;
    }

    /**
     * 批量设置加载参数，你只需要提前将LoadOptions通过Sketch.putLoadOptions()方法存起来，
     * 然后在这里指定其名称即可，另外这会是一个合并的过程，并不会完全覆盖
     */
    public LoadHelper optionsByName(Enum<?> optionsName) {
        return options(Sketch.getLoadOptions(optionsName));
    }

    /**
     * 设置加载监听器
     */
    public LoadHelper listener(LoadListener loadListener) {
        this.loadListener = loadListener;
        return this;
    }

    /**
     * 设置下载进度监听器
     */
    @SuppressWarnings("unused")
    public LoadHelper downloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
        return this;
    }

    /**
     * 同步处理
     */
    @SuppressWarnings("unused")
    public LoadHelper sync() {
        this.sync = true;
        return this;
    }

    /**
     * 提交
     */
    public LoadRequest commit() {
        if(sync && SketchUtils.isMainThread()){
            throw new IllegalStateException("Cannot sync perform the load in the UI thread ");
        }

        CallbackHandler.postCallbackStarted(loadListener, sync);

        preProcess();

        if (!checkUri()) {
            return null;
        }

        if (!checkUriScheme()) {
            return null;
        }

        if (!checkRequestLevel()) {
            return null;
        }

        return submitRequest();
    }

    /**
     * 对属性进行预处理
     */
    protected void preProcess() {
        Configuration configuration = sketch.getConfiguration();

        // 没有ImageProcessor但有resize的话就需要设置一个默认的图片裁剪处理器
        if (loadOptions.getResize() != null && loadOptions.getImageProcessor() == null) {
            loadOptions.setImageProcessor(configuration.getResizeImageProcessor());
        }

        // 没有设置maxSize的话，就用默认的maxSize
        if (loadOptions.getMaxSize() == null) {
            loadOptions.setMaxSize(configuration.getImageSizeCalculator().getDefaultImageMaxSize(configuration.getContext()));
        }

        // 如果设置了全局禁用磁盘缓存就强制关闭磁盘缓存功能
        if (configuration.isGlobalDisableCacheInDisk()) {
            loadOptions.setDisableCacheInDisk(true);
        }

        // 如果设置了全局使用低质量图片的话就强制使用低质量的图片
        if (configuration.isGlobalLowQualityImage()) {
            loadOptions.setLowQualityImage(true);
        }

        // 如果设置了全局解码质量优先
        if (sketch.getConfiguration().isGlobalInPreferQualityOverSpeed()) {
            loadOptions.setInPreferQualityOverSpeed(true);
        }

        // 如果没有设置请求Level的话就跟据暂停下载和暂停加载功能来设置请求Level
        if (loadOptions.getRequestLevel() == null) {
            if (configuration.isGlobalPauseDownload()) {
                loadOptions.setRequestLevel(RequestLevel.LOCAL);
                loadOptions.setRequestLevelFrom(RequestLevelFrom.PAUSE_DOWNLOAD);
            }

            // 暂停加载对于加载请求并不起作用，因此这里不予处理
        }

        // 根据URI和加载选项生成请求ID
        if (requestAttrs.getId() == null) {
            requestAttrs.setId(SketchUtils.makeMemoryCacheId(requestAttrs.getUri(), loadOptions));
        }
    }

    private boolean checkUri() {
        if (requestAttrs.getUri() == null || "".equals(requestAttrs.getUri().trim())) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(logName, ". uri is null or empty"));
            }
            CallbackHandler.postCallbackError(loadListener, ErrorCause.URI_NULL_OR_EMPTY, sync);
            return false;
        }

        return true;
    }

    private boolean checkUriScheme() {
        if (requestAttrs.getUriScheme() == null) {
            Log.e(Sketch.TAG, SketchUtils.concat(logName, ". unknown uri scheme", ". ", requestAttrs.getId()));
            CallbackHandler.postCallbackError(loadListener, ErrorCause.URI_NO_SUPPORT, sync);
            return false;
        }

        return true;
    }

    private boolean checkRequestLevel() {
        // 如果只从本地加载并且是网络请求并且磁盘中没有缓存就结束吧
        if (loadOptions.getRequestLevel() == RequestLevel.LOCAL
                && requestAttrs.getUriScheme() == UriScheme.NET
                && !sketch.getConfiguration().getDiskCache().exist(requestAttrs.getUri())) {
            boolean isPauseDownload = loadOptions.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;

            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName,
                        ". canceled",
                        ". ", isPauseDownload ? "pause download" : "requestLevel is local",
                        ". ", requestAttrs.getId()));
            }

            CancelCause cancelCause = isPauseDownload ? CancelCause.PAUSE_DOWNLOAD : CancelCause.REQUEST_LEVEL_IS_LOCAL;
            CallbackHandler.postCallbackCanceled(loadListener, cancelCause, sync);

            return false;
        }

        return true;
    }

    private LoadRequest submitRequest() {
        RequestFactory requestFactory = sketch.getConfiguration().getRequestFactory();
        LoadRequest request = requestFactory.newLoadRequest(sketch, requestAttrs, loadOptions, loadListener, downloadProgressListener);
        request.setSync(sync);
        request.submit();
        return request;
    }
}
