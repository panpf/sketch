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

import android.util.Log;
import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

public class LoadHelper {
    private static final String NAME = "LoadHelper";

    // 基本属性
    protected Sketch sketch;
    protected String uri;
    protected String name;
    protected RequestLevel requestLevel = RequestLevel.NET;
    protected RequestLevelFrom requestLevelFrom;

    // 下载属性
    protected boolean cacheInDisk = true;
    protected ProgressListener progressListener;

    // 加载属性
    protected Resize resize;
    protected boolean decodeGifImage = true;
    protected boolean forceUseResize;
    protected boolean lowQualityImage;
    protected MaxSize maxSize;
    protected LoadListener loadListener;
    protected ImageProcessor imageProcessor;

    /**
     * 创建加载请求生成器
     *
     * @param sketch Sketch
     * @param uri    图片Uri，支持以下几种
     *               <blockquote>"http://site.com/image.png"; // from Web
     *               <br>"https://site.com/image.png"; // from Web
     *               <br>"/mnt/sdcard/image.png"; // from SD card
     *               <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     *               <br>"content://media/external/audio/albumart/13"; // from content provider
     *               <br>"asset://image.png"; // from assets
     *               <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     *               </blockquote>
     */
    public LoadHelper(Sketch sketch, String uri) {
        this.sketch = sketch;
        this.uri = uri;
        if (sketch.getConfiguration().isPauseDownload()) {
            this.requestLevel = RequestLevel.LOCAL;
            this.requestLevelFrom = RequestLevelFrom.PAUSE_DOWNLOAD;
        }
    }

    /**
     * 设置名称，用于在log总区分请求
     *
     * @param name 名称
     * @return LoadHelper
     */
    public LoadHelper name(String name) {
        this.name = name;
        return this;
    }

    /**
     * 关闭硬盘缓存
     *
     * @return LoadHelper
     */
    @SuppressWarnings("unused")
    public LoadHelper disableDiskCache() {
        this.cacheInDisk = false;
        return this;
    }

