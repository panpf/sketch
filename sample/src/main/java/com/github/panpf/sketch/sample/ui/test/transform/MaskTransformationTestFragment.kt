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

import android.graphics.Color
import android.os.Bundle
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.MaskTransformationTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.MaskTransformation

class MaskTransformationTestFragment : BindingFragment<MaskTransformationTestFragmentBinding>() {

    private val viewModel by viewModels<MaskTransformationTestViewModel>()

    override fun onViewCreated(
        binding: MaskTransformationTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        viewModel.maskColorData.observe(viewLifecycleOwner) {
            binding.maskTransformationTestImage.displayImage(AssetImages.STATICS.first()) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(CircleCropTransformation(), MaskTransformation(it))
            }
        }

        binding.maskTransformationTestRedButton.isChecked = true

        binding.maskTransformationTestRedButton.setOnClickListener {
            viewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.RED, 128))
        }

        binding.maskTransformationTestGreenButton.setOnClickListener {
            viewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.GREEN, 128))
        }

        binding.maskTransformationTestBlueButton.setOnClickListener {
            viewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.BLUE, 128))
        }
    }
}