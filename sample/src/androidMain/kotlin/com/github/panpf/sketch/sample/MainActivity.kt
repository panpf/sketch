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

package com.github.panpf.sketch.sample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.github.panpf.sketch.sample.service.NotificationService
import com.github.panpf.sketch.sample.ui.ComposeMainActivity
import com.github.panpf.sketch.sample.ui.ViewMainActivity
import com.github.panpf.sketch.sample.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyDarkMode(appSettings)

        if (appSettings.composePage.value) {
            startActivity(Intent(this, ComposeMainActivity::class.java))
        } else {
            startActivity(Intent(this, ViewMainActivity::class.java))
        }
        finish()
    }

    override fun onFirstResume() {
        super.onFirstResume()
        // TODO Unable to execute because finish is in onCreate
        // It can only be executed here, not in onCreate.
        // Because when the app is started when the phone is locked, the app is in the background state in the onCreate method, so the app will crash when the service is started.
        startService(Intent(this@MainActivity, NotificationService::class.java))
    }
}