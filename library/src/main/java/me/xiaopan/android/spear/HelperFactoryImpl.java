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

package me.xiaopan.android.spear;

import android.widget.ImageView;

public class HelperFactoryImpl implements HelperFactory{
    private DisplayHelper obsoletingDisplayHelper;

    @Override
    public DownloadHelper getDownloadHelper(Spear spear, String uri) {
        return new DownloadHelperImpl(spear, uri);
    }

    @Override
    public LoadHelper getLoadHelper(Spear spear, String uri) {
        return new LoadHelperImpl(spear, uri);
    }

    @Override
    public DisplayHelper getDisplayHelper(Spear spear, String uri, ImageView imageView) {
        if(this.obsoletingDisplayHelper == null){
            return new DisplayHelperImpl(spear, uri, imageView);
        }else{
            DisplayHelper displayHelper = this.obsoletingDisplayHelper;
            this.obsoletingDisplayHelper = null;
            displayHelper.init(spear, uri, imageView);
            return displayHelper;
        }
    }

    @Override
    public DisplayHelper getDisplayHelper(Spear spear, DisplayParams displayParams, ImageView imageView) {
        if(this.obsoletingDisplayHelper == null){
            return new DisplayHelperImpl(spear, displayParams, imageView);
        }else{
            DisplayHelper displayHelper = this.obsoletingDisplayHelper;
            this.obsoletingDisplayHelper = null;
            displayHelper.init(spear, displayParams, imageView);
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
}
