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

package me.xiaopan.sketch.drawable;

/**
 * 关于图片的一些真实属性，例如宽、高、类型、旋转角度等
 */
public class ImageAttrs {
    private int originWidth;
    private int originHeight;
    private String mimeType;
    private int orientation;    // 顺时针方向将图片旋转多少度能回正

    public ImageAttrs(String mimeType, int originWidth, int originHeight, int orientation) {
        this.mimeType = mimeType;
        this.originWidth = originWidth;
        this.originHeight = originHeight;
        this.orientation = orientation;
    }

    public int getOrientation() {
        return orientation;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getOriginHeight() {
        return originHeight;
    }

    public int getOriginWidth() {
        return originWidth;
    }

    public void resetSize(int originWidth, int originHeight){
        this.originWidth = originWidth;
        this.originHeight = originHeight;
    }
}
