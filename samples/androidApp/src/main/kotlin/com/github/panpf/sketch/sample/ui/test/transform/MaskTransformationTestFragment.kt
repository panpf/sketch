/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationMaskBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.MaskTransformation
import org.koin.androidx.viewmodel.ext.android.viewModel

class MaskTransformationTestFragment :
    BaseBindingFragment<FragmentTestTransformationMaskBinding>() {

    private val maskTransformationTestViewModel by viewModel<MaskTransformationTestViewModel>()

    override fun onViewCreated(
        binding: FragmentTestTransformationMaskBinding,
        savedInstanceState: Bundle?
    ) {
        maskTransformationTestViewModel.maskColorData.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.CREATED
        ) {
            binding.redButton.isChecked = it == MaskTransformationTestViewModel.MaskColor.RED
            binding.greenButton.isChecked = it == MaskTransformationTestViewModel.MaskColor.GREEN
            binding.blueButton.isChecked = it == MaskTransformationTestViewModel.MaskColor.BLUE

            binding.myImage.loadImage(ComposeResImageFiles.png.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(MaskTransformation(it.colorInt))
            }
        }

        binding.redButton.setOnClickListener {
            maskTransformationTestViewModel.changeMaskColor(MaskTransformationTestViewModel.MaskColor.RED)
        }

        binding.greenButton.setOnClickListener {
            maskTransformationTestViewModel.changeMaskColor(MaskTransformationTestViewModel.MaskColor.GREEN)
        }

        binding.blueButton.setOnClickListener {
            maskTransformationTestViewModel.changeMaskColor(MaskTransformationTestViewModel.MaskColor.BLUE)
        }
    }
}