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

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationRotateBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.RotateTransformation
import org.koin.androidx.viewmodel.ext.android.viewModel

class RotateTransformationTestFragment :
    BaseBindingFragment<FragmentTestTransformationRotateBinding>() {

    private val rotateTransformationTestViewModel by viewModel<RotateTransformationTestViewModel>()

    override fun onViewCreated(
        binding: FragmentTestTransformationRotateBinding,
        savedInstanceState: Bundle?
    ) {
        rotateTransformationTestViewModel.rotateData.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.CREATED
        ) {
            binding.myImage.loadImage(ResourceImages.statics.first().uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(RotateTransformation(it))
                colorType(Bitmap.Config.RGB_565) // To test automatic conversion Config
            }

            binding.degreesText.text = "$it"
        }

        binding.degreesSeekBar.apply {
            max = 360
            progress = rotateTransformationTestViewModel.rotateData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    rotateTransformationTestViewModel.changeRotate(progress.coerceAtLeast(0))
                }
            })
        }
    }
}