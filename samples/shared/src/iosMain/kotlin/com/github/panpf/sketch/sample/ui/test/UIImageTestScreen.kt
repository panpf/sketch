package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.components.regionSelector
import com.github.panpf.sketch.sample.ui.components.rememberRegionSelectorState
import com.github.panpf.sketch.util.Rect
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun UIImageTestScreen() {
    ToolbarScaffold(title = "UIImageTest") {
        val viewModel: UIImageTestViewModel = koinViewModel()
        val windowInfo = LocalWindowInfo.current
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            val bitmap by viewModel.originBitmapFlow.collectAsState()
            if (bitmap != null) {
                val bitmap = bitmap!!
                Text(text = "Origin Bitmap: ${bitmap.width}x${bitmap.height}", fontSize = 14.sp)
                var imageSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
                val density = LocalDensity.current
                val imageMaxHeight: Dp = remember {
                    with(density) {
                        (windowInfo.containerSize.height * 0.4f).toDp()
                    }
                }
                val state = rememberRegionSelectorState()
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .heightIn(0.dp, imageMaxHeight)
                        .regionSelector(state)
                        .onSizeChanged {
                            imageSize = it.toSize()
                        }
                )

                LaunchedEffect(Unit) {
                    state.rectFlow.collect {
                        val scale = bitmap.width / imageSize.width
                        viewModel.updateRect(it.let {
                            Rect(
                                left = (it.left * scale).roundToInt().coerceIn(0, bitmap.width),
                                top = (it.top * scale).roundToInt().coerceIn(0, bitmap.height),
                                right = (it.right * scale).roundToInt().coerceIn(0, bitmap.width),
                                bottom = (it.bottom * scale).roundToInt().coerceIn(0, bitmap.height)
                            )
                        })
                    }
                }

                Spacer(Modifier.size(20.dp))
                val rect by viewModel.regionFlow.collectAsState()
                Text(text = "Region Rect: ${rect?.toShortString()}", fontSize = 14.sp)

                Spacer(Modifier.size(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Sample Size: ", fontSize = 14.sp)
                    Spacer(Modifier.size(20.dp))

                    val sampleSize by viewModel.sampleSizeFlow.collectAsState()
                    Button(
                        onClick = {
                            viewModel.updateSampleSize((sampleSize / 2).coerceAtLeast(1))
                        },
                        contentPadding = PaddingValues.Zero,
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    ) {
                        Text(text = "-")
                    }

                    Spacer(Modifier.size(10.dp))
                    Text(text = sampleSize.toString())
                    Spacer(Modifier.size(10.dp))

                    Button(
                        onClick = {
                            viewModel.updateSampleSize(sampleSize * 2)
                        },
                        contentPadding = PaddingValues.Zero,
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    ) {
                        Text(text = "+")
                    }
                }

                val bitmap2 by viewModel.subsamplingBitmapFlow.collectAsState()
                if (bitmap2 != null) {
                    Spacer(Modifier.size(20.dp))
                    Text(
                        text = "Result Bitmap: ${bitmap2!!.width}x${bitmap2!!.height}",
                        fontSize = 14.sp
                    )
                    Image(
                        bitmap = bitmap2!!,
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }
    }
}

