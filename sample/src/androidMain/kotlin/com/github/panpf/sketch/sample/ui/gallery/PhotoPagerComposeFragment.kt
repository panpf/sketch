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
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.ui.model.ImageDetail
import com.github.panpf.sketch.sample.ui.MainFragmentDirections
import com.github.panpf.sketch.sample.ui.base.BaseComposeFragment
import com.github.panpf.sketch.sample.ui.base.StatusBarTextStyle
import com.github.panpf.sketch.sample.ui.base.StatusBarTextStyle.White
import com.github.panpf.sketch.sample.ui.screen.PhotoPager
import com.github.panpf.sketch.sample.ui.dialog.Page.ZOOM
import com.github.panpf.sketch.sample.util.WithDataActivityResultContracts
import com.github.panpf.sketch.sample.util.registerForActivityResult
import com.github.panpf.tools4a.toast.ktx.showLongToast
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class PhotoPagerComposeFragment : BaseComposeFragment() {

    private val args by navArgs<PhotoPagerComposeFragmentArgs>()
    private val imageList by lazy {
        Json.decodeFromString<List<ImageDetail>>(args.imageDetailJsonArray)
    }
    private val viewModel by viewModels<PhotoPagerViewModel>()
    private val requestPermissionResult =
        registerForActivityResult(WithDataActivityResultContracts.RequestPermission())

    override var statusBarTextStyle: StatusBarTextStyle? = White

    @Composable
    override fun DrawContent() {
        PhotoPager(
            imageList = imageList,
            totalCount = args.totalCount,
            startPosition = args.startPosition,
            initialPosition = args.initialPosition,
            onSettingsClick = {
                findNavController().navigate(
                    MainFragmentDirections.actionSettingsDialogFragment(ZOOM.name)
                )
            },
            onShowOriginClick = {
                val newValue = !appSettingsService.showOriginImage.value
                appSettingsService.showOriginImage.value = newValue
                if (newValue) {
                    showLongToast("Opened View original image")
                } else {
                    showLongToast("Closed View original image")
                }
            },
            onShareClick = {
                share(it)
            },
            onSaveClick = {
                save(it)
            },
            onImageClick = {
                findNavController().popBackStack()
            },
            onImageLongClick = { imageResult ->
                findNavController()
                    .navigate(PhotoInfoDialogFragment.createNavDirections(imageResult))
            },
            onInfoClick = { imageResult ->
                findNavController()
                    .navigate(PhotoInfoDialogFragment.createNavDirections(imageResult))
            },
        )
    }

    private fun share(image: ImageDetail) {
        val imageUri = if (appSettingsService.showOriginImage.value) {
            image.originUrl
        } else {
            image.mediumUrl ?: image.originUrl
        }
        lifecycleScope.launch {
            handleActionResult(viewModel.share(imageUri))
        }
    }

    private fun save(image: ImageDetail) {
        val imageUri = if (appSettingsService.showOriginImage.value) {
            image.originUrl
        } else {
            image.mediumUrl ?: image.originUrl
        }
        val input = WithDataActivityResultContracts.RequestPermission.Input(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) {
            lifecycleScope.launch {
                handleActionResult(viewModel.save(imageUri))
            }
        }
        requestPermissionResult.launch(input)
    }
}