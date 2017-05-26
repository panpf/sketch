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

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.drawable.SketchGifFactory;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.util.SketchUtils;

public class ContentDataSource implements DataSource {

    private Context context;
    private Uri contentUri;
    private long length = -1;

    public ContentDataSource(Context context, Uri contentUri) {
        this.context = context;
        this.contentUri = contentUri;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return context.getContentResolver().openInputStream(contentUri);
    }

    @Override
    public synchronized long getLength() throws IOException {
        if (length >= 0) {
            return length;
        }

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(contentUri, "r");
            length = fileDescriptor != null ? fileDescriptor.getLength() : 0;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SketchUtils.close(fileDescriptor);
        }
        return length;
    }

    @Override
    public File getFile(File outDir, String outName) throws IOException {
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
            outFile = new File(outDir, SketchUtils.generatorTempFileName(this, contentUri.toString()));
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

    @Override
    public ImageFrom getImageFrom() {
        return ImageFrom.LOCAL;
    }

    @Override
    public SketchGifDrawable makeGifDrawable(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            return SketchGifFactory.createGifDrawable(key, uri, imageAttrs, getImageFrom(), bitmapPool, contentResolver, contentUri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
