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

package me.xiaopan.sketch.decode;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.drawable.SketchGifFactory;
import me.xiaopan.sketch.request.ImageFrom;

public class AssetsDataSource implements DataSource {
    protected String logName = "AssetsDataSource";

    private Context context;
    private String assetsFilePath;

    public AssetsDataSource(Context context, String assetsFilePath) {
        this.context = context;
        this.assetsFilePath = assetsFilePath;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return context.getAssets().open(assetsFilePath);
    }

    @Override
    public SketchGifDrawable makeGifDrawable(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool) {
        AssetManager assetManager = context.getAssets();
        try {
            return SketchGifFactory.createGifDrawable(key, uri, imageAttrs, bitmapPool, assetManager, assetsFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ImageFrom getImageFrom() {
        return ImageFrom.LOCAL;
    }
}
