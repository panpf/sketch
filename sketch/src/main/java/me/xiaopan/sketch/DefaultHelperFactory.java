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

package me.xiaopan.sketch;

public class DefaultHelperFactory implements HelperFactory{
    private static final String NAME = "DefaultHelperFactory";
    private DisplayHelper obsoletingDisplayHelper;

    @Override
    public DownloadHelper getDownloadHelper(Sketch sketch, String uri) {
        return new DefaultDownloadHelper(sketch, uri);
    }

    @Override
    public LoadHelper getLoadHelper(Sketch sketch, String uri) {
        return new DefaultLoadHelper(sketch, uri);
    }

    @Override
    public DisplayHelper getDisplayHelper(Sketch sketch, String uri, SketchImageViewInterface sketchImageViewInterface) {
        if(this.obsoletingDisplayHelper == null){
            return new DefaultDisplayHelper(sketch, uri, sketchImageViewInterface);
        }else{
            DisplayHelper displayHelper = this.obsoletingDisplayHelper;
            this.obsoletingDisplayHelper = null;
            displayHelper.init(sketch, uri, sketchImageViewInterface);
            return displayHelper;
        }
    }

    @Override
    public DisplayHelper getDisplayHelper(Sketch sketch, DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface) {
        if(this.obsoletingDisplayHelper == null){
            return new DefaultDisplayHelper(sketch, displayParams, sketchImageViewInterface);
        }else{
            DisplayHelper displayHelper = this.obsoletingDisplayHelper;
            this.obsoletingDisplayHelper = null;
            displayHelper.init(sketch, displayParams, sketchImageViewInterface);
            return displayHelper;
        }
    }

    @Override
    public void recycleDisplayHelper(DisplayHelper obsoletingDisplayHelper){
        obsoletingDisplayHelper.reset();
        if(this.obsoletingDisplayHelper == null){
            this.obsoletingDisplayHelper = obsoletingDisplayHelper;
        }
    }

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }
}
