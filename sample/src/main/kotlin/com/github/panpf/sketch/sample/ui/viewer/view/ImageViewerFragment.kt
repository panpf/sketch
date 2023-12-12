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
package com.github.panpf.sketch.sample.ui.viewer.view

import android.os.Bundle
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.displayResult
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.ImageViewerFragmentBinding
import com.github.panpf.sketch.sample.eventService
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.stateimage.ThumbnailMemoryCacheStateImage
import com.github.panpf.sketch.viewability.showSectorProgressIndicator
import com.github.panpf.zoomimage.view.zoom.ScrollBarSpec
import com.github.panpf.zoomimage.zoom.AlignmentCompat
import com.github.panpf.zoomimage.zoom.ContentScaleCompat
import com.github.panpf.zoomimage.zoom.ReadMode
import com.github.panpf.zoomimage.zoom.valueOf
import kotlin.math.roundToInt

class ImageViewerFragment : BindingFragment<ImageViewerFragmentBinding>() {

    private val args by navArgs<ImageViewerFragmentArgs>()

    override fun onViewCreated(binding: ImageViewerFragmentBinding, savedInstanceState: Bundle?) {
        binding.root.background = null

        binding.imageViewerRetryButton.setOnClickListener {
            displayImage(binding)
        }

        binding.imageViewerZoomImage.apply {
            appSettingsService.scrollBarEnabled.stateFlow
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    scrollBar = if (it) ScrollBarSpec.Default else null
                }
            zoomable.apply {
                appSettingsService.readModeEnabled.stateFlow
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                        readModeState.value = if (it) ReadMode.Default else null
                    }
                appSettingsService.contentScale.stateFlow
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                        contentScaleState.value = ContentScaleCompat.valueOf(it)
                    }
                appSettingsService.alignment.stateFlow
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                        alignmentState.value = AlignmentCompat.valueOf(it)
                    }
                eventService.viewerPagerRotateEvent
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                        rotate(transformState.value.rotation.roundToInt() + 90)
                    }
            }
            subsampling.apply {
                appSettingsService.showTileBounds.stateFlow
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                        showTileBoundsState.value = it
                    }
            }

            showSectorProgressIndicator()

            setOnClickListener {
                findNavController().popBackStack()
            }

            setOnLongClickListener {
                startImageInfoDialog(this)
                true
            }
            eventService.viewerPagerInfoEvent
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    startImageInfoDialog(this)
                }

            appSettingsService.viewersCombinedFlow
                .ignoreFirst()
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    displayImage(binding)
                }
            appSettingsService.showOriginImage.stateFlow
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    displayImage(binding)
                }
        }
    }

    private fun displayImage(binding: ImageViewerFragmentBinding) {
        val showOriginImage: Boolean = appSettingsService.showOriginImage.stateFlow.value
        val uri = if (showOriginImage) args.originImageUri else args.previewImageUri
        binding.imageViewerZoomImage.displayImage(uri) {
            merge(appSettingsService.buildViewerImageOptions())
            placeholder(ThumbnailMemoryCacheStateImage(uri = args.thumbnailImageUrl))
            crossfade(fadeStart = false)
            listener(
                onStart = {
                    binding.imageViewerErrorLayout.isVisible = false
                },
                onError = { _, _ ->
                    binding.imageViewerErrorLayout.isVisible = true
                }
            )
        }
    }

    private fun startImageInfoDialog(imageView: ImageView) {
        val displayResult = imageView.displayResult
        findNavController()
            .navigate(ImageInfoDialogFragment.createNavDirections(displayResult))
    }

    class ItemFactory : FragmentItemFactory<ImageDetail>(ImageDetail::class) {

        override fun createFragment(
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: ImageDetail
        ): Fragment = ImageViewerFragment().apply {
            arguments = ImageViewerFragmentArgs(
                position = data.position,
                originImageUri = data.originUrl,
                previewImageUri = data.mediumUrl,
                thumbnailImageUrl = data.thumbnailUrl,
            ).toBundle()
        }
    }
}