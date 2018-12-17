package me.panpf.sketch.test;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import me.panpf.sketch.util.SketchUtils;

@RunWith(AndroidJUnit4.class)
public class SketchUtilsTest {
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

        Bitmap highQualityApkIconBitmap = SketchUtils.readApkIcon(context, apkPath, false, "testReadApkIcon", null);
        if (highQualityApkIconBitmap == null) {
            Assert.fail("Read high quality apk icon result is null");
        }
        if (highQualityApkIconBitmap.isRecycled()) {
            Assert.fail("High quality apk icon bitmap recycled");
        }

        Bitmap lowQualityApkIconBitmap = SketchUtils.readApkIcon(context, apkPath, true, "testReadApkIcon", null);
        if (lowQualityApkIconBitmap == null) {
            highQualityApkIconBitmap.recycle();
            Assert.fail("Read low quality apk icon result is null");
        }
        if (lowQualityApkIconBitmap.isRecycled()) {
            highQualityApkIconBitmap.recycle();
            Assert.fail("Low quality apk icon bitmap recycled");
        }

        int highQualityByteCount = SketchUtils.getByteCount(highQualityApkIconBitmap);
        int lowQualityByteCount = SketchUtils.getByteCount(lowQualityApkIconBitmap);

        highQualityApkIconBitmap.recycle();
        lowQualityApkIconBitmap.recycle();

        // KITKAT 以上不再支持 ARGB4444，因此 lowQualityImage 参数应该无效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (highQualityByteCount != lowQualityByteCount) {
                Assert.fail("lowQualityImage attr invalid");
            }
        } else {
            if (highQualityByteCount <= lowQualityByteCount) {
                Assert.fail("lowQualityImage attr invalid");
            }
        }
    }

    @Test
    public void testDrawableToBitmap() {
        Context context = InstrumentationRegistry.getContext();
        //noinspection deprecation
        Drawable drawable = context.getResources().getDrawable(R.drawable.shape_round_rect);

        Bitmap highQualityApkIconBitmap = SketchUtils.drawableToBitmap(drawable, false, null);
        if (highQualityApkIconBitmap == null) {
            Assert.fail("Read quality high apk icon result is null");
        }
        if (highQualityApkIconBitmap.isRecycled()) {
            Assert.fail("High quality apk icon bitmap recycled");
        }

        Bitmap lowQualityApkIconBitmap = SketchUtils.drawableToBitmap(drawable, true, null);
        if (lowQualityApkIconBitmap == null) {
            highQualityApkIconBitmap.recycle();
            Assert.fail("Read low quality apk icon result is null");
        }
        if (lowQualityApkIconBitmap.isRecycled()) {
            highQualityApkIconBitmap.recycle();
            Assert.fail("Low quality apk icon bitmap recycled");
        }

        int highQualityByteCount = SketchUtils.getByteCount(highQualityApkIconBitmap);
        int lowQualityByteCount = SketchUtils.getByteCount(lowQualityApkIconBitmap);

        highQualityApkIconBitmap.recycle();
        lowQualityApkIconBitmap.recycle();

        // KITKAT 以上不再支持 ARGB4444，因此 lowQualityImage 参数应该无效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (highQualityByteCount != lowQualityByteCount) {
                Assert.fail("lowQualityImage attr invalid");
            }
        } else {
            if (highQualityByteCount <= lowQualityByteCount) {
                Assert.fail("lowQualityImage attr invalid");
            }
        }
    }

    @Test
    public void testCleanDir() {
        Context context = InstrumentationRegistry.getContext();
        File filesDir = context.getFilesDir();

        /*
            files/
                test/
                    testFile1.temp
                    childDir1/
                        testFile2.temp
         */
        File testDir = new File(filesDir, "test");
        File testFile1 = new File(testDir, "testFile1.temp");
        File childDir1 = new File(testDir, "childDir1");
        File testFile2 = new File(childDir1, "testFile2.temp");
        //noinspection ResultOfMethodCallIgnored
        testDir.mkdirs();
        if (!testDir.exists()) {
            Assert.fail("Create dir failed. " + testDir.getPath());
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            testFile1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!testFile1.exists()) {
            Assert.fail("Create file failed. " + testFile1.getPath());
        }

        //noinspection ResultOfMethodCallIgnored
        childDir1.mkdirs();
        if (!childDir1.exists()) {
            Assert.fail("Create dir failed. " + childDir1.getPath());
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            testFile2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!testFile2.exists()) {
            Assert.fail("Create file failed. " + testFile2.getPath());
        }

        SketchUtils.cleanDir(testDir);

        if (testFile2.exists()) {
            Assert.fail("Clean failed. " + testFile2.getPath());
        }

        if (childDir1.exists()) {
            Assert.fail("Clean failed. " + childDir1.getPath());
        }

        if (testFile1.exists()) {
            Assert.fail("Clean failed. " + testFile1.getPath());
        }

        if (!testDir.exists()) {
            Assert.fail("Root dir deleted. " + testDir.getPath());
        }

        File[] childFiles = testDir.listFiles();
        if (childFiles != null && childFiles.length > 0) {
            Assert.fail("Clean failed. " + testFile1.getPath());
        }
    }

    @Test
    public void testDeleteFile() {
        Context context = InstrumentationRegistry.getContext();
        File filesDir = context.getFilesDir();

        /*
            files/
                test/
                    testFile1.temp
                    childDir1/
                        testFile2.temp
         */
        File testDir = new File(filesDir, "test");
        File testFile1 = new File(testDir, "testFile1.temp");
        File childDir1 = new File(testDir, "childDir1");
        File testFile2 = new File(childDir1, "testFile2.temp");
        //noinspection ResultOfMethodCallIgnored
        testDir.mkdirs();
        if (!testDir.exists()) {
            Assert.fail("Create dir failed. " + testDir.getPath());
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            testFile1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!testFile1.exists()) {
            Assert.fail("Create file failed. " + testFile1.getPath());
        }

        //noinspection ResultOfMethodCallIgnored
        childDir1.mkdirs();
        if (!childDir1.exists()) {
            Assert.fail("Create dir failed. " + childDir1.getPath());
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            testFile2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!testFile2.exists()) {
            Assert.fail("Create file failed. " + testFile2.getPath());
        }

        SketchUtils.deleteFile(testDir);

        if (testFile2.exists()) {
            Assert.fail("Delete failed. " + testFile2.getPath());
        }

        if (childDir1.exists()) {
            Assert.fail("Delete failed. " + childDir1.getPath());
        }

        if (testFile1.exists()) {
            Assert.fail("Delete failed. " + testFile1.getPath());
        }

        if (testDir.exists()) {
            Assert.fail("Delete failed. " + testDir.getPath());
        }

        File testFile = new File(filesDir, "test.temp");
        try {
            //noinspection ResultOfMethodCallIgnored
            testFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!testFile.exists()) {
            Assert.fail("Create file failed. " + testFile.getPath());
        }

        SketchUtils.deleteFile(testFile);

        if (testFile.exists()) {
            Assert.fail("Delete failed. " + testFile.getPath());
        }
    }
}
