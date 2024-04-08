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
package com.github.panpf.sketch.sample.ui.gallery

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScaleTransition
import com.github.panpf.sketch.sample.ui.MyEvents
import com.github.panpf.sketch.sample.ui.base.BaseFragment
import com.github.panpf.sketch.sample.ui.base.parentViewModels
import com.github.panpf.sketch.sample.ui.theme.AppTheme
import com.github.panpf.sketch.sample.util.WithDataActivityResultContracts
import com.github.panpf.sketch.sample.util.registerForActivityResult
import kotlinx.coroutines.launch

class ComposeHomeFragment : BaseFragment() {

    private val photoActionViewModel by parentViewModels<PhotoActionViewModel>()
    private val requestPermissionResult =
        registerForActivityResult(WithDataActivityResultContracts.RequestPermission())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view as ComposeView).setContent {
            AppTheme {
                Navigator(HomeScreen) { navigator ->
                    ScaleTransition(navigator = navigator)
                    lightStatusAndNavigationBar = navigator.lastItem !is PhotoPagerScreen
                }
            }
        }

        val context = view.context
        viewLifecycleOwner.lifecycleScope.launch {
            MyEvents.toastFlow.collect {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            MyEvents.savePhotoFlow.collect {
                save(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            MyEvents.sharePhotoFlow.collect {
                share(it)
            }
        }
    }

    private fun share(imageUri: String) {
        lifecycleScope.launch {
            handleActionResult(photoActionViewModel.share(imageUri))
        }
    }

    private fun save(imageUri: String) {
        val input = WithDataActivityResultContracts.RequestPermission.Input(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) {
            lifecycleScope.launch {
                handleActionResult(photoActionViewModel.save(imageUri))
            }
        }
        requestPermissionResult.launch(input)
    }
}