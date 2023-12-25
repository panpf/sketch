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
import com.github.panpf.sketch.sample.databinding.FragmentTestProgressIndicatorBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.createDayNightMaskProgressDrawable
import com.github.panpf.sketch.sample.ui.common.createDayNightRingProgressDrawable
import com.github.panpf.sketch.sample.ui.common.createDayNightSectorProgressDrawable
import com.github.panpf.sketch.sample.ui.test.ProgressIndicatorTestViewModel.Model
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.viewability.ProgressIndicatorAbility
import com.github.panpf.sketch.viewability.ViewAbilityContainer
import com.github.panpf.sketch.viewability.removeProgressIndicator
import com.github.panpf.sketch.viewability.showProgressIndicator
import kotlinx.coroutines.flow.combine

class ProgressIndicatorTestFragment :
    BaseToolbarBindingFragment<FragmentTestProgressIndicatorBinding>() {

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
        binding: FragmentTestProgressIndicatorBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "ProgressIndicator"

        if (VERSION.SDK_INT >= 21) {
            binding.image1.setImageResource(R.drawable.im_placeholder)
            binding.image2.setImageResource(R.drawable.im_placeholder)
            binding.image3.setImageResource(R.drawable.im_placeholder)
        } else {
            binding.image1.setImageResource(R.drawable.im_placeholder_bg)
            binding.image2.setImageResource(R.drawable.im_placeholder_bg)
            binding.image3.setImageResource(R.drawable.im_placeholder_bg)
        }

        binding.actionButton.setOnClickListener {
            viewModel.action()
        }

        viewModel.modelState.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            setupModel(binding, it)
        }
        binding.progressRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeModel(Model.Progress)
            }
        }
        binding.completedRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeModel(Model.DirectlyComplete)
            }
        }
        binding.errorRadioButton.setOnCheckedChangeListener { _, isChecked ->
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
        binding.hiddenIndeterminateCheckBox.apply {
            viewModel.hiddenWhenIndeterminateState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeHiddenWhenIndeterminate(isChecked)
            }
        }
        binding.hiddenCompletedCheckBox.apply {
            viewModel.hiddenWhenCompletedState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeHiddenWhenCompleted(isChecked)
            }
        }
        binding.shortStepCheckBox.apply {
            viewModel.shortStepState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeShortStep(isChecked)
            }
        }

        viewModel.runningState.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            binding.actionButton.text = if (it) "Stop" else "Start"
        }

        viewModel.progressState.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.STARTED
        ) { progress ->
            val request = DisplayRequest(requireContext(), "http://sample.com/sample.jpeg")
            val totalLength: Long = 100
            val completedLength = (progress * totalLength).toLong()
            binding.image1.progressIndicatorAbility
                .onUpdateRequestProgress(request, totalLength, completedLength)
            binding.image2.progressIndicatorAbility
                .onUpdateRequestProgress(request, totalLength, completedLength)
            binding.image3.progressIndicatorAbility
                .onUpdateRequestProgress(request, totalLength, completedLength)
        }
    }

    private fun setupModel(binding: FragmentTestProgressIndicatorBinding, model: Model) {
        binding.progressRadioButton.isChecked = model == Model.Progress
        binding.completedRadioButton.isChecked = model == Model.DirectlyComplete
        binding.errorRadioButton.isChecked = model == Model.Error
    }

    private fun setupProgressIndicator(binding: FragmentTestProgressIndicatorBinding) {
        binding.image1.removeProgressIndicator()
        binding.image2.removeProgressIndicator()
        binding.image3.removeProgressIndicator()

        val hiddenWhenIndeterminate = viewModel.hiddenWhenIndeterminateState.value
        val hiddenWhenCompleted = viewModel.hiddenWhenCompletedState.value
        val shortStep = viewModel.shortStepState.value
        val stepAnimationDuration = if (shortStep) 1000 else 300
        binding.image1.showProgressIndicator(
            createDayNightMaskProgressDrawable(
                context = requireContext(),
                hiddenWhenIndeterminate = hiddenWhenIndeterminate,
                hiddenWhenCompleted = hiddenWhenCompleted,
                stepAnimationDuration = stepAnimationDuration
            )
        )
        binding.image2.showProgressIndicator(
            createDayNightSectorProgressDrawable(
                context = requireContext(),
                hiddenWhenIndeterminate = hiddenWhenIndeterminate,
                hiddenWhenCompleted = hiddenWhenCompleted,
                stepAnimationDuration = stepAnimationDuration
            )
        )
        binding.image3.showProgressIndicator(
            createDayNightRingProgressDrawable(
                context = requireContext(),
                hiddenWhenIndeterminate = hiddenWhenIndeterminate,
                hiddenWhenCompleted = hiddenWhenCompleted,
                stepAnimationDuration = stepAnimationDuration
            )
        )
    }

    private val ViewAbilityContainer.progressIndicatorAbility: ProgressIndicatorAbility
        get() = viewAbilityList.find { it is ProgressIndicatorAbility }!!
            .let { it as ProgressIndicatorAbility }
}