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
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.RoundedCornersTransformationTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.transform.RoundedCornersTransformation

class RoundedCornersTransformationTestFragment :
    BindingFragment<RoundedCornersTransformationTestFragmentBinding>() {

    private val viewModel by viewModels<RoundedCornersTransformationTestViewModel>()

    override fun onViewCreated(
        binding: RoundedCornersTransformationTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        viewModel.radiusData.observe(viewLifecycleOwner) {
            binding.roundedCornersTransformationTestImage.displayImage(AssetImages.STATICS.first()) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(RoundedCornersTransformation(it.toFloat()))
            }

            binding.roundedCornersTransformationTestValueText.text =
                "%d/%d".format(it, binding.roundedCornersTransformationTestSeekBar.max)
        }

        binding.roundedCornersTransformationTestSeekBar.apply {
            max = 100
            progress = viewModel.radiusData.value!!
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeRadius(progress)
                }
            })
        }
    }
}