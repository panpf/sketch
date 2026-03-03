package com.github.panpf.sketch.sample.ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher

@Stable
@Immutable
data class PhotoTestItem(
    val photoUri: String,
    val title: String? = null,
    val apiSupport: Boolean = true,
    val imageDecoder: Decoder.Factory? = null,
    val imageFetcher: Fetcher.Factory? = null,
)