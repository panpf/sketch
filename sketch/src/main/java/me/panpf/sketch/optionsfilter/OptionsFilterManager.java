/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.optionsfilter;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import me.panpf.sketch.Configuration;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchView;
import me.panpf.sketch.request.DownloadOptions;
import me.panpf.sketch.request.LoadListener;

/**
 * 负责管理 {@link OptionsFilter} 和过滤 Options，内置了以下选项过滤器<p>
 * <ul>
 * <li>全局暂停下载功能 == {@link PauseDownloadOptionsFilter}</li>
 * <li>全局暂停加载功能 == {@link PauseLoadOptionsFilter}</li>
 * <li>全局使用低质量图片功能 == {@link LowQualityOptionsFilter}</li>
 * <li>全局解码时质量优先功能 == {@link InPreferQualityOverSpeedOptionsFilter}</li>
 * <li>全局移动数据下暂停下载功能 == {@link MobileDataPauseDownloadController}</li>
 * </ul>
 */
public class OptionsFilterManager {

    private PauseDownloadOptionsFilter pauseDownloadOptionsFilter;
    private PauseLoadOptionsFilter pauseLoadOptionsFilter;
    private LowQualityOptionsFilter lowQualityOptionsFilter;
    private InPreferQualityOverSpeedOptionsFilter inPreferQualityOverSpeedOptionsFilter;
    private MobileDataPauseDownloadController mobileDataPauseDownloadController;
    private List<OptionsFilter> extrasFilters;

    /**
     * 添加一个 {@link OptionsFilter}
     *
     * @param optionsFilter {@link OptionsFilter}
     * @return {@link OptionsFilterManager}. 为了支持链式调用
     */
    @NonNull
    public OptionsFilterManager add(@NonNull OptionsFilter optionsFilter) {
        //noinspection ConstantConditions
        if (optionsFilter != null) {
            if (extrasFilters == null) {
                extrasFilters = new LinkedList<>();
            }
            extrasFilters.add(optionsFilter);
        }
        return this;
    }

    /**
     * 添加一个 {@link OptionsFilter} 到指定位置
     *
     * @param index         指定位置
     * @param optionsFilter {@link OptionsFilter}
     * @return {@link OptionsFilterManager}. 为了支持链式调用
     */
    @NonNull
    public OptionsFilterManager add(int index, @NonNull OptionsFilter optionsFilter) {
        //noinspection ConstantConditions
        if (optionsFilter != null) {
            if (extrasFilters == null) {
                extrasFilters = new LinkedList<>();
            }
            extrasFilters.add(index, optionsFilter);
        }
        return this;
    }

    /**
     * 删除一个 {@link OptionsFilter}
     *
     * @param optionsFilter {@link OptionsFilter}
     * @return true：存在指定 {@link OptionsFilter} 并已删除
     */
    public boolean remove(@NonNull OptionsFilter optionsFilter) {
        //noinspection ConstantConditions
        return optionsFilter != null && extrasFilters != null && extrasFilters.remove(optionsFilter);
    }

    /**
     * 过滤指定 Options
     *
     * @param options {@link DownloadOptions}
     */
    public void filter(@NonNull DownloadOptions options) {
        //noinspection ConstantConditions
        if (options == null) {
            return;
        }

        if (pauseLoadOptionsFilter != null) {
            pauseLoadOptionsFilter.filter(options);
        }
        if (pauseDownloadOptionsFilter != null) {
            pauseDownloadOptionsFilter.filter(options);
        }
        if (lowQualityOptionsFilter != null) {
            lowQualityOptionsFilter.filter(options);
        }
        if (inPreferQualityOverSpeedOptionsFilter != null) {
            inPreferQualityOverSpeedOptionsFilter.filter(options);
        }

        if (extrasFilters != null) {
            for (OptionsFilter filter : extrasFilters) {
                filter.filter(options);
            }
        }
    }


    /**
     * 全局暂停下载图片？开启后将不再从网络下载图片，只影响 {@link Sketch#display(String, SketchView)} 方法和 {@link Sketch#load(String, LoadListener)} 方法
     */
    public boolean isPauseDownloadEnabled() {
        return pauseDownloadOptionsFilter != null;
    }

