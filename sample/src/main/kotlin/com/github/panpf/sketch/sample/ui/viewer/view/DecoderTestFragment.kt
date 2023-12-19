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

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.request.LoadState.Error
import com.github.panpf.sketch.sample.databinding.ImageFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.base.parentViewModels
import com.github.panpf.sketch.sample.ui.test.format.DecoderTestFragmentArgs
import com.github.panpf.sketch.sample.ui.test.format.DecoderTestViewModel
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.viewability.showDataFromLogo
import com.github.panpf.sketch.viewability.showProgressIndicator

class DecoderTestFragment : BaseBindingFragment<ImageFragmentBinding>() {

    private val args by navArgs<DecoderTestFragmentArgs>()
    private val viewModel by parentViewModels<DecoderTestViewModel>()

    override fun onViewCreated(binding: ImageFragmentBinding, savedInstanceState: Bundle?) {
        viewModel.data
            .repeatCollectWithLifecycle(viewLifecycleOwner, Lifecycle.State.STARTED) { list ->
                val item = list[args.position]

                if (item.minAPI == null || Build.VERSION.SDK_INT >= item.minAPI) {
                    binding.imageImage.apply {
                        showDataFromLogo()
                        showProgressIndicator(SectorProgressDrawable())
                        displayImage(item.imageUri) {
                            memoryCachePolicy(DISABLED)
                            resultCachePolicy(DISABLED)
                            downloadCachePolicy(DISABLED)
                            when (val decoder = item.imageDecoder) {
                                is BitmapDecoder.Factory -> components {
                                    addBitmapDecoder(decoder)
                                }

                                is DrawableDecoder.Factory -> components {
                                    addDrawableDecoder(decoder)
                                }
                            }
                        }
                    }

                    binding.imageState.apply {
                        binding.imageImage.requestState.loadState
                            .repeatCollectWithLifecycle(
                                viewLifecycleOwner,
                                Lifecycle.State.STARTED
                            ) {
                                if (it is Error) {
                                    errorWithRetry {
                                        SketchUtils.restart(binding.imageImage)
                                    }
                                } else {
                                    gone()
                                }
                            }
                    }
                } else {
                    binding.imageState.error(message = "This format requires API ${item.minAPI} or higher")
                }
            }
    }

    class ItemFactory : FragmentItemFactory<Int>(Int::class) {

        override fun createFragment(
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: Int
        ): Fragment = DecoderTestFragment().apply {
            arguments = DecoderTestFragmentArgs(data).toBundle()
        }
    }
}