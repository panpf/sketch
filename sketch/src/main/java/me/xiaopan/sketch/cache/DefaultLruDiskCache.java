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

package me.xiaopan.sketch.cache;

import android.content.Context;
import android.text.format.Formatter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

public class DefaultLruDiskCache implements DiskCache {
    private static final String NAME = "DefaultLruDiskCache";

    private Context context;
    private File cacheDir;
    private int maxSize;
    private int appVersionCode;

    private DiskLruCache cache;

    public DefaultLruDiskCache(Context context, File cacheDir, int appVersionCode, int maxSize) {
        this.context = context;
        this.cacheDir = cacheDir;
        this.maxSize = maxSize;
        this.appVersionCode = appVersionCode;
        resetCache();
    }

    private synchronized void resetCache() {
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            cache = DiskLruCache.open(cacheDir, appVersionCode, 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("cacheDir disable. " + cacheDir.getPath());
        }
    }

    @Override
    public synchronized File getCacheFile(String uri) {
        DiskLruCache.Entry entry = cache.getEntry(uriToFileName(uri));
        return entry != null ? entry.getCleanFile(0) : null;
    }

    @Override
    public synchronized DiskLruCache.Editor edit(String uri) {
        try {
            return cache.edit(uriToFileName(uri));
        } catch (IOException e) {
            e.printStackTrace();
            // 发生异常的时候（比如SD卡被拔出，导致不能使用），尝试重建DiskLryCache，能显著提高遇错恢复能力
            resetCache();
            try {
                return cache.edit(uriToFileName(uri));
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public File getCacheDir() {
        return cacheDir;
    }

    @Override
    public long getMaxSize() {
        return maxSize;
    }

    @Override
    public String uriToFileName(String uri) {
        if (SketchUtils.checkSuffix(uri, ".apk")) {
            uri += ".icon";
        }
        try {
            return URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public synchronized long getSize() {
        return cache.size();
    }

    @Override
    public synchronized void clear() {
        try {
            cache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        resetCache();
    }

    @Override
    public void close() {
        try {
            cache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME)
                .append(". ")
                .append("dir").append("=").append(cacheDir.getPath())
                .append(", ")
                .append("maxSize").append("=").append(Formatter.formatFileSize(context, maxSize))
                .append(", ")
                .append("appVersionCode").append("=").append(appVersionCode);
    }
}