    /**
     * 设置全局暂停下载图片，开启后将不再从网络下载图片，只影响 {@link Sketch#display(String, SketchView)} 方法和 {@link Sketch#load(String, LoadListener)} 方法
     *
     * @param pauseDownloadEnabled 全局暂停下载新图片
     */
    public void setPauseDownloadEnabled(boolean pauseDownloadEnabled) {
        if (isPauseDownloadEnabled() != pauseDownloadEnabled) {
            this.pauseDownloadOptionsFilter = pauseDownloadEnabled ? new PauseDownloadOptionsFilter() : null;
        }
    }

    /**
     * 全局暂停加载新图片？开启后将只从内存缓存中找寻图片，只影响 {@link Sketch#display(String, SketchView)} 方法
     */
    public boolean isPauseLoadEnabled() {
        return pauseLoadOptionsFilter != null;
    }

    /**
     * 设置全局暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响 {@link Sketch#display(String, SketchView)} 方法
     *
     * @param pauseLoadEnabled 全局暂停加载新图片
     */
    public void setPauseLoadEnabled(boolean pauseLoadEnabled) {
        if (isPauseLoadEnabled() != pauseLoadEnabled) {
            this.pauseLoadOptionsFilter = pauseLoadEnabled ? new PauseLoadOptionsFilter() : null;
        }
    }

    /**
     * 全局使用低质量的图片？
     */
    public boolean isLowQualityImageEnabled() {
        return lowQualityOptionsFilter != null;
    }

    /**
     * 设置全局使用低质量的图片
     *
     * @param lowQualityImageEnabled 全局使用低质量图片
     */
    public void setLowQualityImageEnabled(boolean lowQualityImageEnabled) {
        if (isLowQualityImageEnabled() != lowQualityImageEnabled) {
            this.lowQualityOptionsFilter = lowQualityImageEnabled ? new LowQualityOptionsFilter() : null;
        }
    }

    /**
     * 全局解码时优先考虑速度还是质量 (默认优先考虑速度)
     *
     * @return true：质量；false：速度
     */
    public boolean isInPreferQualityOverSpeedEnabled() {
        return inPreferQualityOverSpeedOptionsFilter != null;
    }

    /**
     * 开启全局解码时优先考虑速度还是质量 (默认优先考虑速度)
     *
     * @param inPreferQualityOverSpeedEnabled true：质量优先；false：速度优先
     */
    public void setInPreferQualityOverSpeedEnabled(boolean inPreferQualityOverSpeedEnabled) {
        if (isInPreferQualityOverSpeedEnabled() != inPreferQualityOverSpeedEnabled) {
            this.inPreferQualityOverSpeedOptionsFilter = inPreferQualityOverSpeedEnabled ? new InPreferQualityOverSpeedOptionsFilter() : null;
        }
    }

    /**
     * 全局移动数据下暂停下载？只影响display请求和load请求
     */
    @SuppressWarnings("unused")
    public boolean isMobileDataPauseDownloadEnabled() {
        return mobileDataPauseDownloadController != null && mobileDataPauseDownloadController.isOpened();
    }

    /**
     * 开启全局移动数据或有流量限制的 WIFI 下暂停下载的功能，只影响 {@link Sketch#display(String, SketchView)} 方法和 {@link Sketch#load(String, LoadListener)} 方法
     *
     * @param mobileDataPauseDownloadEnabled 全局移动数据下暂停下载
     */
    public void setMobileDataPauseDownloadEnabled(Configuration configuration, boolean mobileDataPauseDownloadEnabled) {
        if (isMobileDataPauseDownloadEnabled() != mobileDataPauseDownloadEnabled) {
            if (mobileDataPauseDownloadEnabled) {
                if (this.mobileDataPauseDownloadController == null) {
                    this.mobileDataPauseDownloadController = new MobileDataPauseDownloadController(configuration);
                }
                this.mobileDataPauseDownloadController.setOpened(true);
            } else {
                if (this.mobileDataPauseDownloadController != null) {
                    this.mobileDataPauseDownloadController.setOpened(false);
                }
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "OptionsFilterManager";
    }
}
