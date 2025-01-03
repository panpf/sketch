package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class ProgressIndicatorTestFragment :
    BaseToolbarBindingFragment<FragmentTestProgressIndicatorBinding>() {

    private val viewModel by viewModels<ProgressIndicatorTestViewModel>()

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
            viewModel.action()
        }

        viewModel.modelState.repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
            setupModel(binding, it)
        }
        binding.progressRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeModel(ProgressIndicatorTestModel.Progress)
            }
        }
        binding.completedRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeModel(ProgressIndicatorTestModel.DirectlyComplete)
            }
        }
        binding.errorRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeModel(ProgressIndicatorTestModel.Error)
            }
        }

        combine(
            flows = listOf(
                viewModel.hiddenWhenIndeterminateState,
                viewModel.hiddenWhenCompletedState,
                viewModel.shortStepState
            ),
            transform = { it.joinToString() }
        ).repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
            setupProgressIndicator(binding)
        }
        binding.hiddenIndeterminateCheckBox.apply {
            viewModel.hiddenWhenIndeterminateState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeHiddenWhenIndeterminate(isChecked)
            }
        }
        binding.hiddenCompletedCheckBox.apply {
            viewModel.hiddenWhenCompletedState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeHiddenWhenCompleted(isChecked)
            }
        }
        binding.shortStepCheckBox.apply {
            viewModel.shortStepState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
                    isChecked = it
                }
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeShortStep(isChecked)
            }
        }

        viewModel.runningState.repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
            binding.actionButton.text = if (it) "Stop" else "Start"
        }

        viewModel.progressState
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


    class ProgressIndicatorTestViewModel : ViewModel() {

        private var runningJob: Job? = null

        private val _progressState = MutableStateFlow(0f)
        val progressState: StateFlow<Float> = _progressState

        private val _runningState = MutableStateFlow(false)
        val runningState: StateFlow<Boolean> = _runningState

        private val _modelState = MutableStateFlow(ProgressIndicatorTestModel.Progress)
        val modelState: StateFlow<ProgressIndicatorTestModel> = _modelState

        private val _hiddenWhenIndeterminateState = MutableStateFlow(false)
        val hiddenWhenIndeterminateState: StateFlow<Boolean> = _hiddenWhenIndeterminateState

        private val _hiddenWhenCompletedState = MutableStateFlow(true)
        val hiddenWhenCompletedState: StateFlow<Boolean> = _hiddenWhenCompletedState

        private val _shortStepState = MutableStateFlow(false)
        val shortStepState: StateFlow<Boolean> = _shortStepState

        fun changeModel(model: ProgressIndicatorTestModel) {
            _modelState.value = model
        }

        fun changeHiddenWhenIndeterminate(hidden: Boolean) {
            _hiddenWhenIndeterminateState.value = hidden
        }

        fun changeHiddenWhenCompleted(hidden: Boolean) {
            _hiddenWhenCompletedState.value = hidden
        }

        fun changeShortStep(hidden: Boolean) {
            _shortStepState.value = hidden
        }

        fun action() {
            val runningJob = this.runningJob
            if (runningJob != null && runningJob.isActive) {
                runningJob.cancel()
                _runningState.value = false
            } else {
                _runningState.value = true
                this.runningJob = viewModelScope.launch {
                    when (_modelState.value) {
                        ProgressIndicatorTestModel.Progress -> {
                            var progress = 0f
                            val shortSteps = _shortStepState.value
                            do {
                                _progressState.value = progress
                                if (shortSteps) {
                                    delay(500)
                                } else {
                                    delay(Random.nextLong(150, 1000))
                                }
                                progress = (progress + 0.2f)
                            } while (progress <= 1f && isActive)
                        }

                        ProgressIndicatorTestModel.DirectlyComplete -> {
                            _progressState.value = 0f
                            delay(2000)
                            _progressState.value = 1f
                        }

                        ProgressIndicatorTestModel.Error -> {
                            _progressState.value = 0f
                            delay(2000)
                            _progressState.value = -1f
                        }
                    }

                    _runningState.value = false
                }
            }
        }
    }
}