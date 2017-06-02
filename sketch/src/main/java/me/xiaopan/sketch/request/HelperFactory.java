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
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchView;

public class HelperFactory implements Identifier {
    private static final String KEY = "HelperFactory";

    private DisplayHelper cacheDisplayHelper;

    public DownloadHelper getDownloadHelper(Sketch sketch, String uri) {
        return new DownloadHelper(sketch, uri);
    }

    public LoadHelper getLoadHelper(Sketch sketch, String uri) {
        return new LoadHelper(sketch, uri);
    }

    public DisplayHelper getDisplayHelper(Sketch sketch, String uri, SketchView sketchView) {
        if (this.cacheDisplayHelper == null) {
            this.cacheDisplayHelper = new DisplayHelper();
        }

        DisplayHelper displayHelper = this.cacheDisplayHelper;
        this.cacheDisplayHelper = null;
        displayHelper.init(sketch, uri, sketchView);
        return displayHelper;
    }

    /**
     * 用完了要回收
     */
    public void recycleDisplayHelper(DisplayHelper displayHelper) {
        displayHelper.reset();
        if (this.cacheDisplayHelper == null) {
            this.cacheDisplayHelper = displayHelper;
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }
}