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

import androidx.annotation.NonNull;

import me.panpf.sketch.Key;

/**
 * 用于计算 inSimpleSize 缩小图片
 */
public class MaxSize implements Key {
    private int width;
    private int height;

    public MaxSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof MaxSize) {
            MaxSize other = (MaxSize) obj;
            return width == other.width && height == other.height;
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("MaxSize(%dx%d)", width, height);
    }

    @Override
    public String getKey() {
        return toString();
    }
}
