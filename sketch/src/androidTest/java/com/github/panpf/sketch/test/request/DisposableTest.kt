package com.github.panpf.sketch.test.request

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.DISK_CACHE
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.OneShotDisposable
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisposableTest {

    @Test
    fun testOneShotDisposable() {
        runBlocking {
            val job = async {
                delay(100)
                delay(100)
                delay(100)
            }
            val disposable = OneShotDisposable(job)
            Assert.assertFalse(disposable.isDisposed)
            delay(100)
            Assert.assertFalse(disposable.isDisposed)
            disposable.dispose()
            delay(100)
            Assert.assertTrue(disposable.isDisposed)
            disposable.dispose()
        }
    }

    @Test
    fun testViewTargetDisposable() {
        val context = getTestContext()
        runBlocking {
            val view = ImageView(context)
            val job = async<DisplayResult> {
                delay(100)
                delay(100)
                delay(100)
                DisplayResult.Success(
                    DisplayRequest(view, TestAssets.SAMPLE_JPEG_URI),
                    ColorDrawable(Color.BLACK),
                    ImageInfo(100, 100, "image/jpeg"), 0, DISK_CACHE, null
                )
            }

            val disposable = view.requestManager.getDisposable(job)
            Assert.assertFalse(disposable.isDisposed)
            delay(100)
            Assert.assertFalse(disposable.isDisposed)
            disposable.dispose()
            delay(100)
            Assert.assertTrue(disposable.isDisposed)
            disposable.dispose()

            disposable.job = job
        }
    }
}