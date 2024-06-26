package com.github.panpf.sketch.sample.data.api

import com.github.panpf.sketch.sample.data.api.giphy.GiphyApi
import com.github.panpf.sketch.sample.data.api.pexels.PexelsApi

object Apis {
    val pexelsApi = PexelsApi(Client.client)
    val giphyApi = GiphyApi(Client.client)
}