package com.github.panpf.sketch.test.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExifOrientationTestFileHelper(val context: Context, val assetFileName: String) {

    private val cacheDir: File = File(
        context.getExternalFilesDir(null) ?: context.filesDir,
        "exif_files" + "/" + File(assetFileName).nameWithoutExtension
    )
    private val configs = arrayOf(
        Config("ROTATE_90", ExifInterface.ORIENTATION_ROTATE_90, cacheDir),
        Config("TRANSVERSE", ExifInterface.ORIENTATION_TRANSVERSE, cacheDir),
        Config("ROTATE_180", ExifInterface.ORIENTATION_ROTATE_180, cacheDir),
        Config("FLIP_VER", ExifInterface.ORIENTATION_FLIP_VERTICAL, cacheDir),
        Config("ROTATE_270", ExifInterface.ORIENTATION_ROTATE_270, cacheDir),
        Config("TRANSPOSE", ExifInterface.ORIENTATION_TRANSPOSE, cacheDir),
        Config("FLIP_HOR", ExifInterface.ORIENTATION_FLIP_HORIZONTAL, cacheDir),
    )

    fun files(): List<TestFile> {
        val needReset = configs.any { !it.file.exists() }
        if (needReset) {
            cacheDir.deleteRecursively()
            cacheDir.mkdirs()
            val originBitmap = context.assets.open(assetFileName).use {
                BitmapFactory.decodeStream(it)
            }!!
            for (config in configs) {
                val file = config.file
                if (!file.exists()) {
                    generatorTestFile(
                        file = file,
                        sourceBitmap = originBitmap,
                        orientation = config.orientation
                    )
                }
            }
            originBitmap.recycle()
        }

        return configs.map {
            TestFile(it.name, it.file, it.orientation)
        }
    }

    private fun generatorTestFile(
        file: File,
        sourceBitmap: Bitmap,
        orientation: Int
    ) {
        val newBitmap =
            ExifOrientationHelper(orientation).addToBitmap(sourceBitmap) ?: sourceBitmap
        FileOutputStream(file).use {
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        newBitmap.recycle()

        val exifInterface: ExifInterface
        try {
            exifInterface = ExifInterface(file.path)
        } catch (e: IOException) {
            e.printStackTrace()
            file.delete()
            return
        }

        exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, orientation.toString())
        try {
            exifInterface.saveAttributes()
        } catch (e: IOException) {
            e.printStackTrace()
            file.delete()
        }
    }

    private class Config(
        val name: String,
        val orientation: Int,
        cacheDir: File,
    ) {
        val file = File(cacheDir, "${name}.jpeg")
    }

    class TestFile(val title: String, val file: File, val exifOrientation: Int)
}