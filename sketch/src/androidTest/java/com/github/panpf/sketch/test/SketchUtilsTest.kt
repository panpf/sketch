package com.github.panpf.sketch.test

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.util.SketchUtils.Companion.cleanDir
import com.github.panpf.sketch.util.SketchUtils.Companion.deleteFile
import com.github.panpf.sketch.util.SketchUtils.Companion.drawableToBitmap
import com.github.panpf.sketch.util.SketchUtils.Companion.getByteCount
import com.github.panpf.sketch.util.SketchUtils.Companion.readApkIcon
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SketchUtilsTest {
    @Test
    fun testReadApkIcon() {
        val context = InstrumentationRegistry.getContext()
        val packageInfo: PackageInfo = try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            Assert.fail(e.message)
            return
        }
        val apkPath = packageInfo.applicationInfo.sourceDir
        if (!File(apkPath).exists()) {
            Assert.fail("Backup test app failed. $apkPath")
        }
        val highQualityApkIconBitmap = readApkIcon(context, apkPath, false, "testReadApkIcon", null)
        if (highQualityApkIconBitmap == null) {
            Assert.fail("Read high quality apk icon result is null")
        }
        if (highQualityApkIconBitmap!!.isRecycled) {
            Assert.fail("High quality apk icon bitmap recycled")
        }
        val lowQualityApkIconBitmap = readApkIcon(context, apkPath, true, "testReadApkIcon", null)
        if (lowQualityApkIconBitmap == null) {
            highQualityApkIconBitmap.recycle()
            Assert.fail("Read low quality apk icon result is null")
        }
        if (lowQualityApkIconBitmap!!.isRecycled) {
            highQualityApkIconBitmap.recycle()
            Assert.fail("Low quality apk icon bitmap recycled")
        }
        val highQualityByteCount = getByteCount(highQualityApkIconBitmap)
        val lowQualityByteCount = getByteCount(lowQualityApkIconBitmap)
        highQualityApkIconBitmap.recycle()
        lowQualityApkIconBitmap.recycle()

        // KITKAT 以上不再支持 ARGB4444，因此 lowQualityImage 参数应该无效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (highQualityByteCount != lowQualityByteCount) {
                Assert.fail("lowQualityImage attr invalid")
            }
        } else {
            if (highQualityByteCount <= lowQualityByteCount) {
                Assert.fail("lowQualityImage attr invalid")
            }
        }
    }

    @Test
    fun testDrawableToBitmap() {
        val context = InstrumentationRegistry.getContext()
        val drawable = context.resources.getDrawable(R.drawable.shape_round_rect)
        val highQualityApkIconBitmap = drawableToBitmap(drawable, false, null)
        if (highQualityApkIconBitmap == null) {
            Assert.fail("Read quality high apk icon result is null")
        }
        if (highQualityApkIconBitmap!!.isRecycled) {
            Assert.fail("High quality apk icon bitmap recycled")
        }
        val lowQualityApkIconBitmap = drawableToBitmap(drawable, true, null)
        if (lowQualityApkIconBitmap == null) {
            highQualityApkIconBitmap.recycle()
            Assert.fail("Read low quality apk icon result is null")
        }
        if (lowQualityApkIconBitmap!!.isRecycled) {
            highQualityApkIconBitmap.recycle()
            Assert.fail("Low quality apk icon bitmap recycled")
        }
        val highQualityByteCount = getByteCount(highQualityApkIconBitmap)
        val lowQualityByteCount = getByteCount(lowQualityApkIconBitmap)
        highQualityApkIconBitmap.recycle()
        lowQualityApkIconBitmap.recycle()

        // KITKAT 以上不再支持 ARGB4444，因此 lowQualityImage 参数应该无效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (highQualityByteCount != lowQualityByteCount) {
                Assert.fail("lowQualityImage attr invalid")
            }
        } else {
            if (highQualityByteCount <= lowQualityByteCount) {
                Assert.fail("lowQualityImage attr invalid")
            }
        }
    }

    @Test
    fun testCleanDir() {
        val context = InstrumentationRegistry.getContext()
        val filesDir = context.filesDir

        /*
            files/
                test/
                    testFile1.temp
                    childDir1/
                        testFile2.temp
         */
        val testDir = File(filesDir, "test")
        val testFile1 = File(testDir, "testFile1.temp")
        val childDir1 = File(testDir, "childDir1")
        val testFile2 = File(childDir1, "testFile2.temp")
        testDir.mkdirs()
        if (!testDir.exists()) {
            Assert.fail("Create dir failed. " + testDir.path)
        }
        try {
            testFile1.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (!testFile1.exists()) {
            Assert.fail("Create file failed. " + testFile1.path)
        }
        childDir1.mkdirs()
        if (!childDir1.exists()) {
            Assert.fail("Create dir failed. " + childDir1.path)
        }
        try {
            testFile2.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (!testFile2.exists()) {
            Assert.fail("Create file failed. " + testFile2.path)
        }
        cleanDir(testDir)
        if (testFile2.exists()) {
            Assert.fail("Clean failed. " + testFile2.path)
        }
        if (childDir1.exists()) {
            Assert.fail("Clean failed. " + childDir1.path)
        }
        if (testFile1.exists()) {
            Assert.fail("Clean failed. " + testFile1.path)
        }
        if (!testDir.exists()) {
            Assert.fail("Root dir deleted. " + testDir.path)
        }
        val childFiles = testDir.listFiles()
        if (childFiles != null && childFiles.size > 0) {
            Assert.fail("Clean failed. " + testFile1.path)
        }
    }

    @Test
    fun testDeleteFile() {
        val context = InstrumentationRegistry.getContext()
        val filesDir = context.filesDir

        /*
            files/
                test/
                    testFile1.temp
                    childDir1/
                        testFile2.temp
         */
        val testDir = File(filesDir, "test")
        val testFile1 = File(testDir, "testFile1.temp")
        val childDir1 = File(testDir, "childDir1")
        val testFile2 = File(childDir1, "testFile2.temp")
        testDir.mkdirs()
        if (!testDir.exists()) {
            Assert.fail("Create dir failed. " + testDir.path)
        }
        try {
            testFile1.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (!testFile1.exists()) {
            Assert.fail("Create file failed. " + testFile1.path)
        }
        childDir1.mkdirs()
        if (!childDir1.exists()) {
            Assert.fail("Create dir failed. " + childDir1.path)
        }
        try {
            testFile2.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (!testFile2.exists()) {
            Assert.fail("Create file failed. " + testFile2.path)
        }
        deleteFile(testDir)
        if (testFile2.exists()) {
            Assert.fail("Delete failed. " + testFile2.path)
        }
        if (childDir1.exists()) {
            Assert.fail("Delete failed. " + childDir1.path)
        }
        if (testFile1.exists()) {
            Assert.fail("Delete failed. " + testFile1.path)
        }
        if (testDir.exists()) {
            Assert.fail("Delete failed. " + testDir.path)
        }
        val testFile = File(filesDir, "test.temp")
        try {
            testFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (!testFile.exists()) {
            Assert.fail("Create file failed. " + testFile.path)
        }
        deleteFile(testFile)
        if (testFile.exists()) {
            Assert.fail("Delete failed. " + testFile.path)
        }
    }
}