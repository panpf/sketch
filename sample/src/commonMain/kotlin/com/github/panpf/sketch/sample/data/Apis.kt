package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.sample.data.giphy.GiphyApi
import com.github.panpf.sketch.sample.data.pexels.PexelsApi

object Apis {
    val pexelsApi = PexelsApi(Client.client)
    val giphyApi = GiphyApi(Client.client)
}