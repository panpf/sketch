/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.sample.ui.base

import androidx.appcompat.app.AppCompatActivity
import com.github.panpf.sketch.sample.AppSettings
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity() {

    protected val appSettings: AppSettings by inject()

    private var resumeCount = 0

    var resumed = false

    override fun onResume() {
        super.onResume()
        resumed = true
        resumeCount++
        if (resumeCount == 1) {
            onFirstResume()
        }
    }

    override fun onPause() {
        super.onPause()
        resumed = false
    }

    protected open fun onFirstResume() {

    }
}