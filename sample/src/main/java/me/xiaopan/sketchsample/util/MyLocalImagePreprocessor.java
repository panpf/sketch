package me.xiaopan.sketchsample.util;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.feture.LocalImagePreprocessor;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 在继承LocalImagePreprocessor的基础上扩展了解析XPK文件的图标
 */
public class MyLocalImagePreprocessor extends LocalImagePreprocessor {
    private static final String NAME = "MyLocalImagePreprocessor";

    @Override
    public boolean isSpecific(LoadRequest loadRequest) {
        return super.isSpecific(loadRequest) || isXpkFile(loadRequest);
    }

    @Override
    public DiskCache.Entry getDiskCacheEntry(LoadRequest loadRequest) {
        if(isXpkFile(loadRequest)){
            return getXpkIconCacheFile(loadRequest);
        }
        return super.getDiskCacheEntry(loadRequest);
    }

    private boolean isXpkFile(LoadRequest loadRequest) {
        return loadRequest.getAttrs().getUriScheme() == UriScheme.FILE && SketchUtils.checkSuffix(loadRequest.getAttrs().getRealUri(), ".xpk");
    }

    /**
     * 获取XPK图标的缓存文件
     */
    private DiskCache.Entry getXpkIconCacheFile(LoadRequest loadRequest) {
        String realUri = loadRequest.getAttrs().getRealUri();
        Configuration configuration = loadRequest.getAttrs().getSketch().getConfiguration();

        File apkFile = new File(realUri);
        if (!apkFile.exists()) {
            return null;
        }
        long lastModifyTime = apkFile.lastModified();
        String diskCacheKey = realUri + "." + lastModifyTime;

        DiskCache.Entry xpkIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (xpkIconDiskCacheEntry != null) {
            return xpkIconDiskCacheEntry;
        }

        ZipFile zipFile;
        try {
            zipFile = new ZipFile(realUri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        InputStream inputStream;
        ZipEntry zipEntry = zipFile.getEntry("icon.png");
        if(zipEntry == null){
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "not found icon.png in ", realUri));
            }
            return null;
        }

        try {
            inputStream = zipFile.getInputStream(zipEntry);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        DiskLruCache.Editor editor = configuration.getDiskCache().edit(diskCacheKey);
        BufferedOutputStream outputStream;
        try {
            outputStream = new BufferedOutputStream(editor.newOutputStream(0), 8 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException e1) {
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
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException e1) {
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
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "not found xpk icon cache file", " - ", realUri));
            }
        }

        return xpkIconDiskCacheEntry;
    }
}
