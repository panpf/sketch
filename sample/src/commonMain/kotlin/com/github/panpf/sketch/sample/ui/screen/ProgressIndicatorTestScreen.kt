package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import com.github.panpf.sketch.compose.ability.progressIndicator
import com.github.panpf.sketch.compose.painter.rememberIconPainter
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.sample.ui.model.ProgressIndicatorTestModel
import com.github.panpf.sketch.sample.ui.rememberIconImageOutlinePainter
import com.github.panpf.sketch.sample.ui.screen.base.BaseScreen
import com.github.panpf.sketch.sample.ui.screen.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.util.rememberThemeMaskProgressPainter
import com.github.panpf.sketch.sample.ui.util.rememberThemeRingProgressPainter
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random


class ProgressIndicatorTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "ProgressIndicatorTest") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val viewModel = rememberScreenModel {
                    ProgressIndicatorTestScreenModel()
                }
                val icImage = rememberIconImageOutlinePainter()
                val colorScheme = MaterialTheme.colorScheme
                val placeholderPainter = rememberIconPainter(
                    icon = icImage,
                    background = colorScheme.primaryContainer,
                    iconTint = colorScheme.onPrimaryContainer,
                )

                val hiddenWhenIndeterminate by viewModel.hiddenWhenIndeterminateState.collectAsState()
                val hiddenWhenCompleted by viewModel.hiddenWhenCompletedState.collectAsState()
                val shortStep by viewModel.shortStepState.collectAsState()
                val stepAnimationDuration = if (shortStep) 1000 else 300
                val maskProgressPainter = rememberThemeMaskProgressPainter(
                    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
                    hiddenWhenCompleted = hiddenWhenCompleted,
                    stepAnimationDuration = stepAnimationDuration
                )
                val sectorProgressPainter = rememberThemeSectorProgressPainter(
                    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
                    hiddenWhenCompleted = hiddenWhenCompleted,
                    stepAnimationDuration = stepAnimationDuration,
                )
                val ringProgressPainter = rememberThemeRingProgressPainter(
                    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
                    hiddenWhenCompleted = hiddenWhenCompleted,
                    stepAnimationDuration = stepAnimationDuration
                )
                val progress by viewModel.progressState.collectAsState()
                LaunchedEffect(progress) {
                    maskProgressPainter.progress = progress
                    sectorProgressPainter.progress = progress
                    ringProgressPainter.progress = progress
                }

                val imageState = rememberAsyncImageState()
                Text(text = "MaskProgressIndicator")
                Spacer(modifier = Modifier.size(4.dp))
                Image(
                    painter = placeholderPainter,
                    contentDescription = "Image",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .progressIndicator(imageState, maskProgressPainter)
                )

                Spacer(modifier = Modifier.size(20.dp))

                Text(text = "SectorProgressIndicator")
                Spacer(modifier = Modifier.size(4.dp))
                Image(
                    painter = placeholderPainter,
                    contentDescription = "Image",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .progressIndicator(imageState, sectorProgressPainter)
                )

                Spacer(modifier = Modifier.size(20.dp))

                Text(text = "RingProgressIndicator")
                Spacer(modifier = Modifier.size(4.dp))
                Image(
                    painter = placeholderPainter,
                    contentDescription = "Image",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .progressIndicator(imageState, ringProgressPainter)
                )

                Spacer(modifier = Modifier.size(20.dp))
                Row(horizontalArrangement = Arrangement.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.changeHiddenWhenIndeterminate(!hiddenWhenIndeterminate)
                        }
                    ) {
                        Checkbox(
                            checked = hiddenWhenIndeterminate,
                            onCheckedChange = { viewModel.changeHiddenWhenIndeterminate(!hiddenWhenIndeterminate) },
                        )
                        Text(text = "Hidden(0f)", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.changeHiddenWhenCompleted(!hiddenWhenCompleted)
                        }
                    ) {
                        Checkbox(
                            checked = hiddenWhenCompleted,
                            onCheckedChange = { viewModel.changeHiddenWhenCompleted(!hiddenWhenCompleted) },
                        )
                        Text(text = "Hidden(1f)", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.changeShortStep(!shortStep)
                        }
                    ) {
                        Checkbox(
                            checked = shortStep,
                            onCheckedChange = { viewModel.changeShortStep(!shortStep) },
                        )
                        Text(text = "ShortStep", fontSize = 14.sp)
                    }
                }

                val model by viewModel.modelState.collectAsState()
                Spacer(modifier = Modifier.size(10.dp))
                Row(horizontalArrangement = Arrangement.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.changeModel(
                                ProgressIndicatorTestModel.Progress
                            )
                        }
                    ) {
                        RadioButton(
                            selected = model == ProgressIndicatorTestModel.Progress,
                            onClick = { viewModel.changeModel(ProgressIndicatorTestModel.Progress) },
                        )
                        Text(text = "Progress", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.changeModel(
                                ProgressIndicatorTestModel.DirectlyComplete
                            )
                        }
                    ) {
                        RadioButton(
                            selected = model == ProgressIndicatorTestModel.DirectlyComplete,
                            onClick = { viewModel.changeModel(ProgressIndicatorTestModel.DirectlyComplete) },
                        )
                        Text(text = "FastComplete", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.changeModel(
                                ProgressIndicatorTestModel.Error
                            )
                        }
                    ) {
                        RadioButton(
                            selected = model == ProgressIndicatorTestModel.Error,
                            onClick = { viewModel.changeModel(ProgressIndicatorTestModel.Error) },
                        )
                        Text(text = "Error", fontSize = 14.sp)
                    }
                }

                val running by viewModel.runningState.collectAsState()
                Spacer(modifier = Modifier.size(20.dp))
                Button(
                    modifier = Modifier.width(140.dp),
                    shape = RoundedCornerShape(50),
                    onClick = { viewModel.action() }
                ) {
                    Text(text = if (running) "Stop" else "Start")
                }
            }
        }
    }
}

class ProgressIndicatorTestScreenModel : ScreenModel {

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
            this.runningJob = screenModelScope.launch {
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