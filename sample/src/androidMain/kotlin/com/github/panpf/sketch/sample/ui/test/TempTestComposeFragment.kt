package com.github.panpf.sketch.sample.ui.test

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.R.color
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.image.DelayDecodeInterceptor
import com.github.panpf.sketch.sample.ui.base.BaseToolbarComposeFragment
import com.github.panpf.sketch.stateimage.AnimatableIconStateImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TempTestComposeFragment : BaseToolbarComposeFragment() {

    override fun onViewCreated(toolbar: Toolbar, savedInstanceState: Bundle?) {
        super.onViewCreated(toolbar, savedInstanceState)
        toolbar.title = "Temp Test (Compose)"
    }

    @Composable
    override fun ComposeContent() {
        AnimatablePainterTest()
    }
}

@Composable
private fun AnimatablePainterTest() {
    AsyncImage(
        request = ImageRequest(LocalContext.current, AssetImages.animGif.uri) {
            placeholder(
                AnimatableIconStateImage(drawable.ic_placeholder_eclipse_animated) {
                    resColorBackground(color.placeholder_bg)
                }
            )
            resizeOnDraw()
            crossfade()
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            components {
                addDecodeInterceptor(DelayDecodeInterceptor(2000))
            }
        },
        contentDescription = "",
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun RememberObserverTest() {
    Log.i("RememberObserverTest", "DrawContent. start")
    val state = rememberTestState()
    Component1(state)
    Box(Modifier.fillMaxSize()) {
        if (state.showLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        } else {
            Button(
                onClick = { state.next() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp)
            ) {
                Text(text = "NEXT")
            }
        }
    }
    Log.w("RememberObserverTest", "DrawContent. end")
}

@Composable
private fun Component1(state: TestState) {
    Log.i("RememberObserverTest", "Component1. start")
    Component2(state)
    Log.w("RememberObserverTest", "Component1. end")
}

@Composable
private fun Component2(state: TestState) {
    Log.i("RememberObserverTest", "Component2. start")
    val painter = state.painter
    if (painter != null) {
        Image(
            painter = painter,
            contentDescription = "Image",
            modifier = Modifier.fillMaxSize()
        )
    }
    Log.w("RememberObserverTest", "Component2. end")
}

@Composable
private fun rememberTestState(): TestState {
    val context = LocalContext.current
    return remember { TestState(context) }
}

@Stable
class TestState internal constructor(private val context: Context) : RememberObserver {

    @OptIn(ExperimentalStdlibApi::class)
    private val longModule = "TestState@${this.hashCode().toHexString()}"
    private var rememberedCount = 0
    private var currentIndex: Int by mutableIntStateOf(0)
    private val imagePaths = arrayOf(
        AssetImages.jpeg.fileName,
        AssetImages.png.fileName,
        AssetImages.webp.fileName,
        AssetImages.bmp.fileName,
    )
    private var coroutineScope: CoroutineScope? = null

    var showLoading: Boolean by mutableStateOf(false)
    var painter: Painter? by mutableStateOf(null)

    init {
        Log.i(
            "RememberObserverTest",
            "$longModule. init"
        )
    }

    fun next() {
        currentIndex = (currentIndex + 1) % imagePaths.size
    }

    private suspend fun loadImage(index: Int) {
        val bitmap = withContext(Dispatchers.IO) {
            context.assets.open(imagePaths[index]).use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, null)
            }!!
        }
        this.painter = BitmapPainter(bitmap.asImageBitmap())
    }

    override fun onRemembered() {
        rememberedCount++
        Log.d(
            "RememberObserverTest",
            "$longModule. onRemembered. rememberedCount=$rememberedCount"
        )

        if (this.coroutineScope != null) return
        val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        this.coroutineScope = coroutineScope

        coroutineScope.launch {
            snapshotFlow { currentIndex }
                .collectLatest {
                    showLoading = true
                    loadImage(it)
                    showLoading = false
                }
        }
    }

    override fun onAbandoned() = onForgotten()

    override fun onForgotten() {
        rememberedCount--
        Log.w(
            "RememberObserverTest",
            "$longModule. onForgotten. rememberedCount=$rememberedCount"
        )
        if (rememberedCount > 0) return
        val coroutineScope = this.coroutineScope ?: return
        coroutineScope.cancel()
        this.coroutineScope = null
    }
}