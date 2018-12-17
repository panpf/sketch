/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.request;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import me.panpf.sketch.SLog;

/**
 * 顺风车管理器，用于解决重复下载、重复加载
 * <p>
 * 对于相同的请求（不同类型条件不一样），只要第一个请求执行完毕了，后续请求可以直接使用第一个请求的结果，那么我们可以将所有后续的请求都绑定在第一个请求上，
 * 等第一个请求执行完毕后直接将结果交给后续请求处理即可，对于这样的情况我们称之为顺风车
 */
public class FreeRideManager {
    private static final String NAME = "FreeRideManager";

    private final Object displayFreeRideProviderMapLock = new Object();
    private final Object downloadFreeRideProviderMapLock = new Object();
    private Map<String, DisplayFreeRide> displayFreeRideProviderMap;
    private Map<String, DownloadFreeRide> downloadFreeRideProviderMap;

    /**
     * 成为显示顺风车主
     */
    public void registerDisplayFreeRideProvider(DisplayFreeRide provider) {
        if (!provider.canByDisplayFreeRide()) {
            return;
        }

        synchronized (displayFreeRideProviderMapLock) {
            if (displayFreeRideProviderMap == null) {
                synchronized (this) {
                    if (displayFreeRideProviderMap == null) {
                        displayFreeRideProviderMap = new WeakHashMap<>();
                    }
                }
            }

            displayFreeRideProviderMap.put(provider.getDisplayFreeRideKey(), provider);

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "display. register free ride provider. %s",
                        provider.getDisplayFreeRideLog());
            }
        }
    }

    /**
     * 取消显示顺风车主身份并回调那些显示顺风车
     */
    public void unregisterDisplayFreeRideProvider(DisplayFreeRide provider) {
        if (!provider.canByDisplayFreeRide()) {
            return;
        }

        // 取消顺风车主身份
        DisplayFreeRide freeRideProvider = null;
        synchronized (displayFreeRideProviderMapLock) {
            if (displayFreeRideProviderMap != null) {
                freeRideProvider = displayFreeRideProviderMap.remove(provider.getDisplayFreeRideKey());
                if (freeRideProvider != null) {
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                        SLog.d(NAME, "display. unregister free ride provider. %s",
                                freeRideProvider.getDisplayFreeRideLog());
                    }
                }
            }
        }

        // 回调那些顺风车
        if (freeRideProvider != null) {
            Set<DisplayFreeRide> freeRideSet = freeRideProvider.getDisplayFreeRideSet();
            if (freeRideSet == null || freeRideSet.size() == 0) {
                return;
            }

            String providerId = freeRideProvider.getDisplayFreeRideLog();
            for (DisplayFreeRide childFreeRide : freeRideSet) {
                if (!childFreeRide.isCanceled()) {
                    boolean success = childFreeRide.processDisplayFreeRide();

                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                        SLog.d(NAME, "display. callback free ride. %s. %s  <-  %s",
                                success ? "success" : "failed", childFreeRide.getDisplayFreeRideLog(), providerId);
                    }
                } else {
                    SLog.w(NAME, "display. callback free ride. %s. %s  <-  %s",
                            "canceled", childFreeRide.getDisplayFreeRideLog(), providerId);
                }
            }
            freeRideSet.clear();
        }
    }

    /**
     * 坐个显示顺风车
     *
     * @param childFreeRide {@link DisplayFreeRide}
     * @return 坐上了
     */
    public boolean byDisplayFreeRide(DisplayFreeRide childFreeRide) {
        if (!childFreeRide.canByDisplayFreeRide()) {
            return false;
        }

        synchronized (displayFreeRideProviderMapLock) {
            DisplayFreeRide freeRideProvider = null;
            if (displayFreeRideProviderMap != null) {
                freeRideProvider = displayFreeRideProviderMap.get(childFreeRide.getDisplayFreeRideKey());
            }
            if (freeRideProvider == null) {
                return false;
            }

            freeRideProvider.byDisplayFreeRide(childFreeRide);

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "display. by free ride. %s -> %s",
                        childFreeRide.getDisplayFreeRideLog(), freeRideProvider.getDisplayFreeRideLog());
            }
            return true;
        }
    }

    /**
     * 成为下载顺风车主
     */
    public void registerDownloadFreeRideProvider(DownloadFreeRide provider) {
        if (!provider.canByDownloadFreeRide()) {
            return;
        }

        synchronized (downloadFreeRideProviderMapLock) {
            if (downloadFreeRideProviderMap == null) {
                synchronized (this) {
                    if (downloadFreeRideProviderMap == null) {
                        downloadFreeRideProviderMap = new WeakHashMap<>();
                    }
                }
            }

            downloadFreeRideProviderMap.put(provider.getDownloadFreeRideKey(), provider);

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "download. register free ride provider. %s",
                        provider.getDownloadFreeRideLog());
            }
        }
    }

    /**
     * 取消下载顺风车主身份并回调那些下载顺风车
     */
    public void unregisterDownloadFreeRideProvider(DownloadFreeRide provider) {
        if (!provider.canByDownloadFreeRide()) {
            return;
        }

        // 取消顺风车主身份
        DownloadFreeRide freeRideProvider = null;
        synchronized (downloadFreeRideProviderMapLock) {
            if (downloadFreeRideProviderMap != null) {
                freeRideProvider = downloadFreeRideProviderMap.remove(provider.getDownloadFreeRideKey());
                if (freeRideProvider != null) {
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                        SLog.d(NAME, "download. unregister free ride provider. %s",
                                freeRideProvider.getDownloadFreeRideLog());
                    }
                }
            }
        }

        // 回调那些顺风车
        if (freeRideProvider != null) {
            Set<DownloadFreeRide> freeRideSet = freeRideProvider.getDownloadFreeRideSet();
            if (freeRideSet == null || freeRideSet.size() == 0) {
                return;
            }

            String providerId = freeRideProvider.getDownloadFreeRideLog();
            for (DownloadFreeRide childFreeRide : freeRideSet) {
                if (!childFreeRide.isCanceled()) {
                    boolean success = childFreeRide.processDownloadFreeRide();

                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                        SLog.d(NAME, "download. callback free ride. %s. %s  <-  %s",
                                success ? "success" : "failed", childFreeRide.getDownloadFreeRideLog(), providerId);
                    }
                } else {
                    SLog.w(NAME, "download. callback free ride. %s. %s  <-  %s",
                            "canceled", childFreeRide.getDownloadFreeRideLog(), providerId);
                }
            }
            freeRideSet.clear();
        }
    }

    /**
     * 坐个下载顺风车
     *
     * @param childFreeRide {@link DownloadFreeRide}
     * @return 坐上了
     */
    public boolean byDownloadFreeRide(DownloadFreeRide childFreeRide) {
        if (!childFreeRide.canByDownloadFreeRide()) {
            return false;
        }

        synchronized (downloadFreeRideProviderMapLock) {
            DownloadFreeRide freeRideProvider = null;
            if (downloadFreeRideProviderMap != null) {
                freeRideProvider = downloadFreeRideProviderMap.get(childFreeRide.getDownloadFreeRideKey());
            }
            if (freeRideProvider == null) {
                return false;
            }

            freeRideProvider.byDownloadFreeRide(childFreeRide);

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "download. by free ride. %s -> %s",
                        childFreeRide.getDownloadFreeRideLog(), freeRideProvider.getDownloadFreeRideLog());
            }
            return true;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return NAME;
    }

    /**
     * 显示顺风车
     */
    public interface DisplayFreeRide {
        /**
         * 已取消
         */
        boolean isCanceled();

        /**
         * 获取显示顺风车KEY
         */
        String getDisplayFreeRideKey();

        /**
         * 获取日志
         */
        String getDisplayFreeRideLog();

        /**
         * 是否可以使用显示顺风车功能（不同类型条件不一样）
         */
        boolean canByDisplayFreeRide();

        /**
         * 让别人搭乘显示顺风车
         */
        void byDisplayFreeRide(DisplayFreeRide request);

        /**
         * 获取显示顺风车集合
         */
        Set<DisplayFreeRide> getDisplayFreeRideSet();

        /**
         * 执行结束，处理那些坐显示顺风车的请求
         *
         * @return 成功找到可以用的资源
         */
        boolean processDisplayFreeRide();
    }

    /**
     * 下载顺风车
     */
    public interface DownloadFreeRide {
        /**
         * 已取消
         */
        boolean isCanceled();

        /**
         * 获取下载顺风车KEY
         */
        String getDownloadFreeRideKey();

        /**
         * 获取日志
         */
        String getDownloadFreeRideLog();

        /**
         * 是否可以使用下载顺风车功能（不同类型条件不一样）
         */
        boolean canByDownloadFreeRide();

        /**
         * 让别人搭乘下载顺风车
         */
        void byDownloadFreeRide(DownloadFreeRide request);

        /**
         * 获取下载顺风车集合
         */
        Set<DownloadFreeRide> getDownloadFreeRideSet();

        /**
         * 执行结束，处理那些坐下载顺风车的请求
         *
         * @return 成功找到可以用的资源
         */
        boolean processDownloadFreeRide();
    }
}
