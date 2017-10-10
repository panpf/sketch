/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.zoom.huge;

import android.text.TextUtils;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.util.KeyCounter;

/**
 * 碎片解码器
 */
class TileDecoder {

    private static final String NAME = "TileDecoder";

    private KeyCounter initKeyCounter;
    private ImageRegionDecoder decoder;
    private HugeImageViewer hugeImageViewer;
    private boolean running;
    private boolean initializing;

    public TileDecoder(HugeImageViewer hugeImageViewer) {
        this.hugeImageViewer = hugeImageViewer;
        this.initKeyCounter = new KeyCounter();
    }

    /**
     * 设置新的图片
     */
    void setImage(String imageUri, boolean correctImageOrientationDisabled) {
        clean("setImage");

        if (decoder != null) {
            decoder.recycle();
            decoder = null;
        }

        if (!TextUtils.isEmpty(imageUri)) {
            running = initializing = true;
            hugeImageViewer.getTileExecutor().submitInit(imageUri, initKeyCounter, correctImageOrientationDisabled);
        } else {
            running = initializing = false;
        }
    }

    /**
     * 解码
     */
    void decodeTile(Tile tile) {
        if (!isReady()) {
            SLog.w(NAME, "not ready. decodeTile. %s", tile.getInfo());
            return;
        }

        tile.decoder = decoder;
        hugeImageViewer.getTileExecutor().submitDecodeTile(tile.getKey(), tile);
    }

    void clean(String why) {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
            SLog.d(NAME, "clean. %s", why);
        }

        initKeyCounter.refresh();
    }

    void recycle(String why) {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
            SLog.d(NAME, "recycle. %s", why);
        }

        if (decoder != null) {
            decoder.recycle();
        }
    }

    void initCompleted(String imageUri, ImageRegionDecoder decoder) {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
            SLog.d(NAME, "init completed. %s", imageUri);
        }

        initializing = false;
        this.decoder = decoder;
    }

    void initError(String imageUri, Exception e) {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
            SLog.d(NAME, "init failed. %s. %s", e.getMessage(), imageUri);
        }

        initializing = false;
    }

    boolean isReady() {
        return running && decoder != null && decoder.isReady();
    }

    boolean isInitializing() {
        return running && initializing;
    }

    public ImageRegionDecoder getDecoder() {
        return decoder;
    }
}
