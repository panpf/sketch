/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.zoom;

import android.content.Context;
import android.widget.ImageView.ScaleType;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.decode.ImageSizeCalculator;

/**
 * 根据预览图尺寸、原始图尺寸和 ImageView 尺寸计算出两级缩放比例
 */
public class AdaptiveTwoLevelScales implements ZoomScales {

    private static final float DEFAULT_MAXIMIZE_SCALE = 1.75f;
    private static final float DEFAULT_MINIMUM_SCALE = 1.0f;
    private static final float[] DEFAULT_DOUBLE_CLICK_ZOOM_SCALES = new float[]{DEFAULT_MINIMUM_SCALE, DEFAULT_MAXIMIZE_SCALE};

    private float minZoomScale = DEFAULT_MINIMUM_SCALE;
    private float maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
    private float[] doubleClickZoomScales = DEFAULT_DOUBLE_CLICK_ZOOM_SCALES; // 双击缩放所使用的比例
    private float fullZoomScale;
    private float fillZoomScale;
    private float originZoomScale;
    private float initZoomScale;

    @Override
    public void reset(final Context context, final Sizes sizes, final ScaleType scaleType, final float rotateDegrees, final boolean readMode) {
        final int drawableWidth = rotateDegrees % 180 == 0 ? sizes.drawableSize.getWidth() : sizes.drawableSize.getHeight();
        final int drawableHeight = rotateDegrees % 180 == 0 ? sizes.drawableSize.getHeight() : sizes.drawableSize.getWidth();
        final int imageWidth = rotateDegrees % 180 == 0 ? sizes.imageSize.getWidth() : sizes.imageSize.getHeight();
        final int imageHeight = rotateDegrees % 180 == 0 ? sizes.imageSize.getHeight() : sizes.imageSize.getWidth();

        final float widthScale = (float) sizes.viewSize.getWidth() / drawableWidth;
        final float heightScale = (float) sizes.viewSize.getHeight() / drawableHeight;
        final boolean imageThanViewLarge = drawableWidth > sizes.viewSize.getWidth() || drawableHeight > sizes.viewSize.getHeight();

        final ScaleType finalScaleType;
        if (scaleType == ScaleType.MATRIX) {
            finalScaleType = ScaleType.FIT_CENTER;
        } else if (scaleType == ScaleType.CENTER_INSIDE) {
            finalScaleType = imageThanViewLarge ? ScaleType.FIT_CENTER : ScaleType.CENTER;
        } else {
            finalScaleType = scaleType;
        }

        // 小的是完整显示比例，大的是充满比例
        fullZoomScale = Math.min(widthScale, heightScale);
        fillZoomScale = Math.max(widthScale, heightScale);
        originZoomScale = Math.max((float) imageWidth / drawableWidth, (float) imageHeight / drawableHeight);
        initZoomScale = getInitScale(context, sizes, finalScaleType, rotateDegrees, readMode);

        ImageSizeCalculator sizeCalculator = Sketch.with(context).getConfiguration().getSizeCalculator();
        if (readMode && sizeCalculator.canUseReadModeByHeight(imageWidth, imageHeight)) {
            // 阅读模式下保证阅读效果最重要
            minZoomScale = fullZoomScale;
            maxZoomScale = Math.max(originZoomScale, fillZoomScale);
        } else if (readMode && sizeCalculator.canUseReadModeByWidth(imageWidth, imageHeight)) {
            // 阅读模式下保证阅读效果最重要
            minZoomScale = fullZoomScale;
            maxZoomScale = Math.max(originZoomScale, fillZoomScale);
        } else if (finalScaleType == ScaleType.CENTER) {
            minZoomScale = 1.0f;
            maxZoomScale = Math.max(originZoomScale, fillZoomScale);
        } else if (finalScaleType == ScaleType.CENTER_CROP) {
            minZoomScale = fillZoomScale;
            // 由于CENTER_CROP的时候最小缩放比例就是充满比例，所以最大缩放比例一定要比充满比例大的多
            maxZoomScale = Math.max(originZoomScale, fillZoomScale * 1.5f);
        } else if (finalScaleType == ScaleType.FIT_START || finalScaleType == ScaleType.FIT_CENTER || finalScaleType == ScaleType.FIT_END) {
            minZoomScale = fullZoomScale;

            // 如果原始比例仅仅比充满比例大一点点，还是用充满比例作为最大缩放比例比较好，否则谁大用谁
            if (originZoomScale > fillZoomScale && (fillZoomScale * 1.2f) >= originZoomScale) {
                maxZoomScale = fillZoomScale;
            } else {
                maxZoomScale = Math.max(originZoomScale, fillZoomScale);
            }

            // 最大缩放比例和最小缩放比例的差距不能太小，最小得是最小缩放比例的1.5倍
            maxZoomScale = Math.max(maxZoomScale, minZoomScale * 1.5f);
        } else if (finalScaleType == ScaleType.FIT_XY) {
            minZoomScale = fullZoomScale;
            maxZoomScale = fullZoomScale;
        } else {
            // 基本不会走到这儿
            minZoomScale = fullZoomScale;
            maxZoomScale = fullZoomScale;
        }

        // 这样的情况基本不会出现，不过还是加层保险
        if (minZoomScale > maxZoomScale) {
            minZoomScale = minZoomScale + maxZoomScale;
            maxZoomScale = minZoomScale - maxZoomScale;
            minZoomScale = minZoomScale - maxZoomScale;
        }

        // 双击缩放比例始终由最小缩放比例和最大缩放比例组成
        doubleClickZoomScales = new float[]{minZoomScale, maxZoomScale};
    }

