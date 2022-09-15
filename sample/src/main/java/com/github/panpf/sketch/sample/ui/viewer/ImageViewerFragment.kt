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
package com.github.panpf.sketch.sample.ui.viewer

import android.Manifest
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.databinding.ImageViewerFragmentBinding
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.sample.ui.base.parentViewModels
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.stateimage.InexactlyMemoryCacheStateImage
import com.github.panpf.sketch.sample.util.observeWithFragmentView
import com.github.panpf.sketch.viewability.showSectorProgressIndicator
import com.github.panpf.tools4a.toast.ktx.showLongToast
import kotlinx.coroutines.launch

class ImageViewerFragment : BindingFragment<ImageViewerFragmentBinding>() {

    private val args by navArgs<ImageViewerFragmentArgs>()
    private val viewModel by viewModels<ImageViewerViewModel>()
    private val pagerViewModel by parentViewModels<ImageViewerPagerViewModel>()
    private val requestPermissionResult = registerForActivityResult(RequestPermission()) {
        lifecycleScope.launch {
            val imageUri = if (prefsService.showOriginImage.value) {
                args.originImageUri
            } else {
                args.previewImageUri ?: args.originImageUri
            }
            handleActionResult(viewModel.save(imageUri))
        }
    }

    override fun onViewCreated(binding: ImageViewerFragmentBinding, savedInstanceState: Bundle?) {
        binding.root.background = null

        binding.imageViewerZoomImage.apply {
            showSectorProgressIndicator()
            prefsService.readModeEnabled.stateFlow.observeWithFragmentView(this@ImageViewerFragment) {
                readModeEnabled = it
            }
            setOnClickListener {
                findNavController().popBackStack()
            }
            setOnLongClickListener {
                startImageInfoDialog(this)
                true
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(State.STARTED) {
                    prefsService.showOriginImage.stateFlow.collect {
                        displayImage(if (it) args.originImageUri else args.previewImageUri) {
                            placeholder(InexactlyMemoryCacheStateImage(uri = args.thumbnailImageUrl))
                            crossfade(fadeStart = false)
                            lifecycle(viewLifecycleOwner.lifecycle)
                            listener(onError = { _, _ ->
                                showLongToast("Image display failure")
                            })
                        }
                    }
                }
            }
        }

        pagerViewModel.apply {
            shareEvent.listen(viewLifecycleOwner) {
                if (isResumed) {
                    lifecycleScope.launch {
                        val imageUri = if (prefsService.showOriginImage.value) {
                            args.originImageUri
                        } else {
                            args.previewImageUri ?: args.originImageUri
                        }
                        handleActionResult(viewModel.share(imageUri))
                    }
                }
            }
            saveEvent.listen(viewLifecycleOwner) {
                if (isResumed) {
                    requestPermissionResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            rotateEvent.listen(viewLifecycleOwner) {
                if (isResumed) {
                    binding.imageViewerZoomImage.rotateBy(90)
                }
            }
            infoEvent.listen(viewLifecycleOwner) {
                if (isResumed) {
                    startImageInfoDialog(binding.imageViewerZoomImage)
                }
            }
        }
    }

    private fun startImageInfoDialog(imageView: ImageView) {
        val arguments1 =
            ImageInfoDialogFragment.createDirectionsFromImageView(imageView, null).arguments
        childFragmentManager.beginTransaction()
            .add(ImageInfoDialogFragment().apply {
                arguments = arguments1
            }, null)
            .commit()
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
                previewImageUri = data.previewUrl,
                thumbnailImageUrl = data.thumbnailUrl,
            ).toBundle()
        }
    }
}