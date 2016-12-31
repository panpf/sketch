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

import me.xiaopan.sketch.Identifier;

/**
 * ImageView的固定尺寸，只能是通过layout_width和layout_height设置的固定值才能算是固定尺寸
 */
public class FixedSize implements Identifier {
    private int width;
    private int height;

    public FixedSize(int width, int height) {
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
        if (obj instanceof FixedSize) {
            FixedSize other = (FixedSize) obj;
            return width == other.width && height == other.height;
        }
        return false;
    }

    @Override
    public String getKey() {
        return String.format("FixedSize(%dx%d)", width, height);
    }
}
