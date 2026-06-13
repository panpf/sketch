package com.github.panpf.sketch.sample.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.data.api.Response
import com.github.panpf.sketch.sample.data.api.pexels.PexelsApi
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.mp.KoinPlatform

class FetcherTestViewModel(context: PlatformContext) : ViewModel() {

    private val _data = MutableStateFlow<List<PhotoTestItem>>(emptyList())
    val data: StateFlow<List<PhotoTestItem>> = _data

    init {
        viewModelScope.launch {
            _data.value = buildFetcherTestItems(context, fromCompose = false)
        }
    }
}

expect suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean = true
): List<PhotoTestItem>

suspend fun getOnePexelsPhoto(): String = withContext(ioCoroutineDispatcher()) {
    val result = runCatching {
        val pexelsApi: PexelsApi = KoinPlatform.getKoin().get()
        val response = pexelsApi.curated(1, 1)
        if (response is Response.Success) {
            response.body.photos.first()
        } else {
            response as Response.Error
            throw Exception("Http error: ${response.throwable?.message}")
        }
    }
    return@withContext result.getOrNull()?.src?.large ?: "http://sample.com/404.jpg"
}
