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

package me.xiaopan.sketch.request;

/**
 * 错误原因
 */
public enum ErrorCause {
    /**
     * URI为NULL或空
     */
    URI_NULL_OR_EMPTY,

    /**
     * URI不支持
     */
    URI_NO_SUPPORT,

    /**
     * 下载失败
     */
    DOWNLOAD_FAIL,

    /**
     * 解码失败
     */
    DECODE_FAIL,

    /**
     * Bitmap已回收
     */
    BITMAP_RECYCLED,

    /**
     * 旧的Bitmap被回收了
     */
    SOURCE_BITMAP_RECYCLED,

    /**
     * GifDrawable已回收
     */
    GIF_DRAWABLE_RECYCLED,

    /**
     * 预处理结果是空的
     */
    PRE_PROCESS_RESULT_IS_NULL,

    /**
     * 下载结果是空的
     */
    DOWNLOAD_RESULT_IS_NULL,

    /**
     * 无法准备数据源
     */
    NOT_FOUND_DATA_SOURCE_BY_UNKNOWN_URI,

    /**
     * 纠正图片方向失败
     */
    CORRECT_ORIENTATION_FAIL,

    /**
     * 处理图片失败
     */
    PROCESS_IMAGE_FAIL,
}