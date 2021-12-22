package com.github.panpf.sketch3.test

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.download.DownloadSuccessResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getContext()
        val sketch3 = Sketch3.Builder(context).build()
        val result = runBlocking {
            sketch3.executeDownload("http://5b0988e595225.cdn.sohucs.com/images/20171219/fd5717876ab046b8aa889c9aaac4b56c.jpeg")
        }
        Assert.assertTrue(result is DownloadSuccessResult)
    }
}