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
package com.github.panpf.sketch

import android.content.ComponentCallbacks2
import android.content.Context
import android.graphics.Bitmap
import com.github.panpf.sketch.cache.*
import com.github.panpf.sketch.decode.*
import com.github.panpf.sketch.display.DefaultImageDisplayer
import com.github.panpf.sketch.display.ImageDisplayer
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.http.ImageDownloader
import com.github.panpf.sketch.optionsfilter.OptionsFilter
import com.github.panpf.sketch.optionsfilter.OptionsFilterManager
import com.github.panpf.sketch.process.ImageProcessor
import com.github.panpf.sketch.process.ResizeImageProcessor
import com.github.panpf.sketch.request.RequestExecutor
import com.github.panpf.sketch.request.Resize
import com.github.panpf.sketch.request.ResultShareManager
import com.github.panpf.sketch.uri.UriModel
import com.github.panpf.sketch.uri.UriModelManager
import com.github.panpf.sketch.util.SketchUtils.Companion.getTrimLevelName

class Configuration internal constructor(context: Context) {

    companion object {
        private const val NAME = "Configuration"
    }

    /**
     * 获取 [Context]
     *
     * @return [Context]
     */
    val context: Context = context.applicationContext

    /**
     * 获取 [UriModel] 管理器
     *
     * @return [UriModelManager]. [UriModel] 管理器
     */
    val uriModelManager: UriModelManager = UriModelManager()

    /**
     * 获取 [OptionsFilter] 管理器
     *
     * @return [OptionsFilterManager]. [OptionsFilter] 管理器
     */
    val optionsFilterManager: OptionsFilterManager = OptionsFilterManager()

    /**
     * 获取已处理图片缓存器
     *
     * @return [TransformCacheManager]. 已处理图片缓存器
     */
    val transformCacheManager: TransformCacheManager = TransformCacheManager()

    val resultShareManager: ResultShareManager = ResultShareManager()

    private val memorySizeCalculator = MemorySizeCalculator(context.applicationContext)

    /**
     * 获取磁盘缓存管理器
     *
     * @return [DiskCache]. 磁盘缓存管理器
     */
    // 由于默认的缓存文件名称从 URLEncoder 加密变成了 MD5 所以这里要升级一下版本号，好清除旧的缓存
    var diskCache: DiskCache =
        LruDiskCache(context.applicationContext, this, 2, DiskCache.DISK_CACHE_MAX_SIZE)
        set(value) {
            field.close()
            field = value
            SLog.wmf(NAME, "diskCache=%s", value.toString())
        }

    /**
     * 获取 [Bitmap] 复用管理器
     *
     * @return [BitmapPool]. [Bitmap] 复用管理器
     */
    var bitmapPool: BitmapPool =
        LruBitmapPool(context.applicationContext, memorySizeCalculator.bitmapPoolSize)
        set(value) {
            field.close()
            field = value
            SLog.wmf(NAME, "bitmapPool=%s", value.toString())
        }

    /**
     * 获取内存缓存管理器
     *
     * @return [MemoryCache]. 内存缓存管理器
     */
    var memoryCache: MemoryCache =
        LruMemoryCache(context.applicationContext, memorySizeCalculator.memoryCacheSize)
        set(value) {
            field.close()
            field = value
            SLog.wmf(NAME, "memoryCache=", value.toString())
        }

    /**
     * 获取 HTTP 请求执行器
     *
     * @return [HttpStack] HTTP 请求执行器
     */
    var httpStack: HttpStack = HurlStack()
        set(value) {
            field = value
            SLog.wmf(NAME, "httpStack=", value.toString())
        }

    /**
     * 获取图片解码器
     *
     * @return [ImageDecoder]. 图片解码器
     */
    var decoder: ImageDecoder = ImageDecoder()
        set(value) {
            field = value
            SLog.wmf(NAME, "decoder=%s", value.toString())
        }

    /**
     * 获取图片下载器
     *
     * @return [ImageDownloader]. 图片下载器
     */
    var downloader: ImageDownloader = ImageDownloader()
        set(value) {
            field = value
            SLog.wmf(NAME, "downloader=%s", value.toString())
        }

    /**
     * 获取图片方向纠正器
     *
     * @return [ImageOrientationCorrector]. 图片方向纠正器
     */
    var orientationCorrector: ImageOrientationCorrector = ImageOrientationCorrector()
        set(value) {
            field = value
            SLog.wmf(NAME, "orientationCorrector=%s", value.toString())
        }

    /**
     * 获取默认的图片显示器
     *
     * @return [ImageDisplayer]. 默认的图片显示器
     */
    var defaultDisplayer: ImageDisplayer = DefaultImageDisplayer()
        set(value) {
            field = value
            SLog.wmf(NAME, "defaultDisplayer=%s", value.toString())
        }

    /**
     * 获取 [Resize] 属性处理器
     *
     * @return [ImageProcessor]. [Resize] 属性处理器
     */
    var resizeProcessor: ImageProcessor = ResizeImageProcessor()
        set(value) {
            field = value
            SLog.wmf(NAME, "resizeProcessor=%s", value.toString())
        }

