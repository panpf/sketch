package com.github.panpf.sketch.sample.ui.test.progress

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.transition.TransitionInflater
import com.github.panpf.sketch.drawable.IconDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.R.color
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.R.transition
import com.github.panpf.sketch.sample.databinding.FragmentTestProgressIndicatorBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.util.createThemeMaskProgressDrawable
import com.github.panpf.sketch.sample.ui.util.createThemeRingProgressDrawable
import com.github.panpf.sketch.sample.ui.util.createThemeSectorProgressDrawable
import com.github.panpf.sketch.sample.ui.test.progress.ProgressIndicatorTestViewModel.Model
import com.github.panpf.sketch.sample.ui.test.progress.ProgressIndicatorTestViewModel.Model.DirectlyComplete
import com.github.panpf.sketch.sample.ui.test.progress.ProgressIndicatorTestViewModel.Model.Error
import com.github.panpf.sketch.sample.ui.test.progress.ProgressIndicatorTestViewModel.Model.Progress
import com.github.panpf.sketch.sample.ui.util.getDrawableCompat
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.ability.ProgressIndicatorAbility
import com.github.panpf.sketch.ability.ViewAbilityContainer
import com.github.panpf.sketch.ability.removeProgressIndicator
import com.github.panpf.sketch.ability.showProgressIndicator
import kotlinx.coroutines.flow.combine

class ProgressIndicatorTestViewFragment :
    BaseToolbarBindingFragment<FragmentTestProgressIndicatorBinding>() {

    private val viewModel by viewModels<ProgressIndicatorTestViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(requireContext())
                .inflateTransition(transition.my_move)
        }
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
            viewModel.action()
        }

        viewModel.modelState.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            setupModel(binding, it)
        }
        binding.progressRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeModel(Progress)
            }
        }
        binding.completedRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeModel(DirectlyComplete)
            }
        }
        binding.errorRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeModel(Error)
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
            val request = ImageRequest(requireContext(), "http://sample.com/sample.jpeg")
            val totalLength: Long = 100
            val progress1 = com.github.panpf.sketch.request.Progress(100, (progress * totalLength).toLong())
            binding.image1.progressIndicatorAbility
                .onUpdateRequestProgress(request, progress1)
            binding.image2.progressIndicatorAbility
                .onUpdateRequestProgress(request, progress1)
            binding.image3.progressIndicatorAbility
                .onUpdateRequestProgress(request, progress1)
        }
    }

    private fun setupModel(binding: FragmentTestProgressIndicatorBinding, model: Model) {
        binding.progressRadioButton.isChecked = model == Progress
        binding.completedRadioButton.isChecked = model == DirectlyComplete
        binding.errorRadioButton.isChecked = model == Error
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