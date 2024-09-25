package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.RequestState
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestStateTest {

    @Suppress("RemoveExplicitTypeArguments")
    @Test
    fun test() {
        val requestState = RequestState()

        runTest {
            val loadStates = mutableListOf<LoadState?>()
            val job1 = launch(ioCoroutineDispatcher()) {
                requestState.loadState.collect {
                    loadStates.add(it)
                }
            }
            val resultStates = mutableListOf<ImageResult?>()
            val job2 = launch(ioCoroutineDispatcher()) {
                requestState.resultState.collect {
                    resultStates.add(it)
                }
            }
            val progressStates = mutableListOf<Progress?>()
            val job3 = launch(ioCoroutineDispatcher()) {
                requestState.progressState.collect {
                    progressStates.add(it)
                }
            }

            val context = getTestContext()
            val request = ImageRequest(context, "http://test.com/test.jpg")

            requestState.onStart(request)
            block(100)
            assertEquals(
                expected = listOf<LoadState?>(null, LoadState.Started(request)),
                actual = loadStates
            )
            assertEquals(
                expected = listOf<ImageResult?>(null),
                actual = resultStates
            )
            assertEquals(
                expected = listOf<Progress?>(null),
                actual = progressStates
            )

            val progress1 = Progress(100, 10)
            requestState.onUpdateProgress(request, progress1)
            block(100)
            assertEquals(
                expected = listOf<LoadState?>(null, LoadState.Started(request)),
                actual = loadStates
            )
            assertEquals(
                expected = listOf<ImageResult?>(null),
                actual = resultStates
            )
            assertEquals(
                expected = listOf<Progress?>(null, progress1),
                actual = progressStates
            )

            val progress2 = Progress(100, 50)
            requestState.onUpdateProgress(request, progress2)
            block(100)
            assertEquals(
                expected = listOf<LoadState?>(null, LoadState.Started(request)),
                actual = loadStates
            )
            assertEquals(
                expected = listOf<ImageResult?>(null),
                actual = resultStates
            )
            assertEquals(
                expected = listOf<Progress?>(null, progress1, progress2),
                actual = progressStates
            )

            val progress3 = Progress(100, 100)
            requestState.onUpdateProgress(request, progress3)
            block(100)
            assertEquals(
                expected = listOf<LoadState?>(null, LoadState.Started(request)),
                actual = loadStates
            )
            assertEquals(
                expected = listOf<ImageResult?>(null),
                actual = resultStates
            )
            assertEquals(
                expected = listOf<Progress?>(null, progress1, progress2, progress3),
                actual = progressStates
            )

            val successResult = ImageResult.Success(
                request = request,
                image = FakeImage(100, 100),
                cacheKey = "cacheKey",
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                dataFrom = DataFrom.LOCAL,
                resize = Resize(200, 200),
                transformeds = null,
                extras = null
            )
            requestState.onSuccess(request, successResult)
            block(100)
            assertEquals(
                expected = listOf<LoadState?>(
                    null,
                    LoadState.Started(request),
                    LoadState.Success(request, successResult)
                ),
                actual = loadStates
            )
            assertEquals(
                expected = listOf<ImageResult?>(null, successResult),
                actual = resultStates
            )
            assertEquals(
                expected = listOf<Progress?>(null, progress1, progress2, progress3),
                actual = progressStates
            )

            val errorResult = ImageResult.Error(
                request = request,
                image = FakeImage(100, 100),
                throwable = Exception("test")
            )
            requestState.onError(request, errorResult)
            block(100)
            assertEquals(
                expected = listOf<LoadState?>(
                    null,
                    LoadState.Started(request),
                    LoadState.Success(request, successResult),
                    LoadState.Error(request, errorResult)
                ),
                actual = loadStates
            )
            assertEquals(
                expected = listOf<ImageResult?>(null, successResult, errorResult),
                actual = resultStates
            )
            assertEquals(
                expected = listOf<Progress?>(null, progress1, progress2, progress3),
                actual = progressStates
            )

            requestState.onCancel(request)
            block(100)
            assertEquals(
                expected = listOf<LoadState?>(
                    null,
                    LoadState.Started(request),
                    LoadState.Success(request, successResult),
                    LoadState.Error(request, errorResult),
                    LoadState.Canceled(request),
                ),
                actual = loadStates
            )
            assertEquals(
                expected = listOf<ImageResult?>(null, successResult, errorResult),
                actual = resultStates
            )
            assertEquals(
                expected = listOf<Progress?>(null, progress1, progress2, progress3),
                actual = progressStates
            )

            job1.cancel()
            job2.cancel()
            job3.cancel()
        }
    }
}