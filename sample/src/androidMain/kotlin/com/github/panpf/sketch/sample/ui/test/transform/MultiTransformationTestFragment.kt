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
import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationMultiBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.MaskTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation

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
        binding.myImage.displayImage(AssetImages.statics.first().uri) {
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
}