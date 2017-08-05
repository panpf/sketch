package me.xiaopan.sketch.androidtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.sketch.util.SketchUtils;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SketchUtilsTest {
    @Test
    public void testReadApkIcon() {
        Context context = InstrumentationRegistry.getContext();
        File backupFile = new File(SketchUtils.getAppCacheDir(context), "test_app.apk");
        if (backupFile.exists()) {
            backupFile.delete();
        }

        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("jiekuan.apk");
            TestUitls.copyFile(inputStream, backupFile);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Not found asset resource 'jiekuan.apk'. " + e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!backupFile.exists()) {
            Assert.fail("Backup test app failed. " + backupFile.getPath());
        }

        Bitmap highApkIconBitmap = SketchUtils.readApkIcon(context, backupFile.getPath(), false, "testReadApkIcon", null);
        if (highApkIconBitmap == null) {
            Assert.fail("Read high apk icon result is null");
        }
        if (highApkIconBitmap.isRecycled()) {
            Assert.fail("High apk icon bitmap recycled");
        }

        Bitmap lowApkIconBitmap = SketchUtils.readApkIcon(context, backupFile.getPath(), true, "testReadApkIcon", null);
        if (lowApkIconBitmap == null) {
            Assert.fail("Read low apk icon result is null");
        }
        if (lowApkIconBitmap.isRecycled()) {
            Assert.fail("Low apk icon bitmap recycled");
        }

        int highByeCount = SketchUtils.getByteCount(highApkIconBitmap);
        int lowByeCount = SketchUtils.getByteCount(lowApkIconBitmap);

        highApkIconBitmap.recycle();
        lowApkIconBitmap.recycle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (highByeCount != lowByeCount) {
                Assert.fail("lowQualityImage attr invalid");
            }
        } else {
            if (highByeCount <= lowByeCount) {
                Assert.fail("lowQualityImage attr invalid");
            }
        }
    }
}
