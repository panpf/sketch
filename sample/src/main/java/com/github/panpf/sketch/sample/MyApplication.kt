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

package com.github.panpf.sketch.sample

import androidx.multidex.MultiDexApplication
import com.github.panpf.sketch.BuildConfig
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.SketchFactory
import com.github.panpf.sketch.extensions.PauseLoadWhenScrollDisplayInterceptor
import com.github.panpf.sketch.gif.GifDecoder
import com.github.panpf.sketch.http.OkHttpStack
import com.github.panpf.sketch.request.SaveCellularTrafficDisplayInterceptor
import com.tencent.bugly.crashreport.CrashReport

class MyApplication : MultiDexApplication(), SketchFactory {

    override fun onCreate() {
        super.onCreate()
        CrashReport.initCrashReport(baseContext, "900007777", BuildConfig.DEBUG)
    }

    override fun newSketch(): Sketch = Sketch.new(this) {
        httpStack(OkHttpStack.Builder().build())
        addDisplayInterceptor(SaveCellularTrafficDisplayInterceptor())
        addDisplayInterceptor(PauseLoadWhenScrollDisplayInterceptor())
        components {
            addDecoder(GifDecoder.Factory())
        }
    }
}