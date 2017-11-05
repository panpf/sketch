/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.zoom.block;

import android.text.TextUtils;

import me.panpf.sketch.SLog;
import me.panpf.sketch.util.KeyCounter;
import me.panpf.sketch.zoom.BlockDisplayer;

/**
 * 碎片解码器
 */
public class BlockDecoder {

    private static final String NAME = "BlockDecoder";

    private KeyCounter initKeyCounter;
    private ImageRegionDecoder decoder;
    private BlockDisplayer blockDisplayer;
    private boolean running;
    private boolean initializing;

    public BlockDecoder(BlockDisplayer blockDisplayer) {
        this.blockDisplayer = blockDisplayer;
        this.initKeyCounter = new KeyCounter();
    }

    /**
     * 设置新的图片
     */
    public void setImage(String imageUri, boolean correctImageOrientationDisabled) {
        clean("setImage");

        if (decoder != null) {
            decoder.recycle();
            decoder = null;
        }

        if (!TextUtils.isEmpty(imageUri)) {
            running = initializing = true;
            blockDisplayer.getBlockExecutor().submitInit(imageUri, initKeyCounter, correctImageOrientationDisabled);
        } else {
            running = initializing = false;
        }
    }

    /**
     * 解码
     */
    void decodeBlock(Block block) {
        if (!isReady()) {
            SLog.w(NAME, "not ready. decodeBlock. %s", block.getInfo());
            return;
        }

        block.decoder = decoder;
        blockDisplayer.getBlockExecutor().submitDecodeBlock(block.getKey(), block);
    }

    void clean(String why) {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
            SLog.d(NAME, "clean. %s", why);
        }

        initKeyCounter.refresh();
    }

    public void recycle(String why) {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
            SLog.d(NAME, "recycle. %s", why);
        }

        if (decoder != null) {
            decoder.recycle();
        }
    }

    public void initCompleted(String imageUri, ImageRegionDecoder decoder) {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
            SLog.d(NAME, "init completed. %s", imageUri);
        }

        initializing = false;
        this.decoder = decoder;
    }

    public void initError(String imageUri, Exception e) {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
            SLog.d(NAME, "init failed. %s. %s", e.getMessage(), imageUri);
        }

        initializing = false;
    }

    public boolean isReady() {
        return running && decoder != null && decoder.isReady();
    }

    public boolean isInitializing() {
        return running && initializing;
    }

    public ImageRegionDecoder getDecoder() {
        return decoder;
    }
}
