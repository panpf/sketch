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

import me.xiaopan.sketch.util.SketchUtils;

/**
 * DownloadHelper
 */
public class DefaultDownloadHelper implements DownloadHelper{
    private static final String NAME = "DefaultDownloadHelper";

    // 基本属性
    protected Sketch sketch;
    protected String uri;
    protected String name;
    protected RequestLevel requestLevel = RequestLevel.NET;
    protected RequestLevelFrom requestLevelFrom;

    // 下载属性
    protected boolean cacheInDisk = true;
    protected ProgressListener progressListener;
    protected DownloadListener downloadListener;

    /**
     * 创建下载请求生成器
     * @param sketch Sketch
     * @param uri 图片Uri，支持以下几种
     * <blockquote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * </blockquote>
     */
    public DefaultDownloadHelper(Sketch sketch, String uri) {
        this.sketch = sketch;
        this.uri = uri;
        if(sketch.getConfiguration().isPauseDownload()){
            this.requestLevel = RequestLevel.LOCAL;
            this.requestLevelFrom = null;
        }
    }

    @Override
    public DefaultDownloadHelper name(String name){
        this.name = name;
        return this;
    }

    @Override
    public DefaultDownloadHelper listener(DownloadListener downloadListener){
        this.downloadListener = downloadListener;
        return this;
    }

    @Override
    public DefaultDownloadHelper disableDiskCache() {
        this.cacheInDisk = false;
        return this;
    }

    @Override
    public DefaultDownloadHelper progressListener(ProgressListener progressListener){
        this.progressListener = progressListener;
        return this;
    }

    @Override
    public DefaultDownloadHelper options(DownloadOptions options){
        if(options == null){
            return this;
        }

        this.cacheInDisk = options.isCacheInDisk();
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
    public DefaultDownloadHelper options(Enum<?> optionsName){
        return options((DownloadOptions) Sketch.getOptions(optionsName));
    }

    @Override
    public DefaultDownloadHelper requestLevel(RequestLevel requestLevel){
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
        if(!sketch.getConfiguration().isCacheInDisk()){
            cacheInDisk = false;
        }
    }

    @Override
    public Request commit(){
        handleParams();

        if(downloadListener != null){
            downloadListener.onStarted();
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "uri is null or empty"));
            }
            if(downloadListener != null){
                downloadListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            return null;
        }

        if(name == null){
            name = uri;
        }

        // 过滤掉不支持的URI协议类型
        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if(uriScheme == null){
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "unknown uri scheme", " - ", name));
            }
            if(downloadListener != null){
                downloadListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            return null;
        }

        if(!(uriScheme == UriScheme.HTTP || uriScheme == UriScheme.HTTPS)){
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "only support http ot https", " - ", name));
            }
            if(downloadListener != null){
                downloadListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            return null;
        }

        // 创建请求
        DownloadRequest request = sketch.getConfiguration().getRequestFactory().newDownloadRequest(sketch, uri, uriScheme);

        request.setName(name);
        request.setRequestLevel(requestLevel);
        request.setRequestLevelFrom(requestLevelFrom);

        request.setCacheInDisk(cacheInDisk);
        request.setRequestLevel(requestLevel);

        request.setDownloadListener(downloadListener);
        request.setProgressListener(progressListener);

        request.postRunDispatch();

        return request;
    }
}
