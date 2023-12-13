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

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionInflater
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.ProgressIndicatorTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.viewability.ProgressIndicatorAbility
import com.github.panpf.sketch.viewability.ViewAbilityContainer
import com.github.panpf.sketch.viewability.removeProgressIndicator
import com.github.panpf.sketch.viewability.showMaskProgressIndicator
import com.github.panpf.sketch.viewability.showRingProgressIndicator
import com.github.panpf.sketch.viewability.showSectorProgressIndicator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class ProgressIndicatorTestFragment :
    ToolbarBindingFragment<ProgressIndicatorTestFragmentBinding>() {

    private var stepRunningJob: Job? = null
    private var fastRunningJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.my_move)
        }
    }

    override fun onViewCreated(
        toolbar: androidx.appcompat.widget.Toolbar,
        binding: ProgressIndicatorTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "ProgressIndicator"

        if (VERSION.SDK_INT >= 21) {
            binding.testIndicatorTestImage1.setImageResource(R.drawable.im_placeholder)
            binding.testIndicatorTestImage2.setImageResource(R.drawable.im_placeholder)
            binding.testIndicatorTestImage3.setImageResource(R.drawable.im_placeholder)
        } else {
            binding.testIndicatorTestImage1.setImageResource(R.drawable.im_placeholder_noicon)
            binding.testIndicatorTestImage2.setImageResource(R.drawable.im_placeholder_noicon)
            binding.testIndicatorTestImage3.setImageResource(R.drawable.im_placeholder_noicon)
        }

        binding.testIndicatorTestButton.setOnClickListener {
            step(binding)
        }

        binding.testIndicatorTestButton2.setOnClickListener {
            fast(binding)
        }

        binding.testIndicatorTestCheckBox1.setOnCheckedChangeListener { _, _ ->
            setupProgressIndicator(binding)
        }
        binding.testIndicatorTestCheckBox2.setOnCheckedChangeListener { _, _ ->
            setupProgressIndicator(binding)
        }

        setupProgressIndicator(binding)
        step(binding)
    }

    private fun setupProgressIndicator(binding: ProgressIndicatorTestFragmentBinding) {
        binding.testIndicatorTestImage1.removeProgressIndicator()
        binding.testIndicatorTestImage2.removeProgressIndicator()
        binding.testIndicatorTestImage3.removeProgressIndicator()

        val hiddenWhenIndeterminate = binding.testIndicatorTestCheckBox1.isChecked
        val hiddenWhenCompleted = binding.testIndicatorTestCheckBox2.isChecked
        val shortSteps = binding.testIndicatorTestCheckBox3.isChecked
        val stepAnimationDuration = if (shortSteps) 1000 else 300
        binding.testIndicatorTestImage1.showMaskProgressIndicator(
            hiddenWhenIndeterminate = hiddenWhenIndeterminate,
            hiddenWhenCompleted = hiddenWhenCompleted,
            stepAnimationDuration = stepAnimationDuration
        )
        binding.testIndicatorTestImage2.showSectorProgressIndicator(
            hiddenWhenIndeterminate = hiddenWhenIndeterminate,
            hiddenWhenCompleted = hiddenWhenCompleted,
            stepAnimationDuration = stepAnimationDuration
        )
        binding.testIndicatorTestImage3.showRingProgressIndicator(
            hiddenWhenIndeterminate = hiddenWhenIndeterminate,
            hiddenWhenCompleted = hiddenWhenCompleted,
            stepAnimationDuration = stepAnimationDuration
        )
    }

    private fun step(binding: ProgressIndicatorTestFragmentBinding) {
        fastRunningJob?.cancel()
        binding.testIndicatorTestButton2.isClickable = true

        val stepRunningJob = this.stepRunningJob
        if (stepRunningJob != null && stepRunningJob.isActive) {
            stepRunningJob.cancel()
            binding.testIndicatorTestButton.text = "Start"
        } else {
            binding.testIndicatorTestButton.text = "Stop"
            this.stepRunningJob = viewLifecycleOwner.lifecycleScope.launch {
                var progress = 0L
                val request = DisplayRequest(requireContext(), "http://sample.com/sample.jpeg")
                binding.testIndicatorTestImage1.progressIndicatorAbility
                    .onRequestStart(request)
                binding.testIndicatorTestImage2.progressIndicatorAbility
                    .onRequestStart(request)
                binding.testIndicatorTestImage3.progressIndicatorAbility
                    .onRequestStart(request)

                val shortSteps = binding.testIndicatorTestCheckBox2.isChecked
                while (progress < 100 && isActive) {
                    if (shortSteps) {
                        delay(500)
                    } else {
                        delay(Random.nextLong(150, 1000))
                    }
                    progress = (progress + 20).coerceAtMost(100)
                    binding.testIndicatorTestImage1.progressIndicatorAbility
                        .onUpdateRequestProgress(request, 100, progress)
                    binding.testIndicatorTestImage2.progressIndicatorAbility
                        .onUpdateRequestProgress(request, 100, progress)
                    binding.testIndicatorTestImage3.progressIndicatorAbility
                        .onUpdateRequestProgress(request, 100, progress)
                }

                val result = DisplayResult.Success(
                    request = request,
                    requestKey = "",
                    requestCacheKey = "",
                    drawable = ColorDrawable(Color.WHITE),
                    imageInfo = ImageInfo(100, 100, "", ExifInterface.ORIENTATION_NORMAL),
                    dataFrom = LOCAL,
                    transformedList = null,
                    extras = null
                )
                binding.testIndicatorTestImage1.progressIndicatorAbility
                    .onRequestSuccess(request, result)
                binding.testIndicatorTestImage2.progressIndicatorAbility
                    .onRequestSuccess(request, result)
                binding.testIndicatorTestImage3.progressIndicatorAbility
                    .onRequestSuccess(request, result)

                binding.testIndicatorTestButton.text = "Start"
            }
        }
    }

    private fun fast(binding: ProgressIndicatorTestFragmentBinding) {
        binding.testIndicatorTestButton2.isClickable = false
        this.fastRunningJob = viewLifecycleOwner.lifecycleScope.launch {
            val request = DisplayRequest(requireContext(), "http://sample.com/sample.jpeg")
            binding.testIndicatorTestImage1.progressIndicatorAbility
                .onRequestStart(request)
            binding.testIndicatorTestImage2.progressIndicatorAbility
                .onRequestStart(request)
            binding.testIndicatorTestImage3.progressIndicatorAbility
                .onRequestStart(request)

            delay(2000)

            binding.testIndicatorTestImage1.progressIndicatorAbility
                .onUpdateRequestProgress(request, 100, 100)
            binding.testIndicatorTestImage2.progressIndicatorAbility
                .onUpdateRequestProgress(request, 100, 100)
            binding.testIndicatorTestImage3.progressIndicatorAbility
                .onUpdateRequestProgress(request, 100, 100)

            val result = DisplayResult.Success(
                request = request,
                requestKey = "",
                requestCacheKey = "",
                drawable = ColorDrawable(Color.WHITE),
                imageInfo = ImageInfo(100, 100, "", ExifInterface.ORIENTATION_NORMAL),
                dataFrom = LOCAL,
                transformedList = null,
                extras = null
            )
            binding.testIndicatorTestImage1.progressIndicatorAbility
                .onRequestSuccess(request, result)
            binding.testIndicatorTestImage2.progressIndicatorAbility
                .onRequestSuccess(request, result)
            binding.testIndicatorTestImage3.progressIndicatorAbility
                .onRequestSuccess(request, result)

            binding.testIndicatorTestButton2.isClickable = true
        }
    }

    private val ViewAbilityContainer.progressIndicatorAbility: ProgressIndicatorAbility
        get() = viewAbilityList.find { it is ProgressIndicatorAbility }!!
            .let { it as ProgressIndicatorAbility }
}