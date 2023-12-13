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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.widget.Toolbar
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
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import com.github.panpf.sketch.drawable.MaskProgressDrawable
import com.github.panpf.sketch.drawable.RingProgressDrawable
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.ui.base.ToolbarFragment
import com.github.panpf.sketch.sample.ui.photo.pexels.progressIndicator
import com.github.panpf.sketch.sample.ui.photo.pexels.rememberDrawableProgressPainter
import com.github.panpf.sketch.sample.ui.photo.pexels.rememberProgressIndicatorState
import com.github.panpf.sketch.sample.ui.test.ProgressIndicatorTestViewModel.Model.DirectlyComplete
import com.github.panpf.sketch.sample.ui.test.ProgressIndicatorTestViewModel.Model.Error
import com.github.panpf.sketch.sample.ui.test.ProgressIndicatorTestViewModel.Model.Progress
import com.github.panpf.sketch.sample.util.getDrawableCompat
import com.google.accompanist.drawablepainter.DrawablePainter

class ProgressIndicatorTestComposeFragment : ToolbarFragment() {

    private val viewModel by viewModels<ProgressIndicatorTestViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.my_move)
        }
    }

    override fun createView(toolbar: Toolbar, inflater: LayoutInflater, parent: ViewGroup?): View {
        toolbar.title = "ProgressIndicator（Compose）"

        return ComposeView(requireContext()).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setContent {
                Content(viewModel)
            }
        }
    }
}

@Preview
@Composable
private fun ContentPreview() {
    Content(ProgressIndicatorTestViewModel())
}

@Composable
private fun Content(viewModel: ProgressIndicatorTestViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val placeholderPainter = remember {
            DrawablePainter(context.resources.getDrawableCompat(drawable.im_placeholder))
        }

        val hiddenWhenIndeterminate by viewModel.hiddenWhenIndeterminateState.collectAsState()
        val hiddenWhenCompleted by viewModel.hiddenWhenCompletedState.collectAsState()
        val shortStep by viewModel.shortStepState.collectAsState()
        val maskProgressDrawable =
            remember(hiddenWhenIndeterminate, hiddenWhenCompleted, shortStep) {
                val stepAnimationDuration = if (shortStep) 1000 else 300
                MaskProgressDrawable(
                    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
                    hiddenWhenCompleted = hiddenWhenCompleted,
                    stepAnimationDuration = stepAnimationDuration
                )
            }
        val maskProgressIndicatorState = rememberProgressIndicatorState(
            rememberDrawableProgressPainter(maskProgressDrawable)
        )
        val sectorProgressDrawable =
            remember(hiddenWhenIndeterminate, hiddenWhenCompleted, shortStep) {
                val stepAnimationDuration = if (shortStep) 1000 else 300
                SectorProgressDrawable(
                    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
                    hiddenWhenCompleted = hiddenWhenCompleted,
                    stepAnimationDuration = stepAnimationDuration
                )
            }
        val sectorProgressIndicatorState = rememberProgressIndicatorState(
            rememberDrawableProgressPainter(sectorProgressDrawable)
        )
        val ringProgressDrawable =
            remember(hiddenWhenIndeterminate, hiddenWhenCompleted, shortStep) {
                val stepAnimationDuration = if (shortStep) 1000 else 300
                RingProgressDrawable(
                    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
                    hiddenWhenCompleted = hiddenWhenCompleted,
                    stepAnimationDuration = stepAnimationDuration
                )
            }
        val ringProgressIndicatorState = rememberProgressIndicatorState(
            rememberDrawableProgressPainter(ringProgressDrawable)
        )
        val progress by viewModel.progressState.collectAsState()
        LaunchedEffect(progress) {
            maskProgressIndicatorState.progress = progress
            sectorProgressIndicatorState.progress = progress
            ringProgressIndicatorState.progress = progress
        }

        Text(text = "MaskProgressIndicator", color = Color.White)
        Spacer(modifier = Modifier.size(4.dp))
        Image(
            painter = placeholderPainter,
            contentDescription = "Image",
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .progressIndicator(maskProgressIndicatorState)
        )

        Spacer(modifier = Modifier.size(20.dp))

        Text(text = "SectorProgressIndicator", color = Color.White)
        Spacer(modifier = Modifier.size(4.dp))
        Image(
            painter = placeholderPainter,
            contentDescription = "Image",
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .progressIndicator(sectorProgressIndicatorState)
        )

        Spacer(modifier = Modifier.size(20.dp))

        Text(text = "RingProgressIndicator", color = Color.White)
        Spacer(modifier = Modifier.size(4.dp))
        Image(
            painter = placeholderPainter,
            contentDescription = "Image",
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .progressIndicator(ringProgressIndicatorState)
        )

        Spacer(modifier = Modifier.size(20.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    viewModel.changeHiddenWhenIndeterminate(!hiddenWhenIndeterminate)
                }
            ) {
                Checkbox(checked = hiddenWhenIndeterminate, enabled = false, onCheckedChange = {})
                Text(text = "Hidden(0f)")
            }
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    viewModel.changeHiddenWhenCompleted(!hiddenWhenCompleted)
                }
            ) {
                Checkbox(checked = hiddenWhenCompleted, enabled = false, onCheckedChange = {})
                Text(text = "Hidden(1f)")
            }
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    viewModel.changeShortStep(!shortStep)
                }
            ) {
                Checkbox(checked = shortStep, enabled = false, onCheckedChange = {})
                Text(text = "ShortStep")
            }
        }

        val model by viewModel.modelState.collectAsState()
        Spacer(modifier = Modifier.size(10.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { viewModel.changeModel(Progress) }
            ) {
                RadioButton(selected = model == Progress, enabled = false, onClick = {})
                Text(text = "Progress")
            }
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { viewModel.changeModel(DirectlyComplete) }
            ) {
                RadioButton(selected = model == DirectlyComplete, enabled = false, onClick = {})
                Text(text = "DirectlyComplete")
            }
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { viewModel.changeModel(Error) }
            ) {
                RadioButton(selected = model == Error, enabled = false, onClick = {})
                Text(text = "Error")
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