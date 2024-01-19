package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.sample.data.Apis
import com.github.panpf.sketch.sample.data.Response
import com.github.panpf.sketch.sample.data.pexels.PexelsPhoto
import com.github.panpf.sketch.sample.ui.screen.base.BaseRememberObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun rememberPexelsPhotoListViewModel(): PexelsPhotoListViewModel {
    return remember {
        PexelsPhotoListViewModel()
    }
}

class PexelsPhotoListViewModel internal constructor() : BaseRememberObserver() {

    private var coroutineScope: CoroutineScope? = null

    private val _photoList = MutableStateFlow<List<Photo>>(emptyList())
    val photoList: StateFlow<List<Photo>> = _photoList

    override fun onFirstRemembered() {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        this.coroutineScope = coroutineScope

        coroutineScope.launch {
            val response = Apis.pexelsApi.curated(pageIndex = 0, size = 100)
            if (response is Response.Success) {
                _photoList.value = response.body.photos.map { it.toPhoto() }
            } else if(response is Response.Error){
                response.throwable?.printStackTrace()
                _photoList.value = emptyList()
            }
        }
    }

    override fun onLastRemembered() {

    }
}

fun PexelsPhoto.toPhoto(): Photo {
    return Photo(this.src.medium, this.src.original)
}