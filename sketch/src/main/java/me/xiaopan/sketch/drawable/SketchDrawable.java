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

public interface SketchDrawable {

    /**
     * 获取图片原始宽
     */
    int getOriginWidth();

    /**
     * 设置图片原始宽
     */
    void setOriginWidth(int originWidth);

    /**
     * 获取图片原始高
     */
    int getOriginHeight();

    /**
     * 设置图片原始高
     */
    void setOriginHeight(int originHeight);

    /**
     * 获取图片类型
     */
    String getMimeType();

    /**
     * 设置图片类型
     */
    void setMimeType(String mimeType);

    /**
     * 获取一些信息
     */
    String getInfo();
}
