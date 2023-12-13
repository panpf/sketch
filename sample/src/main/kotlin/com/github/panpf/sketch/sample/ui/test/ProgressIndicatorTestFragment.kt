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

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.transition.TransitionInflater
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.ProgressIndicatorTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.test.ProgressIndicatorTestViewModel.Model
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.viewability.ProgressIndicatorAbility
import com.github.panpf.sketch.viewability.ViewAbilityContainer
import com.github.panpf.sketch.viewability.removeProgressIndicator
import com.github.panpf.sketch.viewability.showMaskProgressIndicator
import com.github.panpf.sketch.viewability.showRingProgressIndicator
import com.github.panpf.sketch.viewability.showSectorProgressIndicator
import kotlinx.coroutines.flow.combine

class ProgressIndicatorTestFragment :
    ToolbarBindingFragment<ProgressIndicatorTestFragmentBinding>() {

    private val viewModel by viewModels<ProgressIndicatorTestViewModel>()

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

        binding.testIndicatorTestActionButton.setOnClickListener {
            viewModel.action()
        }

        viewModel.modelState.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            setupModel(binding, it)
        }
        binding.testIndicatorTestProgressRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeModel(Model.Progress)
            }
        }
        binding.testIndicatorTestCompletedRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeModel(Model.DirectlyComplete)
            }
        }
        binding.testIndicatorTestErrorRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeModel(Model.Error)
            }
        }

        combine(
            flows = listOf(
                viewModel.hiddenWhenIndeterminateState,
                viewModel.hiddenWhenCompletedState,
                viewModel.shortStepState
            ),
            transform = { it.joinToString() }
        ).repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            setupProgressIndicator(binding)
        }
        binding.testIndicatorTestHiddenIndeterminateCheckBox.apply {
            viewModel.hiddenWhenIndeterminateState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeHiddenWhenIndeterminate(isChecked)
            }
        }
        binding.testIndicatorTestHiddenCompletedCheckBox.apply {
            viewModel.hiddenWhenCompletedState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeHiddenWhenCompleted(isChecked)
            }
        }
        binding.testIndicatorTestShortStepCheckBox.apply {
            viewModel.shortStepState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeShortStep(isChecked)
            }
        }

        viewModel.runningState.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            binding.testIndicatorTestActionButton.text = if (it) "Stop" else "Start"
        }

        viewModel.progressState.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.STARTED
        ) { progress ->
            val request = DisplayRequest(requireContext(), "http://sample.com/sample.jpeg")
            val totalLength: Long = 100
            val completedLength = (progress * totalLength).toLong()
            binding.testIndicatorTestImage1.progressIndicatorAbility
                .onUpdateRequestProgress(request, totalLength, completedLength)
            binding.testIndicatorTestImage2.progressIndicatorAbility
                .onUpdateRequestProgress(request, totalLength, completedLength)
            binding.testIndicatorTestImage3.progressIndicatorAbility
                .onUpdateRequestProgress(request, totalLength, completedLength)
        }
    }

    private fun setupModel(binding: ProgressIndicatorTestFragmentBinding, model: Model) {
        binding.testIndicatorTestProgressRadioButton.isChecked = model == Model.Progress
        binding.testIndicatorTestCompletedRadioButton.isChecked = model == Model.DirectlyComplete
        binding.testIndicatorTestErrorRadioButton.isChecked = model == Model.Error
    }

    private fun setupProgressIndicator(binding: ProgressIndicatorTestFragmentBinding) {
        binding.testIndicatorTestImage1.removeProgressIndicator()
        binding.testIndicatorTestImage2.removeProgressIndicator()
        binding.testIndicatorTestImage3.removeProgressIndicator()

        val hiddenWhenIndeterminate = viewModel.hiddenWhenIndeterminateState.value
        val hiddenWhenCompleted = viewModel.hiddenWhenCompletedState.value
        val shortStep = viewModel.shortStepState.value
        val stepAnimationDuration = if (shortStep) 1000 else 300
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

    private val ViewAbilityContainer.progressIndicatorAbility: ProgressIndicatorAbility
        get() = viewAbilityList.find { it is ProgressIndicatorAbility }!!
            .let { it as ProgressIndicatorAbility }
}