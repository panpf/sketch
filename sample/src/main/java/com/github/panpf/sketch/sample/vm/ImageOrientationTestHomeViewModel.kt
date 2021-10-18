package com.github.panpf.sketch.sample.vm

import android.app.Application
import android.content.Context
import android.graphics.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.github.panpf.sketch.sample.AssetImage
import com.github.panpf.sketch.sample.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.bean.ImageOrientationTestPage
import com.github.panpf.sketch.uri.AssetUriModel
import com.github.panpf.sketch.util.ExifInterface
import com.github.panpf.sketch.util.SketchUtils
import java.io.*

class ImageOrientationTestHomeViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val listData = MutableLiveData<List<ImageOrientationTestPage>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            listData.postValue(ImageOrientationTestFileHelper(application1).getDatas())
        }
    }

    class ImageOrientationTestFileHelper(val context: Context) {

        private class Config(
            val title: String,
            val fileName: String,
            val degrees: Int,
            val xScale: Int,
            val orientation: Int
        )

        private val configs = arrayOf(
            Config("ROTATE_90", "ROTATE_90.jpg", -90, 1, ExifInterface.ORIENTATION_ROTATE_90),
            Config("ROTATE_180", "ROTATE_180.jpg", -180, 1, ExifInterface.ORIENTATION_ROTATE_180),
            Config("ROTATE_270", "ROTATE_270.jpg", -270, 1, ExifInterface.ORIENTATION_ROTATE_270),
            Config("FLIP_HOR", "FLIP_HOR.jpg", 0, -1, ExifInterface.ORIENTATION_FLIP_HORIZONTAL),
            Config("TRANSPOSE", "TRANSPOSE.jpg", -90, -1, ExifInterface.ORIENTATION_TRANSPOSE),
            Config("FLIP_VER", "FLIP_VER.jpg", -180, -1, ExifInterface.ORIENTATION_FLIP_VERTICAL),
            Config("TRANSVERSE", "TRANSVERSE.jpg", -270, -1, ExifInterface.ORIENTATION_TRANSVERSE)
        )

        fun getDatas(): List<ImageOrientationTestPage> {
            val filesDir = context.getExternalFilesDir(null) ?: context.filesDir
            val testFilesDir = File(filesDir, "TEST_ORIENTATION")

            val needReset = configs.any { !File(testFilesDir, it.fileName).exists() }
            if (needReset) {
                SketchUtils.cleanDir(testFilesDir)
                generateTestFiles(testFilesDir)
            }

            return configs.map {
                ImageOrientationTestPage(
                    it.title,
                    File(testFilesDir, it.fileName).path
                )
            }
        }

        private fun generateTestFiles(testFilesDir: File) {
            val options = BitmapFactory.Options()
            options.inSampleSize = 4

            val inputStream: InputStream
            try {
                inputStream = context.assets.open(AssetUriModel().getUriContent(AssetImage.MEI_NV))
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            val sourceBitmap = inputStream.use {
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            if (sourceBitmap != null) {
                for (config in configs) {
                    val file = File(testFilesDir, config.fileName)
                    generatorTestFile(
                        file,
                        sourceBitmap,
                        config.degrees,
                        config.xScale,
                        config.orientation
                    )
                }

                sourceBitmap.recycle()
            }
        }

        private fun generatorTestFile(
            file: File,
            sourceBitmap: Bitmap,
            rotateDegrees: Int,
            xScale: Int,
            orientation: Int
        ) {
            if (file.exists()) {
                return
            }

            val newBitmap = transformBitmap(sourceBitmap, rotateDegrees, xScale)
            if (newBitmap == null || newBitmap.isRecycled) {
                return
            }

            file.parentFile.mkdirs()

            val outputStream: FileOutputStream
            try {
                outputStream = FileOutputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                newBitmap.recycle()
                return
            }

            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            SketchUtils.close(outputStream)
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

        private fun transformBitmap(sourceBitmap: Bitmap, degrees: Int, xScale: Int): Bitmap? {
            val matrix = Matrix()
            matrix.setScale(xScale.toFloat(), 1f)
            matrix.postRotate(degrees.toFloat())

            // 根据旋转角度计算新的图片的尺寸
            val newRect = RectF(0f, 0f, sourceBitmap.width.toFloat(), sourceBitmap.height.toFloat())
            matrix.mapRect(newRect)
            val newWidth = newRect.width().toInt()
            val newHeight = newRect.height().toInt()

            // 角度不能整除90°时新图片会是斜的，因此要支持透明度，这样倾斜导致露出的部分就不会是黑的
            var config: Bitmap.Config? =
                if (sourceBitmap.config != null) sourceBitmap.config else null
            if (degrees % 90 != 0 && config != Bitmap.Config.ARGB_8888) {
                config = Bitmap.Config.ARGB_8888
            }

            val result = Bitmap.createBitmap(newWidth, newHeight, config!!)

            matrix.postTranslate(-newRect.left, -newRect.top)

            val canvas = Canvas(result)
            val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(sourceBitmap, matrix, paint)

            return result
        }
    }
}