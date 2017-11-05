/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.sample

import android.app.Application
import android.os.Build

import com.squareup.leakcanary.LeakCanary
import com.tencent.bugly.crashreport.CrashReport

import me.panpf.sketch.Sketch

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrashReport.initCrashReport(baseContext, "900007777", BuildConfig.DEBUG)

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this)
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Sketch.with(baseContext).onTrimMemory(level)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Sketch.with(baseContext).onLowMemory()
        }
    }
}