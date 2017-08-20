package me.xiaopan.sketch.androidtest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import me.xiaopan.sketch.util.SketchUtils;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SketchUtilsTest {
    /**
     * Test SketchUtils.readApkIcon(Context, String, boolean, String, BitmapPool) method
     */
    @Test
    public void testReadApkIcon() {
        Context context = InstrumentationRegistry.getContext();
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return;
        }
        String apkPath = packageInfo.applicationInfo.sourceDir;

        if (!new File(apkPath).exists()) {
            Assert.fail("Backup test app failed. " + apkPath);
        }

        Bitmap highApkIconBitmap = SketchUtils.readApkIcon(context, apkPath, false, "testReadApkIcon", null);
        if (highApkIconBitmap == null) {
            Assert.fail("Read high apk icon result is null");
        }
        if (highApkIconBitmap.isRecycled()) {
            Assert.fail("High apk icon bitmap recycled");
        }

        Bitmap lowApkIconBitmap = SketchUtils.readApkIcon(context, apkPath, true, "testReadApkIcon", null);
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
