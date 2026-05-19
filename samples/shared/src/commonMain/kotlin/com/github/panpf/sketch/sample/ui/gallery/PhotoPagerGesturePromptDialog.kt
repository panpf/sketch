package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.sample.AppSettings
import org.koin.compose.koinInject

@Composable
fun PhotoPagerGesturePromptDialog() {
    val appSettings: AppSettings = koinInject()
    val pagerGuideShowed by appSettings.pagerGuideShowed.collectAsState()
    var showPagerGuide by remember { mutableStateOf(true) }
    if (!pagerGuideShowed && showPagerGuide) {
        AlertDialog(
            onDismissRequest = { showPagerGuide = false },
            title = { Text("Operation gestures") },
            text = {
                Text(
                    text = """The current page supports the following gestures or operations：
                            |1. Turn page:
                            |    1.2. Key.LeftBracket + (meta/ctrl)/alt, Key.RightBracket + (meta/ctrl)/alt
                            |    1.3. Key.DirectionLeft + (meta/ctrl)/alt, Key.DirectionRight + (meta/ctrl)/alt
                            |2. Scaling image：
                            |    2.1. Double-click the image with one finger
                            |    2.2. Double-click the image with one finger and slide up and down without letting go.
                            |    2.3. Pinch with two fingers
                            |    2.4. Mouse scroll scaling
                            |    2.5. Key.ZoomIn, Key.ZoomOut, 
                            |        Key.Equals + (meta/ctrl)/alt, Key.Minus + (meta/ctrl)/alt, 
                            |        Key.DirectionUp + (meta/ctrl)/alt, Key.DirectionDown + (meta/ctrl)/alt
                            |3. Moving image：
                            |    3.1. Key.DirectionUp, Key.DirectionDown, Key.DirectionLeft, Key.DirectionRight
                            """.trimMargin(),
                    fontSize = 12.sp
                )
            },
            dismissButton = {
                Button(onClick = { showPagerGuide = false }) {
                    Text("I Known")
                }
            },
            confirmButton = {
                Button(onClick = { appSettings.pagerGuideShowed.value = true }) {
                    Text("Not prompting")
                }
            }
        )
    }
}