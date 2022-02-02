package com.github.panpf.sketch.sample.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExifOrientationTestFileHelper(val context: Context) {

    class TestFile(val title: String, val file: File, val exifOrientation: Int)

    private class Config(
        val title: String,
        val fileName: String,
        val degrees: Int,
        val xScale: Int,
        val orientation: Int
    )

    fun files(): List<TestFile> {
        val testFilesDir =
            File(context.getExternalFilesDir(null) ?: context.filesDir, "exif_files")

        val configs = arrayOf(
            Config("ROTATE_90", "ROTATE_90.jpeg", -90, 1, ExifInterface.ORIENTATION_ROTATE_90),
            Config("TRANSPOSE", "TRANSPOSE.jpeg", -90, -1, ExifInterface.ORIENTATION_TRANSPOSE),
            Config("ROTATE_180", "ROTATE_180.jpeg", -180, 1, ExifInterface.ORIENTATION_ROTATE_180),
            Config("FLIP_VER", "FLIP_VER.jpeg", -180, -1, ExifInterface.ORIENTATION_FLIP_VERTICAL),
            Config("ROTATE_270", "ROTATE_270.jpeg", -270, 1, ExifInterface.ORIENTATION_ROTATE_270),
            Config("TRANSVERSE", "TRANSVERSE.jpeg", -270, -1, ExifInterface.ORIENTATION_TRANSVERSE),
            Config("FLIP_HOR", "FLIP_HOR.jpeg", 0, -1, ExifInterface.ORIENTATION_FLIP_HORIZONTAL),
        )
        val needReset = configs.any { !File(testFilesDir, it.fileName).exists() }
        if (needReset) {
            testFilesDir.deleteRecursively()
            testFilesDir.mkdirs()
            val originBitmap = context.assets.open("exif_origin.jpeg").use {
                BitmapFactory.decodeStream(it)
            }!!
            for (config in configs) {
                val file = File(testFilesDir, config.fileName)
                if (!file.exists()) {
                    generatorTestFile(
                        file = file,
                        sourceBitmap = originBitmap,
                        rotateDegrees = config.degrees,
                        xScale = config.xScale,
                        orientation = config.orientation
                    )
                }
            }
            originBitmap.recycle()
        }

        return configs.map {
            TestFile(it.title, File(testFilesDir, it.fileName), it.orientation)
        }
    }

    private fun generatorTestFile(
        file: File,
        sourceBitmap: Bitmap,
        rotateDegrees: Int,
        xScale: Int,
        orientation: Int
    ) {
        val newBitmap = transformBitmap(sourceBitmap, rotateDegrees, xScale)
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

    private fun transformBitmap(inBitmap: Bitmap, degrees: Int, xScale: Int): Bitmap {
        val matrix = Matrix().apply {
            setScale(xScale.toFloat(), 1f)
            postRotate(degrees.toFloat())
        }

        val newRect = RectF(0f, 0f, inBitmap.width.toFloat(), inBitmap.height.toFloat())
        matrix.mapRect(newRect)
        matrix.postTranslate(-newRect.left, -newRect.top)

        val config: Bitmap.Config = inBitmap.config ?: Bitmap.Config.ARGB_8888
        val result = Bitmap.createBitmap(newRect.width().toInt(), newRect.height().toInt(), config)

        val canvas = Canvas(result)
        val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(inBitmap, matrix, paint)
        return result
    }
}
