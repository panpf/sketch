package me.panpf.sketch.sample.util

import android.content.Context
import android.content.res.AssetManager
import android.graphics.*
import android.preference.PreferenceManager
import me.xiaopan.sketch.uri.AssetUriModel
import me.xiaopan.sketch.util.ExifInterface
import me.xiaopan.sketch.util.SketchUtils
import me.panpf.sketch.sample.AssetImage
import java.io.*

class ImageOrientationCorrectTestFileGenerator {

    private var files: Array<Config>? = null
    private var assetManager: AssetManager? = null

    private fun init(context: Context) {
        if (files != null) {
            return
        }

        if (assetManager == null) {
            assetManager = context.assets
        }

        val changed = isChanged(context)
        if (changed) {
            updateVersion(context)
        }

        var externalFilesDir = context.getExternalFilesDir(null)
        if (externalFilesDir == null) {
            externalFilesDir = context.filesDir
        }
        val dirPath = externalFilesDir!!.path + File.separator + "TEST_ORIENTATION"

        val filesList = arrayListOf<Config>()
        for (w in configs.indices) {
            val elements = configs[w].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val filePath = String.format("%s%s%s", dirPath, File.separator, elements[0])
            filesList += Config(filePath, Integer.parseInt(elements[1]), Integer.parseInt(elements[2]), Integer.parseInt(elements[3]))
        }
        files = filesList.toTypedArray()

        if (changed) {
            val dir = File(dirPath)
            if (dir.exists()) {
                SketchUtils.cleanDir(dir)
            }
        }
    }

    private fun isChanged(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(TAG_VERSION, 0) != VERSION
    }

    private fun updateVersion(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(TAG_VERSION, VERSION).apply()
    }

    val filePaths: Array<String>
        get() {
            val filePaths = arrayListOf<String>()
            for (w in configs.indices) {
                filePaths += files!![w].filePath
            }
            return filePaths.toTypedArray()
        }

    fun onAppStart() {
        Thread(Runnable {
            val options = BitmapFactory.Options()
            options.inSampleSize = 4

            val inputStream: InputStream
            try {
                inputStream = assetManager!!.open(AssetUriModel().getUriContent(AssetImage.MEI_NV))
            } catch (e: IOException) {
                e.printStackTrace()
                return@Runnable
            }

            val sourceBitmap = BitmapFactory.decodeStream(inputStream, null, options)
            SketchUtils.close(inputStream)

            for (config in files!!) {
                val file = File(config.filePath)
                generatorTestFile(file, sourceBitmap, config.degrees, config.xScale, config.orientation)
            }

            sourceBitmap.recycle()
        }).start()
    }

    private fun generatorTestFile(file: File, sourceBitmap: Bitmap, rotateDegrees: Int, xScale: Int, orientation: Int) {
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
        var config: Bitmap.Config? = if (sourceBitmap.config != null) sourceBitmap.config else null
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

    private class Config(internal var filePath: String, internal var degrees: Int, internal var xScale: Int, internal var orientation: Int)

    companion object {

        private val instance = ImageOrientationCorrectTestFileGenerator()

        private val VERSION = 5
        private val configs = arrayOf(
                String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_ROTATE_90", VERSION, -90, 1, ExifInterface.ORIENTATION_ROTATE_90),
                String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_ROTATE_180", VERSION, -180, 1, ExifInterface.ORIENTATION_ROTATE_180),
                String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_ROTATE_270", VERSION, -270, 1, ExifInterface.ORIENTATION_ROTATE_270),
                String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_FLIP_HORIZONTAL", VERSION, 0, -1, ExifInterface.ORIENTATION_FLIP_HORIZONTAL),
                String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_TRANSPOSE", VERSION, -90, -1, ExifInterface.ORIENTATION_TRANSPOSE),
                String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_FLIP_VERTICAL", VERSION, -180, -1, ExifInterface.ORIENTATION_FLIP_VERTICAL),
                String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_TRANSVERSE", VERSION, -270, -1, ExifInterface.ORIENTATION_TRANSVERSE))
        private val TAG_VERSION = "TAG_VERSION"

        fun getInstance(context: Context): ImageOrientationCorrectTestFileGenerator {
            instance.init(context)
            return instance
        }
    }
}
