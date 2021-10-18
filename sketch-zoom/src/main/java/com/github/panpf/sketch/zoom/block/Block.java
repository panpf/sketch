/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.zoom.block;

import android.graphics.Bitmap;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import com.github.panpf.sketch.cache.BitmapPool;
import com.github.panpf.sketch.cache.BitmapPoolUtils;
import com.github.panpf.sketch.util.KeyCounter;

/**
 * 碎片
 */
public class Block {
    @NonNull
    public Rect drawRect = new Rect();
    @NonNull
    public Rect srcRect = new Rect();
    public int inSampleSize;
    public float scale = -1;

    @Nullable
    public ImageRegionDecoder decoder;

    @Nullable
    public Bitmap bitmap;
    @NonNull
    public Rect bitmapDrawSrcRect = new Rect();

    // 用来取消解码任务，开始解码这个碎片的时候会获取当时的key
    // 然后在解码过程的各个环节都会检验key是否已经失效
    // 因此如果想取消解码这个碎片，只需刷新key即可
    @NonNull
    private KeyCounter keyCounter = new KeyCounter();

    public boolean isEmpty() {
        return bitmap == null || bitmap.isRecycled() || isDecodeParamEmpty();
    }

    public boolean isDecodeParamEmpty() {
        return drawRect.isEmpty() || drawRect.isEmpty()
                || srcRect.isEmpty() || srcRect.isEmpty()
                || inSampleSize == 0
                || scale == -1;
    }

    public boolean isExpired(int key) {
        return keyCounter.getKey() != key;
    }

    @SuppressWarnings("unused")
    public void clean(@NonNull BitmapPool bitmapPool) {
        if (bitmap != null) {
            BitmapPoolUtils.freeBitmapToPoolForRegionDecoder(bitmap, bitmapPool);
            bitmap = null;
        }
        bitmapDrawSrcRect.setEmpty();

        srcRect.setEmpty();
        drawRect.setEmpty();

        inSampleSize = 0;
        scale = -1;
        decoder = null;
    }

    public int getKey() {
        return keyCounter.getKey();
    }

    public void refreshKey() {
        keyCounter.refresh();
    }

    @NonNull
    public String getInfo() {
        //noinspection StringBufferReplaceableByString
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append("drawRect:").append(drawRect.toShortString());
        builder.append(",");
        builder.append("srcRect:").append(srcRect.toShortString());
        builder.append(",");
        builder.append("inSampleSize:").append(inSampleSize);
        builder.append(",");
        builder.append("scale:").append(scale);
        builder.append(",");
        builder.append("key:").append(keyCounter.getKey());
        builder.append(",");
        builder.append("hashCode:").append(Integer.toHexString(hashCode()));
        builder.append(")");
        return builder.toString();
    }

    /**
     * 将一个碎片列表转换成字符串
     */
    @Nullable
    public static String blockListToString(@Nullable List<Block> blockList) {
        if (blockList == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Block block : blockList) {
            if (builder.length() > 1) {
                builder.append(",");
            }
            builder.append("\"");
            builder.append(block.drawRect.left).append(",");
            builder.append(block.drawRect.top).append(",");
            builder.append(block.drawRect.right).append(",");
            builder.append(block.drawRect.bottom);
            builder.append("\"");
        }
        builder.append("]");
        return builder.toString();
    }
}
