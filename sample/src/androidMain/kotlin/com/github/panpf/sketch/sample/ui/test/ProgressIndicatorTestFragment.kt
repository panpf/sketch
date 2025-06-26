package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.ability.ProgressIndicatorAbility
import com.github.panpf.sketch.ability.ViewAbilityContainer
import com.github.panpf.sketch.ability.removeProgressIndicator
import com.github.panpf.sketch.ability.showProgressIndicator
import com.github.panpf.sketch.drawable.IconDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.R.color
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.databinding.FragmentTestProgressIndicatorBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.model.ProgressIndicatorTestModel
import com.github.panpf.sketch.sample.ui.util.createThemeMaskProgressDrawable
import com.github.panpf.sketch.sample.ui.util.createThemeRingProgressDrawable
import com.github.panpf.sketch.sample.ui.util.createThemeSectorProgressDrawable
import com.github.panpf.sketch.sample.ui.util.getDrawableCompat
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import kotlinx.coroutines.flow.combine
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProgressIndicatorTestFragment :
    BaseToolbarBindingFragment<FragmentTestProgressIndicatorBinding>() {

    private val progressIndicatorTestViewModel by viewModel<ProgressIndicatorTestViewModel>()

    override fun getNavigationBarInsetsView(binding: FragmentTestProgressIndicatorBinding): View {
        return binding.root
    }

    override fun onViewCreated(
        toolbar: androidx.appcompat.widget.Toolbar,
        binding: FragmentTestProgressIndicatorBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "ProgressIndicator"

        binding.image1.setImageDrawable(
            IconDrawable(
                icon = requireContext().getDrawableCompat(drawable.ic_image_outline),
                background = requireContext().getDrawableCompat(color.placeholder_bg),
            )
        )
        binding.image2.setImageDrawable(
            IconDrawable(
                icon = requireContext().getDrawableCompat(drawable.ic_image_outline),
                background = requireContext().getDrawableCompat(color.placeholder_bg),
            )
        )
        binding.image3.setImageDrawable(
            IconDrawable(
                icon = requireContext().getDrawableCompat(drawable.ic_image_outline),
                background = requireContext().getDrawableCompat(color.placeholder_bg),
            )
        )

        binding.actionButton.setOnClickListener {
            progressIndicatorTestViewModel.action()
        }

        progressIndicatorTestViewModel.modelState.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.CREATED
        ) {
            setupModel(binding, it)
        }
        binding.progressRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                progressIndicatorTestViewModel.changeModel(ProgressIndicatorTestModel.Progress)
            }
        }
        binding.completedRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                progressIndicatorTestViewModel.changeModel(ProgressIndicatorTestModel.DirectlyComplete)
            }
        }
        binding.errorRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                progressIndicatorTestViewModel.changeModel(ProgressIndicatorTestModel.Error)
            }
        }

        combine(
            flows = listOf(
                progressIndicatorTestViewModel.hiddenWhenIndeterminateState,
                progressIndicatorTestViewModel.hiddenWhenCompletedState,
                progressIndicatorTestViewModel.shortStepState
            ),
            transform = { it.joinToString() }
        ).repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
            setupProgressIndicator(binding)
        }
        binding.hiddenIndeterminateCheckBox.apply {
            progressIndicatorTestViewModel.hiddenWhenIndeterminateState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                progressIndicatorTestViewModel.changeHiddenWhenIndeterminate(isChecked)
            }
        }
        binding.hiddenCompletedCheckBox.apply {
            progressIndicatorTestViewModel.hiddenWhenCompletedState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                progressIndicatorTestViewModel.changeHiddenWhenCompleted(isChecked)
            }
        }
        binding.shortStepCheckBox.apply {
            progressIndicatorTestViewModel.shortStepState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                progressIndicatorTestViewModel.changeShortStep(isChecked)
            }
        }

        progressIndicatorTestViewModel.runningState.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.CREATED
        ) {
            binding.actionButton.text = if (it) "Stop" else "Start"
        }

        progressIndicatorTestViewModel.progressState
            .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) { progress ->
                val request = ImageRequest(requireContext(), "http://sample.com/sample.jpeg")
                val totalLength: Long = 100
                val progress1 =
                    com.github.panpf.sketch.request.Progress(100, (progress * totalLength).toLong())
                binding.image1.progressIndicatorAbility
                    .onUpdateRequestProgress(request, progress1)
                binding.image2.progressIndicatorAbility
                    .onUpdateRequestProgress(request, progress1)
                binding.image3.progressIndicatorAbility
                    .onUpdateRequestProgress(request, progress1)
            }
    }

    private fun setupModel(
        binding: FragmentTestProgressIndicatorBinding,
        model: ProgressIndicatorTestModel
    ) {
        binding.progressRadioButton.isChecked = model == ProgressIndicatorTestModel.Progress
        binding.completedRadioButton.isChecked =
            model == ProgressIndicatorTestModel.DirectlyComplete
        binding.errorRadioButton.isChecked = model == ProgressIndicatorTestModel.Error
    }

    private fun setupProgressIndicator(binding: FragmentTestProgressIndicatorBinding) {
        binding.image1.removeProgressIndicator()
        binding.image2.removeProgressIndicator()
        binding.image3.removeProgressIndicator()

        val hiddenWhenIndeterminate =
            progressIndicatorTestViewModel.hiddenWhenIndeterminateState.value
        val hiddenWhenCompleted = progressIndicatorTestViewModel.hiddenWhenCompletedState.value
        val shortStep = progressIndicatorTestViewModel.shortStepState.value
        val stepAnimationDuration = if (shortStep) 1000 else 300
        binding.image1.showProgressIndicator(
            createThemeMaskProgressDrawable(
                context = requireContext(),
                hiddenWhenIndeterminate = hiddenWhenIndeterminate,
                hiddenWhenCompleted = hiddenWhenCompleted,
                stepAnimationDuration = stepAnimationDuration
            )
        )
        binding.image2.showProgressIndicator(
            createThemeSectorProgressDrawable(
                context = requireContext(),
                hiddenWhenIndeterminate = hiddenWhenIndeterminate,
                hiddenWhenCompleted = hiddenWhenCompleted,
                stepAnimationDuration = stepAnimationDuration
            )
        )
        binding.image3.showProgressIndicator(
            createThemeRingProgressDrawable(
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