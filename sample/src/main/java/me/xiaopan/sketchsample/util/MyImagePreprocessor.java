package me.xiaopan.sketchsample.util;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.feture.ImagePreprocessor;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 在继承ImagePreprocessor的基础上扩展了解析XPK文件的图标
 */
public class MyImagePreprocessor extends ImagePreprocessor {

    public MyImagePreprocessor() {
        logName = "MyImagePreprocessor";
    }

    @Override
    public boolean isSpecific(LoadRequest loadRequest) {
        return super.isSpecific(loadRequest) || isXpkFile(loadRequest);
    }

    @Override
    public DiskCache.Entry getDiskCacheEntry(LoadRequest loadRequest) {
        if (isXpkFile(loadRequest)) {
            return getXpkIconCacheFile(loadRequest);
        }
        return super.getDiskCacheEntry(loadRequest);
    }

    private boolean isXpkFile(LoadRequest loadRequest) {
        return loadRequest.getAttrs().getUriScheme() == UriScheme.FILE
                && SketchUtils.checkSuffix(loadRequest.getAttrs().getRealUri(), ".xpk");
    }

    /**
     * 获取XPK图标的缓存文件
     */
    private DiskCache.Entry getXpkIconCacheFile(LoadRequest loadRequest) {
        String realUri = loadRequest.getAttrs().getRealUri();
        Configuration configuration = loadRequest.getSketch().getConfiguration();

        File xpkFile = new File(realUri);
        if (!xpkFile.exists()) {
            return null;
        }
        long lastModifyTime = xpkFile.lastModified();
        String diskCacheKey = realUri + "." + lastModifyTime;

        ReentrantLock lock = configuration.getDiskCache().getEditorLock(diskCacheKey);
        lock.lock();

        DiskCache.Entry diskCacheEntry = readXpkIcon(configuration, loadRequest, diskCacheKey, realUri);

        lock.unlock();

        return diskCacheEntry;
    }

    private DiskCache.Entry readXpkIcon(Configuration configuration, LoadRequest loadRequest, String diskCacheKey, String realUri){
        DiskCache.Entry xpkIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (xpkIconDiskCacheEntry != null) {
            return xpkIconDiskCacheEntry;
        }

        DiskCache.Editor diskCacheEditor = configuration.getDiskCache().edit(diskCacheKey);
        if (diskCacheEditor == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName,
                        " - ", "disk cache disable",
                        " - ", loadRequest.getAttrs().getId()));
            }
            return null;
        }

        ZipFile zipFile;
        try {
            zipFile = new ZipFile(realUri);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                diskCacheEditor.abort();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return null;
        }
        InputStream inputStream;
        ZipEntry zipEntry = zipFile.getEntry("icon.png");
        if (zipEntry == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName,
                        " - ", "not found icon.png in ",
                        " - ", loadRequest.getAttrs().getId()));
            }
            try {
                diskCacheEditor.abort();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return null;
        }

        try {
            inputStream = zipFile.getInputStream(zipEntry);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                diskCacheEditor.abort();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return null;
        }

        BufferedOutputStream outputStream;
        try {
            outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                diskCacheEditor.abort();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            SketchUtils.close(inputStream);
            return null;
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
            diskCacheEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                diskCacheEditor.abort();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            SketchUtils.close(inputStream);
            SketchUtils.close(outputStream);
        }

        xpkIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (xpkIconDiskCacheEntry == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName,
                        " - ", "not found xpk icon cache file",
                        " - ", loadRequest.getAttrs().getId()));
            }
        }

        return xpkIconDiskCacheEntry;
    }
}
