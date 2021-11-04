/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.drawable

import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.net.Uri
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.ImageAttrs
import com.github.panpf.sketch.decode.NotFoundGifLibraryException
import com.github.panpf.sketch.request.ImageFrom
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

class SketchGifFactory {
    companion object {

        @JvmStatic
        private var isExistGifLibraryValue = 0

        @JvmStatic
        fun isExistGifLibrary(): Boolean {
            if (isExistGifLibraryValue == 0) {
                synchronized(SketchGifFactory::class.java) {
                    if (isExistGifLibraryValue == 0) {
                        isExistGifLibraryValue = try {
                            // todo gif 改为显示添加的方式
                            Class.forName("com.github.panpf.sketch.gif.BuildConfig")
                            Class.forName("pl.droidsonroids.gif.GifDrawable")
                            1
                        } catch (e: ClassNotFoundException) {
                            e.printStackTrace()
                            -1
                        }
                    }
                }
            }
            return isExistGifLibraryValue == 1
        }

        @Throws(NotFoundGifLibraryException::class)
        @JvmStatic
        fun assetExistGifLibrary() {
            if (!isExistGifLibrary()) {
                throw NotFoundGifLibraryException()
            }
        }

        @Throws(IOException::class, NotFoundGifLibraryException::class)
        @JvmStatic
        fun createGifDrawable(
            key: String,
            uri: String,
            imageAttrs: ImageAttrs,
            imageFrom: ImageFrom,
            bitmapPool: BitmapPool,
            afd: AssetFileDescriptor
        ): SketchGifDrawable {
            assetExistGifLibrary()
            return SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, afd)
        }

        @Throws(IOException::class, NotFoundGifLibraryException::class)
        @JvmStatic
        fun createGifDrawable(
            key: String,
            uri: String,
            imageAttrs: ImageAttrs,
            imageFrom: ImageFrom,
            bitmapPool: BitmapPool?,
            assets: AssetManager,
            assetName: String
        ): SketchGifDrawable {
            assetExistGifLibrary()
            return SketchGifDrawableImpl(
                key,
                uri,
                imageAttrs,
                imageFrom,
                bitmapPool,
                assets,
                assetName
            )
        }

        @Throws(IOException::class, NotFoundGifLibraryException::class)
        @JvmStatic
        fun createGifDrawable(
            key: String,
            uri: String,
            imageAttrs: ImageAttrs,
            imageFrom: ImageFrom,
            bitmapPool: BitmapPool?,
            buffer: ByteBuffer
        ): SketchGifDrawable {
            assetExistGifLibrary()
            return SketchGifDrawableImpl(
                key,
                uri,
                imageAttrs,
                imageFrom,
                bitmapPool,
                buffer
            )
        }

        @Throws(IOException::class, NotFoundGifLibraryException::class)
        @JvmStatic
        fun createGifDrawable(
            key: String,
            uri: String,
            imageAttrs: ImageAttrs,
            imageFrom: ImageFrom,
            bitmapPool: BitmapPool?,
            bytes: ByteArray
        ): SketchGifDrawable {
            assetExistGifLibrary()
            return SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, bytes)
        }

        @Throws(IOException::class, NotFoundGifLibraryException::class)
        @JvmStatic
        fun createGifDrawable(
            key: String,
            uri: String,
            imageAttrs: ImageAttrs,
            imageFrom: ImageFrom,
            bitmapPool: BitmapPool?,
            fd: FileDescriptor
        ): SketchGifDrawable {
            assetExistGifLibrary()
            return SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, fd)
        }

        @Throws(IOException::class, NotFoundGifLibraryException::class)
        @JvmStatic
        fun createGifDrawable(
            key: String,
            uri: String,
            imageAttrs: ImageAttrs,
            imageFrom: ImageFrom,
            bitmapPool: BitmapPool?,
            file: File
        ): SketchGifDrawable {
            assetExistGifLibrary()
            return SketchGifDrawableImpl(key, uri, imageAttrs, imageFrom, bitmapPool, file)
        }

        @Throws(IOException::class, NotFoundGifLibraryException::class)
        @JvmStatic
        fun createGifDrawable(
            key: String,
            uri: String,
            imageAttrs: ImageAttrs,
            imageFrom: ImageFrom,
            bitmapPool: BitmapPool?,
            filePath: String
        ): SketchGifDrawable {
            assetExistGifLibrary()
            return SketchGifDrawableImpl(
                key,
                uri,
                imageAttrs,
                imageFrom,
                bitmapPool,
                filePath
            )
        }

        @Throws(NotFoundException::class, IOException::class, NotFoundGifLibraryException::class)
        @JvmStatic
        fun createGifDrawable(
            key: String,
            uri: String,
            imageAttrs: ImageAttrs,
            imageFrom: ImageFrom,
            bitmapPool: BitmapPool?,
            res: Resources,
            id: Int
        ): SketchGifDrawable {
            assetExistGifLibrary()
            return SketchGifDrawableImpl(
                key,
                uri,
                imageAttrs,
                imageFrom,
                bitmapPool,
                res,
                id
            )
        }

        @Throws(IOException::class, NotFoundGifLibraryException::class)
        @JvmStatic
        fun createGifDrawable(
            key: String,
            imageUri: String,
            imageAttrs: ImageAttrs,
            imageFrom: ImageFrom,
            bitmapPool: BitmapPool?,
            resolver: ContentResolver?,
            uri: Uri
        ): SketchGifDrawable {
            assetExistGifLibrary()
            return SketchGifDrawableImpl(
                key,
                imageUri,
                imageAttrs,
                imageFrom,
                bitmapPool,
                resolver,
                uri
            )
        }

        @Throws(IOException::class, NotFoundGifLibraryException::class)
        @JvmStatic
        fun createGifDrawable(
            key: String,
            uri: String,
            imageAttrs: ImageAttrs,
            imageFrom: ImageFrom,
            bitmapPool: BitmapPool?,
            stream: InputStream
        ): SketchGifDrawable {
            assetExistGifLibrary()
            return SketchGifDrawableImpl(
                key,
                uri,
                imageAttrs,
                imageFrom,
                bitmapPool,
                stream
            )
        }
    }
}