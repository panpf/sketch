/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.sketch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExifOrientationTestFileHelper constructor(
    val context: Context,
    private val assetFileName: String,
    private val inSampleSize: Int = 1
) {

    private val cacheDir: File = File(
        context.getExternalFilesDir(null) ?: context.filesDir,
        "exif_files" + "/" + File(assetFileName).nameWithoutExtension + "_${inSampleSize}"
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
                BitmapFactory.decodeStream(it, null, Options().apply {
                    inSampleSize = this@ExifOrientationTestFileHelper.inSampleSize
                })
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
        val newBitmap = ExifOrientationHelper(orientation).addToBitmap(
            sourceBitmap,
//            context.sketch.bitmapPool,
            false
        ) ?: sourceBitmap
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

    class TestFile(val title: String, val file: File, @Suppress("unused") val exifOrientation: Int)
}
