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

package me.xiaopan.android.spear.util;

import android.widget.ImageView;

import me.xiaopan.android.spear.DisplayHelper;
import me.xiaopan.android.spear.Spear;

/**
 * DisplayHelper管理器
 */
public class DisplayHelperManager {
    private DisplayHelper displayHelper;

    public DisplayHelper getDisplayHelper(Spear spear, String uri, ImageView imageView){
        if(displayHelper == null){
            return spear.getConfiguration().getHelperFactory().newDisplayHelper(spear, uri, imageView);
        }else{
            DisplayHelper newDisplayHelper = displayHelper;
            displayHelper = null;
            newDisplayHelper.reset();
            newDisplayHelper.init(spear, uri, imageView);
            return newDisplayHelper;
        }
    }

    public void recoveryDisplayHelper(DisplayHelper waitDisplayHelper){
        if(displayHelper == null){
            displayHelper = waitDisplayHelper;
        }
    }
}