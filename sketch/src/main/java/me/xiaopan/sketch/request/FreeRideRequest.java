/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

import java.util.HashSet;
import java.util.Set;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 支持顺风车的请求
 */
abstract class FreeRideRequest extends AsyncRequest implements FreeRideManager.FreeRide {
    private Set<FreeRideManager.FreeRide> freeRideSet;

    public FreeRideRequest(Sketch sketch, DownloadInfo info) {
        super(sketch, info);
    }

    @Override
    public String getFreeRideId() {
        return String.format("%s@%s", SketchUtils.toHexString(this), getId());
    }

    @Override
    public boolean canByFreeRide() {
        return false;
    }

    @Override
    public synchronized void byFreeRide(FreeRideManager.FreeRide request) {
        if (freeRideSet == null) {
            synchronized (this) {
                if (freeRideSet == null) {
                    freeRideSet = new HashSet<FreeRideManager.FreeRide>();
                }
            }
        }

        freeRideSet.add(request);
    }

    @Override
    public Set<FreeRideManager.FreeRide> getFreeRideSet() {
        return freeRideSet;
    }

    @Override
    public synchronized boolean processFreeRideRequests() {
        return false;
    }
}