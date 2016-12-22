/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.request;

import android.util.Log;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import me.xiaopan.sketch.Sketch;

/**
 * 顺风车管理器
 * <p/>
 * 对于相同的请求（不同类型条件不一样），只要第一个请求执行完毕了，后续请求可以直接使用第一个请求的结果，那么我们可以将所有后续的请求都绑定在第一个请求上，
 * 等第一个请求执行完毕后直接将结果交给后续请求处理即可，对于这样的情况我们称之为顺风车，主要用于解决重复下载、重复加载的情况
 */
public class FreeRideManager {
    private static final String LOG_NAME = "FreeRideManager";

    private final Object freeRideProviderMapLock = new Object();
    private Map<String, FreeRide> freeRideProviderMap;

    /**
     * 成为顺风车主
     */
    public void registerFreeRideProvider(DisplayRequest request) {
        if (!request.canByFreeRide()) {
            return;
        }

        synchronized (freeRideProviderMapLock) {
            if (freeRideProviderMap == null) {
                synchronized (this) {
                    if (freeRideProviderMap == null) {
                        freeRideProviderMap = new WeakHashMap<String, FreeRide>();
                    }
                }
            }

            freeRideProviderMap.put(request.getId(), request);

            if (Sketch.isDebugMode()) {
                Log.v(Sketch.TAG, String.format("%s. register free ride provider. %s",
                        LOG_NAME, request.getFreeRideId()));
            }
        }
    }

    /**
     * 取消顺风车主身份并回调那些顺风车
     */
    public void unregisterFreeRideProvider(DisplayRequest request) {
        if (!request.canByFreeRide()) {
            return;
        }

        // 取消顺风车主身份
        FreeRide freeRideProvider = null;
        synchronized (freeRideProviderMapLock) {
            if (freeRideProviderMap != null) {
                freeRideProvider = freeRideProviderMap.remove(request.getId());
                if (freeRideProvider != null && Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, String.format("%s. unregister free ride provider. %s",
                            LOG_NAME, freeRideProvider.getFreeRideId()));
                }
            }
        }

        // 回调那些顺风车
        if (freeRideProvider != null) {
            Set<FreeRide> freeRideSet = freeRideProvider.getFreeRideSet();
            if (freeRideSet == null || freeRideSet.size() == 0) {
                return;
            }

            String providerId = freeRideProvider.getFreeRideId();
            for (FreeRide freeRide : freeRideSet) {
                boolean success = freeRide.processFreeRideRequests();

                if (Sketch.isDebugMode()) {
                    String result = success ? "success" : "failed";
                    Log.d(Sketch.TAG, String.format("%s. callback free ride. %s. %s  <------  %s",
                            LOG_NAME, result, freeRide.getFreeRideId(), providerId));
                }
            }
            freeRideSet.clear();
        }
    }

    /**
     * 坐个顺风车
     *
     * @param request {@link DisplayRequest}
     * @return 坐上了
     */
    public boolean byFreeRide(DisplayRequest request) {
        if (!request.canByFreeRide()) {
            return false;
        }

        synchronized (freeRideProviderMapLock) {
            FreeRide freeRideProvider = null;
            if (freeRideProviderMap != null) {
                freeRideProvider = freeRideProviderMap.get(request.getId());
            }
            if (freeRideProvider == null) {
                return false;
            }

            freeRideProvider.byFreeRide(request);

            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, String.format("%s. by free ride success. %s  ------>  %s",
                        LOG_NAME, request.getFreeRideId(), freeRideProvider.getFreeRideId()));
            }
            return true;
        }
    }

    public interface FreeRide {
        /**
         * 获取ID，用户日志
         */
        String getFreeRideId();

        /**
         * 是否可以使用顺风车功能（不同类型条件不一样）
         */
        boolean canByFreeRide();

        /**
         * 让别人搭乘顺风车
         */
        void byFreeRide(FreeRide request);

        /**
         * 获取顺风车集合
         */
        Set<FreeRide> getFreeRideSet();

        /**
         * 执行结束，处理那些坐顺风车的请求
         *
         * @return 成功找到可以用的资源
         */
        boolean processFreeRideRequests();
    }
}
