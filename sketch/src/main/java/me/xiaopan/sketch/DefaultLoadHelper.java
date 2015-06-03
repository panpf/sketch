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

/**
 * LoadHelper
 */
public class DefaultLoadHelper implements LoadHelper{
    private static final String NAME = "DefaultLoadHelper";

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
     * @param sketch Sketch
     * @param uri 图片Uri，支持以下几种
     * <blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     */
    public DefaultLoadHelper(Sketch sketch, String uri) {
        this.sketch = sketch;
        this.uri = uri;
        if(sketch.getConfiguration().isPauseDownload()){
            this.requestLevel = RequestLevel.LOCAL;
            this.requestLevelFrom = RequestLevelFrom.PAUSE_DOWNLOAD;
        }
    }

    @Override
    public DefaultLoadHelper name(String name){
        this.name = name;
        return this;
    }

    @Override
    public DefaultLoadHelper disableDiskCache() {
        this.cacheInDisk = false;
        return this;
    }

    @Override
    public DefaultLoadHelper disableDecodeGifImage() {
        this.decodeGifImage = false;
        return this;
    }

    @Override
    public DefaultLoadHelper maxSize(MaxSize maxSize){
        this.maxSize = maxSize;
        return this;
    }

    @Override
    public DefaultLoadHelper maxSize(int width, int height){
        this.maxSize = new MaxSize(width, height);
        return this;
    }

    @Override
    public DefaultLoadHelper resize(int width, int height){
        this.resize = new Resize(width, height);
        return this;
    }

    @Override
    public DefaultLoadHelper resize(int width, int height, ScaleType scaleType) {
        this.resize = new Resize(width, height, scaleType);
        return this;
    }

    @Override
    public DefaultLoadHelper forceUseResize() {
        this.forceUseResize = true;
        return this;
    }

    @Override
    public DefaultLoadHelper lowQualityImage() {
        this.lowQualityImage = true;
        return this;
    }

    @Override
    public DefaultLoadHelper processor(ImageProcessor processor){
        this.imageProcessor = processor;
        return this;
    }

    @Override
    public DefaultLoadHelper listener(LoadListener loadListener){
        this.loadListener = loadListener;
        return this;
    }

    @Override
    public DefaultLoadHelper progressListener(ProgressListener progressListener){
        this.progressListener = progressListener;
        return this;
    }

    @Override
    public DefaultLoadHelper options(LoadOptions options){
        if(options == null){
            return this;
        }

        this.cacheInDisk = options.isCacheInDisk();
        if(this.maxSize == null){
            this.maxSize = options.getMaxSize();
        }
        if(this.resize == null && options.getResize() != null){
            this.resize = new Resize(options.getResize());
        }
        this.forceUseResize = options.isForceUseResize();
        this.lowQualityImage = options.isLowQualityImage();
        if(this.imageProcessor == null){
            this.imageProcessor = options.getImageProcessor();
        }
        this.decodeGifImage = options.isDecodeGifImage();
        RequestLevel optionRequestLevel = options.getRequestLevel();
        if(requestLevel != null && optionRequestLevel != null){
            if(optionRequestLevel.getLevel() < requestLevel.getLevel()){
                this.requestLevel = optionRequestLevel;
                this.requestLevelFrom = null;
            }
        }else if(optionRequestLevel != null){
            this.requestLevel = optionRequestLevel;
            this.requestLevelFrom = null;
        }

        return this;
    }

    @Override
    public DefaultLoadHelper options(Enum<?> optionsName){
        return options((LoadOptions) Sketch.getOptions(optionsName));
    }

    @Override
    public DefaultLoadHelper requestLevel(RequestLevel requestLevel){
        if(requestLevel != null){
            this.requestLevel = requestLevel;
            this.requestLevelFrom = null;
        }
        return this;
    }

    /**
     * 处理一下参数
     */
    protected void handleParams(){
        if(imageProcessor == null && resize != null){
            imageProcessor = sketch.getConfiguration().getDefaultCutImageProcessor();
        }
        if(maxSize == null){
            maxSize = sketch.getConfiguration().getImageSizeCalculator().getDefaultImageMaxSize(sketch.getConfiguration().getContext());
        }
        if(name == null){
            name = uri;
        }
        if(!sketch.getConfiguration().isDecodeGifImage()){
            decodeGifImage = false;
        }
        if(!sketch.getConfiguration().isCacheInDisk()){
            cacheInDisk = false;
        }
        if(sketch.getConfiguration().isLowQualityImage()){
            lowQualityImage = true;
        }
    }

    @Override
    public Request commit() {
        handleParams();

        if(loadListener != null){
            loadListener.onStarted();
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "uri is null or empty"));
            }
            if(loadListener != null){
                loadListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            return null;
        }

        // 过滤掉不支持的URI协议类型
        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if(uriScheme == null){
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "unknown uri scheme", " - ", name));
            }
            if(loadListener != null){
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