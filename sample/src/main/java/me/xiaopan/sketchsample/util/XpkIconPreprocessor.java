package me.xiaopan.sketchsample.util;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.preprocess.PreProcessResult;
import me.xiaopan.sketch.preprocess.Preprocessor;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.UriInfo;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 解析XPK文件的图标
 */
public class XpkIconPreprocessor implements Preprocessor {

    private static final String LOG_NAME = "XpkIconPreprocessor";

    @Override
    public boolean match(Context context, UriInfo uriInfo) {
        return uriInfo.getScheme() == UriScheme.FILE
                && uriInfo.getContent() != null
                && SketchUtils.checkSuffix(uriInfo.getContent(), ".xpk");
    }

    @Override
    public PreProcessResult process(Context context, UriInfo uriInfo) {
        File xpkFile = new File(uriInfo.getContent());
        if (!xpkFile.exists()) {
            return null;
        }
        long lastModifyTime = xpkFile.lastModified();
        String diskCacheKey = uriInfo.getContent() + "." + lastModifyTime;

        DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();

        DiskCache.Entry cacheEntry = diskCache.get(diskCacheKey);
        if (cacheEntry != null) {
            return new PreProcessResult(cacheEntry, ImageFrom.DISK_CACHE);
        }

        ReentrantLock diskCacheEditLock = diskCache.getEditLock(diskCacheKey);
        diskCacheEditLock.lock();

        PreProcessResult result;
        try {
            cacheEntry = diskCache.get(diskCacheKey);
            if (cacheEntry != null) {
                result = new PreProcessResult(cacheEntry, ImageFrom.DISK_CACHE);
            } else {
                result = readXpkIcon(uriInfo, diskCache, diskCacheKey);
            }
        } finally {
            diskCacheEditLock.unlock();
        }

        return result;
    }

    private PreProcessResult readXpkIcon(UriInfo uriInfo, DiskCache diskCache, String diskCacheKey) {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(uriInfo.getContent());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        InputStream inputStream;
        ZipEntry zipEntry = zipFile.getEntry("icon.png");
        if (zipEntry == null) {
            if (SLogType.REQUEST.isEnabled()) {
                SLog.fw(SLogType.REQUEST, LOG_NAME, "not found icon.png in. %s", uriInfo.getUri());
            }
            return null;
        }

        try {
            inputStream = zipFile.getInputStream(zipEntry);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        DiskCache.Editor diskCacheEditor = diskCache.edit(diskCacheKey);
        OutputStream outputStream;
        if (diskCacheEditor != null) {
            try {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
                diskCacheEditor.abort();
                SketchUtils.close(inputStream);
                return null;
            }
        } else {
            outputStream = new ByteArrayOutputStream();
        }

        try {
            byte[] buffer = new byte[8 * 1024];
            int realLength;
            while (true) {
                realLength = inputStream.read(buffer);
                if (realLength < 0) {
                    break;
                }
                outputStream.write(buffer, 0, realLength);
            }

            if (diskCacheEditor != null) {
                diskCacheEditor.commit();
            }
        } catch (DiskLruCache.EditorChangedException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            if (diskCacheEditor != null) {
                diskCacheEditor.abort();
            }
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
            SketchUtils.close(inputStream);
            SketchUtils.close(outputStream);
        }

        if (diskCacheEditor != null) {
            DiskCache.Entry cacheEntry = diskCache.get(diskCacheKey);
            if (cacheEntry != null) {
                return new PreProcessResult(cacheEntry, ImageFrom.LOCAL);
            } else {
                if (SLogType.REQUEST.isEnabled()) {
                    SLog.fw(SLogType.REQUEST, LOG_NAME, "not found xpk icon cache file. %s", uriInfo.getUri());
                }
                return null;
            }
        } else {
            return new PreProcessResult(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.LOCAL);
        }
    }
}
