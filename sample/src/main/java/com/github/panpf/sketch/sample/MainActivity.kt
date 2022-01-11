/*
 * Copyright 2013 Peng fei Pan
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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.github.panpf.sketch.sample.base.BaseActivity
import com.github.panpf.sketch.sample.databinding.ActivityMainBinding
import com.github.panpf.sketch.sample.service.NotificationService

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                finish()
            }
        }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = ActivityMainBinding.inflate(inflater, parent, false)

    override fun onInitData(binding: ActivityMainBinding, savedInstanceState: Bundle?) {
        startMultiProcess()
        requestPermission()
    }

    private fun startMultiProcess() {
        lifecycleScope.launchWhenResumed {
            startService(Intent(this@MainActivity, NotificationService::class.java))
        }
    }

    private fun requestPermission() {
        val permissionResult = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permissionResult != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // SketchZoomImageView gestures may trigger exceptions
        return try {
            super.dispatchTouchEvent(ev)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            true
        }
    }
}
