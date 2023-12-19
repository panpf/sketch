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
package com.github.panpf.sketch.sample.ui.test.transform

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationCircleCropBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.CircleCropTransformation

class CircleCropTransformationTestFragment :
    BaseBindingFragment<FragmentTestTransformationCircleCropBinding>() {

    val viewModel by viewModels<CircleCropTransformationTestViewModel>()

    override fun onViewCreated(
        binding: FragmentTestTransformationCircleCropBinding,
        savedInstanceState: Bundle?
    ) {
        viewModel.scaleData.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            binding.startButton.isChecked = it == START_CROP
            binding.centerButton.isChecked = it == CENTER_CROP
            binding.endButton.isChecked = it == END_CROP
            binding.fillButton.isChecked = it == FILL

            binding.myImage.displayImage(AssetImages.statics.first()) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                resizeScale(it)
                addTransformations(CircleCropTransformation())
            }
        }

        binding.startButton.setOnClickListener {
            viewModel.changeScale(START_CROP)
        }

        binding.centerButton.setOnClickListener {
            viewModel.changeScale(CENTER_CROP)
        }

        binding.endButton.setOnClickListener {
            viewModel.changeScale(END_CROP)
        }

        binding.fillButton.setOnClickListener {
            viewModel.changeScale(FILL)
        }
    }
}