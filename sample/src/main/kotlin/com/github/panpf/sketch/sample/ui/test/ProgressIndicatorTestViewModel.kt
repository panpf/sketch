package com.github.panpf.sketch.sample.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class ProgressIndicatorTestViewModel : ViewModel() {

    private var runningJob: Job? = null

    private val _progressState = MutableStateFlow(0f)
    val progressState: StateFlow<Float> = _progressState

    private val _runningState = MutableStateFlow(false)
    val runningState: StateFlow<Boolean> = _runningState

    private val _modelState = MutableStateFlow(Model.Progress)
    val modelState: StateFlow<Model> = _modelState

    private val _hiddenWhenIndeterminateState = MutableStateFlow(false)
    val hiddenWhenIndeterminateState: StateFlow<Boolean> = _hiddenWhenIndeterminateState

    private val _hiddenWhenCompletedState = MutableStateFlow(true)
    val hiddenWhenCompletedState: StateFlow<Boolean> = _hiddenWhenCompletedState

    private val _shortStepState = MutableStateFlow(false)
    val shortStepState: StateFlow<Boolean> = _shortStepState

    enum class Model {
        Progress, DirectlyComplete, Error
    }

    fun changeModel(model: Model) {
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
                    Model.Progress -> {
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

                    Model.DirectlyComplete -> {
                        _progressState.value = 0f
                        delay(2000)
                        _progressState.value = 1f
                    }

                    Model.Error -> {
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