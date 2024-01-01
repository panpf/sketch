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
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.displayResult
import com.github.panpf.sketch.request.LoadState.Error
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.FragmentImageViewerBinding
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.base.StatusBarTextStyle
import com.github.panpf.sketch.sample.ui.base.StatusBarTextStyle.White
import com.github.panpf.sketch.sample.ui.base.parentViewModels
import com.github.panpf.sketch.sample.ui.util.createDayNightSectorProgressDrawable
import com.github.panpf.sketch.sample.util.WithDataActivityResultContracts
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.sample.util.registerForActivityResult
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.stateimage.ThumbnailMemoryCacheStateImage
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.viewability.showProgressIndicator
import com.github.panpf.tools4k.lang.asOrThrow
import com.github.panpf.zoomimage.view.zoom.ScrollBarSpec
import com.github.panpf.zoomimage.zoom.AlignmentCompat
import com.github.panpf.zoomimage.zoom.ContentScaleCompat
import com.github.panpf.zoomimage.zoom.ReadMode
import com.github.panpf.zoomimage.zoom.valueOf
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class PhotoViewerViewFragment : BaseBindingFragment<FragmentImageViewerBinding>() {

    private val args by navArgs<PhotoViewerViewFragmentArgs>()
    private val pagerViewModel by parentViewModels<PhotoPagerViewModel>()
    private val requestPermissionResult =
        registerForActivityResult(WithDataActivityResultContracts.RequestPermission())

    override var statusBarTextStyle: StatusBarTextStyle? = White
    override var isPage = false

    override fun onViewCreated(binding: FragmentImageViewerBinding, savedInstanceState: Bundle?) {
        binding.zoomImage.apply {
            appSettingsService.scrollBarEnabled
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    scrollBar = if (it) ScrollBarSpec.Default else null
                }
            zoomable.apply {
                appSettingsService.readModeEnabled
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                        readModeState.value = if (it) ReadMode.Default else null
                    }
                appSettingsService.contentScale
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                        contentScaleState.value = ContentScaleCompat.valueOf(it)
                    }
                appSettingsService.alignment
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                        alignmentState.value = AlignmentCompat.valueOf(it)
                    }
            }
            subsampling.apply {
                appSettingsService.showTileBounds
                    .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                        showTileBoundsState.value = it
                    }
            }

            showProgressIndicator(
                createDayNightSectorProgressDrawable(requireContext())
            )

            setOnClickListener {
                findNavController().popBackStack()
            }

            setOnLongClickListener {
                startImageInfoDialog(this)
                true
            }

            appSettingsService.viewersCombinedFlow
                .ignoreFirst()
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    displayImage(binding)
                }
            appSettingsService.showOriginImage
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    displayImage(binding)
                }
        }

        binding.shareIcon.setOnClickListener {
            share()
        }

        binding.saveIcon.setOnClickListener {
            save()
        }

        binding.zoomIcon.apply {
            val zoomable = binding.zoomImage.zoomable
            setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val nextStepScale = zoomable.getNextStepScale()
                    zoomable.scale(nextStepScale, animated = true)
                }
            }
            zoomable.transformState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    val zoomIn =
                        zoomable.getNextStepScale() > zoomable.transformState.value.scaleX
                    if (zoomIn) {
                        setImageResource(R.drawable.ic_zoom_in)
                    } else {
                        setImageResource(R.drawable.ic_zoom_out)
                    }
                }
        }

        binding.rotateIcon.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val zoomable = binding.zoomImage.zoomable
                zoomable.rotate(zoomable.transformState.value.rotation.roundToInt() + 90)
            }
        }

        binding.infoIcon.setOnClickListener {
            startImageInfoDialog(binding.zoomImage)
        }

        pagerViewModel.buttonBgColor.repeatCollectWithLifecycle(
            owner = viewLifecycleOwner,
            state = State.STARTED
        ) { color ->
            listOf(
                binding.shareIcon,
                binding.saveIcon,
                binding.zoomIcon,
                binding.rotateIcon,
                binding.infoIcon
            ).forEach {
                it.background.asOrThrow<GradientDrawable>().setColor(color)
            }
        }
    }

    private fun getImageUrl(): String {
        return if (appSettingsService.showOriginImage.value) {
            args.originImageUri
        } else {
            args.previewImageUri ?: args.originImageUri
        }
    }

    private fun displayImage(binding: FragmentImageViewerBinding) {
        val imageUri = getImageUrl()
        binding.zoomImage.displayImage(imageUri) {
            merge(appSettingsService.buildViewerImageOptions())
            placeholder(ThumbnailMemoryCacheStateImage(uri = args.thumbnailImageUrl))
            crossfade(fadeStart = false)
        }

        binding.smallState.apply {
            binding.zoomImage.requestState.loadState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    if (it is Error) {
                        error {
                            retryAction {
                                SketchUtils.restart(binding.zoomImage)
                            }
                        }
                    } else {
                        gone()
                    }
                }
        }
    }

    private fun share() {
        val imageUri = getImageUrl()
        lifecycleScope.launch {
            handleActionResult(pagerViewModel.share(imageUri))
        }
    }

    private fun save() {
        val imageUri = getImageUrl()
        val input = WithDataActivityResultContracts.RequestPermission.Input(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) {
            lifecycleScope.launch {
                handleActionResult(pagerViewModel.save(imageUri))
            }
        }
        requestPermissionResult.launch(input)
    }

    private fun startImageInfoDialog(imageView: ImageView) {
        val displayResult = imageView.displayResult
        findNavController()
            .navigate(PhotoInfoDialogFragment.createNavDirections(displayResult))
    }

    class ItemFactory : FragmentItemFactory<ImageDetail>(ImageDetail::class) {

        override fun createFragment(
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: ImageDetail
        ): Fragment = PhotoViewerViewFragment().apply {
            arguments = PhotoViewerViewFragmentArgs(
                itemIndex = bindingAdapterPosition,
                originImageUri = data.originUrl,
                previewImageUri = data.mediumUrl,
                thumbnailImageUrl = data.thumbnailUrl,
            ).toBundle()
        }
    }
}