package me.xiaopan.sketch;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 默认的本地图片处理器，可解析APK文件的图标
 */
public class DefaultLocalImageProcessor implements LocalImageProcessor {
    private static final String NAME = "DefaultSpecificLocalImageProcessor";

    @Override
    public boolean isSpecific(LoadRequest loadRequest) {
        return isApkFile(loadRequest);
    }

    @Override
    public DiskCache.Entry getDiskCacheEntry(LoadRequest loadRequest) {
        if(isApkFile(loadRequest)){
            return getApkIconCacheFile(loadRequest);
        }
        return null;
    }

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }

    private boolean isApkFile(LoadRequest loadRequest){
        return loadRequest.getUriScheme() == UriScheme.FILE && SketchUtils.checkSuffix(loadRequest.getUri(), ".apk");
    }

    /**
     * 获取APK图标的缓存文件
     *
     * @return APK图标的缓存文件
     */
    private DiskCache.Entry getApkIconCacheFile(LoadRequest loadRequest) {
        String uri = loadRequest.getUri();
        Configuration configuration = loadRequest.getSketch().getConfiguration();

        File apkFile = new File(uri);
        if (!apkFile.exists()) {
            return null;
        }
        long lastModifyTime = apkFile.lastModified();
        String diskCacheKey = uri + "." + lastModifyTime;

        DiskCache.Entry apkIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (apkIconDiskCacheEntry != null) {
            return apkIconDiskCacheEntry;
        }

        Bitmap iconBitmap = SketchUtils.decodeIconFromApk(configuration.getContext(), uri, loadRequest.isLowQualityImage(), NAME);
        if (iconBitmap == null) {
            return null;
        }

        if (iconBitmap.isRecycled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "apk icon bitmap recycled", " - ", uri));
            }
            return null;
        }

        DiskLruCache.Editor editor = configuration.getDiskCache().edit(diskCacheKey);
        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(editor.newOutputStream(0), 8 * 1024);
            iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            SketchUtils.close(outputStream);
        }

        apkIconDiskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
        if (apkIconDiskCacheEntry == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "not found apk icon cache file", " - ", uri));
            }
        }

        return apkIconDiskCacheEntry;
    }
}
