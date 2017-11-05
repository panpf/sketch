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

import java.lang.ref.WeakReference;

import me.panpf.sketch.SketchView;
import me.panpf.sketch.util.SketchUtils;

/**
 * Request与ImageView的关系绑定器
 */
public class RequestAndViewBinder {
    private DisplayRequest displayRequest;
    private WeakReference<SketchView> imageViewReference;

    public RequestAndViewBinder(SketchView imageView) {
        this.imageViewReference = new WeakReference<>(imageView);
    }

    public void setDisplayRequest(DisplayRequest displayRequest) {
        this.displayRequest = displayRequest;
    }

    public SketchView getView() {
        final SketchView sketchView = imageViewReference.get();
        if (displayRequest != null) {
            DisplayRequest holderDisplayRequest = SketchUtils.findDisplayRequest(sketchView);
            if (holderDisplayRequest != null && holderDisplayRequest == displayRequest) {
                return sketchView;
            } else {
                return null;
            }
        } else {
            return sketchView;
        }
    }

    public boolean isBroken() {
        return getView() == null;
    }
}
