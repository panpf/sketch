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

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationMultiBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.MaskTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MultiTransformationTestFragment :
    BaseBindingFragment<FragmentTestTransformationMultiBinding>() {

    private val viewModel by viewModels<MultiTransformationTestViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        binding: FragmentTestTransformationMultiBinding,
        savedInstanceState: Bundle?
    ) {
        viewModel.rotateData.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            updateImage(binding)
            binding.degreesText.text = "$it"
        }
        binding.degreesSeekBar.apply {
            max = 360
            progress = viewModel.rotateData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeRotate(progress.coerceAtLeast(0))
                }
            })
        }

        viewModel.blurRadiusData.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            updateImage(binding)
            binding.blurValueText.text = "$it"
        }
        binding.blurSeekBar.apply {
            max = 100
            progress = viewModel.blurRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeBlurRadius(progress.coerceAtLeast(1))
                }
            })
        }

        viewModel.roundedCornersRadiusData.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.STARTED
        ) {
            updateImage(binding)
            binding.roundedCornersRadiusText.text = "$it"
        }
        binding.roundedCornersSeekBar.apply {
            max = 100
            progress = viewModel.roundedCornersRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeRoundedCornersRadius(progress)
                }
            })
        }

        viewModel.maskColorData.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            updateImage(binding)
        }
        binding.redButton.isChecked = true
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
    }

    private fun updateImage(binding: FragmentTestTransformationMultiBinding) {
        binding.myImage.displayImage(MyImages.statics.first().uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            addTransformations(
                BlurTransformation(radius = viewModel.blurRadiusData.value),
                RoundedCornersTransformation(viewModel.roundedCornersRadiusData.value.toFloat()),
                MaskTransformation(
                    maskColor = viewModel.maskColorData.value ?: Color.TRANSPARENT
                ),
                RotateTransformation(viewModel.rotateData.value),
            )
        }
    }

    class MultiTransformationTestViewModel(application1: Application) :
        LifecycleAndroidViewModel(application1) {

        private val _blurRadiusData = MutableStateFlow(30)
        val blurRadiusData: StateFlow<Int> = _blurRadiusData

        private val _maskColorData = MutableStateFlow<Int?>(ColorUtils.setAlphaComponent(Color.RED, 128))
        val maskColorData: StateFlow<Int?> = _maskColorData

        private val _roundedCornersRadiusData = MutableStateFlow(30)
        val roundedCornersRadiusData: StateFlow<Int> = _roundedCornersRadiusData

        private val _rotateData = MutableStateFlow(45)
        val rotateData: StateFlow<Int> = _rotateData

        fun changeRotate(rotate: Int) {
            _rotateData.value = rotate
        }

        fun changeRoundedCornersRadius(radius: Int) {
            _roundedCornersRadiusData.value = radius
        }

        fun changeBlurRadius(radius: Int) {
            _blurRadiusData.value = radius
        }

        fun changeMaskColor(color: Int?) {
            _maskColorData.value = color
        }
    }
}