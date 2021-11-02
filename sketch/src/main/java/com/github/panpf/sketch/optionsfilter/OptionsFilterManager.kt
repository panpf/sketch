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
package com.github.panpf.sketch.optionsfilter

import com.github.panpf.sketch.Configuration
import com.github.panpf.sketch.request.DownloadOptions
import java.util.*

/**
 * 负责管理 [OptionsFilter] 和过滤 Options，内置了以下选项过滤器
 *
 *
 *
 *  * 全局暂停下载功能 == [PauseDownloadOptionsFilter]
 *  * 全局暂停加载功能 == [PauseLoadOptionsFilter]
 *  * 全局使用低质量图片功能 == [LowQualityOptionsFilter]
 *  * 全局解码时质量优先功能 == [InPreferQualityOverSpeedOptionsFilter]
 *  * 全局移动数据下暂停下载功能 == [MobileDataPauseDownloadController]
 *
 */
class OptionsFilterManager {
    private var pauseDownloadOptionsFilter: PauseDownloadOptionsFilter? = null
    private var pauseLoadOptionsFilter: PauseLoadOptionsFilter? = null
    private var lowQualityOptionsFilter: LowQualityOptionsFilter? = null
    private var inPreferQualityOverSpeedOptionsFilter: InPreferQualityOverSpeedOptionsFilter? = null
    private var mobileDataPauseDownloadController: MobileDataPauseDownloadController? = null
    private var extrasFilters: MutableList<OptionsFilter>? = null

    /**
     * 添加一个 [OptionsFilter]
     *
     * @param optionsFilter [OptionsFilter]
     * @return [OptionsFilterManager]. 为了支持链式调用
     */
    fun add(optionsFilter: OptionsFilter): OptionsFilterManager {
        if (extrasFilters == null) {
            extrasFilters = LinkedList()
        }
        extrasFilters!!.add(optionsFilter)
        return this
    }

    /**
     * 添加一个 [OptionsFilter] 到指定位置
     *
     * @param index         指定位置
     * @param optionsFilter [OptionsFilter]
     * @return [OptionsFilterManager]. 为了支持链式调用
     */
    fun add(index: Int, optionsFilter: OptionsFilter): OptionsFilterManager {
        if (extrasFilters == null) {
            extrasFilters = LinkedList()
        }
        extrasFilters!!.add(index, optionsFilter)
        return this
    }

    /**
     * 删除一个 [OptionsFilter]
     *
     * @param optionsFilter [OptionsFilter]
     * @return true：存在指定 [OptionsFilter] 并已删除
     */
    fun remove(optionsFilter: OptionsFilter): Boolean {
        return extrasFilters != null && extrasFilters!!.remove(
            optionsFilter
        )
    }

    /**
     * 过滤指定 Options
     *
     * @param options [DownloadOptions]
     */
    fun filter(options: DownloadOptions) {
        if (pauseLoadOptionsFilter != null) {
            pauseLoadOptionsFilter!!.filter(options)
        }
        if (pauseDownloadOptionsFilter != null) {
            pauseDownloadOptionsFilter!!.filter(options)
        }
        if (lowQualityOptionsFilter != null) {
            lowQualityOptionsFilter!!.filter(options)
        }
        if (inPreferQualityOverSpeedOptionsFilter != null) {
            inPreferQualityOverSpeedOptionsFilter!!.filter(options)
        }
        if (extrasFilters != null) {
            for (filter in extrasFilters!!) {
                filter.filter(options)
            }
        }
    }

    /**
     * 设置全局暂停下载图片，开启后将不再从网络下载图片，只影响 [Sketch.display] 方法和 [Sketch.load] 方法
     */
    var isPauseDownloadEnabled: Boolean
        get() = pauseDownloadOptionsFilter != null
        set(pauseDownloadEnabled) {
            if (isPauseDownloadEnabled != pauseDownloadEnabled) {
                pauseDownloadOptionsFilter =
                    if (pauseDownloadEnabled) PauseDownloadOptionsFilter() else null
            }
        }

    /**
     * 设置全局暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响 [Sketch.display] 方法
     */
    var isPauseLoadEnabled: Boolean
        get() = pauseLoadOptionsFilter != null
        set(pauseLoadEnabled) {
            if (isPauseLoadEnabled != pauseLoadEnabled) {
                pauseLoadOptionsFilter = if (pauseLoadEnabled) PauseLoadOptionsFilter() else null
            }
        }

    /**
     * 设置全局使用低质量的图片
     */
    var isLowQualityImageEnabled: Boolean
        get() = lowQualityOptionsFilter != null
        set(lowQualityImageEnabled) {
            if (isLowQualityImageEnabled != lowQualityImageEnabled) {
                lowQualityOptionsFilter =
                    if (lowQualityImageEnabled) LowQualityOptionsFilter() else null
            }
        }

    /**
     * 开启全局解码时优先考虑速度还是质量 (默认优先考虑速度)
     */
    var isInPreferQualityOverSpeedEnabled: Boolean
        get() = inPreferQualityOverSpeedOptionsFilter != null
        set(inPreferQualityOverSpeedEnabled) {
            if (isInPreferQualityOverSpeedEnabled != inPreferQualityOverSpeedEnabled) {
                inPreferQualityOverSpeedOptionsFilter =
                    if (inPreferQualityOverSpeedEnabled) InPreferQualityOverSpeedOptionsFilter() else null
            }
        }

    /**
     * 全局移动数据下暂停下载？只影响display请求和load请求
     */
    val isMobileDataPauseDownloadEnabled: Boolean
        get() = mobileDataPauseDownloadController != null && mobileDataPauseDownloadController!!.isOpened

    /**
     * 开启全局移动数据或有流量限制的 WIFI 下暂停下载的功能，只影响 [Sketch.display] 方法和 [Sketch.load] 方法
     *
     * @param mobileDataPauseDownloadEnabled 全局移动数据下暂停下载
     */
    fun setMobileDataPauseDownloadEnabled(
        configuration: Configuration?,
        mobileDataPauseDownloadEnabled: Boolean
    ) {
        if (isMobileDataPauseDownloadEnabled != mobileDataPauseDownloadEnabled) {
            if (mobileDataPauseDownloadEnabled) {
                if (mobileDataPauseDownloadController == null) {
                    mobileDataPauseDownloadController = MobileDataPauseDownloadController(
                        configuration!!
                    )
                }
                mobileDataPauseDownloadController!!.isOpened = true
            } else {
                if (mobileDataPauseDownloadController != null) {
                    mobileDataPauseDownloadController!!.isOpened = false
                }
            }
        }
    }

    override fun toString(): String {
        return "OptionsFilterManager"
    }
}