    /**
     * 不解码Gif图片
     *
     * @return LoadHelper
     */
    @SuppressWarnings("unused")
    public LoadHelper disableDecodeGifImage() {
        this.decodeGifImage = false;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     *
     * @param maxSize 最大尺寸
     * @return LoadHelper
     */
    public LoadHelper maxSize(MaxSize maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     *
     * @param width  宽
     * @param height 高
     * @return LoadHelper
     */
    public LoadHelper maxSize(int width, int height) {
        this.maxSize = new MaxSize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resi，也有可能小于resize
     *
     * @param width  宽
     * @param height 高
     * @return LoadHelper
     */
    public LoadHelper resize(int width, int height) {
        this.resize = new Resize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resi，也有可能小于resize
     *
     * @param width     宽
     * @param height    高
     * @param scaleType 缩放方式
     * @return LoadHelper
     */
    public LoadHelper resize(int width, int height, ScaleType scaleType) {
        this.resize = new Resize(width, height, scaleType);
        return this;
    }

    /**
     * 强制使经过resize返回的图片同resize的尺寸一致
     *
     * @return DisplayHelper
     */
    public LoadHelper forceUseResize() {
        this.forceUseResize = true;
        return this;
    }

    /**
     * 返回低质量的图片
     *
     * @return LoadHelper
     */
    public LoadHelper lowQualityImage() {
        this.lowQualityImage = true;
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize创建一张新的图片
     *
     * @param processor 图片处理器
     * @return LoadHelper
     */
    @SuppressWarnings("unused")
    public LoadHelper processor(ImageProcessor processor) {
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 设置加载监听器
     *
     * @param loadListener 加载监听器
     * @return LoadHelper
     */
    public LoadHelper listener(LoadListener loadListener) {
        this.loadListener = loadListener;
        return this;
    }

    /**
     * 设置进度监听器
     *
     * @param progressListener 进度监听器
     * @return LoadHelper
     */
    @SuppressWarnings("unused")
    public LoadHelper progressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    /**
     * 设置加载参数
     *
     * @param options 加载参数
     * @return LoadHelper
     */
    public LoadHelper options(LoadOptions options) {
        if (options == null) {
            return this;
        }

        this.cacheInDisk = options.isCacheInDisk();
        if (this.maxSize == null) {
            this.maxSize = options.getMaxSize();
        }
        if (this.resize == null && options.getResize() != null) {
            this.resize = new Resize(options.getResize());
        }
        this.forceUseResize = options.isForceUseResize();
        this.lowQualityImage = options.isLowQualityImage();
        if (this.imageProcessor == null) {
            this.imageProcessor = options.getImageProcessor();
        }
        this.decodeGifImage = options.isDecodeGifImage();
        RequestLevel optionRequestLevel = options.getRequestLevel();
        if (requestLevel != null && optionRequestLevel != null) {
            if (optionRequestLevel.getLevel() < requestLevel.getLevel()) {
                this.requestLevel = optionRequestLevel;
                this.requestLevelFrom = null;
            }
        } else if (optionRequestLevel != null) {
            this.requestLevel = optionRequestLevel;
            this.requestLevelFrom = null;
        }

        return this;
    }

    /**
     * 设置加载参数，你只需要提前将LoadOptions通过Sketch.putOptions()方法存起来，然后在这里指定其名称即可
     *
     * @param optionsName 参数名称
     * @return LoadHelper
     */
    public LoadHelper options(Enum<?> optionsName) {
        return options((LoadOptions) Sketch.getOptions(optionsName));
    }

    /**
     * 设置请求Level
     *
     * @param requestLevel 请求Level
     * @return DisplayHelper
     */
    public LoadHelper requestLevel(RequestLevel requestLevel) {
        if (requestLevel != null) {
            this.requestLevel = requestLevel;
            this.requestLevelFrom = null;
        }
        return this;
    }

    /**
     * 处理一下参数
     */
    protected void handleParams() {
        if (imageProcessor == null && resize != null) {
            imageProcessor = sketch.getConfiguration().getDefaultCutImageProcessor();
        }
        if (maxSize == null) {
            maxSize = sketch.getConfiguration().getImageSizeCalculator().getDefaultImageMaxSize(sketch.getConfiguration().getContext());
        }
        if (name == null) {
            name = uri;
        }
        if (!sketch.getConfiguration().isDecodeGifImage()) {
            decodeGifImage = false;
        }
        if (!sketch.getConfiguration().isCacheInDisk()) {
            cacheInDisk = false;
        }
        if (sketch.getConfiguration().isLowQualityImage()) {
            lowQualityImage = true;
        }
    }

    /**
     * 提交请求
     *
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    public LoadRequest commit() {
        handleParams();

        if (loadListener != null) {
            loadListener.onStarted();
        }

        // 验证uri参数
        if (uri == null || "".equals(uri.trim())) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "uri is null or empty"));
            }
            if (loadListener != null) {
                loadListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            return null;
        }

        // 过滤掉不支持的URI协议类型
        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if (uriScheme == null) {
            Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "unknown uri scheme", " - ", name));
            if (loadListener != null) {
                loadListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            return null;
        }

        // 创建请求
        LoadRequest request = sketch.getConfiguration().getRequestFactory().newLoadRequest(sketch, uri, uriScheme);

        request.setName(name);
        request.setRequestLevel(requestLevel);
        request.setRequestLevelFrom(requestLevelFrom);

        request.setCacheInDisk(cacheInDisk);
        request.setProgressListener(progressListener);

        request.setResize(resize);
        request.setMaxSize(maxSize);
        request.setForceUseResize(forceUseResize);
        request.setLowQualityImage(lowQualityImage);
        request.setLoadListener(loadListener);
        request.setImageProcessor(imageProcessor);
        request.setDecodeGifImage(decodeGifImage);

        request.postRunDispatch();

        return request;
    }
}
