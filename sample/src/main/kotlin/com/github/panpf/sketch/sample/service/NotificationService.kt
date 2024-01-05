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
package com.github.panpf.sketch.sample.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.execute
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sketch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * This Service is just to test the compatibility of Sketch under multi-process
 */
class NotificationService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // Test whether result LruDiskCache:67 can use different cache folders under multi-process
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch(Dispatchers.Main) {
            ImageRequest(this@NotificationService, AssetImages.statics.first().uri) {
                resize(200, 200)
            }.execute()

            val cacheDirName = sketch.resultCache.directory.name
            require(cacheDirName.startsWith("result") && cacheDirName != "result") {
                cacheDirName
            }

            stopSelf()
        }
    }
}
