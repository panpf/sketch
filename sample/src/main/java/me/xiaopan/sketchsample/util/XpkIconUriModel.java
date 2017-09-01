package me.xiaopan.sketchsample.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

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
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.datasource.ByteArrayDataSource;
import me.xiaopan.sketch.datasource.DataSource;
import me.xiaopan.sketch.datasource.DiskCacheDataSource;
import me.xiaopan.sketch.request.DownloadResult;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.uri.UriModel;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

public class XpkIconUriModel extends UriModel {

    public static final String SCHEME = "xpk.icon://";
    private static final String NAME = "XpkIconUriModel";

    public static String makeUri(String filePath) {
        return SCHEME + filePath;
    }

    @Override
    protected boolean match(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    /**
     * 获取 uri 所真正包含的内容部分，例如 "xpk.icon:///sdcard/test.xpk"，就会返回 "/sdcard/test.xpk"
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，例如 "xpk.icon:///sdcard/test.xpk"，就会返回 "/sdcard/test.xpk"
     */
    @Override
    public String getUriContent(@NonNull String uri) {
        return match(uri) ? uri.substring(SCHEME.length()) : uri;
    }

    @Override
    public DataSource getDataSource(@NonNull Context context, @NonNull String uri, DownloadResult downloadResult) {
        // TODO: 2017/9/1 这里的磁盘缓存key，想办法改一下
        String filePath = getUriContent(uri);
        File xpkFile = new File(filePath);
        if (!xpkFile.exists()) {
            return null;
        }
        long lastModifyTime = xpkFile.lastModified();
        String diskCacheKey = filePath + "." + lastModifyTime;

        DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();

        DiskCache.Entry cacheEntry = diskCache.get(diskCacheKey);
        if (cacheEntry != null) {
            return new DiskCacheDataSource(cacheEntry, ImageFrom.DISK_CACHE);
        }

        ReentrantLock diskCacheEditLock = diskCache.getEditLock(diskCacheKey);
        diskCacheEditLock.lock();

        DataSource dataSource;
        try {
            cacheEntry = diskCache.get(diskCacheKey);
            if (cacheEntry != null) {
                dataSource = new DiskCacheDataSource(cacheEntry, ImageFrom.DISK_CACHE);
            } else {
                dataSource = readXpkIcon(xpkFile, uri, diskCache, diskCacheKey);
            }
        } finally {
            diskCacheEditLock.unlock();
        }

        return dataSource;
    }

    private DataSource readXpkIcon(File file, String uri, DiskCache diskCache, String diskCacheKey) {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        InputStream inputStream;
        ZipEntry zipEntry = zipFile.getEntry("icon.png");
        if (zipEntry == null) {
            SLog.e(NAME, "Not found icon.png in xpk file. %s", uri);
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
                return new DiskCacheDataSource(cacheEntry, ImageFrom.LOCAL);
            } else {
                SLog.e(NAME, "Not found xpk icon cache file. %s", uri);
                return null;
            }
        } else {
            return new ByteArrayDataSource(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.LOCAL);
        }
    }
}
