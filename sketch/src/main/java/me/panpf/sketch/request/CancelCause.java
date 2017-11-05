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

package me.panpf.sketch.request;

/**
 * 取消的原因
 */
public enum CancelCause {
    /**
     * 通过Sketch.cancel()方法取消
     */
    BE_CANCELLED,

    /**
     * 暂停下载
     */
    PAUSE_DOWNLOAD,

    /**
     * 暂停加载
     */
    PAUSE_LOAD,

    /**
     * ImageView从Window移除
     */
    ON_DETACHED_FROM_WINDOW,

    /**
     * 被替换了
     */
    BE_REPLACED,

    /**
     * 在DisplayHelper中替换取消
     */
    BE_REPLACED_ON_HELPER,

    /**
     * 在setImageDrawable的时候替换取消
     */
    BE_REPLACED_ON_SET_DRAWABLE,

    /**
     * 页面隐藏用户看不见
     */
    USERS_NOT_VISIBLE,

    /**
     * 检测到绑定关系断开
     */
    BIND_DISCONNECT,
}