    private float getInitScale(final Context context, final Sizes sizes, final ScaleType scaleType, final float rotateDegrees, final boolean readMode) {
        final int drawableWidth = rotateDegrees % 180 == 0 ? sizes.drawableSize.getWidth() : sizes.drawableSize.getHeight();
        final int drawableHeight = rotateDegrees % 180 == 0 ? sizes.drawableSize.getHeight() : sizes.drawableSize.getWidth();
        final int imageWidth = rotateDegrees % 180 == 0 ? sizes.imageSize.getWidth() : sizes.imageSize.getHeight();
        final int imageHeight = rotateDegrees % 180 == 0 ? sizes.imageSize.getHeight() : sizes.imageSize.getWidth();

        final float widthScale = (float) sizes.viewSize.getWidth() / drawableWidth;
        final float heightScale = (float) sizes.viewSize.getHeight() / drawableHeight;
        boolean imageThanViewLarge = drawableWidth > sizes.viewSize.getWidth() || drawableHeight > sizes.viewSize.getHeight();

        ImageSizeCalculator sizeCalculator = Sketch.with(context).getConfiguration().getSizeCalculator();

        final ScaleType finalScaleType;
        if (scaleType == ScaleType.MATRIX) {
            finalScaleType = ScaleType.FIT_CENTER;
        } else if (scaleType == ScaleType.CENTER_INSIDE) {
            finalScaleType = imageThanViewLarge ? ScaleType.FIT_CENTER : ScaleType.CENTER;
        } else {
            finalScaleType = scaleType;
        }

        if (readMode && sizeCalculator.canUseReadModeByHeight(imageWidth, imageHeight)) {
            return widthScale;
        } else if (readMode && sizeCalculator.canUseReadModeByWidth(imageWidth, imageHeight)) {
            return heightScale;
        } else if (finalScaleType == ScaleType.CENTER) {
            return 1.0f;
        } else if (finalScaleType == ScaleType.CENTER_CROP) {
            return Math.max(widthScale, heightScale);
        } else if (finalScaleType == ScaleType.FIT_START) {
            return Math.min(widthScale, heightScale);
        } else if (finalScaleType == ScaleType.FIT_END) {
            return Math.min(widthScale, heightScale);
        } else if (finalScaleType == ScaleType.FIT_CENTER) {
            return Math.min(widthScale, heightScale);
        } else if (finalScaleType == ScaleType.FIT_XY) {
            return 1.0f;
        } else {
            return 1.0f;
        }
    }

    @Override
    public float getMinZoomScale() {
        return minZoomScale;
    }

    @Override
    public float getMaxZoomScale() {
        return maxZoomScale;
    }

    @Override
    public float getInitZoomScale() {
        return initZoomScale;
    }

    @Override
    public float getFullZoomScale() {
        return fullZoomScale;
    }

    @Override
    public float getFillZoomScale() {
        return fillZoomScale;
    }

    @Override
    public float getOriginZoomScale() {
        return originZoomScale;
    }

    @Override
    public float[] getZoomScales() {
        return doubleClickZoomScales;
    }

    @Override
    public void clean() {
        fullZoomScale = fillZoomScale = originZoomScale = 1f;
        minZoomScale = DEFAULT_MINIMUM_SCALE;
        maxZoomScale = DEFAULT_MAXIMIZE_SCALE;
        doubleClickZoomScales = DEFAULT_DOUBLE_CLICK_ZOOM_SCALES;
    }
}
