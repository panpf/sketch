package com.github.panpf.sketch.sample.ui.gallery

import androidx.lifecycle.ViewModel
import com.github.panpf.sketch.sample.data.api.giphy.GiphyApi

expect class GiphyPhotoListViewModel(giphyApi: GiphyApi) : ViewModel