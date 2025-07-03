package com.github.panpf.sketch.sample.ui.gallery

import androidx.lifecycle.ViewModel
import com.github.panpf.sketch.sample.data.api.pexels.PexelsApi

expect class PexelsPhotoListViewModel(pexelsApi: PexelsApi) : ViewModel