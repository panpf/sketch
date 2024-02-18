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
package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.request.LoadState.Error
import com.github.panpf.sketch.sample.databinding.FragmentImageBinding
import com.github.panpf.sketch.sample.ui.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.util.createThemeSectorProgressDrawable
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.ability.showDataFromLogo
import com.github.panpf.sketch.ability.showProgressIndicator

class ImageFragment : BaseBindingFragment<FragmentImageBinding>() {

    private val args by navArgs<ImageFragmentArgs>()

    override var isPage = false

    override fun onViewCreated(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        binding.myImage.apply {
            showDataFromLogo()
            showProgressIndicator(createThemeSectorProgressDrawable(requireContext()))
            displayImage(args.url) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(DISABLED)
            }
        }

        binding.smallState.apply {
            binding.myImage.requestState.loadState
                .repeatCollectWithLifecycle(viewLifecycleOwner, Lifecycle.State.STARTED) {
                    if (it is Error) {
                        error {
                            retryAction {
                                SketchUtils.restart(binding.myImage)
                            }
                        }
                    } else {
                        gone()
                    }
                }
        }
    }

    class ItemFactory : FragmentItemFactory<ImageDetail>(ImageDetail::class) {

        override fun createFragment(
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: ImageDetail
        ): Fragment = ImageFragment().apply {
            arguments = ImageFragmentArgs(
                data.mediumUrl,
            ).toBundle()
        }
    }
}