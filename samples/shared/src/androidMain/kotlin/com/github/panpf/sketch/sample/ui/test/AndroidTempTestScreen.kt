package com.github.panpf.sketch.sample.ui.test

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.toByteArray
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import com.radzivon.bartoshyk.avif.coder.PreferredColorConfig
import com.radzivon.bartoshyk.avif.coder.ScaleMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AndroidTempTestScreen() {
    ToolbarScaffold(title = "AndroidTempTest") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            val scope = rememberCoroutineScope()
            val context = LocalPlatformContext.current
            var bitmapList by remember { mutableStateOf<List<Bitmap>?>(null) }
            LaunchedEffect(Unit) {
                scope.launch(Dispatchers.IO) {
                    val data = ComposeResImageFiles.avif.toDataSource(context).toByteArray()
                    val coder = HeifCoder()
                    bitmapList = listOf(
                        coder.decodeSampled(
                            byteArray = data,
                            scaledWidth = 500,
                            scaledHeight = 500,
                            preferredColorConfig = PreferredColorConfig.RGBA_8888,
                            scaleMode = ScaleMode.FIT
                        ),
                        coder.decodeSampled(
                            byteArray = data,
                            scaledWidth = 500,
                            scaledHeight = 500,
                            preferredColorConfig = PreferredColorConfig.RGBA_8888,
                            scaleMode = ScaleMode.FILL
                        ),
                        coder.decodeSampled(
                            byteArray = data,
                            scaledWidth = 500,
                            scaledHeight = 500,
                            preferredColorConfig = PreferredColorConfig.RGBA_8888,
                            scaleMode = ScaleMode.RESIZE
                        ),
                    )
                }
            }
            if (bitmapList != null) {
                bitmapList!!.forEach {
                    Spacer(Modifier.size(20.dp))
                    Text("text = ${it.size}")
                    Image(bitmap = it.asImageBitmap(), contentDescription = null)
                }
            }
        }
    }
}