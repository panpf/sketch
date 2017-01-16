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

package me.xiaopan.sketch;

public enum SLogType {
    /**
     * 其它
     */
    BASE,

    /**
     * 内存缓存、bitmap pool、磁盘缓存
     */
    CACHE,

    /**
     * 请求流程
     */
    REQUEST,

    /**
     * DisplayHelper.commit()执行时间和解码耗时
     */
    TIME,

    /**
     * 手势缩放
     */
    ZOOM,

    /**
     * 分块显示超大图
     */
    LARGE,
    ;

    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @SuppressWarnings("unused")
    public void setAllEnabled(boolean enabled) {
        for(SLogType type : values()){
            type.setEnabled(enabled);
        }
    }
}
