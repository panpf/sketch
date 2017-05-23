/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.feature;

import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

public class Base64ImagePreprocessor implements ImagePreprocessor.Preprocessor {

    private static final String LOG_NAME = "Base64ImagePreprocessor";

    @Override
    public boolean match(LoadRequest request) {
        return request.getUriScheme() == UriScheme.BASE64;
    }

    @Override
    public PreProcessResult process(LoadRequest request) {
        Configuration configuration = request.getConfiguration();
        String diskCacheKey = request.getUri();

        DiskCache diskCache = configuration.getDiskCache();
        ReentrantLock diskCacheEditLock = diskCache.getEditLock(diskCacheKey);
        diskCacheEditLock.lock();

        PreProcessResult result = cacheBase64Image(diskCache, request, diskCacheKey);

        diskCacheEditLock.unlock();
        return result;
    }

    private PreProcessResult cacheBase64Image(DiskCache diskCache, LoadRequest request, String diskCacheKey) {
        DiskCache.Entry apkIconDiskCacheEntry = diskCache.get(diskCacheKey);
        if (apkIconDiskCacheEntry != null) {
            return new PreProcessResult(apkIconDiskCacheEntry, ImageFrom.DISK_CACHE);
        }

        String uri = request.getUri();
        String mimeType = uri.substring("data:".length(), uri.indexOf(";"));
        String dataInfo = uri.substring("data:".length() + mimeType.length() + ";base64,".length());

        byte[] data = Base64.decode(dataInfo, Base64.DEFAULT);

        DiskCache.Editor diskCacheEditor = diskCache.edit(diskCacheKey);
        OutputStream outputStream;
        if (diskCacheEditor != null) {
            try {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
                diskCacheEditor.abort();
                return null;
            }
        } else {
            outputStream = new ByteArrayOutputStream();
        }

        try {
            outputStream.write(data);

            if (diskCacheEditor != null) {
                diskCacheEditor.commit();
            }
        } catch (DiskLruCache.EditorChangedException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (DiskLruCache.ClosedException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (DiskLruCache.FileNotExistException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } finally {
            SketchUtils.close(outputStream);
        }

        if (diskCacheEditor != null) {
            apkIconDiskCacheEntry = diskCache.get(diskCacheKey);
            if (apkIconDiskCacheEntry != null) {
                return new PreProcessResult(apkIconDiskCacheEntry, ImageFrom.MEMORY);
            } else {
                if (SLogType.REQUEST.isEnabled()) {
                    SLog.w(SLogType.REQUEST, LOG_NAME, "not found base64 image cache file. %s", request.getKey());
                }
                return null;
            }
        } else {
            return new PreProcessResult(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.MEMORY);
        }
    }
}