    /**
     * 获取 [Resize] 计算器
     *
     * @return [ResizeCalculator]. [Resize] 计算器
     */
    var resizeCalculator: ResizeCalculator = ResizeCalculator()
        set(value) {
            field = value
            SLog.wmf(NAME, "resizeCalculator=%s", value.toString())
        }

    /**
     * 获取和图片尺寸相关的需求的计算器
     *
     * @return [ImageSizeCalculator]. 和图片尺寸相关的需求的计算器
     */
    var sizeCalculator: ImageSizeCalculator = ImageSizeCalculator()
        set(value) {
            field = value
            SLog.wmf(NAME, "sizeCalculator=%s", value.toString())
        }

    /**
     * 获取请求执行器
     *
     * @return [RequestExecutor]. 请求执行器
     */
    var executor: RequestExecutor = RequestExecutor()
        set(value) {
            field.shutdown()
            field = value
            SLog.wmf(NAME, "executor=%s", value.toString())
        }

    var callback: SketchCallback = DefaultSketchCallback()
        set(value) {
            field = value
            SLog.wmf(NAME, "callback=%s", value.toString())
        }

    /**
     * 全局暂停下载新图片？
     */
    var isPauseDownloadEnabled: Boolean
        get() = optionsFilterManager.isPauseDownloadEnabled
        set(value) {
            if (optionsFilterManager.isPauseDownloadEnabled != value) {
                optionsFilterManager.isPauseDownloadEnabled = value
                SLog.wmf(NAME, "pauseDownload=%s", value)
            }
        }

    /**
     * 全局暂停加载新图片？
     */
    var isPauseLoadEnabled: Boolean
        get() = optionsFilterManager.isPauseLoadEnabled
        set(value) {
            if (optionsFilterManager.isPauseLoadEnabled != value) {
                optionsFilterManager.isPauseLoadEnabled = value
                SLog.wmf(NAME, "pauseLoad=%s", value)
            }
        }

    /**
     * 全局使用低质量的图片？
     */
    var isLowQualityImageEnabled: Boolean
        get() = optionsFilterManager.isLowQualityImageEnabled
        set(value) {
            if (optionsFilterManager.isLowQualityImageEnabled != value) {
                optionsFilterManager.isLowQualityImageEnabled = value
                SLog.wmf(NAME, "lowQualityImage=%s", value)
            }
        }

    /**
     * 全局解码时优先考虑速度还是质量 (默认优先考虑速度)
     *
     * @return true：质量优先；false：速度优先
     */
    var isInPreferQualityOverSpeedEnabled: Boolean
        get() = optionsFilterManager.isInPreferQualityOverSpeedEnabled
        set(value) {
            if (optionsFilterManager.isInPreferQualityOverSpeedEnabled != value) {
                optionsFilterManager.isInPreferQualityOverSpeedEnabled = value
                SLog.wmf(NAME, "inPreferQualityOverSpeed=%s", value)
            }
        }

    /**
     * 全局移动数据下暂停下载？
     */
    var isMobileDataPauseDownloadEnabled: Boolean
        get() = optionsFilterManager.isMobileDataPauseDownloadEnabled
        set(value) {
            if (optionsFilterManager.isMobileDataPauseDownloadEnabled != value) {
                optionsFilterManager.setMobileDataPauseDownloadEnabled(this, value)
                SLog.wmf(NAME, "mobileDataPauseDownload=%s", value)
            }
        }

    init {
        context.applicationContext.registerComponentCallbacks(MemoryChangedListener(this))
    }

    override fun toString(): String {
        return """
            $NAME: 
            uriModelManager：$uriModelManager
            optionsFilterManager：$optionsFilterManager
            diskCache：$diskCache
            bitmapPool：$bitmapPool
            memoryCache：$memoryCache
            processedImageCache：$transformCacheManager
            httpStack：$httpStack
            decoder：$decoder
            downloader：$downloader
            orientationCorrector：$orientationCorrector
            defaultDisplayer：$defaultDisplayer
            resizeProcessor：$resizeProcessor
            resizeCalculator：$resizeCalculator
            sizeCalculator：$sizeCalculator
            executor：$executor
            callback：$callback
            pauseDownload：${optionsFilterManager.isPauseDownloadEnabled}
            pauseLoad：${optionsFilterManager.isPauseLoadEnabled}
            lowQualityImage：${optionsFilterManager.isLowQualityImageEnabled}
            inPreferQualityOverSpeed：${optionsFilterManager.isInPreferQualityOverSpeedEnabled}
            mobileDataPauseDownload：$isMobileDataPauseDownloadEnabled
            """.trimIndent()
    }

    private class MemoryChangedListener constructor(private val configuration: Configuration) :
        ComponentCallbacks2 {
        override fun onTrimMemory(level: Int) {
            SLog.wf("Trim of memory, level= %s", getTrimLevelName(level))
            configuration.memoryCache.trimMemory(level)
            configuration.bitmapPool.trimMemory(level)
        }

        override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {}
        override fun onLowMemory() {
            SLog.w("Memory is very low, clean memory cache and bitmap pool")
            configuration.memoryCache.clear()
            configuration.bitmapPool.clear()
        }
    }

    private class DefaultSketchCallback : SketchCallback {
        override fun onError(e: SketchException) {}
        override fun toString(): String {
            return "DefaultSketchCallback"
        }
    }
}