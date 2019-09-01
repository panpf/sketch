/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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
import com.squareup.leakcanary.LeakCanary
import com.tencent.bugly.crashreport.CrashReport

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

//        registerActivityLifecycleCallbacks(ActivityEventRegistrar())

        CrashReport.initCrashReport(baseContext, "900007777", BuildConfig.DEBUG)

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this)
        }
    }
}