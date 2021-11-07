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
package com.github.panpf.sketch.util

import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.content.res.Resources.NotFoundException
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.GLES10
import android.opengl.GLES20
import android.os.*
import android.os.storage.StorageManager
import android.text.TextUtils
import android.text.format.Formatter
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.github.panpf.sketch.*
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageDecodeUtils
import com.github.panpf.sketch.decode.ImageOrientationCorrector
import com.github.panpf.sketch.decode.ImageType
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.drawable.SketchLoadingDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.uri.UriModel
import com.github.panpf.sketch.util.SketchMD5Utils.md5
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.math.BigDecimal
import java.net.URLEncoder
import java.util.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext
import kotlin.math.*

class SketchUtils {
    companion object {
        private val MATRIX_VALUES = FloatArray(9)

        /**
         * Read apk file icon. Although the PackageManager will cache the icon, the bitmap returned by this method every time
         *
         * @param context         [Context]
         * @param apkFilePath     Apk file path
         * @param lowQualityImage If set true use ARGB_4444 create bitmap, KITKAT is above is invalid
         * @param logName         Print log is used identify log type
         * @param bitmapPool      Try to find Reusable bitmap from bitmapPool
         */
        @JvmStatic
        fun readApkIcon(
            context: Context, apkFilePath: String, lowQualityImage: Boolean,
            logName: String, bitmapPool: BitmapPool
        ): Bitmap? {
            val packageManager = context.packageManager
            val packageInfo =
                packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES)
            if (packageInfo == null) {
                SLog.wmf(logName, "get packageInfo is null. %s", apkFilePath)
                return null
            }
            packageInfo.applicationInfo.sourceDir = apkFilePath
            packageInfo.applicationInfo.publicSourceDir = apkFilePath
            var drawable: Drawable? = null
            try {
                drawable = packageManager.getApplicationIcon(packageInfo.applicationInfo)
            } catch (e: NotFoundException) {
                e.printStackTrace()
            }
            if (drawable == null) {
                SLog.wmf(logName, "app icon is null. %s", apkFilePath)
                return null
            }
            return drawableToBitmap(drawable, lowQualityImage, bitmapPool)
        }

