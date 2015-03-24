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
import me.xiaopan.android.spear.DisplayHelperImpl;
import me.xiaopan.android.spear.DownloadHelper;
import me.xiaopan.android.spear.DownloadHelperImpl;
import me.xiaopan.android.spear.LoadHelper;
import me.xiaopan.android.spear.LoadHelperImpl;
import me.xiaopan.android.spear.Spear;

public class DefaultHelperFactory implements HelperFactory{
    @Override
    public DownloadHelper newDownloadHelper(Spear spear, String uri) {
        return new DownloadHelperImpl(spear, uri);
    }

    @Override
    public LoadHelper newLoadHelper(Spear spear, String uri) {
        return new LoadHelperImpl(spear, uri);
    }

    @Override
    public DisplayHelper newDisplayHelper(Spear spear, String uri, ImageView imageView) {
        return new DisplayHelperImpl(spear, uri, imageView);
    }
}
