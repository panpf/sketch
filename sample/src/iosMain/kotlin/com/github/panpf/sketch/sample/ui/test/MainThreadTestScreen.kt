package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSThread
import kotlin.time.measureTime

class MainThreadTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "Main Thread Test") {
            var result by remember { mutableStateOf<Pair<Boolean, Boolean>?>(null) }
            LaunchedEffect(Unit) {
                val isMainThreadInIO = withContext(Dispatchers.IO) {
                    NSThread.isMainThread
                }
                val isMainThreadInMain = withContext(Dispatchers.Main) {
                    NSThread.isMainThread
                }
                result = isMainThreadInIO to isMainThreadInMain
            }
            val time = remember {
                measureTime {
                    repeat(100) {
                        NSThread.isMainThread
                    }
                }
            }
            val result1 = result
            if (result1 != null) {
                val (isMainThreadInIO, isMainThreadInMain) = result1
                Text(
                    text = """
                        isMainThreadInIO: $isMainThreadInIO
                        isMainThreadInMain: $isMainThreadInMain
                        100 times: $time, average: ${time.inWholeMicroseconds / 100} us
                    """.trimIndent(),
                    modifier = Modifier.padding(50.dp)
                )
            }
        }
    }
}