        /**
         * Drawable into Bitmap. Each time a new bitmap is drawn
         */
        @JvmStatic
        fun drawableToBitmap(
            drawable: Drawable?,
            lowQualityImage: Boolean,
            bitmapPool: BitmapPool?
        ): Bitmap? {
            if (drawable == null || drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                return null
            }
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            val config = if (lowQualityImage) Bitmap.Config.ARGB_4444 else Bitmap.Config.ARGB_8888
            val bitmap: Bitmap =
                bitmapPool?.getOrMake(drawable.intrinsicWidth, drawable.intrinsicHeight, config)
                    ?: Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        config
                    )
            val canvas = Canvas(bitmap)
            drawable.draw(canvas)
            return bitmap
        }

        /**
         * 清空目录
         *
         * @return true：成功
         */
        @JvmStatic
        fun cleanDir(dir: File?): Boolean {
            if (dir == null || !dir.exists() || !dir.isDirectory) {
                return true
            }
            val files = dir.listFiles()
            var cleanSuccess = true
            if (files != null) {
                for (tempFile in files) {
                    if (tempFile.isDirectory) {
                        cleanSuccess = cleanSuccess and cleanDir(tempFile)
                    }
                    cleanSuccess = cleanSuccess and tempFile.delete()
                }
            }
            return cleanSuccess
        }

        /**
         * 删除给定的文件，如果当前文件是目录则会删除其包含的所有的文件或目录
         *
         * @param file 给定的文件
         * @return true：删除成功；false：删除失败
         */
        @JvmStatic
        fun deleteFile(file: File?): Boolean {
            if (file == null || !file.exists()) {
                return true
            }
            if (file.isDirectory) {
                cleanDir(file)
            }
            return file.delete()
        }

        /**
         * 检查文件名是不是指定的后缀
         *
         * @param fileName 例如：test.txt
         * @param suffix   例如：.txt
         */
        @JvmStatic
        fun checkSuffix(fileName: String?, suffix: String): Boolean {
            if (fileName == null) {
                return false
            }

            // 截取后缀名
            val fileNameSuffix: String
            val lastIndex = fileName.lastIndexOf(".")
            fileNameSuffix = if (lastIndex > -1) {
                fileName.substring(lastIndex)
            } else {
                return false
            }
            return suffix.equals(fileNameSuffix, ignoreCase = true)
        }

        @JvmStatic
        fun close(closeable: Closeable?) {
            if (closeable == null) {
                return
            }
            if (closeable is OutputStream) {
                try {
                    closeable.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            try {
                closeable.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun close(fileDescriptor: AssetFileDescriptor?) {
            if (fileDescriptor == null) {
                return
            }
            try {
                fileDescriptor.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun isGifImage(drawable: Drawable?): Boolean {
            var drawable1 = drawable
            if (drawable1 != null) {
                var layerDrawable: LayerDrawable
                while (drawable1 is LayerDrawable) {
                    layerDrawable = drawable1
                    drawable1 = if (layerDrawable.numberOfLayers > 0) {
                        layerDrawable.getDrawable(layerDrawable.numberOfLayers - 1)
                    } else {
                        null
                    }
                }
                return drawable1 is SketchDrawable && ImageType.GIF.mimeType == (drawable1 as SketchDrawable).mimeType
            }
            return false
        }

        @JvmStatic
        fun viewLayoutFormatted(size: Int): String {
            return when {
                size >= 0 -> {
                    size.toString()
                }
                size == ViewGroup.LayoutParams.MATCH_PARENT -> {
                    "MATCH_PARENT"
                }
                size == ViewGroup.LayoutParams.WRAP_CONTENT -> {
                    "WRAP_CONTENT"
                }
                else -> {
                    "Unknown"
                }
            }
        }

        /**
         * 是不是主线程
         */
        @JvmStatic
        val isMainThread: Boolean
            get() = Looper.getMainLooper().thread === Thread.currentThread()

        /**
         * 获取当前进程的名字
         */
        @JvmStatic
        fun getProcessName(context: Context): String? {
            val pid = Process.myPid()
            val runningApps =
                ((context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?)?.runningAppProcesses)
                    ?: return null
            for (procInfo in runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName
                }
            }
            return null
        }

        /**
         * 当前进程是不是主进程
         */
        @JvmStatic
        fun isMainProcess(context: Context): Boolean {
            return context.packageName.equals(getProcessName(context), ignoreCase = true)
        }

        /**
         * 获取短的当前进程的名字，例如进程名字为 com.my.app:push，那么短名字就是 :push
         */
        @JvmStatic
        fun getSimpleProcessName(context: Context): String? {
            val processName = getProcessName(context) ?: return null
            val packageName = context.packageName
            val lastIndex = processName.lastIndexOf(packageName)
            return if (lastIndex != -1) processName.substring(lastIndex + packageName.length) else null
        }

        /**
         * 获取 app 缓存目录，优先考虑 sdcard 上的缓存目录
         */
        @JvmStatic
        fun getAppCacheDir(context: Context): File? {
            var appCacheDir: File? = null
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                appCacheDir = context.externalCacheDir
            }
            if (appCacheDir == null) {
                appCacheDir = context.cacheDir
            }
            return appCacheDir
        }

        /**
         * 获取给定目录的可用大小
         */
        @JvmStatic
        fun getAvailableBytes(dir: File): Long {
            if (!dir.exists() && !dir.mkdirs()) {
                return 0
            }
            val dirStatFs: StatFs = try {
                StatFs(dir.path)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                return 0
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                dirStatFs.availableBytes
            } else {
                dirStatFs.availableBlocks.toLong() * dirStatFs.blockSize
            }
        }

        /**
         * 获取给定目录的总大小
         */
        @JvmStatic
        fun getTotalBytes(dir: File): Long {
            if (!dir.exists() && !dir.mkdirs()) {
                return 0
            }
            val dirStatFs: StatFs = try {
                StatFs(dir.path)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                return 0
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                dirStatFs.totalBytes
            } else {
                dirStatFs.blockCount.toLong() * dirStatFs.blockSize
            }
        }

        /**
         * 获取所有可用的 sdcard 的路径
         *
         * @return 所有可用的 sdcard 的路径
         */
        @JvmStatic
        fun getAllAvailableSdcardPath(context: Context): Array<String>? {
            val paths: Array<String>
            val getVolumePathsMethod: Method = try {
                StorageManager::class.java.getMethod("getVolumePaths")
            } catch (e: NoSuchMethodException) {
                SLog.em(
                    "getAllAvailableSdcardPath",
                    "not found StorageManager.getVolumePaths() method"
                )
                return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                    arrayOf(Environment.getExternalStorageDirectory().path)
                } else {
                    null
                }
            }
            val sm = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            paths = try {
                getVolumePathsMethod.invoke(sm) as Array<String>
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                return null
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
                return null
            }
            if (paths.isEmpty()) {
                return null
            }

            // 去掉不可用的存储器
            val storagePathList: MutableList<String> = LinkedList()
            Collections.addAll(storagePathList, *paths)
            val storagePathIterator = storagePathList.iterator()
            var path: String
            var getVolumeStateMethod: Method? = null
            while (storagePathIterator.hasNext()) {
                path = storagePathIterator.next()
                if (getVolumeStateMethod == null) {
                    getVolumeStateMethod = try {
                        StorageManager::class.java.getMethod("getVolumeState", String::class.java)
                    } catch (e: NoSuchMethodException) {
                        e.printStackTrace()
                        return null
                    }
                }
                val status: String = try {
                    getVolumeStateMethod!!.invoke(sm, path) as String
                } catch (e: Exception) {
                    e.printStackTrace()
                    storagePathIterator.remove()
                    continue
                }
                if (!(Environment.MEDIA_MOUNTED == status || Environment.MEDIA_MOUNTED_READ_ONLY == status)) {
                    storagePathIterator.remove()
                }
            }
            return storagePathList.toTypedArray()
        }

        @JvmStatic
        fun appendProcessName(context: Context, dirName: String): String {
            // 目录名字加上进程名字的后缀，不同的进程不同目录，以兼容多进程
            var newDirName = dirName
            val simpleProcessName = getSimpleProcessName(context)
            if (simpleProcessName != null) {
                try {
                    newDirName += URLEncoder.encode(simpleProcessName, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            }
            return newDirName
        }

        @JvmStatic
        fun getDefaultSketchCacheDir(
            context: Context,
            dirName: String,
            compatManyProcess: Boolean
        ): File {
            val appCacheDir = getAppCacheDir(context)
            return File(
                appCacheDir,
                if (compatManyProcess) appendProcessName(context, dirName) else dirName
            )
        }

        @JvmStatic
        @Throws(Exception::class)
        fun testCreateFile(cacheDir: File): Boolean {
            var parentDir: File? = cacheDir
            while (parentDir != null) {
                // 先向上找到一个已存在的目录
                if (!parentDir.exists()) {
                    parentDir = cacheDir.parentFile
                    continue
                }

                // 然后尝试创建文件
                val file = File(parentDir, "create_test.temp")

                // 已存在就先删除，删除失败就抛异常
                if (file.exists() && !file.delete()) {
                    throw Exception("Delete old test file failed: " + file.path)
                }
                file.createNewFile()
                return if (file.exists()) {
                    if (file.delete()) {
                        true
                    } else {
                        throw Exception("Delete test file failed: " + file.path)
                    }
                } else {
                    false
                }
            }
            return false
        }

        /**
         * 创建缓存目录，会优先在 sdcard 上创建
         *
         * @param dirName            目录名称
         * @param compatManyProcess  目录名称是否加上进程名
         * @param minSpaceSize       最小空间
         * @param cleanOnNoSpace     空间不够用时就尝试清理一下
         * @param cleanOldCacheFiles 清除旧的缓存文件
         * @param expandNumber       当 dirName 无法使用时就会尝试 dirName1、dirName2、dirName3...
         * @return 你应当以返回的目录为最终可用的目录
         * @throws NoSpaceException 可用空间小于 minSpaceSize；UnableCreateDirException：无法创建缓存目录；UnableCreateFileException：无法在缓存目录中创建文件
         */
        @JvmStatic
        @Throws(
            NoSpaceException::class,
            UnableCreateDirException::class,
            UnableCreateFileException::class
        )
        fun buildCacheDir(
            context: Context,
            dirName: String,
            compatManyProcess: Boolean,
            minSpaceSize: Long,
            cleanOnNoSpace: Boolean,
            cleanOldCacheFiles: Boolean,
            expandNumber: Int
        ): File {
            val appCacheDirs: MutableList<File> = LinkedList()
            val sdcardPaths = getAllAvailableSdcardPath(context)
            if (sdcardPaths != null && sdcardPaths.isNotEmpty()) {
                for (sdcardPath in sdcardPaths) {
                    appCacheDirs.add(
                        File(
                            sdcardPath,
                            "Android" + File.separator + "data" + File.separator + context.packageName + File.separator + "cache"
                        )
                    )
                }
            }
            appCacheDirs.add(context.cacheDir)
            val diskCacheDirName =
                if (compatManyProcess) appendProcessName(context, dirName) else dirName
            var noSpaceException: NoSpaceException? = null
            var unableCreateFileException: UnableCreateFileException? = null
            var diskCacheDir: File? = null
            var expandCount: Int
            for (appCacheDir in appCacheDirs) {
                expandCount = 0
                while (expandCount <= expandNumber) {
                    diskCacheDir =
                        File(
                            appCacheDir,
                            diskCacheDirName + if (expandCount > 0) expandCount else ""
                        )
                    if (diskCacheDir.exists()) {
                        // 目录已存在的话就尝试清除旧的缓存文件
                        if (cleanOldCacheFiles) {
                            val journalFile = File(diskCacheDir, DiskLruCache.JOURNAL_FILE)
                            if (!journalFile.exists()) {
                                cleanDir(diskCacheDir)
                            }
                        }
                    } else {
                        // 目录不存在就创建，创建结果返回false后检查还是不存在就说明创建失败
                        if (!diskCacheDir.mkdirs() && !diskCacheDir.exists()) {
                            expandCount++
                            continue
                        }
                    }

                    // 检查空间，少于minSpaceSize就不能用了
                    var availableBytes = getAvailableBytes(diskCacheDir)
                    if (availableBytes < minSpaceSize) {
                        // 空间不够用的时候直接清空，然后再次计算可用空间
                        if (cleanOnNoSpace) {
                            cleanDir(diskCacheDir)
                            availableBytes = getAvailableBytes(diskCacheDir)
                        }

                        // 依然不够用，那不好意思了
                        if (availableBytes < minSpaceSize) {
                            val availableFormatted =
                                Formatter.formatFileSize(context, availableBytes)
                            val minSpaceFormatted = Formatter.formatFileSize(context, minSpaceSize)
                            noSpaceException =
                                NoSpaceException("Need " + availableFormatted + ", with only " + minSpaceFormatted + " in " + diskCacheDir.path)
                            break
                        }
                    }

                    // 创建文件测试
                    try {
                        if (testCreateFile(diskCacheDir)) {
                            return diskCacheDir
                        } else {
                            unableCreateFileException =
                                UnableCreateFileException("Unable create file in " + diskCacheDir.path)
                            expandCount++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        unableCreateFileException =
                            UnableCreateFileException(e.javaClass.simpleName + ": " + e.message)
                        expandCount++
                    }
                }
            }
            when {
                noSpaceException != null -> {
                    throw noSpaceException
                }
                unableCreateFileException != null -> {
                    throw unableCreateFileException
                }
                else -> {
                    throw UnableCreateDirException("Unable create dir: " + if (diskCacheDir != null) diskCacheDir.path else "null")
                }
            }
        }

        /**
         * 从 [SketchView] 上查找 [DisplayRequest]
         */
        @JvmStatic
        fun findDisplayRequest(sketchView: SketchView?): DisplayRequest? {
            if (sketchView != null) {
                val drawable = sketchView.getDrawable()
                if (drawable is SketchLoadingDrawable) {
                    return drawable.request
                }
            }
            return null
        }

        /**
         * 根据给定的信息，生成最终的图片信息
         *
         * @param type            类型
         * @param imageWidth      图片宽
         * @param imageHeight     图片高
         * @param mimeType        图片格式
         * @param exifOrientation 图片方向
         * @param bitmap          [Bitmap]
         * @param byteCount       [Bitmap] 占用字节数
         */
        @JvmStatic
        fun makeImageInfo(
            type: String?, imageWidth: Int, imageHeight: Int, mimeType: String?,
            exifOrientation: Int, bitmap: Bitmap?, byteCount: Long, key: String?
        ): String {
            if (bitmap == null) {
                return "Unknown"
            }
            val newType = if (TextUtils.isEmpty(type)) "Bitmap" else type
            val hashCode = Integer.toHexString(bitmap.hashCode())
            val config = if (bitmap.config != null) bitmap.config.name else null
            val finalKey = if (key != null) String.format(", key=%s", key) else ""
            return String.format(
                Locale.US,
                "%s(image=%dx%d,%s,%s, bitmap=%dx%d,%s,%d,%s%s)",
                newType,
                imageWidth,
                imageHeight,
                mimeType,
                ImageOrientationCorrector.toName(exifOrientation),
                bitmap.width,
                bitmap.height,
                config,
                byteCount,
                hashCode,
                finalKey
            )
        }

        /**
         * 如果是 [LayerDrawable]，则返回其最后一张图片，不是的就返回自己
         */
        @JvmStatic
        fun getLastDrawable(drawable: Drawable?): Drawable? {
            if (drawable == null) {
                return null
            }
            if (drawable !is LayerDrawable) {
                return drawable
            }
            val layerCount = drawable.numberOfLayers
            return if (layerCount <= 0) {
                null
            } else getLastDrawable(drawable.getDrawable(layerCount - 1))
        }

        /**
         * 获取矩阵中指定位置的值
         *
         * @param matrix     [Matrix]
         * @param whichValue 指定的位置，例如 [Matrix.MSCALE_X]
         */
        fun getMatrixValue(matrix: Matrix, whichValue: Int): Float {
            synchronized(MATRIX_VALUES) {
                matrix.getValues(MATRIX_VALUES)
                return MATRIX_VALUES[whichValue]
            }
        }

        /**
         * 从 [Matrix] 中获取缩放比例
         */
        @JvmStatic
        fun getMatrixScale(matrix: Matrix): Float {
            synchronized(MATRIX_VALUES) {
                matrix.getValues(MATRIX_VALUES)
                val scaleX = MATRIX_VALUES[Matrix.MSCALE_X]
                val skewY = MATRIX_VALUES[Matrix.MSKEW_Y]
                return sqrt(
                    (scaleX.toDouble().pow(2.0).toFloat() + skewY.toDouble().pow(2.0)
                        .toFloat()).toDouble()
                ).toFloat()
            }
        }

        /**
         * 从 [Matrix] 中获取旋转角度
         */
        @JvmStatic
        fun getMatrixRotateDegrees(matrix: Matrix): Int {
            synchronized(MATRIX_VALUES) {
                matrix.getValues(MATRIX_VALUES)
                val skewX = MATRIX_VALUES[Matrix.MSKEW_X]
                val scaleX = MATRIX_VALUES[Matrix.MSCALE_X]
                val degrees = (atan2(
                    skewX.toDouble(),
                    scaleX.toDouble()
                ) * (180 / Math.PI)).roundToInt()
                return when {
                    degrees < 0 -> {
                        abs(degrees)
                    }
                    degrees > 0 -> {
                        360 - degrees
                    }
                    else -> {
                        0
                    }
                }
            }
        }

        /**
         * 从 [Matrix] 中获取偏移位置
         */
        @JvmStatic
        fun getMatrixTranslation(matrix: Matrix, point: PointF) {
            synchronized(MATRIX_VALUES) {
                matrix.getValues(MATRIX_VALUES)
                point.x = MATRIX_VALUES[Matrix.MTRANS_X]
                point.y = MATRIX_VALUES[Matrix.MTRANS_Y]
            }
        }

        /**
         * 获取 OpenGL 的版本
         */
        @JvmStatic
        fun getOpenGLVersion(context: Context): String {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            return if (am != null) am.deviceConfigurationInfo.glEsVersion else ""
        }

        /**
         * 获取 OpenGL 所允许的图片的最大尺寸(单边长)
         */
        @JvmStatic
        val openGLMaxTextureSize: Int
            get() {
                var maxTextureSize = 0
                try {
                    maxTextureSize =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            openGLMaxTextureSizeJB1
                        } else {
                            openGLMaxTextureSizeBase
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (maxTextureSize == 0) {
                    maxTextureSize = 4096
                }
                return maxTextureSize
            }

        // TROUBLE! No config found.

        // To make a context current, which we will need later,
        // you need a rendering surface, even if you don't actually plan to render.
        // To satisfy this requirement, create a small offscreen (Pbuffer) surface:

        // Next, create the context:

        // Ready to make the context current now:

        // If all of the above succeeded (error checking was omitted), you can make your OpenGL calls now:

        // Once you're all done, you can tear down everything:
        // Then get a hold of the default display, and initialize.
        // This could get more complex if you have to deal with devices that could have multiple displays,
        // but will be sufficient for a typical phone/tablet:
        @JvmStatic
        @get:TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        private val openGLMaxTextureSizeJB1: Int
            // Next, we need to find a config. Since we won't use this context for rendering,
            // the exact attributes aren't very critical:
            get() {
                // Then get a hold of the default display, and initialize.
                // This could get more complex if you have to deal with devices that could have multiple displays,
                // but will be sufficient for a typical phone/tablet:
                val dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
                val vers = IntArray(2)
                EGL14.eglInitialize(dpy, vers, 0, vers, 1)

                // Next, we need to find a config. Since we won't use this context for rendering,
                // the exact attributes aren't very critical:
                val configAttr = intArrayOf(
                    EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
                    EGL14.EGL_LEVEL, 0,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                    EGL14.EGL_NONE
                )
                val configs = arrayOfNulls<EGLConfig>(1)
                val numConfig = IntArray(1)
                EGL14.eglChooseConfig(
                    dpy, configAttr, 0,
                    configs, 0, 1, numConfig, 0
                )
                if (numConfig[0] == 0) {
                    // TROUBLE! No config found.
                }
                val config = configs[0]

                // To make a context current, which we will need later,
                // you need a rendering surface, even if you don't actually plan to render.
                // To satisfy this requirement, create a small offscreen (Pbuffer) surface:
                val surfAttr = intArrayOf(
                    EGL14.EGL_WIDTH, 64,
                    EGL14.EGL_HEIGHT, 64,
                    EGL14.EGL_NONE
                )
                val surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0)

                // Next, create the context:
                val ctxAttrib = intArrayOf(
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
                )
                val ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0)

                // Ready to make the context current now:
                EGL14.eglMakeCurrent(dpy, surf, surf, ctx)

                // If all of the above succeeded (error checking was omitted), you can make your OpenGL calls now:
                val maxSize = IntArray(1)
                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0)

                // Once you're all done, you can tear down everything:
                EGL14.eglMakeCurrent(
                    dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT
                )
                EGL14.eglDestroySurface(dpy, surf)
                EGL14.eglDestroyContext(dpy, ctx)
                EGL14.eglTerminate(dpy)
                return maxSize[0]
            }// missing in EGL10// TROUBLE! No config found.

        // In JELLY_BEAN will collapse
        @JvmStatic
        private val openGLMaxTextureSizeBase: Int
            get() {
                // In JELLY_BEAN will collapse
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
                    return 0
                }
                val egl = EGLContext.getEGL() as EGL10
                val dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
                val vers = IntArray(2)
                egl.eglInitialize(dpy, vers)
                val configAttr = intArrayOf(
                    EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                    EGL10.EGL_LEVEL, 0,
                    EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                    EGL10.EGL_NONE
                )
                val configs = arrayOfNulls<javax.microedition.khronos.egl.EGLConfig>(1)
                val numConfig = IntArray(1)
                egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig)
                if (numConfig[0] == 0) {
                    // TROUBLE! No config found.
                }
                val config = configs[0]
                val surfAttr = intArrayOf(
                    EGL10.EGL_WIDTH, 64,
                    EGL10.EGL_HEIGHT, 64,
                    EGL10.EGL_NONE
                )
                val surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr)
                val EGL_CONTEXT_CLIENT_VERSION = 0x3098 // missing in EGL10
                val ctxAttrib = intArrayOf(
                    EGL_CONTEXT_CLIENT_VERSION, 1,
                    EGL10.EGL_NONE
                )
                val ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib)
                egl.eglMakeCurrent(dpy, surf, surf, ctx)
                val maxSize = IntArray(1)
                GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0)
                egl.eglMakeCurrent(
                    dpy,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_CONTEXT
                )
                egl.eglDestroySurface(dpy, surf)
                egl.eglDestroyContext(dpy, ctx)
                egl.eglTerminate(dpy)
                return maxSize[0]
            }

        /**
         * 格式化小数，可以指定保留多少位小数
         */
        @JvmStatic
        fun formatFloat(floatValue: Float, newScale: Int): Float {
            val b = BigDecimal(floatValue.toDouble())
            return b.setScale(newScale, BigDecimal.ROUND_HALF_UP).toFloat()
        }

        /**
         * 根据图片格式型判断是否支持读取图片碎片
         */
        @JvmStatic
        fun formatSupportBitmapRegionDecoder(imageType: ImageType?): Boolean {
            return imageType == ImageType.JPEG || imageType == ImageType.PNG || imageType == ImageType.WEBP
        }

        /**
         * 判断两个矩形是否相交
         */
        @JvmStatic
        fun isCross(rect1: Rect, rect2: Rect): Boolean {
            return rect1.left < rect2.right && rect2.left < rect1.right && rect1.top < rect2.bottom && rect2.top < rect1.bottom
        }

        /**
         * dp 转换成 px
         */
        @JvmStatic
        fun dp2px(context: Context, dpValue: Int): Int {
            return (dpValue * context.resources.displayMetrics.density + 0.5).toInt()
        }

        /**
         * 生成请求 key
         *
         * @param imageUri   图片地址
         * @param optionsKey 选项 key
         * @see SketchImageView.getOptionsKey
         */
        @JvmStatic
        fun makeRequestKey(imageUri: String, uriModel: UriModel, optionsKey: String): String {
            var imageUri1 = imageUri
            if (uriModel.isConvertShortUriForKey) {
                imageUri1 = md5(imageUri1)
            }
            if (TextUtils.isEmpty(optionsKey)) {
                return imageUri1
            }
            val builder = StringBuilder(imageUri1)
            if (imageUri1.lastIndexOf("?") == -1) {
                builder.append('?')
            } else {
                builder.append('&')
            }
            builder.append("options")
            builder.append("=")
            builder.append(optionsKey)
            return builder.toString()
        }

        /**
         * 根据指定的 [Bitmap] 配置获取合适的压缩格式
         */
        @JvmStatic
        fun bitmapConfigToCompressFormat(config: Bitmap.Config?): CompressFormat {
            return if (config == Bitmap.Config.RGB_565) CompressFormat.JPEG else CompressFormat.PNG
        }

        /**
         * 获取 [Bitmap] 占用内存大小，单位字节
         */
        @JvmStatic
        fun getByteCount(bitmap: Bitmap?): Int {
            // bitmap.isRecycled()过滤很关键，在4.4以及以下版本当bitmap已回收时调用其getAllocationByteCount()方法将直接崩溃
            return if (bitmap == null || bitmap.isRecycled) {
                0
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                bitmap.allocationByteCount
            } else {
                bitmap.byteCount
            }
        }

        /**
         * 根据宽、高和配置计算所占用的字节数
         */
        @JvmStatic
        fun computeByteCount(width: Int, height: Int, config: Bitmap.Config?): Int {
            return width * height * getBytesPerPixel(config)
        }

        /**
         * 获取指定配置单个像素所占的字节数
         */
        @JvmStatic
        fun getBytesPerPixel(config: Bitmap.Config?): Int {
            // A bitmap by decoding a gif has null "config" in certain environments.
            var config1 = config
            if (config1 == null) {
                config1 = Bitmap.Config.ARGB_8888
            }
            val bytesPerPixel: Int = when (config1) {
                Bitmap.Config.ALPHA_8 -> 1
                Bitmap.Config.RGB_565, Bitmap.Config.ARGB_4444 -> 2
                Bitmap.Config.ARGB_8888 -> 4
                else -> 4
            }
            return bytesPerPixel
        }

        /**
         * 获取修剪级别的名称
         */
        @JvmStatic
        fun getTrimLevelName(level: Int): String {
            return when (level) {
                ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> "COMPLETE"
                ComponentCallbacks2.TRIM_MEMORY_MODERATE -> "MODERATE"
                ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> "BACKGROUND"
                ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> "UI_HIDDEN"
                ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> "RUNNING_CRITICAL"
                ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> "RUNNING_LOW"
                ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> "RUNNING_MODERATE"
                else -> "UNKNOWN"
            }
        }

        /**
         * 指定的栈历史中是否存在指定的类的指定的方法
         */
        @JvmStatic
        fun invokeIn(
            stackTraceElements: Array<StackTraceElement>?,
            cla: Class<*>,
            methodName: String
        ): Boolean {
            if (stackTraceElements == null || stackTraceElements.isEmpty()) {
                return false
            }
            val targetClassName = cla.name
            var element: StackTraceElement
            var elementClassName: String
            var elementMethodName: String
            for (stackTraceElement in stackTraceElements) {
                element = stackTraceElement
                elementClassName = element.className
                elementMethodName = element.methodName
                if (targetClassName == elementClassName && methodName == elementMethodName) {
                    return true
                }
            }
            return false
        }

        @JvmStatic
        fun toHexString(`object`: Any?): String? {
            return if (`object` == null) {
                null
            } else Integer.toHexString(`object`.hashCode())
        }

        @JvmStatic
        fun calculateSamplingSize(value1: Int, inSampleSize: Int): Int {
            return ceil((value1 / inSampleSize.toFloat()).toDouble()).toInt()
        }

        @JvmStatic
        fun calculateSamplingSizeForRegion(value1: Int, inSampleSize: Int): Int {
            return floor((value1 / inSampleSize.toFloat()).toDouble()).toInt()
        }

        @JvmStatic
        val isDisabledARGB4444: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        @JvmStatic
        fun generatorTempFileName(dataSource: DataSource, uri: String): String {
            var options: BitmapFactory.Options? = BitmapFactory.Options()
            options!!.inJustDecodeBounds = true
            try {
                ImageDecodeUtils.decodeBitmap(dataSource, options)
            } catch (e: Throwable) {
                e.printStackTrace()
                options = null
            }
            val uriEncode = md5(uri)
            return if (options?.outMimeType != null && options.outMimeType.startsWith("image/")) {
                val suffix = options.outMimeType.replace("image/", "")
                String.format("%s.%s", uriEncode, suffix)
            } else {
                uriEncode
            }
        }

        @JvmStatic
        fun findInitializer(context: Context): Initializer? {
            val appInfo: ApplicationInfo = try {
                context.packageManager.getApplicationInfo(
                    context.packageName,
                    PackageManager.GET_META_DATA
                )
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }
            var initializerClassName: String? = null
            if (appInfo.metaData != null) {
                for (key in appInfo.metaData.keySet()) {
                    if (Sketch.META_DATA_KEY_INITIALIZER == appInfo.metaData[key]) {
                        initializerClassName = key
                        break
                    }
                }
            }
            if (TextUtils.isEmpty(initializerClassName)) {
                return null
            }
            val initializerClass: Class<*> = try {
                Class.forName(initializerClassName)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                return null
            }
            if (!Initializer::class.java.isAssignableFrom(initializerClass)) {
                SLog.em("findInitializer", "$initializerClassName must be implements Initializer")
                return null
            }
            try {
                return initializerClass.newInstance() as Initializer
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * 生成文件 uri 的磁盘缓存 key，关键在于要在 uri 的后面加上文件的修改时间来作为缓存 key，这样当文件发生变化时能及时更新缓存
         *
         * @param uri      文件 uri
         * @param filePath 文件路径，要获取文件的修改时间
         * @return 文件 uri 的磁盘缓存 key
         */
        fun createFileUriDiskCacheKey(uri: String, filePath: String): String {
            val file = File(filePath)
            return if (file.exists()) {
                val lastModifyTime = file.lastModified()
                // 这里必须用 uri 连接修改时间，不能用 filePath，因为使用 filePath 的话当同一个文件可以用于多种 uri 时会导致磁盘缓存错乱
                "$uri.$lastModifyTime"
            } else {
                uri
            }
        }

        @JvmStatic
        fun postOnAnimation(view: View, runnable: Runnable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.postOnAnimation(runnable)
            } else {
                view.postDelayed(runnable, (1000 / 60).toLong())
            }
        }

        @JvmStatic
        fun getPointerIndex(action: Int): Int {
            return action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
        }

        /**
         * Match MimeType
         *
         * @param template For example: application/ *
         * @param mimeType For example: application/zip
         */
        @JvmStatic
        fun matchMimeType(template: String, mimeType: String?): Boolean {
            val templateItems = template.split("/").toTypedArray()
            val mimeItems = (mimeType ?: "").split("/").toTypedArray()
            var result = true
            if (templateItems.isNotEmpty() && templateItems.size == mimeItems.size) {
                for (index in templateItems.indices) {
                    val templateItem = templateItems[index].trim { it <= ' ' }
                    val mimeItem = mimeItems[index].trim { it <= ' ' }
                    result =
                        "*" == templateItem || templateItem.equals(mimeItem, ignoreCase = true)
                    if (!result) {
                        break
                    }
                }
            } else {
                result = false
            }
            return result
        }
    }
}