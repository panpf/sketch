package com.github.panpf.sketch.compose.core.common.test.target.internal

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.target.AsyncImageTarget
import com.github.panpf.sketch.target.internal.AsyncImageListener
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AsyncImageListenerTest {

    @Test
    fun test() {
        val context = getTestContext()
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val listener = AsyncImageListener(AsyncImageTarget(state))
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")

        assertEquals(expected = null, actual = state.loadState)
        assertEquals(expected = null, actual = state.result)
        assertEquals(expected = null, actual = state.progress)

        listener.onStart(request)
        assertEquals(
            expected = LoadState.Started(request),
            actual = state.loadState
        )
        assertEquals(expected = null, actual = state.result)
        assertEquals(expected = null, actual = state.progress)

        listener.onUpdateProgress(request, Progress(1024, 100))
        assertEquals(
            expected = LoadState.Started(request),
            actual = state.loadState
        )
        assertEquals(expected = null, actual = state.result)
        assertEquals(expected = Progress(1024, 100), actual = state.progress)

        val successResult = ImageResult.Success(
            request = request,
            image = ColorPainter(Color.Red).asImage(),
            cacheKey = request.key,
            imageInfo = ImageInfo(101, 202, "image/jpeg"),
            dataFrom = DataFrom.LOCAL,
            resize = Resize(100, 100, Precision.EXACTLY, Scale.CENTER_CROP),
            transformeds = null,
            extras = null
        )
        listener.onSuccess(request, successResult)
        assertEquals(
            expected = LoadState.Success(request, successResult),
            actual = state.loadState
        )
        assertEquals(expected = successResult, actual = state.result)
        assertEquals(expected = Progress(1024, 100), actual = state.progress)

        listener.onUpdateProgress(request, Progress(1024, 500))
        assertEquals(
            expected = LoadState.Success(request, successResult),
            actual = state.loadState
        )
        assertEquals(expected = successResult, actual = state.result)
        assertEquals(expected = Progress(1024, 500), actual = state.progress)

        val errorResult = ImageResult.Error(
            request = request,
            image = ColorPainter(Color.Green).asImage(),
            throwable = Exception("Test"),
        )
        listener.onError(request, errorResult)
        assertEquals(
            expected = LoadState.Error(request, errorResult),
            actual = state.loadState
        )
        assertEquals(expected = errorResult, actual = state.result)
        assertEquals(expected = Progress(1024, 500), actual = state.progress)

        listener.onUpdateProgress(request, Progress(1024, 1024))
        assertEquals(
            expected = LoadState.Error(request, errorResult),
            actual = state.loadState
        )
        assertEquals(expected = errorResult, actual = state.result)
        assertEquals(expected = Progress(1024, 1024), actual = state.progress)

        listener.onCancel(request)
        assertEquals(
            expected = LoadState.Canceled(request),
            actual = state.loadState
        )
        assertEquals(expected = errorResult, actual = state.result)
        assertEquals(expected = Progress(1024, 1024), actual = state.progress)
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val element1 = AsyncImageListener(AsyncImageTarget(state))
        val element11 = AsyncImageListener(AsyncImageTarget(state))

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val target = AsyncImageTarget(state)
        val asyncImageListener = AsyncImageListener(target)
        assertEquals(
            expected = "AsyncImageListener@${asyncImageListener.toHexString()}",
            actual = asyncImageListener.toString()
        )
    }
}