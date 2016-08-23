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

package me.xiaopan.sketch.feature.large;

import android.graphics.Matrix;
import android.graphics.RectF;

public class UpdateParams {
    Matrix drawMatrix = new Matrix();
    RectF visibleRect = new RectF();
    int previewDrawableWidth;
    int previewDrawableHeight;
    int imageViewWidth;
    int imageViewHeight;

    public void set(UpdateParams updateParams) {
        if (updateParams == null) {
            reset();
        } else {
            this.drawMatrix.set(updateParams.drawMatrix);
            this.visibleRect.set(updateParams.visibleRect);
            this.previewDrawableWidth = updateParams.previewDrawableWidth;
            this.previewDrawableHeight = updateParams.previewDrawableHeight;
            this.imageViewWidth = updateParams.imageViewWidth;
            this.imageViewHeight = updateParams.imageViewHeight;
        }
    }

    public boolean isEmpty() {
        return visibleRect.isEmpty()
                || previewDrawableWidth == 0 || previewDrawableHeight == 0
                || imageViewWidth == 0 || imageViewHeight == 0;
    }

    public void reset() {
        drawMatrix.reset();
        visibleRect.setEmpty();
        previewDrawableWidth = 0;
        previewDrawableHeight = 0;
        imageViewWidth = 0;
        imageViewHeight = 0;
    }

    public Matrix getDrawMatrix() {
        return drawMatrix;
    }

    public RectF getVisibleRect() {
        return visibleRect;
    }

    public void setPreviewDrawableSize(int previewDrawableWidth, int previewDrawableHeight) {
        this.previewDrawableWidth = previewDrawableWidth;
        this.previewDrawableHeight = previewDrawableHeight;
    }

    public void setImageViewSize(int imageViewWidth, int imageViewHeight) {
        this.imageViewWidth = imageViewWidth;
        this.imageViewHeight = imageViewHeight;
    }
}
