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

import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.process.ImageProcessor;

public class DisplayParams {
    // 基本属性
    public String uri;
    public String name;
    public RequestLevel requestLevel = RequestLevel.NET;
    public RequestLevelFrom requestLevelFrom;

    // 下载属性
    public boolean cacheInDisk = true;
    public ProgressListener progressListener;

    // 加载属性
    public Resize resize;
    public boolean forceUseResize;
    public boolean decodeGifImage = true;
    public boolean lowQualityImage;
    public MaxSize maxSize;
    public ImageProcessor imageProcessor;

    // 显示属性
    public String memoryCacheId;
    public boolean cacheInMemory = true;
    public FixedSize fixedSize;
    public ImageHolder loadingImageHolder;
    public ImageHolder loadFailImageHolder;
    public ImageHolder pauseDownloadImageHolder;
    public ImageDisplayer imageDisplayer;
}
