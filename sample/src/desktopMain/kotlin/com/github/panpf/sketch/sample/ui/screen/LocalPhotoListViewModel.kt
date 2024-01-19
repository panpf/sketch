package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.resources.ResourcesImages
import com.github.panpf.sketch.sample.ui.screen.base.BaseRememberObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun rememberLocalPhotoListViewModel(): LocalPhotoListViewModel {
    return remember {
        LocalPhotoListViewModel()
    }
}

class LocalPhotoListViewModel internal constructor() : BaseRememberObserver() {

    private var coroutineScope: CoroutineScope? = null

    private val _photoList = MutableStateFlow<List<Photo>>(emptyList())
    val photoList: StateFlow<List<Photo>> = _photoList

    override fun onFirstRemembered() {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        this.coroutineScope = coroutineScope

        coroutineScope.launch {
            val longImages = arrayOf(ResourcesImages.longQMSHT)
            _photoList.value =
                (ResourcesImages.statics + ResourcesImages.anims + ResourcesImages.clockExifs + longImages).map {
                    Photo(it.uri, it.uri)
                }
        }
    }

    override fun onLastRemembered() {

    }
}