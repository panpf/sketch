package com.github.panpf.sketch3.common.fetch

import android.net.Uri
import com.github.panpf.sketch3.Sketch3

fun interface Fetcher {

    suspend fun fetch(): FetchResult?

    fun interface Factory {

        fun create(data: Uri, sketch3: Sketch3): Fetcher?
    }
}
