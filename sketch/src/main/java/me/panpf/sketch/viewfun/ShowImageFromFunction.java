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

package me.panpf.sketch.viewfun;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import me.panpf.sketch.drawable.SketchDrawable;
import me.panpf.sketch.drawable.SketchLoadingDrawable;
import me.panpf.sketch.request.ImageFrom;
import me.panpf.sketch.uri.UriModel;
import me.panpf.sketch.util.SketchUtils;

/**
 * 显示图片来源功能，会在 {@link android.widget.ImageView} 的左上角显示一个三角形的色块用于标识本次图片是从哪里来的
 * <ul>
 * <li>红色：网络</li>
 * <li>黄色：磁盘缓存</li>
 * <li>蓝色：本地</li>
 * <li>绿色：内存缓存
 * <li>紫色：内存
 * </ul>
 */
public class ShowImageFromFunction extends ViewFunction {
    private static final int FROM_FLAG_COLOR_MEMORY = 0x88A020F0;
    private static final int FROM_FLAG_COLOR_MEMORY_CACHE = 0x8800FF00;
    private static final int FROM_FLAG_COLOR_LOCAL = 0x880000FF;
    private static final int FROM_FLAG_COLOR_DISK_CACHE = 0x88FFFF00;
    private static final int FROM_FLAG_COLOR_NETWORK = 0x88FF0000;

    private View view;

    private Path imageFromPath;
    private Paint imageFromPaint;
    private ImageFrom imageFrom;

    public ShowImageFromFunction(View view) {
        this.view = view;
    }

    @Override
    public boolean onReadyDisplay(@Nullable UriModel uriModel) {
        imageFrom = null;
        return true;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        initImageFromPath();
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (imageFrom == null) {
            return;
        }

        if (imageFromPath == null) {
            initImageFromPath();
        }
        if (imageFromPaint == null) {
            imageFromPaint = new Paint();
            imageFromPaint.setAntiAlias(true);
        }
        switch (imageFrom) {
            case MEMORY_CACHE:
                imageFromPaint.setColor(FROM_FLAG_COLOR_MEMORY_CACHE);
                break;
            case DISK_CACHE:
                imageFromPaint.setColor(FROM_FLAG_COLOR_DISK_CACHE);
                break;
            case NETWORK:
                imageFromPaint.setColor(FROM_FLAG_COLOR_NETWORK);
                break;
            case LOCAL:
                imageFromPaint.setColor(FROM_FLAG_COLOR_LOCAL);
                break;
            case MEMORY:
                imageFromPaint.setColor(FROM_FLAG_COLOR_MEMORY);
                break;
            default:
                return;
        }
        canvas.drawPath(imageFromPath, imageFromPaint);
    }

    private void initImageFromPath() {
        if (imageFromPath == null) {
            imageFromPath = new Path();
        } else {
            imageFromPath.reset();
        }
        int x = view.getWidth() / 10;
        int y = view.getWidth() / 10;
        int left2 = view.getPaddingLeft();
        int top2 = view.getPaddingTop();
        imageFromPath.moveTo(left2, top2);
        imageFromPath.lineTo(left2 + x, top2);
        imageFromPath.lineTo(left2, top2 + y);
        imageFromPath.close();
    }

    @Override
    public boolean onDetachedFromWindow() {
        // drawable都已经被清空了，图片来源标识当然要重置了
        imageFrom = null;
        return false;
    }

    @Override
    public boolean onDrawableChanged(@NonNull String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        ImageFrom oldImageFrom = imageFrom;
        ImageFrom newImageFrom = null;
        Drawable lastDrawable = SketchUtils.getLastDrawable(newDrawable);
        if (!(lastDrawable instanceof SketchLoadingDrawable) && lastDrawable instanceof SketchDrawable) {
            SketchDrawable sketchDrawable = (SketchDrawable) lastDrawable;
            newImageFrom = sketchDrawable.getImageFrom();
        }
        imageFrom = newImageFrom;
        return oldImageFrom != newImageFrom;
    }

    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }
}
