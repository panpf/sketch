package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.sample.data.Apis
import com.github.panpf.sketch.sample.data.Response
import com.github.panpf.sketch.sample.data.giphy.GiphyGif
import com.github.panpf.sketch.sample.ui.screen.base.BaseRememberObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun rememberGiphyPhotoListViewModel(): GiphyPhotoListViewModel {
    return remember {
        GiphyPhotoListViewModel()
    }
}

class GiphyPhotoListViewModel internal constructor() : BaseRememberObserver() {

    private var coroutineScope: CoroutineScope? = null

    private val _photoList = MutableStateFlow<List<Photo>>(emptyList())
    val photoList: StateFlow<List<Photo>> = _photoList

    override fun onFirstRemembered() {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        this.coroutineScope = coroutineScope

        coroutineScope.launch {
            val response = Apis.giphyApi.trending(pageStart = 0, pageSize = 100)
            if (response is Response.Success) {
                _photoList.value = response.body.dataList?.map { it.toPhoto() } ?: emptyList()
            } else if (response is Response.Error) {
                response.throwable?.printStackTrace()
                _photoList.value = emptyList()
            }
        }
    }

    override fun onLastRemembered() {

    }
}

fun GiphyGif.toPhoto(): Photo {
    return Photo(
        originalUrl = images.original.url,
        thumbnailUrl = images.fixedWidth.url,
    )
}