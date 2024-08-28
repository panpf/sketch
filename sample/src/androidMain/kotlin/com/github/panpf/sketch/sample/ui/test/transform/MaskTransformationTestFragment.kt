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

import android.app.Application
import android.graphics.Color
import android.os.Bundle
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationMaskBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.ui.test.transform.MaskTransformationTestFragment.MaskTransformationTestViewModel.MaskColor.RED
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.MaskTransformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MaskTransformationTestFragment :
    BaseBindingFragment<FragmentTestTransformationMaskBinding>() {

    private val viewModel by viewModels<MaskTransformationTestViewModel>()

    override fun onViewCreated(
        binding: FragmentTestTransformationMaskBinding,
        savedInstanceState: Bundle?
    ) {
        viewModel.maskColorData.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            binding.redButton.isChecked = it == MaskTransformationTestViewModel.MaskColor.RED
            binding.greenButton.isChecked = it == MaskTransformationTestViewModel.MaskColor.GREEN
            binding.blueButton.isChecked = it == MaskTransformationTestViewModel.MaskColor.BLUE

            binding.myImage.loadImage(ResourceImages.png.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(MaskTransformation(it.colorInt))
            }
        }

        binding.redButton.setOnClickListener {
            viewModel.changeMaskColor(MaskTransformationTestViewModel.MaskColor.RED)
        }

        binding.greenButton.setOnClickListener {
            viewModel.changeMaskColor(MaskTransformationTestViewModel.MaskColor.GREEN)
        }

        binding.blueButton.setOnClickListener {
            viewModel.changeMaskColor(MaskTransformationTestViewModel.MaskColor.BLUE)
        }
    }

    class MaskTransformationTestViewModel(application1: Application) :
        LifecycleAndroidViewModel(application1) {

        private val _maskColorData = MutableStateFlow(RED)
        val maskColorData: StateFlow<MaskColor> = _maskColorData

        fun changeMaskColor(maskColor: MaskColor) {
            _maskColorData.value = maskColor
        }

        enum class MaskColor(val colorInt: Int) {
            RED(ColorUtils.setAlphaComponent(Color.RED, 128)),
            GREEN(ColorUtils.setAlphaComponent(Color.GREEN, 128)),
            BLUE(ColorUtils.setAlphaComponent(Color.BLUE, 128))
        }
    }
}