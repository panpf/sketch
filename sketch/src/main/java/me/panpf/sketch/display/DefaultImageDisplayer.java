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

package me.panpf.sketch.display;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import me.panpf.sketch.SketchView;

/**
 * 默认的图片显示器，没有任何动画效果
 */
public class DefaultImageDisplayer implements ImageDisplayer {
    private static final String KEY = "DefaultImageDisplayer";

    public DefaultImageDisplayer() {
    }

    @Override
    public void display(@NonNull SketchView sketchView, @NonNull Drawable newDrawable) {
        if (newDrawable == null) {
            return;
        }
        sketchView.clearAnimation();
        sketchView.setImageDrawable(newDrawable);
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public boolean isAlwaysUse() {
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return KEY;
    }
}
