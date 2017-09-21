package me.xiaopan.sketchsample.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.uri.AbsStreamDiskCacheUriModel;
import me.xiaopan.sketch.uri.GetDataSourceException;
import me.xiaopan.sketch.util.SketchUtils;

public class XpkIconUriModel extends AbsStreamDiskCacheUriModel {

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
    @NonNull
    @Override
    public String getUriContent(@NonNull String uri) {
        return match(uri) ? uri.substring(SCHEME.length()) : uri;
    }

    @NonNull
    @Override
    public String getDiskCacheKey(@NonNull String uri) {
        return SketchUtils.createFileUriDiskCacheKey(uri, getUriContent(uri));
    }

    @NonNull
    @Override
    protected InputStream getContent(@NonNull Context context, @NonNull String uri) throws GetDataSourceException {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(new File(getUriContent(uri)));
        } catch (IOException e) {
            String cause = String.format("Unable open xpk file. %s", uri);
            SLog.e(NAME, e, cause);
            throw new GetDataSourceException(cause, e);
        }

        ZipEntry zipEntry = zipFile.getEntry("icon.png");
        if (zipEntry == null) {
            String cause = String.format("Not found icon.png in xpk file. %s", uri);
            SLog.e(NAME, cause);
            throw new GetDataSourceException(cause);
        }

        try {
            return zipFile.getInputStream(zipEntry);
        } catch (IOException e) {
            String cause = String.format("Open \"icon.png\" input stream exception. %s", uri);
            SLog.e(NAME, e, cause);
            throw new GetDataSourceException(cause, e);
        }
    }
}
