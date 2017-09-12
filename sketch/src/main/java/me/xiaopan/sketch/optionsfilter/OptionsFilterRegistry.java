/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.optionsfilter;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.request.DownloadOptions;

public class OptionsFilterRegistry implements Identifier {

    private PauseDownloadOptionsFilter pauseDownloadOptionsFilter;
    private PauseLoadOptionsFilter pauseLoadOptionsFilter;
    private LowQualityOptionsFilter lowQualityOptionsFilter;
    private InPreferQualityOverSpeedOptionsFilter inPreferQualityOverSpeedOptionsFilter;
    private MobileNetworkPauseDownloadController mobileNetworkPauseDownloadController;
    private List<OptionsFilter> extrasFilters;

    @NonNull
    public OptionsFilterRegistry add(@NonNull OptionsFilter optionsFilter) {
        //noinspection ConstantConditions
        if (optionsFilter != null) {
            if (extrasFilters == null) {
                extrasFilters = new LinkedList<>();
            }
            extrasFilters.add(optionsFilter);
        }
        return this;
    }

    @NonNull
    public OptionsFilterRegistry add(int index, @NonNull OptionsFilter optionsFilter) {
        //noinspection ConstantConditions
        if (optionsFilter != null) {
            if (extrasFilters == null) {
                extrasFilters = new LinkedList<>();
            }
            extrasFilters.add(index, optionsFilter);
        }
        return this;
    }

    public boolean remove(@NonNull OptionsFilter optionsFilter) {
        //noinspection ConstantConditions
        return optionsFilter != null && extrasFilters != null && extrasFilters.remove(optionsFilter);
    }

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
     * 全局暂停下载图片？开启后将不再从网络下载图片，只影响 display 请求和 load 请求
     */
    public boolean isPauseDownload() {
        return pauseDownloadOptionsFilter != null;
    }

    /**
     * 设置全局暂停下载图片，开启后将不再从网络下载图片，只影响 display 请求和 load 请求
     */
    public void setPauseDownload(boolean pauseDownload) {
        if (isPauseDownload() != pauseDownload) {
            this.pauseDownloadOptionsFilter = pauseDownload ? new PauseDownloadOptionsFilter() : null;
        }
    }

    /**
     * 全局暂停加载新图片？开启后将只从内存缓存中找寻图片，只影响 display 请求
     */
    public boolean isPauseLoad() {
        return pauseLoadOptionsFilter != null;
    }

    /**
     * 设置全局暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响 display 请求
     */
    public void setPauseLoad(boolean pauseLoad) {
        if (isPauseLoad() != pauseLoad) {
            this.pauseLoadOptionsFilter = pauseLoad ? new PauseLoadOptionsFilter() : null;
        }
    }

    /**
     * 全局使用低质量的图片？
     */
    public boolean isLowQualityImage() {
        return lowQualityOptionsFilter != null;
    }

    /**
     * 设置全局使用低质量的图片
     */
    public void setLowQualityImage(boolean lowQualityImage) {
        if (isLowQualityImage() != lowQualityImage) {
            this.lowQualityOptionsFilter = lowQualityImage ? new LowQualityOptionsFilter() : null;
        }
    }

    /**
     * 全局解码时优先考虑速度还是质量 (默认优先考虑速度)
     *
     * @return true：质量；false：速度
     */
    public boolean isInPreferQualityOverSpeed() {
        return inPreferQualityOverSpeedOptionsFilter != null;
    }

    /**
     * 设置全局解码时优先考虑速度还是质量 (默认优先考虑速度)
     *
     * @param inPreferQualityOverSpeed true：质量；false：速度
     */
    public void setInPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        if (isInPreferQualityOverSpeed() != inPreferQualityOverSpeed) {
            this.inPreferQualityOverSpeedOptionsFilter = inPreferQualityOverSpeed ? new InPreferQualityOverSpeedOptionsFilter() : null;
        }
    }

    /**
     * 全局移动网络下暂停下载？只影响display请求和load请求
     */
    @SuppressWarnings("unused")
    public boolean isGlobalMobileNetworkGlobalPauseDownload() {
        return mobileNetworkPauseDownloadController != null && mobileNetworkPauseDownloadController.isOpened();
    }

    /**
     * 设置开启移动网络下暂停下载的功能，只影响 display 请求和 load 请求
     */
    public void setGlobalMobileNetworkPauseDownload(Configuration configuration, boolean globalMobileNetworkPauseDownload) {
        if (isGlobalMobileNetworkGlobalPauseDownload() != globalMobileNetworkPauseDownload) {
            if (globalMobileNetworkPauseDownload) {
                if (this.mobileNetworkPauseDownloadController == null) {
                    this.mobileNetworkPauseDownloadController = new MobileNetworkPauseDownloadController(configuration);
                }
                this.mobileNetworkPauseDownloadController.setOpened(true);
            } else {
                if (this.mobileNetworkPauseDownloadController != null) {
                    this.mobileNetworkPauseDownloadController.setOpened(false);
                }
            }
        }
    }

    @NonNull
    @Override
    public String getKey() {
        return "OptionsFilterRegistry";
    }
}
