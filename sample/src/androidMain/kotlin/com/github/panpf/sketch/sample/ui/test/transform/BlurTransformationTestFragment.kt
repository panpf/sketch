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
import android.widget.SeekBar
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationBlurBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.BlurTransformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BlurTransformationTestFragment :
    BaseBindingFragment<FragmentTestTransformationBlurBinding>() {

    private val viewModel by viewModels<BlurTransformationTestViewModel>()

    override fun onViewCreated(
        binding: FragmentTestTransformationBlurBinding,
        savedInstanceState: Bundle?
    ) {
        viewModel.radiusData.repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
            updateImage(binding = binding)
            binding.valueText.text = "$it"
        }
        binding.seekBar.apply {
            max = 100
            progress = viewModel.radiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeRadius(progress.coerceAtLeast(1))
                }
            })
        }

        viewModel.maskColorData.repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
            updateImage(binding = binding)
        }
        binding.noneButton1.isChecked = true
        binding.noneButton1.setOnClickListener {
            viewModel.changeMaskColor(null)
        }
        binding.redButton.setOnClickListener {
            viewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.RED, 128))
        }
        binding.greenButton.setOnClickListener {
            viewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.GREEN, 128))
        }
        binding.blueButton.setOnClickListener {
            viewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.BLUE, 128))
        }

        viewModel.backgroundColorData
            .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
                updateImage(binding = binding)
            }
        binding.noneButton2.isChecked = true
        binding.noneButton2.setOnClickListener {
            viewModel.changeBackgroundColor(null)
        }
        binding.blackButton.setOnClickListener {
            viewModel.changeBackgroundColor(Color.BLACK)
        }
        binding.whiteButton.setOnClickListener {
            viewModel.changeBackgroundColor(Color.WHITE)
        }
    }

    private fun updateImage(binding: FragmentTestTransformationBlurBinding) {
        val radius = viewModel.radiusData.value
        val maskColor = viewModel.maskColorData.value
        val backgroundColor = viewModel.backgroundColorData.value

        binding.myImage1.loadImage(ResourceImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            addTransformations(
                BlurTransformation(
                    radius = radius,
                    maskColor = maskColor,
                    hasAlphaBitmapBgColor = backgroundColor
                )
            )
        }

        binding.myImage2.loadImage(ResourceImages.png.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            addTransformations(
                BlurTransformation(
                    radius = radius,
                    maskColor = maskColor,
                    hasAlphaBitmapBgColor = backgroundColor
                )
            )
        }
    }

    class BlurTransformationTestViewModel(application1: Application) :
        LifecycleAndroidViewModel(application1) {

        private val _radiusData = MutableStateFlow(30)
        val radiusData: StateFlow<Int> = _radiusData
        private val _maskColorData = MutableStateFlow<Int?>(null)
        val maskColorData: StateFlow<Int?> = _maskColorData
        private val _backgroundColorData = MutableStateFlow<Int?>(null)
        val backgroundColorData: StateFlow<Int?> = _backgroundColorData

        fun changeRadius(radius: Int) {
            _radiusData.value = radius
        }

        fun changeMaskColor(color: Int?) {
            _maskColorData.value = color
        }

        fun changeBackgroundColor(color: Int?) {
            _backgroundColorData.value = color
        }
    }
}