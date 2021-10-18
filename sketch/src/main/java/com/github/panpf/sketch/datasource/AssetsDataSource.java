/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.datasource;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.panpf.sketch.cache.BitmapPool;
import com.github.panpf.sketch.decode.ImageAttrs;
import com.github.panpf.sketch.decode.NotFoundGifLibraryException;
import com.github.panpf.sketch.drawable.SketchGifDrawable;
import com.github.panpf.sketch.drawable.SketchGifFactory;
import com.github.panpf.sketch.request.ImageFrom;
import com.github.panpf.sketch.util.SketchUtils;

/**
 * 用于读取来自 asset 的图片
 */
public class AssetsDataSource implements DataSource {

    @NonNull
    private Context context;
    @NonNull
    private String assetsFilePath;
    private long length = -1;

    public AssetsDataSource(@NonNull Context context, @NonNull String assetsFilePath) {
        this.context = context;
        this.assetsFilePath = assetsFilePath;
    }

    @NonNull
    @Override
    public InputStream getInputStream() throws IOException {
        return context.getAssets().open(assetsFilePath);
    }

    @Override
    public synchronized long getLength() throws IOException {
        if (length >= 0) {
            return length;
        }

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getAssets().openFd(assetsFilePath);
            length = fileDescriptor.getLength();
        } finally {
            SketchUtils.close(fileDescriptor);
        }
        return length;
    }

    @Override
    public File getFile(@Nullable File outDir, @Nullable String outName) throws IOException {
        if (outDir == null) {
            return null;
        }

        if (!outDir.exists() && !outDir.getParentFile().mkdirs()) {
            return null;
        }

        File outFile;
        if (!TextUtils.isEmpty(outName)) {
            outFile = new File(outDir, outName);
        } else {
            outFile = new File(outDir, SketchUtils.generatorTempFileName(this, assetsFilePath));
        }

        InputStream inputStream = getInputStream();

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(outFile);
        } catch (IOException e) {
            SketchUtils.close(inputStream);
            throw e;
        }

        byte[] data = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, length);
            }
        } finally {
            SketchUtils.close(outputStream);
            SketchUtils.close(inputStream);
        }

        return outFile;
    }

    @NonNull
    @Override
    public ImageFrom getImageFrom() {
        return ImageFrom.LOCAL;
    }

    @NonNull
    @Override
    public SketchGifDrawable makeGifDrawable(@NonNull String key, @NonNull String uri, @NonNull ImageAttrs imageAttrs,
                                             @NonNull BitmapPool bitmapPool) throws IOException, NotFoundGifLibraryException {
        AssetManager assetManager = context.getAssets();
        return SketchGifFactory.createGifDrawable(key, uri, imageAttrs, getImageFrom(), bitmapPool, assetManager, assetsFilePath);
    }
}
