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

package me.xiaopan.sketch.process;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.request.Resize;

/**
 * 旋转图片处理器
 */
public class RotateImageProcessor extends DefaultImageProcessor {
    private int degrees;

    public RotateImageProcessor(int degrees) {
        this.degrees = degrees;
    }

    @Override
    public Bitmap process(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        Bitmap resizeBitmap = super.process(sketch, bitmap, resize, forceUseResize, lowQualityImage);

        if (degrees == 0) {
            return resizeBitmap;
        }

        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap rotateBitmap = Bitmap.createBitmap(resizeBitmap, 0, 0, resizeBitmap.getWidth(), resizeBitmap.getHeight(), matrix, true);

        if (resizeBitmap != bitmap) {
            resizeBitmap.recycle();
        }

        return rotateBitmap;
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder stringBuilder) {
        return stringBuilder.append("RotateImageProcessor")
                .append("(")
                .append("degrees").append("=").append(degrees)
                .append(")");
    }
}
