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

package me.panpf.sketch.decode;

/**
 * 图片的真实宽、高、格式、方向等属性
 */
public class ImageAttrs {
    private int width;
    private int height;
    private String mimeType;
    private int exifOrientation;

    public ImageAttrs(String mimeType, int width, int height, int exifOrientation) {
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.exifOrientation = exifOrientation;
    }

    public int getExifOrientation() {
        return exifOrientation;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    void resetSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
