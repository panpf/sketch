package com.github.panpf.sketch.sample.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DisplayInsanityTestViewModel : ViewModel() {

    private val _data = MutableStateFlow<List<Photo>>(emptyList())
    val data: StateFlow<List<Photo>> = _data

    init {
        viewModelScope.launch {
            _data.value = buildList {
                var index = 0
                repeat(100) {
                    ComposeResImageFiles.numbers.forEach { image ->
                        val photo = Photo(
                            originalUrl = image.uri,
                            mediumUrl = null,
                            thumbnailUrl = null,
                            width = image.size.width,
                            height = image.size.height,
                            index = index++,
                        )
                        add(photo)
                    }
                }
            }
        }
    }
}